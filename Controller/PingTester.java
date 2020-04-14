package Controller;

import Network.CommandHandler;

import java.io.IOException;
import java.net.*;

/**
 * Used for testing if a firewall is blocking the program.
 */
public class PingTester {
    public static void main(String[] args) throws IOException {

        String iu = "127.0.0.1";
        InetAddress az = InetAddress.getByName(iu);

        System.exit(0);

        String networkToTest = "192.168.62.28";
        int port = 6051;

        byte[] helloworld = new String("Hello World").getBytes();
        CommandHandler h = new CommandHandler();
        byte[] out = h.generatePacket(3, helloworld);

        DatagramPacket packet = new DatagramPacket(out, 0, out.length, InetAddress.getByName(networkToTest), port);
        DatagramSocket sock = new DatagramSocket();
        sock.send(packet);
    }
}
