package reddit_network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Snope {
	private Comment snope;
	private Comment parent;
	private ArrayList<Comment> otherResponsesToParent = new ArrayList<Comment>();
	private static int randomCommentsToGet = 5;
	public static ArrayList<Comment> commentsSeen = new ArrayList<Comment>();
	public ArrayList<Comment> commentsScouted = new ArrayList<Comment>();
	
	public static boolean notReviewed(Comment comment){
		boolean seen = false;
		Iterator<Comment> comments = commentsSeen.iterator();
		while(!seen && comments.hasNext()){
			Comment c = comments.next();
			seen = seen || c.equals(comment) || (c.getAuthor().equals(comment.getAuthor()) && c.getLink_id() == comment.getLink_id());
		}
		return !seen;
	}
	public boolean notScouted(Comment comment){
		boolean seen = false;
		Iterator<Comment> scout = commentsScouted.iterator();
		while(!seen && scout.hasNext()){
			Comment c = scout.next();
			seen = seen || (c.getAuthor().equals(comment.getAuthor()) && c.getLink_id() == comment.getLink_id());
		}
		return !seen;
	}
	public static boolean isAcceptableRandom(Comment comment){
		return Network.isAcceptable(comment) && !comment.isSnope() && notReviewed(comment);
	}
	public Snope (Network network, Thread thread, Comment snope, String oldFilepath, int c2) throws IOException {
		this.snope = snope;
		commentsSeen.add(snope);
		this.parent = network.getNetwork().get(snope.getParent_id());
		if (this.parent != null){
			
			int length = thread.getComments().size();
			int sampleSize = 0;
			Iterator<Comment> comments = thread.getComments().iterator();
			while (comments.hasNext() && sampleSize < randomCommentsToGet + 1){
				Comment comment = comments.next();
				if (Snope.isAcceptableRandom(comment) && notScouted(comment)){
					sampleSize++;
					commentsScouted.add(comment);
					
				}
			}
			Comment[] randoms = new Comment[sampleSize];
			//System.out.println("getting random comments");
			for (int i = 0; i < sampleSize; i++ ) {
				boolean found = true;
				while (found) {
					int index = (int)Math.floor(Math.random() * length);
					Comment c = thread.getComments().get(index);
					if (isAcceptableRandom(c)){
						commentsSeen.add(c);
						randoms[i] = c;
						found = false;
					}
				}
			}
			//System.out.println("random comments found");
			String filepath = oldFilepath + File.separator + "snope_id" + snope.getId();
			new File(filepath).mkdirs(); 
			
			String fileText = (c2 == 0 ? CommentHistory.fileHeader() : "") + 
					new CommentHistory(network, snope, filepath).toString();
			
			
			
			for (Comment comment : randoms) {
				fileText += new CommentHistory(network, comment, filepath).toString();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(oldFilepath + File.separator + "commentHistorySet.txt", true));
			bw.append(fileText);
			bw.close();
			
			fileText = "role\t" + Comment.tableHeader();
			System.out.println("Parent");
			fileText += "snopee\t" + parent.toString();
			fileText += "snoper\t" + this.snope.toString();
			bw = new BufferedWriter(new FileWriter(oldFilepath + File.separator + "snopeids.txt", true));
			bw.append(fileText);
			bw.close();
		} else {
			System.out.println("NULL");
		}
		
		
		
	    
		
	}
}
