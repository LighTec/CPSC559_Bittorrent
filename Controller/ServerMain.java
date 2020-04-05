package Controller;

import Network.NetworkStatics;
import Network.Server.FileManager;
import Network.Server.UDPServer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.NoSuchFileException;

public class ServerMain {
    public static void main(String args[]) throws IOException {
        FileManager fman = new FileManager();

        String hash = fman.addFile("./TestFiles/alphabet.txt");

        // NetworkStatics.printPacket(hash, "HASH");

        // RandomAccessFile raf = fman.getFile(hash);

        // System.out.println(raf.length());

        //  System.exit(0);
        UDPServer u = new UDPServer(fman);
        u.start();
    }
}
