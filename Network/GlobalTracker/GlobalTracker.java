package Network.GlobalTracker;

import java.util.ArrayList;
import java.util.Random;

public class GlobalTracker {

    private ArrayList<String> connectedNodes;
    private HeartbeatThread beat;
    private ConnectionThread connect;
    private Random rand;

    private GlobalTracker() {
        this.connectedNodes = new ArrayList<>();
        this.connect = new ConnectionThread(this);
        this.beat = new HeartbeatThread(this);
        this.rand = new Random();

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

    synchronized String[] getRandomNodes(int count) {
        if (count >= this.connectedNodes.size()) {
            return this.getConnectedNodes();
        }
        ArrayList<String> allNodes = new ArrayList<>(this.connectedNodes);
        String[] nodes = new String[count];
        for (int i = 0; i < count; i++) {
            int idx = rand.nextInt(allNodes.size());
            nodes[i] = allNodes.get(idx);
            allNodes.remove(idx);
        }
        return nodes;
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
