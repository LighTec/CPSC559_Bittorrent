package Network;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MD5hash {

    public boolean compareHash(byte[] hash, byte[] hash2)
    {
        return Arrays.equals(hash, hash2);
    }

    /// was trying some stuff with paths but just left it so uses files in current directory
    /// maybe we can ask user for the path were they will store there files then use paths
    public byte[] getHashFile(String filename) throws NoSuchAlgorithmException, IOException {
        //URL url = getClass().getResource(filename);
        Path p = Paths.get(filename);
        byte[] b = Files.readAllBytes(p);
        return hashBytes(b);
    }

    public byte[] hashBytes(byte[] b) throws NoSuchAlgorithmException
    {
        byte[] hash = MessageDigest.getInstance("MD5").digest(b);
        return hash;
    }

    public void printHash(byte[] hash)
    {
        BigInteger num = new BigInteger(1,hash);
        String hashout = num.toString(16);
        while (hashout.length()<32)
            hashout += "0";
        System.out.println(hashout);
    }
}
