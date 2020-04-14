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
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Node {

    public final static boolean LOCAL_ONLY = false;

    private FileManager fm;
    private UDPServer server;
    private ArrayList<Tracker> trackers;
    private String ip;

    public final static String TESTFILE = ".\\TestFiles\\alphabet.txt"; // DELETE ME

    Node() {
        // initialize filemanager, start UDP server, create empty arraylist of trackers
        this.fm = new FileManager();
        this.server = new UDPServer(fm, this, NetworkStatics.SERVER_CONTROL_RECEIVE);
        this.server.start();
        this.trackers = new ArrayList<>();

        // if running in LAN only mode, get local address. Otherwise, get WAN address.
        if(LOCAL_ONLY){
            try {
                this.ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }else{
            try {
                URL myIP = new URL("http://checkip.amazonaws.com");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        myIP.openStream()));
                this.ip = in.readLine().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Get IP address of this machine, as other peers see it.
     * @return
     */
    public String getIP() {
        return this.ip;
    }

    /**
     * Add a local file to the node to make it available.
     * @param filename
     * @return
     */
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

    /**
     * Terminate this node.
     */
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

    /**
     * Get the current head tracker for a filename.
     * @param filename
     * @return
     */
    public String getLeader(String filename) {
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                return t.getLeader();
            }

        }
        return "";
    }

    /**
     * Update the lead tracker for a filename, in the local tracker list.
     * @param filename
     * @param leader
     */
    public void updateLeader(String filename, String leader) {
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                t.updateLeader(leader);
            }
        }
    }

    /**
     * Add a peer to the local tracker list, for a specific filename.
     * @param filename
     * @param peer
     */
    public void addPeerToTracker(String filename, String peer) {
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                t.addPeerData(peer);
                System.out.println("Added " + peer + " to " + filename);
            }
        }
    }

    /**
     * Delete a peer from the local tracker list, for a specfic filename.
     * @param filename
     * @param peer
     */
    public void deletePeerFromTracker(String filename, String peer) {
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                t.deletePeerData(peer);
            }
        }
    }

    /**
     * Get the peer list for a filename from the local tracker.
     * @param filename
     * @return
     */
    public ArrayList<String> getPeerListFromTracker(String filename) {
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                return t.getPeerList();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Start the UDPClient to send requests to other nodes, and receive file data.
     * @param filename
     */
    public void startClient(String filename) {
        new UDPClient(filename, this).start();
    }

    /**
     * Check if this Node has a tracker for the specified file. If it does not, then the file is not "owned" by anybody.
     * @param filename
     * @return
     */
    public boolean fileOwned(String filename) {
        for (Tracker t : this.trackers) {
            if (t.getFileName().equals(filename)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a new tracker to the list, used when a new file is added locally.
     * @param tracker
     */
    public void addTracker(Tracker tracker) {
        this.trackers.add(tracker);
    }

    /**
     * Return the FileManager, which is used to access the physical files on disk.
     * @return
     */
    public FileManager getFileManager() {
        return this.fm;
    }

    public static void main(String[] args) throws Exception {
        HeartbeatThread.init();
        CommandHandler cm = new CommandHandler();
        Node n = new Node();
//        new NodeList().getNodes();
//        String file = n.addFile(TESTFILE);
//        System.out.println(file);
//        n.addFile("./TestFiles/413.pdf");
        n.startClient("alphabet.txt");

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
