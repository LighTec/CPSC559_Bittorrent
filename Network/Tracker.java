package Network;

import java.util.ArrayList;

public class Tracker {

    private ArrayList<String[]> peerList;
    private int fileSize;
    private byte[] hash;
    private String fileName;
    private String[] leader;

    public Tracker (ArrayList<String[]> peerList, int fileSize, byte[] hash, String fileName, String[] leader) {
        this.peerList = peerList;
        this.fileSize = fileSize;
        this.hash = hash;
        this.fileName = fileName;
        this.leader = leader;
    }

    // A method to add a new peer to current peerList
    // @param newPeer contains ["ip address", "port"]
    public void addPeerData(String[] newPeer) {
        this.peerList.add(newPeer);
    }

    public void deletePeerData(String[] deletePeer) {
        this.peerList.remove(deletePeer);
    }

    public void updateLeader (String[] newLeader) {
        this.leader = newLeader;
    }

    public ArrayList<String[]> getPeerList () {
        return this.peerList;
    }

    // A method to get the current leader for the file
    // @return the leader info which contains ["ip address", "port"]
    public String[] getLeader () {
        return this.leader;
    }

    public String getFileName() {
        return this.fileName;
    }

    public int getFileSize() {
        return this.fileSize;
    }

    public byte[] getHash() {
        return this.hash;
    }


}
