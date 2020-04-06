package Network.Client;
import Controller.Node;
import Network.NetworkStatics;
import Network.MD5hash;
import Network.Tracker;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Master extends Thread {
	
	private ArrayList<String> peerdata;
	private String filename;
	private int filesize;
	private byte[] filehash;
	private MD5hash util = new MD5hash();
	BlockingQueue<byte[]> queue = new ArrayBlockingQueue(8);//change size
	private String leader;
	private Node n;

	public Master(final ArrayList<String> peerdata, String filename, int filesize, byte[] filehash, Node n,String leader)
	{
		this.peerdata = peerdata;
		this.filename = filename;
		this.filesize = filesize;
		this.filehash = filehash;
		this.leader = leader;
		this.n = n;
	}
	
	public void run() ////add split file algorithm to get byte range for each peer
	{
		ArrayList<Slave> threadList = new ArrayList<Slave>(); // arraylist for tracking slave threads
		int numPeers = this.peerdata.size();
		int remainder = this.filesize%numPeers;
		int size = this.filesize/numPeers;
		int x = 0;
		final Thread fileThread = new Thread(new FileThread(this.queue,this.filename,numPeers));
		fileThread.start();
		
		for(int i=0;i<numPeers;i++) //cycle threw peer list assign to slave thread
		{
			String addr = this.peerdata.get(i);
			int start;
			int end;
			if(remainder!=0)
			{
				start = i * (size+1);
				end = start + size;
				if(remainder==1)
					x = end+1;
				remainder--;
			}
			else
			{
				if(this.filesize%numPeers==0)
				{
					start = i*size;
					end = start+size-1;
				}
				else
				{
					start = x;
					end = start+size-1;
					x = end+1;
				}
			}

			try {
				InetAddress ip = InetAddress.getByName(addr);
				final Slave slaveThread = new Slave(ip,i+5000,start,end,this.filename,this.queue); //create slave thread with specified byte range and file
				slaveThread.start(); // start current slave thread
				threadList.add(slaveThread); // add current slave thread to thread list
			} catch (IOException e) {}
		}

		for (Slave slave : threadList) {
			if (slave.isAlive()) {
				try {
					slave.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			byte[] filehash2 = util.getHashFile(this.filename);
			if(util.compareHash(this.filehash,filehash2)) {
				System.out.println("hash match, download complete");
				readyToSeed();
				Tracker t = new Tracker(this.peerdata, this.filename, this.leader);
				n.addTracker(t);
			}
			else
				System.out.println("hash dont match");
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
	}

	public void readyToSeed() throws IOException {
		InetAddress ip = InetAddress.getByName(leader);
		DatagramSocket udpSocket = new DatagramSocket(7000);
		byte[] cmd = ByteBuffer.allocate(4).putInt(20).array();
		byte[] fname = filename.getBytes();
		byte[] len = ByteBuffer.allocate(4).putInt(fname.length).array();
		byte[] out = new byte[8+fname.length];
		System.arraycopy(cmd,0,out,0,cmd.length);
		System.arraycopy(len,0,out,cmd.length,len.length);
		System.arraycopy(fname,0,out,len.length,fname.length);
		DatagramPacket packet = new DatagramPacket(out,out.length, ip, NetworkStatics.SERVER_CONTROL_RECEIVE);
		udpSocket.send(packet);
	}

}
