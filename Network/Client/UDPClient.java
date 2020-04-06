package Network.Client;
import Controller.Node;
import Network.MD5hash;
import Network.NetworkStatics;
import Network.NodeList;
import Network.QueryNodes;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class UDPClient extends Thread {
	private MD5hash hasher = new MD5hash();
	private String filename;
	private NodeList findNodes = new NodeList();
	private Node n;

	public UDPClient(final String filename,Node n)
	{
		this.filename = filename;
		this.n = n;
	}

	public void run()
	{
		String[] nodeList = findNodes.getNodes();
		System.out.println(Arrays.toString(nodeList));
		ArrayList<String> nlist = new ArrayList<String>();

		for(String b:nodeList)
			nlist.add(b);

		byte[] cmd = ByteBuffer.allocate(4).putInt(5).array();
		byte[] fname = ByteBuffer.allocate(32).put(filename.getBytes()).array();
		byte[] message = new byte[36];
		System.arraycopy(cmd,0,message,0,cmd.length);
		System.arraycopy(fname,0,message,cmd.length,fname.length);
		QueryNodes qNodes = new QueryNodes(message,nlist);
		byte[] queryData = null;
		try {
			queryData = qNodes.fileQuery();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int queryCmd = NetworkStatics.byteArrayToInt(queryData);
		ArrayList<String> peerList = new ArrayList<String>();
		if(queryCmd==46) //file not found
			System.out.println("File Not Found");
		else if(queryCmd==45) //direct peer list is head cmd4byte:filesize16byte:hash16pyte:yourip9bytes:stringip(9*n)
		{
			int filesize = ByteBuffer.wrap(Arrays.copyOfRange(queryData,4,20)).getInt();
			byte[] hash = Arrays.copyOfRange(queryData,20,36);
			byte[] hip = Arrays.copyOfRange(queryData,36,45);
			String leader = new String(hip);
			for(int i=45;i<queryData.length;i+=12)
			{
				byte[] b = Arrays.copyOfRange(queryData,i,i+12);
				String s = new String(b);
				peerList.add(s);
			}
			Master master = new Master(peerList,this.filename,filesize,hash,this.n,leader);
			master.start();
		}
		else //using head tracker info cmd 44 cmd:headtrackerip9byte:yourip9bytes
		{
			byte[] headip = Arrays.copyOfRange(queryData,4,16);
			String hd = new String(headip);
			byte[] trackerip = Arrays.copyOfRange(queryData,16,28);
			String td = new String(trackerip);
			ArrayList<byte[]> peerData = new ArrayList<byte[]>();
			try {
				peerData = getPeerData(hd);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String newhead = null;
			if(peerData==null)
			{
				try {
					hd = startElection(td);
					peerData = getPeerData(newhead);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			byte[] fsize = peerData.get(0);
			int fiSize = ByteBuffer.wrap(fsize).getInt();
			byte[] fhash = peerData.get(1);
			byte[] plist = peerData.get(2);

			for(int i=0;i<plist.length;i+=12)
			{
				byte[] b = Arrays.copyOfRange(plist,i,i+12);
				String s = new String(b);
				peerList.add(s);
			}
			Master master = new Master(peerList,this.filename,fiSize,fhash,this.n,hd);
			master.start();
		}
	}

	public String startElection(String addr) throws IOException {
		InetAddress ip = InetAddress.getByName(addr);
		DatagramSocket udpSocket = new DatagramSocket(7777);
		byte[] cmd = ByteBuffer.allocate(4).putInt(24).array();
		byte[] fname = filename.getBytes();
		byte[] len = ByteBuffer.allocate(4).putInt(fname.length).array();
		byte[] out = new byte[8+fname.length];
		System.arraycopy(cmd,0,out,0,cmd.length);
		System.arraycopy(len,0,out,cmd.length,len.length);
		System.arraycopy(fname,0,out,len.length,fname.length);
		byte[] bytes = new byte[NetworkStatics.MAX_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(out,out.length, ip, NetworkStatics.SERVER_CONTROL_RECEIVE);
		udpSocket.send(packet);
		packet = new DatagramPacket(bytes,bytes.length);
		udpSocket.receive(packet);
		int ipstart = 4 + fname.length + 1;
		byte[] data = new byte[packet.getLength()];
		data = Arrays.copyOfRange(bytes,ipstart,ipstart+9);
		String nip = new String(data);
		udpSocket.close();
		return nip;
	}

	public ArrayList<byte[]> getPeerData(String addr) throws IOException {
		InetAddress ip = InetAddress.getByName(addr);
		DatagramSocket udpSocket = new DatagramSocket(7776);
		ArrayList<byte []> peerData = new ArrayList<byte []>();
		byte[] bytes = new byte[NetworkStatics.MAX_PACKET_SIZE];
		byte[] cmd = ByteBuffer.allocate(4).putInt(5).array();
		byte[] fname = ByteBuffer.allocate(32).put(filename.getBytes()).array();
		byte[] message = new byte[36];
		System.arraycopy(cmd,0,message,0,cmd.length);
		System.arraycopy(fname,0,message,cmd.length,cmd.length+32);
		DatagramPacket packet = new DatagramPacket(message,message.length, ip, NetworkStatics.SERVER_CONTROL_RECEIVE);
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

		byte[] nout = new byte[packet.getLength()];
		System.arraycopy(bytes, 0, nout, 0, nout.length);

		byte[] filesize = Arrays.copyOfRange(nout,4,20);
		peerData.add(filesize);
		byte[] filehash = Arrays.copyOfRange(nout,20,36);
		peerData.add(filehash);
		byte[] peerList = Arrays.copyOfRange(nout,36,nout.length);
		peerData.add(peerList);

		udpSocket.close();
		return peerData;
	}
}
