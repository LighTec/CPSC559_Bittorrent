package Network.GlobalTracker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class HeartbeatThread extends Thread {

    public static final int PORT = 1961;
    public static final int BUFFER_SIZE = 1024;

    private GlobalTracker gt;
    private boolean running;
    private DatagramSocket socket;

    HeartbeatThread(GlobalTracker gt) {
        this.gt = gt;
        this.running = false;
        try {
            this.socket = new DatagramSocket();
            this.socket.setSoTimeout(1500);
        } catch (SocketException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.running = true;
        while (this.running) {
            String[] nodes = this.gt.getConnectedNodes();
            for (String node : nodes) {
                System.out.println(">> Checking " + node);
                boolean alive = false;
                for (int i = 1; i <= 5; i++) {
                    try {
                        String message = "Heartbeat";

                        byte[] msg = message.getBytes();
                        DatagramPacket outPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(node), PORT);
                        socket.send(outPacket);

                        byte[] inMsg = new byte[BUFFER_SIZE];
                        DatagramPacket inPacket = new DatagramPacket(inMsg, inMsg.length);
                        socket.receive(inPacket);

                        if (new String(inPacket.getData(), 0, inPacket.getLength()).equals(message)) {
                            alive = true;
                            System.out.println("<< Alive " + node);
                            break;
                        }
                    } catch (IOException ioe) {
                        System.out.println(String.format("!! Timeout %d %s", i, node));
                    }
                }
                if (!alive) {
                    System.out.println(">> Removed node " + node);
                    this.gt.deleteNode(node);
                }
            }
            try {
                System.out.println(">> Heartbeat sleeping");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void finish() {
        this.running = false;
    }
}
