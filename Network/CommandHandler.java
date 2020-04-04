package Network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class CommandHandler {
    private int[] cmdlen;

    public CommandHandler(){
        initcmdlen();
    }

    /**
     * Given a command number, get the length of data that should follow the command. -2 means invalid command,
     * -1 means variable length command
     * @param cmd
     * @return
     */
    public int getCmdLen(int cmd){
        return this.cmdlen[cmd];
    }


    private void initcmdlen(){
        this.cmdlen = new int[256];
        Arrays.fill(this.cmdlen, -2);
        this.cmdlen[0] = 0;
        this.cmdlen[1] = 0;
        this.cmdlen[3] = -1;
        this.cmdlen[4] = 4;
        this.cmdlen[5] = 20;
        this.cmdlen[6] = -1;
        this.cmdlen[10] = 24;
        this.cmdlen[11] = -1;
        this.cmdlen[12] = 4;
    }

    /**
     * Parse incoming data into a command and its data. Throws the "UnsupportedOperationException" if the command
     * is invalid.
     * @param data
     * @return
     */
    public byte[][] tokenizepacket(byte[] data){
        byte[][] parsed = new byte[2][]; // create 2d array containing command number then data
        parsed[0] = Arrays.copyOfRange(data,0,4); // get command from data
        int cmd = NetworkStatics.byteArrayToInt(data);
        int lencmd = this.getCmdLen(cmd); // check command length
        if(lencmd == -2) { // if invalid, throw exception
            throw new UnsupportedOperationException("Command not available.");
        } else if(lencmd == -1){ // if variable length, read next 4 bytes as length integer
            lencmd = NetworkStatics.byteArrayToInt(data,4);
            parsed[1] = Arrays.copyOfRange(data,8, lencmd + 8);
        }else{ // otherwise use static length
            parsed[1] = Arrays.copyOfRange(data,4, lencmd + 4);
        }
        return parsed;
    }

    public byte[] generatePacket(int cmd, byte[] data){
        int len = this.getCmdLen(cmd);
        byte[] cmdbytes = NetworkStatics.intToByteArray(cmd);
        byte[] output;
        if(len == -2){
            throw new UnsupportedOperationException("Command not available.");
        }else if(len == -1){
            len = data.length; // get variable data length (data length + cmd bytes + len bytes)
            output = new byte[data.length + 8]; // create array of appropriate size
            System.arraycopy(cmdbytes,0,output,0,4); // copy cmd bytes to output
            byte[] lenbytes = NetworkStatics.intToByteArray(len); // get byte array of length integer
            System.arraycopy(lenbytes,0,output,4,4); // copy length bytes to output
            System.arraycopy(data,0,output,8, len); // copy data to output

        }else{
            if(data.length != len){
                throw new IllegalArgumentException("data length does not match cmd length requirement!");
            }else{
                output = new byte[data.length + 4]; // get variable data length (data length + cmd bytes)
                System.arraycopy(cmdbytes,0,output,0,4); // copy cmd bytes to output
                System.arraycopy(data,0,output,4, len); // copy data to output
            }
        }
        return output;
    }
}
