package reddit_network;

import java.util.ArrayList;

public class Thread {
	private int commentDirectReplies = 0;
	private int commentWithUrlDirectReplies = 0;
	private int commentsWithUrls = 0;
	private int numComments = 0;
	public ArrayList<Comment> commentsInThread = new ArrayList<Comment>(100);
	private long linkID = 0;
	private int numReplies;
	
	
	public Thread(Comment comment) {
		this.linkID = comment.getLink_id();
		addComment(comment);
		
	}


	public void addComment(Comment comment) {
		
		commentDirectReplies += (comment.getReplies() > 0 ? 1 : 0) * (comment.getParent_id() != 0 ? 1 : 0); //don't count link replies
		commentWithUrlDirectReplies += (comment.hasUrl()  && comment.getParent_id() != 0 ? comment.getReplies() : 0);
		commentsWithUrls += (comment.hasUrl() && comment.getParent_id() != 0 ? 1 : 0);
		numComments++;
		numReplies += (comment.getId() == comment.getLink_id() ? 0 : 1);
		commentsInThread.add(comment);
		
	}
	
	public ArrayList<Comment> getComments() { return commentsInThread; }
	
	public static String fileHeader() {
		return  "link_id\t" + 
				"comment_direct_replies\t" +
				"comment_with_url_direct_replies\t" +
				"comments_with_urls\t" +
				"total_number_comments\t" +
				"total_number_replies\n";
		
				
	}
	public String toString() {
		return 	linkID + "\t" + 
				commentDirectReplies + "\t" +
				commentWithUrlDirectReplies + "\t" +
				commentsWithUrls + "\t" +
				numComments + "\t" +
				numReplies + "\n";
		
	}
	
}
