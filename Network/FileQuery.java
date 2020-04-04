package Network;

import java.io.IOException;
import java.net.*;

public class FileQuery extends Thread {

    private InetAddress ip;
    private DatagramSocket udpSocket;
    private byte[] message;
    private QueryNodes query;

    public FileQuery(byte[] message,InetAddress ip,int port,QueryNodes query) throws SocketException {
        this.message = message;
        this.ip = ip;
        this.udpSocket = new DatagramSocket(port);
        this.query = query;
    }

    public void run()
    {
        byte[] bytes = new byte[65507];
        DatagramPacket packet = new DatagramPacket(message,message.length, ip, 8778);
        try {
            udpSocket.send(packet);
            packet = new DatagramPacket(bytes, bytes.length);
            udpSocket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] nout = new byte[packet.getLength()];
        System.arraycopy(bytes, 0, nout, 0, nout.length);
        query.processQuery(nout);
        udpSocket.close();
    }
}
