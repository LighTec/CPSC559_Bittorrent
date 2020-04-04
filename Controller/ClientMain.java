package Controller;

import Network.NodeList;

public class ClientMain {

    static String[] nodes;

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

        NodeList nl = new NodeList();
        nodes = nl.getNodes();

        MessageThread mt = new MessageThread();
        mt.start();
    }
}
