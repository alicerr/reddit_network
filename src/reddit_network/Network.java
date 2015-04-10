package reddit_network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Network {

	public HashMap<Long, Comment> network;
	public static long window = 1000*60*60*24*7*4; //milliseconds/sec* sec/min * min/hour * hour/day * day/week * weeks
	public static long maxDate = 1407754529;
	public static long minDate = 1354320000;
	
	
	public Network(long start, long end, String filebase, String filename) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(filename));
		int i = 0;
		network = new HashMap<Long, Comment>();
		String line;
		br.readLine();
		int p = 0;
		boolean timeExceded = false;
		while (br != null && ((line = br.readLine()) != null) && !timeExceded){
			if (line != ""){
				p++;
			
				Comment c = new Comment(line);
				//System.out.println(c.getCreated_utc() - 1354320000);
				//System.out.println(end);
				timeExceded = c.getCreated_utc() < start;
				if (c.getCreated_utc() <= end && !timeExceded) {
					try {
						network.put(Long.valueOf(c.getId()), c); 
						//System.out.println("added");
						if (i % 500 == 0) 
								System.out.println("added" + i);
						i++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (p %5000 == 0) {
				System.out.println("looked through: " + p);
			}
		}
		System.out.println("left");
		br.close();
		HashMap<Long, Comment> linkSet = new HashMap<Long, Comment>();
		i = 0;
	    int rem = 0;
		//assign parent author names
		for (Comment comment: network.values()) {
			try {
				if (i % 10 == 0)
					System.out.println("connect " + i);
				comment.setParentAuthor(network.get(comment.getParent_id()).getAuthorAddReply());
				
			} catch (Exception e1){
				try {
					
					comment.setParentAuthor(linkSet.get(comment.getParent_id()).getAuthorAddReply());
					//System.out.println("found in linkset");
				} catch (Exception e2) {
					String url = "no url made";
					try {
						url = comment.getUrl().replace(Long.toString(comment.getId(), 36) + "/?context=4",  Long.toString(comment.getParent_id(), 36) + "/.json");
						Comment parent = getLinkAsComment(url, comment.getParent_id());
						//System.out.println(url);
						if (parent != null){
							linkSet.put(comment.getParent_id(), parent);
							comment.setParentAuthor(parent.getAuthor());
							//System.out.println("found in intertubes");
							//System.out.println(comment.getAuthor());
							//System.out.println(comment.getParent_author());
							
						} else {
							rem++;
							System.out.print("Removed: " + comment.toString());
						    System.out.println("Failed at: " + url);
						}
					} catch (Exception e3) {
						e3.printStackTrace();
						e2.printStackTrace();
						rem++;
						System.out.print("Removed: " + comment.toString());
					}
				}
			}
			i++;
		}
		network.putAll(linkSet);
		new File(filebase).mkdirs();
		BufferedWriter bw = new BufferedWriter(new FileWriter(filebase + "/networkdata.txt"));
		for(Comment comment : network.values()) {
			bw.write(comment.toString());
		}
		bw.close();
		
		//retrieve arent authors not in network
		//report(filebase);
		//new Network(filebase, filebase + "/networkdata.txt");
	}
	public void repair(){
		HashMap<Long, Comment> linkSet = new HashMap<Long, Comment>();
		int i = 0;
	
	    int rem = 0;
		//assign parent author names
		for (Comment comment: network.values()) {
			if (comment.getParent_author().equals("NA")){
				
				try {
					
					comment.setParentAuthor(linkSet.get(comment.getParent_id()).getAuthorAddReply());
					//System.out.println("found in linkset");
				} catch (Exception e2) {
					String url = "";
					try {
						
						url = comment.getUrl().replace(Long.toString(comment.getId(), 36) + "/?context=4",  Long.toString(comment.getParent_id(), 36) + "/.json");
						Comment parent = getLinkAsComment(url, comment.getParent_id());
						linkSet.put(parent.getId(), parent);
						comment.setParentAuthor(parent.getAuthor());
						i++;
						if (i % 50 == 0) System.out.println("Repaired " + i);
					} catch (Exception e3) {
						//e3.printStackTrace();
						
						rem++;
						if (rem % 50 == 0) System.out.println("Dead " + rem);
						//System.out.print("Removed: " + comment.toString());
					}
				}
			}
			i++;
		}
		network.putAll(linkSet);
		
	
	}
	public Network(String filebase, String fileName) throws Exception {
		network = new HashMap<Long, Comment>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = "";
		int i = 0;
		while ((line = br.readLine()) != null) {
			i++;
			if (i % 10000 == 0){
				System.out.println("network data lines added: " + i);
			}
			Comment comment = new Comment(line, 1);
			network.put(comment.getId(), comment);
			//System.out.println(comment);
		}
		br.close();
		//repair();

		//int i = 0;
        //for (Comment comment : network.values()) {
			
			//Author.addComment(comment);
			//i++;
			//if (i % 100 == 0) System.out.println("authors added :" + i);
		//}
		//Author.toFile(filebase);		
		//new File(filebase+"/report").mkdirs();
		//report(filebase+"/report");
		//Graph.graphNetwork(filebase, this);
	}
   public static void getAuthors(Network network, String filebase) throws IOException {
		int i = 0;
        for (Comment comment : network.network.values()) {
			
			Author.addComment(comment);
			i++;
			if (i % 100 == 0) System.out.println("authors added :" + i);
		}
        new File(filebase).mkdirs();
		Author.toFile(filebase);		
   }
	
	//report interactions between two users during a date range surrounding a snope
	public void report(String filename) throws Exception{
		
	
		new File(filename).mkdirs();
		//System.out.println(filename);
		//
		//gather snopes
		int t = 0;
		int c = 0;
		ThreadCollection threads = new ThreadCollection(this, filename);
		Iterator<Thread> threadIt = threads.getThreads().values().iterator();
		while (threadIt.hasNext()) {
			Thread thread = threadIt.next();
			Iterator<Comment> commentIt = thread.getComments().iterator();
			
			while (commentIt.hasNext()) {
				Comment comment = commentIt.next();
				if (comment.isSnope() && Network.isAcceptable(comment)) {
					System.out.println("found snope");
					
					new Snope(this, thread, comment, filename, c);
					c=1;
				}
				
				
			}
			t++;
			if (t % 100 == 0){
				System.out.println("threads viewed: " + t);
			}
		}
		//BufferedWriter bw = new BufferedWriter(new FileWriter(filename+"/analysis.txt"));
		
	}
	public static boolean isAcceptable(Comment comment){
		return !comment.getAuthor().equals("[deleted]") 
				&& !comment.getParent_author().equals("[deleted]") 
				&& comment.getCreated_utc() > Network.minDate 
				&& comment.getCreated_utc() < Network.maxDate; 
	}
	public static boolean isInTimeRange(Comment comment){
		return  comment.getCreated_utc() > Network.minDate 
				&& comment.getCreated_utc() < Network.maxDate; 
	}
	private Comment getLinkAsComment(String url, Long parentID) throws Exception {
		//String linkID36 = Long.toString(parentID, 36);
		//String url = "http://www.reddit.com/"+linkID36+".json";
		//System.out.println(url);
		String jsonText = UrlReader.read(url);
		JsonArray allListings = new JsonParser().parse(jsonText).getAsJsonArray();
		JsonArray allListings2 = allListings.get(0).getAsJsonObject().get("data").getAsJsonObject().get("children").getAsJsonArray();
		//System.out.println(allListings.get(1));
		allListings2.addAll(allListings.get(1).getAsJsonObject().get("data").getAsJsonObject().get("children").getAsJsonArray());
		JsonObject link = findParent(allListings2, parentID);
		//System.out.println("start");
		//System.out.println(Long.toString(parentID, 36));
		//System.out.println(link);
		link = link != null ? link.get("data").getAsJsonObject() : null;
		return link != null? new Comment(link, parentID, url) : null;
	}
	private JsonObject findParent(JsonArray thread, Long parentId){
		JsonObject parent = null;
		//System.out.println(thread);
		boolean found = false;
	    int numChildren = thread.size();
		for (int i = 0; i < numChildren && !found; i++) {
		  JsonObject child = thread.get(i).getAsJsonObject();
		  if (isParent(child, parentId)) {
		    found = true;
		    parent = child;
		  } else {
			  
			  //System.out.println(child);
			  //System.out.println(Long.toString(parentId, 36));
			  if (child.has("data") && !child.get("data").toString().equals("\"\"")) {
				  JsonObject hold = child.get("data").getAsJsonObject();
				  if (hold.has("replies") && !hold.get("replies").toString().equals("\"\"")) {
					  //System.out.println(hold.get("replies").toString().equals("\"\""));

					  //System.out.println(hold.get("replies").toString());
					  hold = hold.get("replies").getAsJsonObject();
					  if (hold.has("data") && !hold.get("data").toString().equals("\"\"")) {
						  hold = hold.get("data").getAsJsonObject();
						  //System.out.println("here");
						  if (hold.has("children")  && !hold.get("children").toString().equals("\"\"")) {
							  JsonArray grandchildren = hold.get("children").getAsJsonArray();
							  thread.addAll(grandchildren);
						      numChildren += grandchildren.size();  
						      //System.out.println(numChildren);
						  }
					  }
				  }
			  }
		   }
		}
		return parent;
	}
	private boolean isParent(JsonObject data, long parentId) {
		//System.out.println(data.get("data").getAsJsonObject());
		String thisIdString = data.has("data")  && !data.get("data").toString().equals("\"\"") ? data.get("data").getAsJsonObject().get("id").getAsString() : null;
		return (thisIdString != null && !thisIdString.equals("") && Long.parseLong(thisIdString, 36) == parentId);
	}
		
	public HashMap<Long, Comment> getNetwork() {
		return network; //SHALLOW COPY
	}
	
	public static void authors(String filebase, Network network) throws IOException{
		int i = 0;
		for (Comment comment : network.network.values()) {
			i++;
			Author.addComment(comment);
			if (i %100 == 0) {
				System.out.println("authors added: " + i);
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(filebase + "/authors2.txt"));
		bw.write(Author.fileHeader());
	
		for (Author author : Author.allAuthors.values()) {
			bw.write(author.toString());
			
		}
		bw.close();
	}
	public void repopAuthors(String filebase) throws IOException{
		Author.addfromfile(filebase + "/authors.txt");
		authors(filebase, this);
	}
		
	public static void loadAuthorsFromFile(String filebase) throws IOException{
		Author.addfromfile(filebase + "/authors2.txt");
	}
		
		
		
		
	
}
