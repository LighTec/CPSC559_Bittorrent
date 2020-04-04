package Controller;

import Network.GlobalTracker.HeartbeatThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class MessageThread extends Thread {

    private boolean running;
    private DatagramSocket socket;

    MessageThread() {
        this.running = false;
        try {
            this.socket = new DatagramSocket(HeartbeatThread.PORT);
        } catch (SocketException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.running = true;
        while (this.running) {
            try {
                byte[] buffer = new byte[HeartbeatThread.BUFFER_SIZE];
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(inPacket);

                DatagramPacket outPacket = new DatagramPacket(inPacket.getData(), inPacket.getLength(), inPacket.getAddress(), inPacket.getPort());
                socket.send(outPacket);

                System.out.println("Beat");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }

    void finish() {
        this.running = false;
    }
}
