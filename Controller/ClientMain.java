package Controller;

import Network.Client.UDPClient;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ClientMain {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, URISyntaxException  //client driver
    {
        UDPClient client = new UDPClient("alphabet.txt");
        client.start();
    }
}
