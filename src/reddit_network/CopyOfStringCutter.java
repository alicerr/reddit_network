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

public class CopyOfStringCutter {
	
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
			boolean getNext = false;
			for (Comment c : cl){
				ArrayList<CommentLinkedList>straws = trees.get(c.getLink_id()).commentSets(c, blackList, null);
				if (straws != null){
					for (CommentLinkedList straw : straws){
						if (straw.next.get(0).self.isSnope()){
							for (CommentLinkedList s : straws)
								for (CommentLinkedList ss : s.next){
									ss.self.sister_to_snope = true;
									for (CommentLinkedList sss : ss.next){
										sss.self.has_snope_aunt = true;
									}
								}
							if (straw.next.get(0).next.size() > 0){
								straw.next.get(0).next.get(0).self.is_snope_response = true;
								
							}
							for (CommentLinkedList s : straws){
								
								allStraws.add(s);
								
							}
							
							getNext = true;
							if (last != null){
								//allStraws.addAll(last);
								for (CommentLinkedList s : last){
									
									//allStraws.add(s);
								
								}
								
							}
						}
					}
				} 
				if ((true && straws != null)|| getNext){
					//allStraws.addAll(straws);
					for (CommentLinkedList s : straws){
						
						allStraws.add(s);
					
						
					}
					getNext = false;
				}
				last = straws;
			}
			new CommentFetcher(allStraws, Author.allAuthors.get(acl.getKey()), fileBase + "\\" + fileName(), hm);
			//bw = new BufferedWriter(new FileWriter(fileBase + "/" + fileName(), true));
			/*uncomment for non-straw*/
			//String authorData = Author.allAuthors.get(cl.get(0).getAuthor()).toString();
			//bw.append(authorData.substring(0, authorData.length() - 1) + "\t" + crc.toString());
			
			/*uncomment for straw
			 * if (crc.straws != null)
			 *
				for (CommentLinkedList straw : crc.straws)
					bw.append(Straw.strawToFileString(straw));
					*/
			//bw.close();
			
			
		}
	}

	private static boolean meetCrteria(Author a) {
		return a.snoped > 0 && a.comments > 4;
	}
	
	
	private static String fileName() {
		// TODO Auto-generated method stub
		return "string_cutter5";
	}
}
