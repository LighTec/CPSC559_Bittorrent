package client;

import java.util.ArrayList;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Master extends Thread {
	
	private ArrayList<String> peerdata;
	
	public Master(final ArrayList<String> peerdata)
	{
		this.peerdata = peerdata; // arraylist for storing ip/port for each peer form = "127.0.0.1:8778"
	}
	
	public void run() ////add split file algorithm to get byte range for each peer
	{
		int start = 0; //byte range
		int end = 5;
		ArrayList<Slave> threadList = new ArrayList<Slave>(); // arraylist for tracking slave threads
		
		for(int i=0;i<this.peerdata.size();i++) //cycle threw peer list assign to slave thread
		{
			String addip = this.peerdata.get(i);
			ParseData parse = new ParseData(addip); //parse request string
			String addr = parse.getIP();
			int port = parse.getPort();
			try {
				InetAddress ip = InetAddress.getByName(addr);
				final Slave slaveThread = new Slave(ip,port,start,end,"test.txt"); //create slave thread with specified byte range and file
				slaveThread.start(); // start current slave thread
				threadList.add(slaveThread); // add current slave thread to thread list
			} catch (IOException e) {}
		}
		
		for(int i=0;i<threadList.size();i++) //thread operations
		{
			if(threadList.get(i).isAlive())
			{
				try {
					threadList.get(i).join();
				} catch (InterruptedException e) {}
			}
		}
	}

}
