package Controller;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Leadership {

    InetAddress ip;
    StringBuilder sb = new StringBuilder();

    //https://mkyong.com/java/how-to-get-mac-address-in-java/
    public static void main(String[] args) throws SocketException {

        //InetAddress ip;

        try{
            ip = InetAddress.getLocalHost();
            System.out.println("IP address: " + ip.getHostAddress());

            NetworkInterface nw = NetworkInterface.getByInetAddress(ip);

            System.out.print("Mac address: ");
            byte[] mac = nw.getHardwareAddress();

           // StringBuilder sb = new StringBuilder();
            for (int i  = 0; i < mac.length; i++){
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }

            System.out.println(sb.toString());

        }catch (UnknownHostException e) {

            e.printStackTrace();

        }catch (SocketException e){

            e.printStackTrace();

        }

    }
}
