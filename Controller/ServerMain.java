package Controller;

import Network.Server.UDPServer;

public class ServerMain {
    public static void main(String args[]){
        UDPServer u = new UDPServer(6006);
        System.out.println("Starting Server");
        //UDPServer u2 = new UDPServer(6007);
        u.run();
        //u2.run();
    }
}
