package Network;

import java.nio.ByteBuffer;
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
    public int Getcmdlen(int cmd){
        return this.cmdlen[cmd];
    }


    private void initcmdlen(){
        this.cmdlen = new int[256];
        Arrays.fill(this.cmdlen, -2);
        this.cmdlen[0] = 0;
        this.cmdlen[1] = 0;
        this.cmdlen[2] = 20;
        this.cmdlen[3] = -1;
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
        int cmd = ByteBuffer.wrap(parsed[0]).getInt(); // get command integer from array of 4 bytes
        int lencmd = this.Getcmdlen(cmd); // check command length
        if(lencmd == -2) { // if invalid, throw exception
            throw new UnsupportedOperationException("Command not available.");
        } else if(lencmd == -1){ // if variable length, read next 4 bytes as length integer
            lencmd = ByteBuffer.wrap(Arrays.copyOfRange(data,4,8)).getInt();
            parsed[1] = Arrays.copyOfRange(data,8, lencmd + 8);
        }else{ // otherwise use static length
            parsed[1] = Arrays.copyOfRange(data,4, lencmd + 4);
        }
        return parsed;
    }

}
