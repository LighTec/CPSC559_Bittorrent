package Network.Server;

import Network.CommandHandler;
import com.sun.media.jfxmedia.logging.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class UDPServer extends Thread{

    public static final int BUFFERSIZE = 64000;

    private CommandHandler handler;
    private int port;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private byte[] buf;

    private boolean running = false;

    public UDPServer(int port){
        this.port = port;
        this.handler = new CommandHandler();
        try {
            this.buf = new byte[BUFFERSIZE];
            this.socket = new DatagramSocket(this.port);
        } catch (SocketException e) {
            System.err.println("Failure initializing datagram socket on port " + this.port + ".");
            e.printStackTrace();
        }
    }

    public void run(){
        this.running = true;
        while(this.running){
            this.packet = new DatagramPacket(this.buf, this.buf.length);
            try {
                this.socket.receive(this.packet); // wait until we get some data
                this.buf = this.packet.getData(); // put data into byte buffer
                byte[][] parsed = this.handler.tokenizepacket(this.buf); // tokenize data
                int cmd = ByteBuffer.wrap(parsed[0]).getInt(); // get command number
                System.out.println("Command number: " + cmd);
                System.out.println("Command length: " + parsed[1].length); // print data length
                String out = new String(parsed[1]); // parse data to String
                System.out.println("Received data: \"" + out + "\"");
            } catch (IOException e) {
                System.err.println("Failure reading data on port " + this.port + ".");
                e.printStackTrace();
            }
        }
    }

    public void terminate(){
        this.running = false;
    }
}
