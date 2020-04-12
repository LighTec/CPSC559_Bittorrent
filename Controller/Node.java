package Controller;

import Network.Client.UDPClient;
import Network.CommandHandler;
import Network.GlobalTracker.HeartbeatThread;
import Network.NetworkStatics;
import Network.NodeList;
import Network.Server.FileManager;
import Network.Server.UDPServer;
import Network.Tracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Node {

    private FileManager fm;
    private UDPServer server;
    private ArrayList<Tracker> trackers;
    private String ip;

    public final static String TESTFILE = ".\\TestFiles\\alphabet.txt"; // DELETE ME

    Node() {
        this.fm = new FileManager();
        this.server = new UDPServer(fm, this, NetworkStatics.SERVER_CONTROL_RECEIVE);
        this.server.start();
        this.trackers = new ArrayList<>();
        try {
            URL myIP = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    myIP.openStream()));
            this.ip = in.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIP() {
        return this.ip;
    }

    public String addFile(String filename) {
        boolean duplicate = false;
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                duplicate = true;
                break;
            }
        }
        if (!duplicate) {
            System.out.println("Create Tracker");
            String fileName = NetworkStatics.getFilenameFromFilepath(filename);
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(this.ip);
            trackers.add(new Tracker(arrayList, fileName, this.ip, this, true));
            return "Added File: " + this.fm.addFile(filename);
        } else {
            return "File Added Already";
        }
    }

    public void stop() {
        this.server.terminate();
    }

    /**
     * Checks if this node has a tracker for a certain file
     *
     * @param filename
     * @return 0 if head tracker, 1 if tracker, 2 if none
     */
    public int checkTrackers(String filename) {
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                if (t.getLeader().equals(this.ip)) {
                    return 0;
                }

                return 1;
            }
        }

        return 2;
    }

    public String getLeader(String filename) {
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                return t.getLeader();
            }

        }
        return "";
    }

    public void updateLeader(String filename, String leader) {
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                t.updateLeader(leader);
            }
        }
    }

    public void addPeerToTracker(String filename, String peer) {
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                t.addPeerData(peer);
                System.out.println("Added " + peer + " to " + filename);
            }
        }
    }

    public void deletePeerFromTracker(String filename, String peer) {
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                t.deletePeerData(peer);
            }
        }
    }

    public ArrayList<String> getPeerListFromTracker(String filename) {
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                return t.getPeerList();
            }
        }
        return new ArrayList<>();
    }

    public void startClient(String filename) {
        new UDPClient(filename, this).start();
    }

    public boolean fileOwned(String filename) {
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                return true;
            }
        }
        return false;
    }

    public void addTracker(Tracker tracker) {
        this.trackers.add(tracker);
    }

    public FileManager getFileManager() {
        return this.fm;
    }

    public static void main(String[] args) throws Exception {
  //      HeartbeatThread.init();
   //     CommandHandler cm = new CommandHandler();
        Node n = new Node();
        new NodeList().getNodes();
//        String file = n.addFile(TESTFILE);
//        System.out.println(file);
//        n.addFile("./TestFiles/413.pdf");
//        n.startClient("413.pdf");

        /* DELETE ONCE DONE*/
//        ArrayList<String> peerList = new ArrayList<>();
//        peerList.add("69.420.96");
//        peerList.add("96.420.69");
//        peerList.add("123");
//        peerList.add("123313231");
//        peerList.add("2131231241");
//        peerList.add("213123124132131");
//        peerList.add("21312312413123213123142314");
//        String fileName = "alphabet.txt";
//        Tracker t = new Tracker(peerList, fileName, "69.420.96");
//        n.addTracker(t);
//        DatagramSocket sendsocket = new DatagramSocket();
//
//        byte[] fileByte = fileName.getBytes();
//        System.out.println("byte " + Arrays.toString(fileByte));
//        byte[] cmd = cm.generatePacket(24, fileByte);
//        DatagramPacket outPacket = new DatagramPacket(cmd, cmd.length, InetAddress.getByName("localhost"), NetworkStatics.SERVER_CONTROL_RECEIVE);
//        sendsocket.send(outPacket);


        Scanner myObj = new Scanner(System.in);

        String input;
        while (true) {
            System.out.println("1: Download a file");
            System.out.println("2: Upload a file");
            System.out.println("3: Exit");

            System.out.print("Enter the number here: ");
            input = myObj.nextLine().trim();
            //For downloading
            if (input.equals("1")) {
                System.out.println("What file do you want to download?");
                System.out.print("Enter file here: (include the type file of i.e. .txt, .zip)");
                input = myObj.nextLine().trim();
                n.startClient(input);
                System.out.println("Downloading.....");
                Thread.sleep(1000);
                System.out.println("Check your the folder where you have these files and see the downloaded file");
                Thread.sleep(1000);
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException i) {
//                    i.printStackTrace();
//                }

            }
            // For uploading file
            else if (input.equals("2")) {
                System.out.println("What file do you want to upload?");
                System.out.print("Enter file here including the directory: (for example: ./TestFiles/alphabet.txt)");
                input = myObj.nextLine().trim();

                n.addFile(input);
                System.out.println("Your file can now be seeded by others");
                Thread.sleep(1000);
                continue;
               // System.out.println("Added a file with hash: " + hash);
                /*try {
                    Thread.sleep(10000);
                } catch (InterruptedException i) {
                    i.printStackTrace();
                }*/
            }
            //Exiting
            else if (input.equals("3")) {
                System.out.println("Exiting.....");
                break;
            }
        }

    }
}
