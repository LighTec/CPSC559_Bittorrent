package Network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkStatics {
    public final static int HEARTBEAT_PORT = 6050;
    public final static int SERVER_CONTROL_RECEIVE = 6051;
    public final static int SERVER_CONTROL_SEND = 6052;

    // TODO implement port agreement handshake, until then use server control send port
    public final static int FILE_SEND_LOWERBOUND = SERVER_CONTROL_SEND;

    public final static int MAX_PACKET_SIZE = 65507;

    /**
     * Formats an integer into a byte array of length 4. Returned array is little endian.
     * @param n input integer
     * @return byte[] array of length 4
     */
    public static byte[] intToByteArray(int n){
        return ByteBuffer.allocate(4).putInt(n).array();
    }

    /**
     * Get an integer from the first 4 indices of a byte array. Assumes little endian.
     * @param b input byte array
     * @return integer n, stored in b[0..3]
     */
    public static int byteArrayToInt(byte[] b){
        return byteArrayToInt(b, 0);
    }

    /**
     * Get an integer from an index of a byte array. Assumes little endian.
     * @param b input byte array
     * @param startindex the start point of the integer to get
     * @return integer n, stored in b[startindex...startindex+3]
     */
    public static int byteArrayToInt(byte[] b, int startindex){
        if(b.length < 4){
            throw new IllegalArgumentException("Input byte array must be big enough to store an integer, in order to cast an integer out of it. (min 4 bytes as input).");
        }
        return ByteBuffer.wrap(Arrays.copyOfRange(b,startindex,startindex + 4)).getInt();
    }

    public static InetAddress byteArraytoInetAddress(byte[] b, int startindex) throws UnknownHostException {
        byte[] inetaddr = new byte[4];
        System.arraycopy(b,startindex,inetaddr,0,4);
        return InetAddress.getByAddress(inetaddr);
    }

    /**
     * Prints out a packet's data to help debug. Prints out as the hex code, character representing it, and then as a normal string.
     * @param packetdata input byte array
     * @param header header for print statement
     */
    public static void printPacket(byte[] packetdata, String header){
        StringBuilder sb = new StringBuilder();
        String sb2 = "";
        String sb3 = "";
        for (byte b : packetdata) {
            sb.append(String.format("%02X  ", b));
            if((b & 0xFF) == 0x0A){
                sb2 += "\\n  ";
            }else if((b & 0xFF) == 0x09){
                sb2 += "\\t  ";
            }else if((b & 0xFF) < 0x20){
                sb2 += "    ";
            }else if((b & 0xFF) == 0x7F){
                sb2 += "    ";
            }else{
                sb2 += ((char) b) + "   ";
            }
            sb3 += String.format("%-4s", b & 0xFF);
        }
        System.out.println("======="+ header + "===============================");
        System.out.println(sb.toString());
        System.out.println(sb2);
        System.out.println(sb3);
        System.out.println("=================================================");
    }

    public static String getFilenameFromFilepath(String fp){
        Pattern pat = Pattern.compile("[^/\\\\]*$");
        Matcher mat = pat.matcher(fp);
        mat.find();
        return mat.group(0);
    }
}
