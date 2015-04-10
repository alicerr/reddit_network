package reddit_network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class StringCutter {
	public static HashMap<String, ArrayList<String>> seen_links = new HashMap<String, ArrayList<String>>();
	public static String key_maker(Comment c){
		if (c.getAuthor().compareTo(c.getParent_author()) > 0)
			return (c.getAuthor() + c.getParent_author() + c.getLink_id()).toLowerCase();
		else return (c.getParent_author() + c.getAuthor() + c.getLink_id()).toLowerCase();
	}
	public static String get_snope_id(Comment c){
		
		return c.getId() + "_snopes_" + c.getParent_id();
		
		
	}
	public static void Cut(String fileBase, HashMap<String, Author> allAuthors, ThreadCollection threads, Network network, HashMap<Long, CommentLinkedList> trees) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileBase + '/' + fileName() + ".txt"));
		bw.write(karmaComment.fileHeader());
		bw.close();
		ArrayList<Long> hm = null;
		
		/*BufferedReader br = new BufferedReader(new FileReader(fileBase + '/' + fileName() + ".txt"));
		String line = br.readLine();
		line = br.readLine();
		
		ArrayList<Long> hm = new ArrayList<Long>();
		while (line != null){
			
			String[]  d = line.split("\t");
			try{
			hm.add(Long.parseLong(d[1]));
			} catch (Exception e){
				e.printStackTrace();
			}
			line = br.readLine();
		}
		br.close();*/
		HashMap<String, Author> authorsToCut = new HashMap<String, Author>();
		for (Entry<String, Author> a : allAuthors.entrySet()) {
			if (meetCrteria(a.getValue())) {
				authorsToCut.put(a.getKey(), a.getValue());
			}
		}
		HashMap<String, ArrayList<Comment>> authorComments = new HashMap<String, ArrayList<Comment>>();
		for (Author a : authorsToCut.values()) {
			authorComments.put(a.name, new ArrayList<Comment>());
		}
		System.out.println("All authors added. Count: " + authorsToCut.size());
		int i = 0;
		int j = 0;
		for (Comment c : network.network.values()) {
			if (authorComments.containsKey(c.getAuthor())) {
				authorComments.get(c.getAuthor()).add(c);
				j++;
			}
			i++;
			if (i % 10000 == 0) {
				System.out.println("Looked at " + i + " comments, kept " + j + " comments");
			}
		}
		System.out.println("All comments collected. List Count: " + authorsToCut.size() + " CommentCount: " + j);
		for (Entry<String, ArrayList<Comment>> acl : authorComments.entrySet()) {
			//CommentReplyComment crc = new CommentReplyComment(cl, trees);
			ArrayList<Comment> cl = acl.getValue();
			Collections.sort(cl, new DateComparator());
			ArrayList<Long> blackList = new ArrayList<Long>();
			ArrayList<CommentLinkedList> allStraws = new ArrayList<CommentLinkedList>();
			ArrayList<CommentLinkedList> last = null;
			Comment lastComment = null;
			boolean getNext = false;
			boolean getNextComment = false;
			boolean lastSnoped = false;
			ArrayList<Comment> prevs = new ArrayList<Comment>();
			ArrayList<Comment> nexts = new ArrayList<Comment>();
			String last_snope_id = "";
			for (Comment c : cl){
				ArrayList<CommentLinkedList>straws = trees.get(c.getLink_id()).commentSets(c, blackList, null);
				boolean snope = false;
				
				if (straws != null){
					
					for (CommentLinkedList straw : straws){
						if (straw.next.get(0).self.isSnope() && !seen(straw.next.get(0))){
							last_snope_id = get_snope_id(straw.next.get(0).self);
							straw.self = straw.self.clone();
							straw.self.type = "snope";
							snope = true;
							lab3(straws, "snope");
							for (CommentLinkedList s : straws){
								s.snope_id = last_snope_id;
								for (CommentLinkedList ss : s.next){

									ss.self.sister_to_snope = true;
									ss.snope_id = last_snope_id;
									for (CommentLinkedList sss : ss.next){
										sss.self.has_snope_aunt = true;
										sss.snope_id = last_snope_id;
									}
								}
							}
							if (straw.next.get(0).next.size() > 0){
								straw.next.get(0).next.get(0).self.is_snope_response = true;
								
							}
							for (CommentLinkedList s : straws){
								s.snope_id = last_snope_id;
								allStraws.add(s);
								
							}
						
							String nLab = last != null && last.size() > 0 && last.get(0).self.getId() == lastComment.getId() ? "prev_and_prev_with_reply" : "prev_with_reply";
							
							if (last != null){
								for (CommentLinkedList s : last)
									s.snope_id = last_snope_id;
								allStraws.addAll(last);
								lab3(last, nLab);
							} 
							if (lastComment != null){
								if (last == null || last.size() == 0 || last.get(0).self.getId() != lastComment.getId()){
									lastComment.type = "prev";
									lastComment.snope_id = last_snope_id;
								    prevs.add(lastComment);
								}
							}

						}
					}
				} 
				if (!snope && getNext && straws != null && straws.size() > 0){
					//allStraws.addAll(straws);
					String nLab = getNext && getNextComment ?  "next_and_next_with_replies" : "next_with_replies";
					lab3(straws, nLab);
					for (CommentLinkedList s : straws){
						s.snope_id = last_snope_id;
						allStraws.add(s);
					
						
					}
					getNext = false;
					getNextComment = false;
				}
				if (!snope && getNextComment){
					
					c.type = "next";
					c.snope_id = last_snope_id;
					nexts.add(c);
					getNextComment = false;
				}
				if (!snope){
					if (straws != null && straws.size() > 0){
						last = straws;
					}
					lastComment = c;
				} else {
					last = null;
					lastComment = null;
					getNext = true;
					getNextComment = true;
				}
					
				
			}
			prevs.addAll(nexts);
			
			new CommentFetcher(allStraws, prevs, Author.allAuthors.get(acl.getKey()), fileBase + "\\" + fileName(), hm);
			bw = new BufferedWriter(new FileWriter(fileBase + "//subsumed_authors.txt"));
			bw.write("subsumer\tsubsumed\n");
			for (ArrayList<String> s : seen_links.values()){
				int u = 0;
				for (String ss : s){
					if (u > 0){
					  bw.append(s.get(0) + "\t" + ss + "\n");
					}
				    u++;
				}
					  
			}
			bw.close();
			/*uncomment for non-straw*/
			//String authorData = Author.allAuthors.get(cl.get(0).getAuthor()).toString();
			//bw.append(authorData.substring(0, authorData.length() - 1) + "\t" + crc.toString());
			
			//uncomment for straw
			 //if (crc.straws != null)
			 
			//	for (CommentLinkedList straw : crc.straws)
				//	bw.append(Straw.strawToFileString(straw));
				
		//	bw.close();
			
			
		}
	}

	private static boolean seen(CommentLinkedList cl) {
		 //return true;
		 String key = key_maker(cl.self);
		 if (seen_links.containsKey(key) ){
			 seen_links.get(key).add(get_snope_id(cl.self));
			 return true;
		 } 
		 ArrayList<String> a = new ArrayList<String>();
		 a.add(get_snope_id(cl.self));
		 seen_links.put(key, a);
		 return false;
	}
	private static void lab3(ArrayList<CommentLinkedList> last, String nLab) {
		for (CommentLinkedList s : last){
			s.self = s.self.clone();
			s.self.type = nLab;
			for (CommentLinkedList ss : s.next){
				ss.self = ss.self.clone();
				ss.self.type = nLab;
				for (CommentLinkedList sss : ss.next){
					sss.self = sss.self.clone();
					sss.self.type = nLab;
				}
			}
		}
		
	}

	private static boolean meetCrteria(Author a) {
		return a.snoped > 0 && a.comments > 4;
	}
	
	
	private static String fileName() {
		// TODO Auto-generated method stub
		return "string_cutter6_with_id";
	}
}
