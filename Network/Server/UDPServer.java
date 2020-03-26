package Network.Server;

import Network.CommandHandler;
import Network.NetworkStatics;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class UDPServer extends Thread{

    private CommandHandler handler;
    private int port;
    private DatagramSocket recvsocket;
    private DatagramSocket sendsocket;
    private DatagramPacket recvpacket;
    private DatagramPacket sendpacket;
    private byte[] buf;

    private FileManager fm;

    private boolean running = false;

    public UDPServer(FileManager man){
        this.port = port;
        this.handler = new CommandHandler();
        try {
            this.buf = new byte[NetworkStatics.MAX_PACKET_SIZE];
            this.recvsocket = new DatagramSocket(NetworkStatics.SERVER_CONTROL_RECEIVE);
            this.sendsocket = new DatagramSocket();
            this.fm = man;
        } catch (SocketException e) {
            System.err.println("Failure initializing datagram socket on port " + this.port + ".");
            e.printStackTrace();
        }
    }

    public void run(){
        this.running = true;
        while(this.running){
            this.recvpacket = new DatagramPacket(this.buf, this.buf.length);
            try {
                this.recvsocket.receive(this.recvpacket); // wait until we get some data
                this.buf = this.recvpacket.getData(); // put data into byte buffer
                byte[][] parsed = this.handler.tokenizepacket(this.buf); // tokenize data
                int cmd = NetworkStatics.byteArrayToInt(parsed[0]);
                System.out.println("Command number: " + cmd);
                System.out.println("Command length: " + parsed[1].length); // print data length

                switch (cmd){
                    case 0:
                    case 1:
                        System.out.println("Hearbeat request/reply sent to server port(" + NetworkStatics.SERVER_CONTROL_RECEIVE + "), should be sent to heartbeat port (" + NetworkStatics.HEARTBEAT_PORT + ").");
                        break;
                    case 2:
                        break;
                    case 3:
                        String out = new String(parsed[1]); // parse data to String
                        System.out.println("Test message sent: " + out);
                        break;
                    case 4:
                        int errorno = NetworkStatics.byteArrayToInt(parsed[0]);
                        InetAddress erroraddr = this.recvpacket.getAddress();
                        System.out.println("Error number " + errorno + " from address " + erroraddr.getHostAddress());
                        break;
                    case 5:
                        // TODO implement seeder request
                        System.err.println("seeder request not implemented yet: " + getClass().getName());
                        break;
                    case 6:
                        // TODO implement return seeder request
                        System.err.println("return seeder not implemented yet: " + getClass().getName());
                        break;
                    case 7:
                    case 8:
                    case 9:
                        // invalid commands reserved for future functions
                        break;
                    case 10:
                        // TODO implement files transfer of 64k and larger files
                        // TODO spin off to separate thread to execute
                        byte[] hash = Arrays.copyOfRange(parsed[1], 0, 16);
                        int startindex = NetworkStatics.byteArrayToInt(parsed[1],16);
                        int endindex = NetworkStatics.byteArrayToInt(parsed[1],20);
                        InetAddress requesterip = this.recvpacket.getAddress();
                        int length = endindex - startindex;

                        RandomAccessFile toget = this.fm.getFile(hash);
                        byte[] datatosend = new byte[length];
                        int bytesread = toget.read(datatosend, startindex, length);

                        if(bytesread != length){
                            System.err.println("File bytes read is not equal to bytes requested to be read!\n" +
                                    "Expected: " + length + "   Actual:" + bytesread + ".");
                        }
                        byte[] output = new byte[datatosend.length + 12];
                        System.arraycopy(NetworkStatics.intToByteArray(0), 0, output, 0, 4);
                        System.arraycopy(NetworkStatics.intToByteArray(startindex), 0, output, 4, 4);
                        System.arraycopy(NetworkStatics.intToByteArray(endindex),0, output, 8, 4);
                        System.arraycopy(datatosend, 0, output, 12, datatosend.length);
                        byte[] tosend = this.handler.generatePacket(11, output);
                        if(tosend.length > 64000){
                            System.err.println("Large file transfer not allowed.");
                            throw new ArrayIndexOutOfBoundsException();
                        }

                        this.sendpacket = new DatagramPacket(tosend, tosend.length, requesterip, NetworkStatics.FILE_SEND_LOWERBOUND);

                        NetworkStatics.printPacket(this.sendpacket.getData(), "FILE SEND PACKET");

                        this.sendsocket.send(this.sendpacket);
                        break;
                    case 11:
                        System.err.println("File chunk sent to server. Discarding...");
                        break;
                    case 12:
                        //TODO implement chunk resend
                        break;
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                        // invalid commands reserved for future functions
                        break;
                    case 20:
                        // TODO implement ready to seed
                        System.err.println("ready to seed not implemented yet: " + getClass().getName());
                        break;
                    case 21:
                        // TODO implement unable to seed
                        System.err.println("unable to seed not implemented yet: " + getClass().getName());
                        break;
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                        // invalid commands reserved for future functions
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value in " + getClass().getName() + " switch statement: " + cmd);
                }
            }catch (IOException e) {
                System.err.println("Failure reading data on port " + this.port + ".");
                e.printStackTrace();
            }
        }
    }

    public void terminate(){
        this.running = false;
    }
}
