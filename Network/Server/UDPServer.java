package Network.Server;

import Network.CommandHandler;
import Network.MD5hash;
import Network.NetworkStatics;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class UDPServer extends Thread{

    private CommandHandler handler;
    private int port;
    private DatagramSocket recvsocket;
    private DatagramSocket sendsocket;
    private DatagramPacket recvpacket;
    private DatagramPacket sendpacket;
    private byte[] buf;

    private MD5hash hasher;

    private FileManager fm;

    private boolean running = false;

    public UDPServer(FileManager man){
        this.port = port;
        this.hasher = new MD5hash();
        this.handler = new CommandHandler();
        try {
            this.buf = new byte[NetworkStatics.MAX_USEABLE_PACKET_SIZE];
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
                        int startindex = NetworkStatics.byteArrayToInt(parsed[1],0);
                        int endindex = NetworkStatics.byteArrayToInt(parsed[1],4);
                        byte[] name = Arrays.copyOfRange(parsed[1], 8, parsed[1].length);
                        InetAddress requesterip = this.recvpacket.getAddress();
                        int length = endindex - startindex;

                        RandomAccessFile toget = this.fm.getFile(name);
                        byte[] datatosend = new byte[length];
                        int bytesread = toget.read(datatosend, startindex, length);

                        if(bytesread != length){
                            System.err.println("File bytes read is not equal to bytes requested to be read!\n" +
                                    "Expected: " + length + "   Actual:" + bytesread + ".");
                        }

                        // split the datatosend array so it can fit into 64k udp packets
                        byte[][] splitdata = NetworkStatics.chunkBytes(datatosend,NetworkStatics.MAX_USEABLE_PACKET_SIZE - 20);
                        DatagramPacket[] sendarray = new DatagramPacket[splitdata.length];

                        for (int i = 0; i < splitdata.length; i++) {
                            byte[] output = new byte[splitdata[i].length + 20];
                            System.arraycopy(NetworkStatics.intToByteArray(i),0, output, 0, 4);
                            byte[] datahash = this.hasher.hashBytes(splitdata[i]);
                            System.arraycopy(datahash, 0, output, 4, 16);
                            System.arraycopy(splitdata[i], 0, output, 20, splitdata[i].length);
                            byte[] tosend = this.handler.generatePacket(11, output);
                            sendarray[i] = new DatagramPacket(tosend, tosend.length, requesterip, this.recvpacket.getPort());
                            this.sendsocket.send(sendarray[i]);
                        }
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
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminate(){
        this.running = false;
    }
}
