package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;

public class Slave extends Thread {
	
	private DatagramSocket udpSocket;
	private DatagramPacket dp;
	private int bytestart;
	private int bytefinish;
	private String filename;
	private InetAddress addr;
	private int port;
	
	
	public Slave(final InetAddress addr,final int port,int bytestart,int bytefinish,final String filename) throws IOException
	{
	 	this.addr = addr;
	 	this.port = port;
	 	this.bytestart = bytestart;
	 	this.bytefinish = bytefinish; 
	 	this.filename = filename;
	 	this.udpSocket= new DatagramSocket(); //init udp socket
	}
	
	public void run()
	{
		byte buf[] = null;
		String s1 = "GET " + filename + " " + "Range:" + bytestart + "-" + bytefinish; // format example: "GET test.txt Range:5-10"
		String message = s1;
		buf = message.getBytes(); //write string to byte buffer
		dp = new DatagramPacket(buf,buf.length,addr,port); //init packet and bind addr,port
		
		try {
			udpSocket.send(dp); //sent message/packet
			System.out.println("request sent...");
		} catch (IOException e) {}
		Receiver receiveThread = new Receiver(this,udpSocket); //init receive thread passes in "this" slave object, and udp socket
		receiveThread.start(); //start receiver thread
		//*******wait, then shutdown cause exception so removed*********//
		
	}
	
	public synchronized void processPacket(byte[] bytes) 
	{
		//add to blocking queue protected by mutexs then have master create a single thread
		//to rebuild file using random access/file channel. Track bytes shutdown receiver
		//when all bytes found. check chunk integrity, ask to resend if bad chunk or no chunk ACK/NACK? 
		//also chunk numbers to track, signal thread class to track dead connections/socket timeouts....
		System.out.println(data(bytes));	
	}
	
	public static StringBuilder data(byte[] a) //takes byte array return stringbuilder type use toString on stringbuilder object to convert
    { 
        if (a == null) 
            return null; 
        StringBuilder ret = new StringBuilder(); 
        int i = 0; 
        while (a[i] != 0) 
        { 
            ret.append((char) a[i]); 
            i++; 
        } 
        return ret; 
    } 
}
