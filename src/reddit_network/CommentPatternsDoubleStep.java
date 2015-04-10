package reddit_network;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CommentPatternsDoubleStep {

	public CommentPatternsDoubleStep(HashMap<String, Author> allAuthors,
			HashMap<Long, CommentLinkedList> trees, String filebase,
			Network network) throws IOException {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filebase + "/" + filename));
			bw.write(Author.fileHeader().substring(0, Author.fileHeader().length() -1) + "\t" + CommentReplyComment.fileHeader());
			//bw.write(Straw.fileHeader());
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
				CommentReplyComment crc = new CommentReplyComment(cl, trees);
				bw = new BufferedWriter(new FileWriter(filebase + "/" + filename, true));
				/*uncomment for non-straw*/
				String authorData = Author.allAuthors.get(cl.get(0).getAuthor()).toString();
				bw.append(authorData.substring(0, authorData.length() - 1) + "\t" + crc.toString());
				
				/*uncomment for straw
				 * if (crc.straws != null)
				 *
					for (CommentLinkedList straw : crc.straws)
						bw.append(Straw.strawToFileString(straw));
						*/
				bw.close();
				
				
			}
			
			
		
	}

	
	private String filename = "doublestepQ2_notSnopedAnd5Comments.txt";
	public boolean criteria(Author a){
		return a.snoped == 0 && a.comments > 4;
	}
	

}
