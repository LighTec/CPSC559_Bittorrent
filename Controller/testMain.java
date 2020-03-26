package Controller;


import Network.CommandHandler;
import Network.NetworkStatics;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;

public class testMain {
    public static void main(String[] args) throws IOException {
        // hash of alphabet.txt
        String filehash = "0C19597843B1C453503EFD57992F7171";
        int startindex = 0;
        int endindex = 128;
        byte buf[] = null;

        byte[] out = new byte[28];
        int commandnumber = 10;
        byte[] cmd = NetworkStatics.intToByteArray(commandnumber);
        byte[] hash = DatatypeConverter.parseHexBinary(filehash);

        CommandHandler handl = new CommandHandler();

        System.arraycopy(cmd,0,out,0,4);
        System.arraycopy(hash,0,out,4,16);
        System.arraycopy(NetworkStatics.intToByteArray(startindex), 0, out, 20, 4);
        System.arraycopy(NetworkStatics.intToByteArray(endindex),0,out,24,4);

        DatagramPacket dp = new DatagramPacket(out, out.length, InetAddress.getLocalHost(), NetworkStatics.SERVER_CONTROL_RECEIVE); //init packet and bind addr,port
        DatagramSocket recv = new DatagramSocket(NetworkStatics.FILE_SEND_LOWERBOUND);
        recv.send(dp);
        dp = new DatagramPacket(new byte[NetworkStatics.MAX_PACKET_SIZE], NetworkStatics.MAX_PACKET_SIZE);
        recv.receive(dp);
        byte[] retdata = dp.getData();
        System.out.println(retdata.length);
        byte[][] parsed = handl.tokenizepacket(retdata);
        NetworkStatics.printPacket(parsed[1], "FILE RETURN DATA");
    }
}
