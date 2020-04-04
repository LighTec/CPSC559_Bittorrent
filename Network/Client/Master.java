package Network.Client;

import Network.MD5hash;

import java.io.IOException;
import java.net.InetAddress;
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

	public Master(final ArrayList<String> peerdata,String filename,int filesize,byte[] filehash)
	{
		this.peerdata = peerdata; // arraylist for storing ip/port for each peer form = "127.0.0.1:8778"
		this.filename = filename;
		this.filesize = filesize;
		this.filehash = filehash;
	}
	
	public void run() ////add split file algorithm to get byte range for each peer
	{
		ArrayList<Slave> threadList = new ArrayList<Slave>(); // arraylist for tracking slave threads
		int numPeers = peerdata.size();
		int remainder = filesize%numPeers;
		int size = filesize/numPeers;
		int x = 0;
		final Thread fileThread = new Thread(new FileThread(queue,filename,numPeers));
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
				if(filesize%numPeers==0)
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
				final Slave slaveThread = new Slave(ip,i+5000,start,end,"test.txt",queue); //create slave thread with specified byte range and file
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
			if(util.compareHash(filehash,filehash2))
				System.out.println("hash match, download complete");
			else
				System.out.println("hash dont match");
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
	}

}
