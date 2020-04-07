package Controller;

import Network.CommandHandler;

import java.io.IOException;
import java.net.*;

public class PingTester {
    public static void main(String[] args) throws IOException {
        String networkToTest = "localhost";
        int port = 6051;

        byte[] helloworld = new String("Hello World").getBytes();
        CommandHandler h = new CommandHandler();
        byte[] out = h.generatePacket(3, helloworld);

        DatagramPacket packet = new DatagramPacket(out,0,out.length, InetAddress.getByName(networkToTest), port);
        DatagramSocket sock = new DatagramSocket();
        sock.send(packet);
    }
}
