package client;

public class ParseData {
	
	private String url;
	
	public ParseData(final String url)
	{
		this.url = url;
	}
	
	//parsing for peerlist data
	
	public int getPort()
	{
		String s = url.substring(url.lastIndexOf(":")+1);
		return Integer.parseInt(s);
	}
	
	public String getIP()
	{
		String s = url.substring(0,url.lastIndexOf(":"));
		return s;
	}
	
}
