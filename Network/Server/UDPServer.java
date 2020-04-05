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
import java.nio.ByteBuffer;
import java.nio.file.NoSuchFileException;
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
                int cmd = NetworkStatics.byteArrayToInt(parsed[0]); // parse first 4 bytes to integer

                // Debug print statements
                //System.out.println("Command number: " + cmd);
                //System.out.println("Command length: " + parsed[1].length); // print data length

                switch (cmd){
                    case 0:
                        System.out.println("Request for heartbeat");
                        InetAddress requesterIP = this.recvpacket.getAddress();
                        int requesterPort = this.recvpacket.getPort();
                        byte[] outCmd = ByteBuffer.allocate(4).putInt(1).array();
                        DatagramPacket outPacket = new DatagramPacket(outCmd, outCmd.length, requesterIP, requesterPort);
                        this.sendsocket.send(outPacket);
                        break;
                    case 1:
                        System.out.println("Reply to heartbeat");
//                        System.out.println("Hearbeat request/reply sent to server port(" + NetworkStatics.SERVER_CONTROL_RECEIVE + "), should be sent to heartbeat port (" + NetworkStatics.HEARTBEAT_PORT + ").");
                        break;
                    case 5:
                        // TODO implement seeder request
                        System.err.println("seeder request not implemented yet: " + getClass().getName());
                        break;
                    case 6:
                        // TODO implement return seeder request
                        System.err.println("return seeder not implemented yet: " + getClass().getName());
                        break;
                    case 10:
                        int startindex10 = NetworkStatics.byteArrayToInt(parsed[1],0);
                        int endindex10 = NetworkStatics.byteArrayToInt(parsed[1],4);
                        byte[] name10 = Arrays.copyOfRange(parsed[1], 8, parsed[1].length);
                        InetAddress requesterip = this.recvpacket.getAddress();
                        int length10 = endindex10 - startindex10 + 1;

                        RandomAccessFile toget = this.fm.getFile(name10);
                        byte[] datatosend10 = new byte[length10];
                        toget.seek(startindex10);
                        int bytesread = toget.read(datatosend10,0, length10);

                        if(bytesread != length10){
                            System.err.println("File bytes read is not equal to bytes requested to be read!\n" +
                                    "Expected: " + length10 + "   Actual:" + bytesread + ".");
                        }

                        // split the datatosend array so it can fit into 64k udp packets
                        byte[][] splitdata = NetworkStatics.chunkBytes(datatosend10,NetworkStatics.MAX_USEABLE_PACKET_SIZE - 20);
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
                        int startindex12 = NetworkStatics.byteArrayToInt(parsed[1],0);
                        int endindex12 = NetworkStatics.byteArrayToInt(parsed[1],4);
                        int chunknumber12 = NetworkStatics.byteArrayToInt(parsed[1], 8);
                        byte[] name12 = Arrays.copyOfRange(parsed[1], 12, parsed[1].length);
                        InetAddress requesterip12 = this.recvpacket.getAddress();

                        byte[] returnedData12 = this.generateFilePacket(name12, startindex12, endindex12, chunknumber12);
                        this.sendpacket = new DatagramPacket(returnedData12, returnedData12.length, requesterip12, this.recvpacket.getPort());
                        this.sendsocket.send(this.sendpacket);
                        break;
                    case 20:
                        // TODO implement ready to seed
                        System.err.println("ready to seed not implemented yet: " + getClass().getName());
                        break;
                    case 22:
                        //TODO new file command
                        break;
                    case 23:
                        break;
                    case 24:
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

    private byte[] generateFilePacket(byte[] name, int start, int end, int chunknum) throws IOException, NoSuchAlgorithmException {
        int chunkstartindex = start + (chunknum * (NetworkStatics.MAX_USEABLE_PACKET_SIZE - 20));
        int chunkendindex = chunkstartindex + NetworkStatics.MAX_USEABLE_PACKET_SIZE - 20;
        int maxchunklen = chunkendindex - chunkstartindex + 1;

        RandomAccessFile toget = this.fm.getFile(name);
        toget.seek(chunkstartindex);
        byte[] filedata = new byte[maxchunklen];
        int bytesread = toget.read(filedata,0, maxchunklen);
        if(bytesread == 0){
            throw new IOException("Failed to read any data during file packet generation...");
        }

        byte[] output = new byte[bytesread + 20];
        System.arraycopy(NetworkStatics.intToByteArray(chunknum),0, output, 0, 4);
        byte[] datahash = this.hasher.hashBytes(filedata);
        System.arraycopy(datahash, 0, output, 4, 16);
        System.arraycopy(filedata, 0, output, 20, filedata.length);

        byte[] tosend = this.handler.generatePacket(11, output);
        return tosend;
    }

    public void terminate(){
        this.running = false;
    }

}
