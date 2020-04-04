package Network.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;


public class Receiver extends Thread {
	
	public static final int MAX_PAYLOAD_SIZE = 65507;
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
		try {
			socket.setSoTimeout(1000);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		while(!this.shutdown) //shuts down when shutdown called from slave
		{
			byte[] bytes = new byte[MAX_PAYLOAD_SIZE]; //??? size
			DatagramPacket packet = new DatagramPacket(bytes,bytes.length);
			try {
				socket.receive(packet);
				byte[] nout = new byte[packet.getLength()];
				System.arraycopy(bytes, 0, nout, 0, nout.length);
				slave.processPacket(nout);
			} catch (SocketTimeoutException e) {
				break;
			} catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void shutdown()
	{
		this.shutdown = true;
	}	

}
