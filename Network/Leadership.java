package Network;

import javax.sound.midi.Track;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.lang.*;
import java.util.Arrays;

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
        ArrayList<String> peerList = new ArrayList<String>();
        peerList.add("204.253.165.170");
        peerList.add("158.213.79.168");
        peerList.add("250.56.204.84");
        election(peerList);  /// gotta connect to the tracker file first

    }

    static void election(ArrayList<String> peerList){
        Boolean running = false;
        int i = 0;
        int[] arr = new int [peerList/*.get(0)*/.size()];
        Random rand = new Random();

        //Assigning random numbers to the ip addresses
        while (i < peerList.size()){
            int randNum = rand.nextInt(10000);
            boolean checker = true;
            for (int j = 0; j < arr.length; j++){
                if (arr[j] == randNum){
                    checker = false;
                }
            }
            if(checker){
                arr[i] = randNum;
                i++;
                System.out.println("random number: " + randNum);
            }
        }

        for (int j = 0; j < arr.length; j++){
            System.out.println(arr[j]);
        }
        int [] l = Arrays.copyOf(arr, arr.length);

        Arrays.sort(l);
        for (int j = 0; j < arr.length; j++){
            System.out.print(l[j]);
            System.out.print(" ");
            System.out.println(arr[j]);
        }
        int newLeader = l[l.length - 1];
        System.out.println(newLeader);
        for (i = 0; i < arr.length; i++){
            if (newLeader == arr[i]){
                System.out.println("The new leader is at index: " + i);
                break;
            }
        }



    }


}
