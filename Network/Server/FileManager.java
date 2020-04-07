package Network.Server;

import Network.MD5hash;
import Network.NetworkStatics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.file.NoSuchFileException;
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
     * Given a file name, returns the file if available in the local list.
     * @param name filename (no not include directory!)
     * @return file with requested name
     * @throws NoSuchFileException if no such file exist in the local list, or if the file cannot be found on disk or cannot be accesses
     */
    public RandomAccessFile getFile(byte[] name) throws NoSuchFileException {
        String toget = new String(name).trim();
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
     * @param name
     */
    public void removeFile(byte[] name){
        String torem = hasher.hashBytesToString(name);
        if(this.mapper.containsKey(torem)){
            this.mapper.remove(torem);
        }else{
            System.err.println("failed to remove file with hash " + torem + " because it does not exist.");
        }
    }

    /**
     * Add a new file to the local list of available files
     * @param filePath
     * @return name of file if successful, false otherwise
     */
    public String addFile(String filePath){
       File newfile = new File(filePath);
       if(newfile.exists()){
           String name = NetworkStatics.getFilenameFromFilepath(filePath);
           this.mapper.put(name, filePath);
           return name;
       }else{
           System.err.println("request to add nonexistent file at " + filePath);
       }
        return null;
    }

    /**
     * Returns the names of files available locally.
     * @return
     */
    public String[] getFileNameList(){
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
        String[] keys = this.getFileNameList();
        String[] paths = new String[keys.length];
        for(int i = 0; i < keys.length; i++){
            paths[i] = this.mapper.get(keys[i]);
        }
        return paths;
    }

    public long getFilesize(String filename){
        String fp = this.mapper.get(filename);
        File f = new File(fp);
        return f.length();
    }
}
