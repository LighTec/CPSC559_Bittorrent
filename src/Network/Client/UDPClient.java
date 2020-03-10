package Network.Client;

import java.util.Scanner;
import java.util.ArrayList;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDPClient {
	private int port;
	private Master master;
	private ArrayList<String[]> peerdata;
	
	
	public UDPClient(final ArrayList<String[]> peerdata)
	{
		this.peerdata = peerdata;
	}
	
	public void start()
	{
		this.master = new Master(peerdata);
		master.run(); //starts master/client
	}
}
