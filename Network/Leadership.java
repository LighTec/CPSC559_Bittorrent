package Network;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Leadership {

    private static StringBuilder mac_address;

    // taken from https://mkyong.com/java/how-to-get-mac-address-in-java/
    // Gets the ip address and the mac address
    public static void main(String[] args) throws SocketException {

        InetAddress ip;

        try{
            ip = InetAddress.getLocalHost();
            System.out.println("IP address: " + ip.getHostAddress());

            NetworkInterface nw = NetworkInterface.getByInetAddress(ip);

            System.out.print("Mac address: ");
            byte[] mac = nw.getHardwareAddress();

            mac_address = new StringBuilder();
            for (int i  = 0; i < mac.length; i++){
                mac_address.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }

            System.out.println(mac_address.toString());

        }catch (UnknownHostException e) {

            e.printStackTrace();

        }
        election(peerList);  /// gotta connect to the tracker file first

    }

    static void election(ArrayList<String[]> peerList){
        Boolean running = false;
        System.out.println(mac_address.toString());
        Array macArray[];
        for (int j = 0; j < peerList.get(1).size; j++){
            macArray = peerList;// wip

        }
        macArray.sort();
        int p = macArray.length - 1;
        String newLeader = macArray[p];

        int i;
        for (i =0; i< /*size*/; i++){
            if( newLeader == peerList[1][i]) // need to figure out how to read
                System.out.print("The new leader is: " + );
                break;
        }


    }


}
