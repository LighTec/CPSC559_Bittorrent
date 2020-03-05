package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class Receiver extends Thread {
	
	public static final int MAX_PAYLOAD_SIZE = 65535;
	private DatagramSocket socket;
	private volatile boolean shutdown;
	private Slave slave;
	
	public Receiver(Slave slave,DatagramSocket socket)
	{
		//init passed in slave object and socket
		this.socket = socket;
		this.shutdown = false;
		this.slave = slave;
	}
	
	public void run()
	{
		while(!this.shutdown) //shuts down when shutdown called from slave
		{
			byte[] bytes = new byte[MAX_PAYLOAD_SIZE]; //??? size
			DatagramPacket pkg = new DatagramPacket(bytes,bytes.length);
			try {
				socket.receive(pkg); //receive packet and write to byte buffer
			} catch (IOException e) {}
			slave.processPacket(bytes); // pass byte buffer into processPacket in slave class for file rebuild ops
		}
	}
	
	public void shutdown()
	{
		this.shutdown = true;
	}	

}
