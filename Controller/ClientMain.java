package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMain {

    public static void main(String[] args) {
        /*
        InetAddress inetAddress = InetAddress.getLocalHost();
        String url = inetAddress.getHostAddress();
        String port = "6006";
        //String port2 = "6007";
        ArrayList<String[]> data = new ArrayList<>();
        String[] connection = {url, port};
        //String[] connection2 = {url, port2};
        data.add(connection); // add peer info "127.0.0.1:8778" to peer list current only 1 being added
        //data.add(connection2); // uncomment to add more peers
        UDPClient client = new UDPClient(data); //init and start stuff above
        client.start();
        */

        try {
            Socket client = new Socket("localhost", 1962);

            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            out.println("connect");
            System.out.println("<< Nodes: " + in.readLine());

            MessageThread mt = new MessageThread();
            mt.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
