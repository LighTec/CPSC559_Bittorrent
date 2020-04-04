package Network.Client;
import Network.MD5hash;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

public class Slave extends Thread {
	
	private DatagramSocket udpSocket;
	private int bytestart;
	private int bytefinish;
	private int numPackets;
	private String filename;
	private InetAddress addr;
	protected BlockingQueue<byte[]> queue = null;
	private ArrayList<byte[]> packetlist;
	private MD5hash util = new MD5hash();

	public Slave(final InetAddress addr, final int port, int bytestart, int bytefinish, final String filename, BlockingQueue<byte[]> queue) throws IOException
	{
	 	this.addr = addr;
	 	this.bytestart = bytestart;
	 	this.bytefinish = bytefinish; 
	 	this.filename = filename;
	 	this.udpSocket= new DatagramSocket(port);
	 	this.queue = queue;
	 	this.numPackets = ((bytefinish-bytestart)/65507)+1; ///CHECK THIS LOGIC
	 	this.packetlist = new ArrayList<byte[]>(numPackets);
	}
	
	public void run()
	{
		for(int i=0;i<packetlist.size();i++)
			packetlist.add(null);

		byte[] out = prepareRange(bytestart,bytefinish);
		Receiver receiveThread = new Receiver(this, udpSocket);
		receiveThread.start(); //start receiver thread

		DatagramPacket dp = new DatagramPacket(out, out.length, addr, 8778); //init packet and bind addr,port
		try {
			udpSocket.send(dp); //sent message/packet
			System.out.println("request sent...");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (receiveThread.isAlive()) {
			receiveThread.shutdown();
			try {
				receiveThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		while(isMissing())
		{
			Receiver rangeThread = new Receiver(this, udpSocket);
			rangeThread.start();

			for(int i=0;i<packetlist.size();i++)
			{
				if(packetlist.get(i)==null)
				{
					int s = i * 65507;
					int e = s + 65507;
					if(i==packetlist.size()-1)
						e = s + ((bytefinish-bytestart)%65507);
					byte[] rangeRequest = prepareRange(s,e);
					DatagramPacket packet = new DatagramPacket(rangeRequest, rangeRequest.length, addr, 8778);
				}
			}
			if (rangeThread.isAlive()) {
				rangeThread.shutdown();
				try {
					rangeThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			queue.put(createChunk());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		udpSocket.close();

	}

	public synchronized void processPacket(byte[] bytes) throws InterruptedException, NoSuchAlgorithmException {
		byte[] seqbyte = Arrays.copyOfRange(bytes,0,4);
		int seqnum = ByteBuffer.wrap(seqbyte).getInt();
		byte[] hashSent = Arrays.copyOfRange(bytes,4,20);
		byte[] message = Arrays.copyOfRange(bytes,20,bytes.length);
		byte[] hashMessage = util.hashBytes(message);

		if(util.compareHash(hashSent,hashMessage))
			packetlist.set(seqnum,message);
	}

	public byte[] createChunk()
	{
		byte[] out = new byte[bytefinish-bytestart+4+1]; // CHECK LATER
		byte[] start = ByteBuffer.allocate(4).putInt(bytestart).array();
		System.arraycopy(start,0,out,0,start.length);
		int index = 4;

		for(int i=0;i<packetlist.size();i++)
		{
			byte[] p = new byte[packetlist.get(i).length];
			System.out.println(p.length);
			p = packetlist.get(i);
			System.arraycopy(p,0,out,index,p.length); //CHECK TOO LATER
			index += p.length;
		}

		return out;
	}

	public boolean isMissing()
	{
		for(int i=0;i<packetlist.size();i++)
		{
			if(packetlist.get(i)==null)
				return true;
		}
		return false;
	}

	public byte[] prepareRange(int start,int finish)
	{
		byte buf[] = null;
		String message = "GET " + filename + " " + "Range:" + start + "-" + finish; // format example: "GET test.txt Range:5-10"
		buf = message.getBytes(); //write string to byte buffer
		byte[] out = new byte[buf.length + 8];
		for(int i = 0; i < buf.length; i++){
			out[8+i] = buf[i];
		}
		int commandnumber = 3;
		byte[] cmd = ByteBuffer.allocate(4).putInt(commandnumber).array();
		byte[] length = ByteBuffer.allocate(4).putInt(buf.length).array();
		for(int i = 0; i < 4; i++){
			out[i] = cmd[i];
			out[4+i] = length[i];
		}

		return out;
	}
}
