package reddit_network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CommentFetcher {
	public HashSet<Long> seen = new HashSet<Long>();
	public Author author;
	public static final int UNKNOWN = -1;
	public static final int LISTING = 0;
	public static final int T1 = 1;
	public static final int T3 = 2;
	public static HashSet<Long> links = new HashSet<Long>();
	public static HashMap<Long, JsonObject> old = null;
	public static final boolean offline = false;
	
	public CommentFetcher(ArrayList<CommentLinkedList> straws, ArrayList<Comment> prevs, Author author, String filename, ArrayList<Long> hm) throws Exception{
		initOld(filename);
		this.author = author;
		ArrayList<Comment> wanted = new ArrayList<Comment>();
		for (CommentLinkedList straw : straws){
			String snope_id = straw.snope_id;
			if (snope_id.equals("")){
				System.out.println("now");
			}
			if (hm != null && hm.contains(straw.self.getId())){
				//hm.remove(new Long(straw.self.getId()));
				if (hm.contains(straw.next.get(0).self.getId())){
					hm.remove(new Long(straw.next.get(0).self.getId()));
				}
			} else {
				straw.self.stat = "a1";
				straw.self.snope_id = snope_id;
				wanted.add(straw.self);
				for (CommentLinkedList ss : straw.next){
					ss.self.stat = "b1";
					if (ss.self.sister_to_snope == null)
						ss.self.sister_to_snope = false;
					ss.self.snope_id = snope_id;
					wanted.add(ss.self);
					for (CommentLinkedList sss : ss.next){
						sss.self.stat = "a2";
						if (sss.self.has_snope_aunt == null)
							sss.self.has_snope_aunt = false;
						if (sss.self.is_snope_response == null)
							sss.self.is_snope_response = false;
						sss.self.snope_id = snope_id;
						wanted.add(sss.self);
					}
				}
				
			}
		}
		wanted.addAll(prevs);
		Collections.sort(wanted, new DateComparator());
		ArrayList<Comment> noDups = new ArrayList<Comment>();
		if (wanted.size() > 0)
		noDups.add(wanted.get(0));
		int length = wanted.size();
		for (int i = 1; i < length; i++){
			if (!(wanted.get(i - 1).getId() == wanted.get(i).getId() 
					&& ((wanted.get(i-1).type == null && wanted.get(i).type == null)
						|| (!(wanted.get(i-1).type == null) && !(wanted.get(i).type == null) && wanted.get(i).type.equals(wanted.get(i-1).type))))){
				noDups.add(wanted.get(i));
			}
		}
		wanted = noDups;
		long last = 0;
		boolean triedStrict = false;
		if (offline){
			HashMap<Long, karmaComment> write = new HashMap<Long, karmaComment>();
			for (Comment c : wanted){
				if (c.getId() != c.getLink_id() && old.containsKey(c.getId())){
					karmaComment x = new karmaComment(this, old.get(c.getId()), c);
					write.put(c.getId(), x);
					
				} else if (old.containsKey(c.getId())){
					karmaComment x = new karmaComment(this, old.get(c.getId()), c, true);
					write.put(c.getId(), x);
					
				}
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename + "_offline.txt", true));
			for (karmaComment w : write.values()){
				bw.append(w.toString());
				
			}
			bw.close();
		} else {
			while (!wanted.isEmpty()){
				Comment next = wanted.get(0);
				if (next.getId() == last && triedStrict){
					wanted.remove(0);
					triedStrict = false;
				} else {
					String url = karmaComment.urlReducer(next.getUrl(), next);
					triedStrict = false;
					if (next.getId() == last){
						url = karmaComment.urlReducer(next.getUrl(), null);
						triedStrict = true;
					}
					JsonArray page = getPage(url);
					if (page != null){
						HashMap< Long, karmaComment> hold = search(page, null, wanted);
						if (hold != null){
							BufferedWriter bw = new BufferedWriter(new FileWriter(filename + ".txt", true));
							BufferedWriter js = new BufferedWriter(new FileWriter(filename + ".json", true));
							for (karmaComment c : hold.values()){
								bw.append(c.toString());
								js.append(c.json + "\n");
								System.out.println(c);
								//c.comment.stat = null;
								//c.comment.has_snope_aunt = null;
								//c.comment.is_snope_response = null;
								//c.comment.sister_to_snope = null;
							}
							
							bw.close();
							js.close();
						}
					}
				}
				last = next.getId();
			}
		}
			
		
	}
	
	private void initOld(String filename) throws IOException {
		if (old == null && offline){
			old = new HashMap<Long, JsonObject>();
			BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Alice\\research\\summer_2014_subreddits-2014-09-12\\summer_2014_subreddits\\r_politics\\string_cutter4_a2.json"));
			String line = br.readLine();
			while (line != null){
				JsonObject j = new JsonParser().parse(line).getAsJsonObject();
				old.put(Long.parseLong(j.get("id").getAsString(), 36), j);
				line = br.readLine();
			}
			br.close();
			
		}
		
	}

	public static JsonArray getPage(String url) throws Exception {
		//String linkID36 = Long.toString(parentID, 36);
		//String url = "http://www.reddit.com/"+linkID36+".json";
		System.out.println(url);
		String jsonText = UrlReader.read30(url);
		try{
			JsonArray allListings = new JsonParser().parse(jsonText).getAsJsonArray();
			return allListings;
		} catch (Exception e){
			System.out.println("URL ERROR AT : " + url);
			e.printStackTrace();
			System.out.println(jsonText);
		}
		return null;
	}
	
	private static int getKind(JsonObject obj){
		if (obj.has("kind")){
			String kind = obj.get("kind").getAsString();
			if (kind.equals("t3")) return T3;
			if (kind.equals("t1")) return T1;
			if (kind.equals("Listing")) return LISTING;
		}
		return UNKNOWN;
	}
	
	private HashMap<Long, karmaComment> search(JsonObject s, karmaComment t12, ArrayList<Comment> wanted){
		
		
		int kind = getKind(s);
		if (kind == UNKNOWN || wanted.size() == 0){
			return null;
		} 
		JsonObject data = s.get("data").getAsJsonObject();
		if (kind == LISTING){
			return search(data.get("children").getAsJsonArray(), t12, wanted);
		} else if (kind == T3){
			links.add(Long.parseLong(data.getAsJsonObject().get("id").getAsString(), 36));
			
			long id = Long.parseLong(data.get("id").getAsString(), 36);
			Comment c = getCommentIfWanted(wanted, id);
			if (c != null){
				HashMap<Long, karmaComment> hold = new HashMap<Long, karmaComment>();
				karmaComment t3 = null;
				t3 = new karmaComment(this, data, c, true);
				hold.put(c.getId(), t3);
				return hold;
			}
			return null;
			
		} else if (kind == T1){
			long id = Long.parseLong(data.get("id").getAsString(), 36);
			HashMap<Long, karmaComment> hold = new HashMap<Long, karmaComment>();
			JsonObject r = (!data.get("replies").isJsonObject() ? null : data.get("replies").getAsJsonObject());
			
			karmaComment t1 = null;
			Comment c = getCommentIfWanted(wanted, id);
			if (c != null){
				t1 = new karmaComment(this, data, c);
				hold.put(c.getId(), t1);
			}
			wanted.remove(id);
			
			HashMap<Long, karmaComment> replies = (r == null ? null : search(r, t1, wanted));
			if (replies != null){
				for (karmaComment k : replies.values()){
					hold.put(k.comment.getId(), k);
				}
			}
			return hold;
			
		}
		return null;
		
		
	}

	private Comment getCommentIfWanted(ArrayList<Comment> wanted, long id) {

		for (int i = 0; i < wanted.size(); i++){
			Comment c = wanted.get(i);
			if (c.getId() == id){
				wanted.remove(i);
				return c;
			} 
			
		}
		return null;
	}

	

	private HashMap<Long, karmaComment> search(JsonArray asJsonArray, karmaComment t1,
			ArrayList<Comment> wanted) {
		HashMap<Long, karmaComment> hold = new HashMap< Long, karmaComment>();
		
		for (JsonElement js : asJsonArray){
			HashMap< Long, karmaComment> holdc = search(js.getAsJsonObject(), null, wanted);
			if (holdc != null){
				for (karmaComment c : holdc.values()){
					hold.put(c.comment.getId(), c);
					/*if (t1 != null && c.comment.getParent_id() == t1.comment.getId() && c.comment.isSnope()){
						t1.wasSnoped = true;
					}*/
				}
			}

		}
		return hold;
	}

}
