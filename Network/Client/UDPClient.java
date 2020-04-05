package Network.Client;
import Network.MD5hash;
import Network.NodeList;
import Network.NetworkStatics;
import Network.QueryNodes;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;

public class UDPClient extends Thread {
	private int port;
	private Master master;
	private ArrayList<String> peerdata;
	private MD5hash hasher = new MD5hash();
	private String filename;
	private NodeList findNodes = new NodeList();

	public UDPClient(final String filename)
	{
		this.filename = filename;
	}

	//need filesize, filehash, peerdata
	public void run()
	{
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String url = inetAddress.getHostName();
		ArrayList<String> tempnodelist = new ArrayList<String>();
		tempnodelist.add(url);
		tempnodelist.add(url);
		String m = "abcdefghijklmnopqrstuvwxyz";
		byte[] m1 = m.getBytes();
		byte[] hash = null;
		try {
			hash = hasher.hashBytes(m1);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		master = new Master(tempnodelist,filename,26,hash);
		master.start();

		String[] nodeList = findNodes.getNodes();
		System.out.println(Arrays.toString(nodeList));
		/*byte[] cmd = ByteBuffer.allocate(4).putInt(45).array();
		byte[] fname = filename.getBytes();
		byte[] message = new byte[cmd.length+fname.length];
		QueryNodes qNodes = new QueryNodes(message,tempnodelist);
		byte[] queryData = null;
		try {
			queryData = qNodes.fileQuery();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int queryCmd = NetworkStatics.byteArrayToInt(queryData);

		if(queryCmd==55)
			System.out.println("File Not Found");
		else if(queryCmd==56)
		{
			//startmaster figure out peerlist direct format
		}
		else
		{
			//getpeerlist if returns null start election then start master
		}*/

	}

	public ArrayList<String> getPeerList(String addr) throws IOException {
		InetAddress ip = InetAddress.getByName(addr);
		DatagramSocket udpSocket = new DatagramSocket(5000);
		ArrayList<String> peerlist = new ArrayList<>();
		byte[] bytes = new byte[NetworkStatics.MAX_PACKET_SIZE];
		int commandnumber = 44;
		byte[] cmd = ByteBuffer.allocate(4).putInt(commandnumber).array();
		DatagramPacket packet = new DatagramPacket(cmd,cmd.length, ip, NetworkStatics.SERVER_CONTROL_RECEIVE);
		udpSocket.send(packet);
		packet = new DatagramPacket(bytes, bytes.length);

		try {
			udpSocket.setSoTimeout(1000);
			udpSocket.receive(packet);
		}	catch (SocketTimeoutException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}

		udpSocket.close();
		byte[] nout = new byte[packet.getLength()];
		System.arraycopy(bytes, 0, nout, 0, nout.length);
		int n = nout.length/9;

		for(int i=0;i<n;i++)
		{
			byte[] b = Arrays.copyOfRange(nout,i*9,(i*9)+9);
			String s = new String(b);
			peerlist.add(s);
		}

		return peerlist;
	}
}
