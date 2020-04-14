package Network.GlobalTracker;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

public class GlobalTracker implements Pulsable {

    private ArrayList<String> connectedNodes;
    private HeartbeatThread<GlobalTracker> beat;
    private ConnectionThread connect;
    private Random rand;

    private GlobalTracker() {
        this.connectedNodes = new ArrayList<>();
        this.connect = new ConnectionThread(this);
        HeartbeatThread.init(1);
        HeartbeatThread.debug = true;
        this.beat = new HeartbeatThread<>(this, "localhost");
        this.rand = new Random();

        this.connect.start();
        this.beat.start();
    }

    synchronized void addNode(String address) throws UnknownHostException {
        if (!this.connectedNodes.contains(address)) {
            if (InetAddress.getByName(address).isLoopbackAddress()) {
                if (HeartbeatThread.debug) System.out.println("Given address is loopback, skipping");
            } else {
                this.connectedNodes.add(address);
            }

        }
    }

    public synchronized void deleteNode(String address) {
        this.connectedNodes.remove(address);
    }

    public synchronized String[] getConnectedNodes() {
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
