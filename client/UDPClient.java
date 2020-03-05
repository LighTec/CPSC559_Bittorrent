package client;

import java.util.Scanner;
import java.util.ArrayList;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDPClient {
	private int port;
	private Master master;
	private ArrayList<String> peerdata;
	
	
	public UDPClient(final ArrayList<String> peerdata)
	{
		this.peerdata = peerdata;
	}
	
	public void start()
	{
		this.master = new Master(peerdata);
		master.run(); //starts master/client
	}

	public static void main(String[] args) throws UnknownHostException  //client driver
	{
		InetAddress inetAddress = InetAddress.getLocalHost();
		String url = inetAddress.getHostAddress();
		String port =":8778";
		String port2 =":8777";
		String port3 =":8776";
		ArrayList<String> data = new ArrayList<String>();
		data.add(url+port); // add peer info "127.0.0.1:8778" to peer list current only 1 being added
		//data.add(url+port2); uncomment to add more peers
		//data.add(url+port3);
		UDPClient client = new UDPClient(data); //init and start stuff above
		client.start();
	}

}
