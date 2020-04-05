package Controller;

import Network.Client.UDPClient;
import Network.Server.FileManager;
import Network.Server.UDPServer;

import java.util.Scanner;

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
        Scanner myObj = new Scanner(System.in);

        String input;
        while (true) {
            System.out.println("1: Download a file");
            System.out.println("2: Upload a file");
            System.out.println("3 Dubugging mode");
            System.out.println("4: Exit");

            System.out.print("Enter input here: ");
            input = myObj.nextLine().trim();
            /*For downloading*/
            if(input.equals("1")){
                System.out.println("What file do you want to download?");
                System.out.print("Enter file here: (include the type file of i.e. .txt, .zip)");
                input = myObj.nextLine().trim();
                n.startClient(input);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException i) {
                    i.printStackTrace();
                }

            }
            /* For uploading file*/
            else if (input.equals("2")) {
                System.out.println("What file do you want to upload?");
                System.out.print("Enter file here including the directory: (for example: ./TestFiles/alphabet.txt)");
                input = myObj.nextLine().trim();
                String hash = n.addFile(input);
                System.out.println("Added a file with hash: " + hash);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException i) {
                    i.printStackTrace();
                }
            }
            /* For debugging*/
            else if (input.equals("3")){
                System.out.println("Debugging mode....");
                // W.I.P.
            }
            /*Exiting*/
            else if ( input.equals("4")){
                System.out.println("Exiting.....");
                break;
            }
        }

        n.stop();


    }
}
