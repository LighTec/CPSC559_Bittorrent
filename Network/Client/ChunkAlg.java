package Network.Client;

public class ChunkAlg {

    public static void main(String[] args)
    {
        int filesize = 683433;
        int numPeers = 7;

        int remainder = filesize%numPeers;
        int size = filesize/numPeers;
        int x = 0;
        System.out.println("filesize: " + filesize + " number of peers: " + numPeers);

        for(int i=0;i<numPeers;i++)
        {
            int start;
            int end;
            if(remainder!=0)
            {
                start = i * (size+1);
                end = start + size;
                if(remainder==1)
                    x = end+1;
                remainder--;
            }
            else
            {
                if(filesize%numPeers==0)
                {
                    start = i*size;
                    end = start+size-1;
                }
                else
                {
                    start = x;
                    end = start+size-1;
                    x = end+1;
                }
            }
            System.out.println("peer " + i + ": range = " + start + "-" + end + " total = " + (end-start+1));
        }
    }
}
