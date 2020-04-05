package Controller;

import Network.Client.UDPClient;
import Network.Server.FileManager;
import Network.Server.UDPServer;

public class Node {

    private FileManager fm;
    private UDPServer server;

    Node () {
        this.fm = new FileManager();
        this.server = new UDPServer(fm);
        this.server.start();
    }

    public String addFile(String filename) {
        return this.fm.addFile(filename);
    }

    public void stop() {
        this.server.terminate();
    }

    public void startClient(String filename) {
        new UDPClient(filename).start();
    }

    public static void main(String[] args) {
        Node n = new Node();
        String hash = n.addFile("./TestFiles/alphabet.txt");
        System.out.println("Added a file with hash: " + hash);
        n.startClient("alphabet.txt");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException i) {
            i.printStackTrace();
        }

        n.stop();
    }
}
