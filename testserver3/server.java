import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket; 
import java.net.DatagramSocket; 
import java.net.InetAddress; 
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;  
  
public class server 
{ 
	//****NOTE removed need for data1
	//******** stringbuilder adds characters so not able to use data1 for receive packet byte buffer, so use data2 (packet byte buffer)
    public static void main(String[] args) throws IOException 
    { 
        DatagramSocket ds = new DatagramSocket(8778); //create socket listen on port 8778
        byte[] receive = new byte[65535]; 
        DatagramPacket DpReceive = new DatagramPacket(receive, receive.length);  //init packet so it can receive
        ds.receive(DpReceive); //receive request message write to receive byte buffer
        String request = data2(receive).toString(); //use data2 function for received
		//request message parse
        String filename = request.substring(request.indexOf(" ")+1,request.indexOf("R")-1);
        int start = Integer.parseInt(request.substring(request.indexOf(":")+1,request.lastIndexOf("-")));
        int end = Integer.parseInt(request.substring(request.indexOf("-")+1));        
        
        //reads whole file into byte buffer
        RandomAccessFile f = new RandomAccessFile(filename, "r");
		byte[] b = new byte[(int) f.length()];
		f.readFully(b);
        byte[] slice = Arrays.copyOfRange(b,start,end); //get byte range needed
	    
        receive = new byte[65535]; //reset recieve byte buffer
        receive = slice; //can proably pass in slice directly but w/e     
        DatagramPacket dp = new DatagramPacket(receive,receive.length,DpReceive.getAddress(),DpReceive.getPort()); //prepare packet use addr,port that were attached to dpRecieve packet .getaddress/.getport
        ds.send(dp); //send requested bytes
        System.out.println("sent bytes " + start + "-" +end);
        ds.close();
    } 
    
    public static StringBuilder data2(byte[] a) 
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
  
    public static StringBuilder data(byte[] a) 
    { 
        if (a == null) 
            return null; 
        StringBuilder ret = new StringBuilder(); 
        for(int i=0;i<a.length;i++)
        	ret.append((char) a[i]);
       
        return ret; 
    }
        
} 
