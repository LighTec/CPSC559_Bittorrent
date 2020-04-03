package Network.Server;

import Network.MD5hash;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class FileManager {

    private HashMap<String,String> mapper;

    private MD5hash hasher;

    public FileManager(){
        this.mapper = new HashMap<>();
        this.hasher = new MD5hash();
    }

    /**
     * Given a file hash, returns the file if available in the local list.
     * @param hash
     * @return file with requested hash
     * @throws NoSuchFileException if no such file exist in the local list, or if the file cannot be found on disk or cannot be accesses
     */
    public RandomAccessFile getFile(byte[] hash) throws NoSuchFileException {
        String toget = this.hasher.hashBytesToString(hash);
        if(this.mapper.containsKey(toget)){
            String fp = this.mapper.get(toget);
            RandomAccessFile f = null;
            try {
                f = new RandomAccessFile(fp, "r");
                return f;
            } catch (FileNotFoundException e) {
                throw new NoSuchFileException("File found, but does not exist for this hash (found in hashmap, but not on disK): " + toget);
            }
        }else{
            throw new NoSuchFileException("No file with hash found (not found in hashmap): " + toget);
        }
    }

    /**
     * Loads a config of files & their hashes from a config file, so files hashes and locations don't have to be recalculated and readded after program start
     * @param configPath
     * @return
     */
    public boolean loadConfig(String configPath){
        //TODO create ability to load filepaths from a config file
        return false;
    }

    /**
     * Saves a config of files & their hashes from a config file, so files hashes and locations don't have to be recalculated and readded after program start
     * @param configPath
     * @return
     */
    public boolean saveConfig(String configPath){
        // TODO allow saving of file locations & their hashes (so we do not have to recompute)
        return false;
    }

    /**
     * Given a hash of a file, remove it from the local list of available files
     * @param hash
     */
    public void removeFile(byte[] hash){
        String torem = hasher.hashBytesToString(hash);
        if(this.mapper.containsKey(torem)){
            this.mapper.remove(torem);
        }else{
            System.err.println("failed to remove file with hash " + torem + " because it does not exist.");
        }
    }

    /**
     * Add a new file to the local list of available files
     * @param filePath
     * @return True if added to list of available files, false otherwise
     */
    public byte[] addFile(String filePath){
       File newfile = new File(filePath);
       if(newfile.exists()){
           try {
               MessageDigest hasher = MessageDigest.getInstance("md5");
               hasher.update(Files.readAllBytes(Paths.get(filePath)));
               byte[] digest = hasher.digest();
               String digestStr = this.hasher.hashBytesToString(digest);
               this.mapper.put(digestStr, filePath);
               return digest;
           } catch (NoSuchAlgorithmException e) {
               System.err.println("failed to initialize md5 hasher.");
               e.printStackTrace();
           } catch (IOException e) {
               System.err.println("failed to add file: IOException when reading file to get hash");
           }
           return null;
       }else{
           System.err.println("request to add nonexistent file at " + filePath);
       }
        return null;
    }

    /**
     * Returns the hashes of files available locally.
     * @return
     */
    public String[] getFileHashList(){
        Set<String> keyset = this.mapper.keySet();
        Iterator<String> iter = keyset.iterator();
        String[] output = new String[this.mapper.size()];
        for(int i = 0; iter.hasNext(); i++){
            output[i] = iter.next();
        }
        return output;
    }

    /**
     * Returns the filepaths of files available locally.
     * @return
     */
    public String[] getFilePathList(){
        String[] keys = this.getFileHashList();
        String[] paths = new String[keys.length];
        for(int i = 0; i < keys.length; i++){
            paths[i] = this.mapper.get(keys[i]);
        }
        return paths;
    }
}
