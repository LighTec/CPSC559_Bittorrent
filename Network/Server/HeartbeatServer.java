package Network.Server;

import Network.CommandHandler;
import Network.NetworkStatics;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class HeartbeatServer extends Thread{


    /*



    WARNING! WORK IN PROGRESS



    WILL ONLY PARTIALLY WORK. WILL FIX IN NEXT PUSH





     */

    private CommandHandler handler;
    private DatagramSocket socket;
    private DatagramPacket packet;

    private byte[] buf;

    private boolean running = false;

    private int packetlen;

    public HeartbeatServer(){
        this.handler = new CommandHandler();
        try {
            this.socket = new DatagramSocket(NetworkStatics.HEARTBEAT_PORT);
        } catch (SocketException e) {
            System.err.println("Failure initializing datagram socket on port " + NetworkStatics.HEARTBEAT_PORT + ".");
            e.printStackTrace();
        }
        this.buf = new byte[this.handler.getCmdLen(0) + 4]; // get error cmd length
        this.packetlen = this.handler.getCmdLen(0) + 4;
    }

    public void run(){
        this.running = true;
        while(this.running){
            this.packet = new DatagramPacket(this.buf, this.buf.length);
            try {
                this.socket.receive(this.packet); // wait until we get some data
                this.buf = this.packet.getData(); // put data into byte buffer
                byte[][] parsed = this.handler.tokenizepacket(this.buf); // tokenize data
                int cmd = NetworkStatics.byteArrayToInt(parsed[0]);
                InetAddress sender = this.packet.getAddress();
                if(cmd == 0){
                    byte[] out = new byte[packetlen];
                    System.arraycopy(NetworkStatics.intToByteArray(1),0,out,0,4);
                    System.arraycopy(InetAddress.getLocalHost().getAddress(),0,out,4,4);
                    this.packet = new DatagramPacket(out, this.packetlen, sender, NetworkStatics.HEARTBEAT_PORT);
                    this.socket.send(this.packet);
                    System.out.println("Received heartbeat request from " + sender.getHostAddress() + ", replying...");
                }if(cmd == 1){
                  // TODO implement receiving heartbeats & managing who to hearbeat and who is replying to heartbeats



                }else{
                    System.out.println("Received command " + cmd + " from " + sender.getHostAddress() + ". This should not happen!");
                }
            } catch (IOException e) {
                System.err.println("Failure reading data on port " + NetworkStatics.HEARTBEAT_PORT + ".");
                e.printStackTrace();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.err.println("Heartbeat got interrupted during a sleep().");
            }
        }
        this.socket.close();
    }

    public void terminate(){
        this.running = false;
    }
}
