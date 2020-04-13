package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class NodeList {

    public String[] getNodes() {
        String[] nodes = new String[0];
        try {
            Socket client = new Socket("192.168.62.28", 1962);

            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            out.println("connect");
            StringBuilder nodesString = new StringBuilder();
            String temp;
            while ((temp = in.readLine()) != null) {
                nodesString.append(temp);
            }
            nodes = nodesString.toString().split(",");
            System.out.println(Arrays.toString(nodes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nodes;
    }
}
