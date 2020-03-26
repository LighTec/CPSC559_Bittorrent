package Network.GlobalTracker;

import java.util.ArrayList;

public class GlobalTracker {

    private ArrayList<String> connectedNodes;
    private HeartbeatThread beat;
    private ConnectionThread connect;

    private GlobalTracker() {
        this.connectedNodes = new ArrayList<>();
        this.connect = new ConnectionThread(this);
        this.beat = new HeartbeatThread(this);

        this.connect.start();
        this.beat.start();
    }

    synchronized void addNode(String address) {
        this.connectedNodes.add(address);
    }

    synchronized void deleteNode(String address) {
        this.connectedNodes.remove(address);
    }

    synchronized String[] getConnectedNodes() {
        return this.connectedNodes.toArray(new String[]{});
    }

    private void stop() {
        this.beat.finish();
        this.connect.finish();
    }

    public static void main(String[] args) {
        GlobalTracker gt = new GlobalTracker();
        System.out.println(">> Global tracker started");
    }
}
