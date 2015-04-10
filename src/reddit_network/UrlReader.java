package reddit_network;

import java.net.*;
import java.io.*;

public abstract class UrlReader {
	public static long lastRead = 0;
	
    public static String read(String url) throws Exception {

        URL oracle = new URL(url);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(oracle.openStream()));

        String inputLine;
        String urlContents = "";
        while ((inputLine = in.readLine()) != null)
            urlContents += inputLine;
        in.close();
        return urlContents;
    }

public static String read30(String url) throws Exception {
	int i = 0;
	while (i < 30){
		try{
			try {
				long l = 2000;
				long m = new java.util.Date().getTime() - lastRead;
				if (m < l)
					java.lang.Thread.sleep(l - m);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    java.lang.Thread.currentThread().interrupt();
			}
	
    URL oracle = new URL(url);
    lastRead = new java.util.Date().getTime();
    BufferedReader in = new BufferedReader(
    new InputStreamReader(oracle.openStream()));

    String inputLine;
    String urlContents = "";
    while ((inputLine = in.readLine()) != null)
        urlContents += inputLine;
    in.close();
    return urlContents;
		} catch (Exception e){
			if (i < 29)
				i++;
			else{
				e.printStackTrace();
				return null;
			}
		}
	}
	return null;

}
}