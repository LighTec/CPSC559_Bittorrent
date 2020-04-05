package Controller;

import Network.Client.UDPClient;
import Network.NetworkStatics;
import Network.Server.FileManager;
import Network.Server.UDPServer;
import Network.Tracker;

import java.util.ArrayList;

public class Node {

    private FileManager fm;
    private UDPServer server;
    private ArrayList<Tracker> trackers;
    private String ip = "2";

    Node () {
        this.fm = new FileManager();
        this.server = new UDPServer(fm);
        this.server.start();
        this.trackers = new ArrayList<Tracker>();
    }

    public String addFile(String filename) {
        boolean duplicate = false;
        for(Tracker t : this.trackers) {
            if(t.getFileName() == filename) {
                duplicate = true;
            }
        }
        if (!duplicate) {
            System.out.println("Create Tracker");
            String fileName = NetworkStatics.getFilenameFromFilepath(filename);
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(this.ip);
            trackers.add(new Tracker(arrayList, fileName, this.ip));
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
     * @param filename
     * @return 0 if head tracker, 1 if tracker, 2 if none
     */
    public int checkTrackers (String filename) {
        for(Tracker t : this.trackers) {
            if(t.getFileName() == filename) {
                if(t.getLeader() == this.ip) {
                    return 0;
                }

                return 1;
            }
        }

        return 2;
    }

    public void updateLeader (String filename, String leader) {
        for (Tracker t : this.trackers) {
            if(t.getFileName() == filename) {
                t.updateLeader(leader);
            }
        }
    }

    public void addPeerToTracker (String filename, String peer) {
        for (Tracker t : this.trackers) {
            if(t.getFileName() == filename) {
                t.addPeerData(peer);
            }
        }
    }

    public void deletePeerFromTracker (String filename, String peer) {
        for (Tracker t : this.trackers) {
            if(t.getFileName() == filename) {
                t.deletePeerData(peer);
            }
        }
    }

    public ArrayList<String> getPeerListFromTracker (String filename) {
        for (Tracker t : this.trackers) {
            if(t.getFileName() == filename) {
                return t.getPeerList();
            }
        }

        return null;
    }

    public void startClient(String filename) {
        new UDPClient(filename).start();
    }

    public boolean fileOwned (String filename){
        for(Tracker t : this.trackers) {
            if(t.getFileName() == filename) {
                return true;
            }
        }
        return false;
    }

    public void addTracker (Tracker tracker) {
        this.trackers.add(tracker);
    }

    public static void main(String[] args) {
        Node n = new Node();
        String file = n.addFile("./TestFiles/alphabet.txt");
        System.out.println(file);
        n.startClient("alphabet.txt");


        try {
            Thread.sleep(10000);
        } catch (InterruptedException i) {
            i.printStackTrace();
        }

        n.stop();
    }
}
