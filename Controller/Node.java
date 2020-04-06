package Controller;

import Network.Client.UDPClient;
import Network.CommandHandler;
import Network.NetworkStatics;
import Network.Server.FileManager;
import Network.Server.UDPServer;
import Network.Tracker;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class Node {

    private FileManager fm;
    private UDPServer server;
    private ArrayList<Tracker> trackers;
    private String ip;

    Node() {
        this.fm = new FileManager();
        this.server = new UDPServer(fm, this);
        this.server.start();
        this.trackers = new ArrayList<>();
        try {
            this.ip = InetAddress.getLocalHost().getHostAddress();
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
            if (t.getFileName() == filename) {
                duplicate = true;
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

    public static void main(String[] args) throws Exception {
        CommandHandler cm = new CommandHandler();
        Node n = new Node();
        String file = n.addFile("./TestFiles/alphabet.txt");
        System.out.println(file);
        n.startClient("alphabet.txt");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        n.stop();

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

        /*
        Scanner myObj = new Scanner(System.in);

        String input;
        while (true) {
            System.out.println("1: Download a file");
            System.out.println("2: Upload a file");
            System.out.println("3 Dubugging mode");
            System.out.println("4: Exit");

            System.out.print("Enter input here: ");
            input = myObj.nextLine().trim();
            //For downloading
            if (input.equals("1")) {
                System.out.println("What file do you want to download?");
                System.out.print("Enter file here: (include the type file of i.e. .txt, .zip)");
                input = myObj.nextLine().trim();
                n.startClient(input);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException i) {
                    i.printStackTrace();
                }

            }
            // For uploading file
            else if (input.equals("2")) {
                System.out.println("What file do you want to upload?");
                System.out.print("Enter file here including the directory: (for example: ./TestFiles/alphabet.txt)");
                input = myObj.nextLine().trim();
                String hash = n.addFile(input);
                System.out.println("Added a file with hash: " + hash);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException i) {
                    i.printStackTrace();
                }
            }
            // For debugging
            else if (input.equals("3")) {
                System.out.println("Debugging mode....");
                // W.I.P.
            }
            //Exiting
            else if (input.equals("4")) {
                System.out.println("Exiting.....");
                break;
            }
        }
        */
    }
}
