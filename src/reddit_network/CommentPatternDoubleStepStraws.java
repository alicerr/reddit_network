package reddit_network;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CommentPatternsDoubleStepStraws {

	public CommentPatternsDoubleStepStraws(HashMap<String, Author> allAuthors,
			HashMap<Long, CommentLinkedList> trees, String filebase,
			Network network) throws IOException {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filebase + "/" + filename));
			bw.write(Author.fileHeader().substring(0, Author.fileHeader().length() -1) + "\t" + CommentReplyComment.fileHeader());
			bw.close();
			HashMap<String, ArrayList<Comment>> checkout = new HashMap<String, ArrayList<Comment>>();
			for (Author a : allAuthors.values()) {
				if (criteria(a)) {
					checkout.put(a.name.toLowerCase(), new ArrayList<Comment>());
				}
			}
			for (Comment c : network.network.values()) {
				if (checkout.containsKey(c.getAuthor().toLowerCase())) {
					checkout.get(c.getAuthor().toLowerCase()).add(c);
				}
			}
			System.out.println("rabbit");
			for (ArrayList<Comment> cl : checkout.values()) {
				java.util.Collections.sort(cl, new DateComparator());
				for (Comment c : cl) {
					ArrayList<CommentLinkedList> straws = threadTrees.get(c.getLink_id()).commentSets(c, blackList, this);
				}
				bw = new BufferedWriter(new FileWriter(filebase + "/" + filename, true));
				String authorData = Author.allAuthors.get(cl.get(0).getAuthor()).toString();
				bw.append(authorData.substring(0, authorData.length() - 1) + "\t" + crc.toString());
				bw.close();
				
			}
			
			
		
	}

	
	private String filename = "doublestep_NotSnopedAnd5Comments.txt";
	public boolean criteria(Author a){
		return a.snoped == 0 && a.comments > 4;
	}
	

}
