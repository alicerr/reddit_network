package reddit_network;

import java.util.HashMap;

public class Triplet {
	public Author author;
	private HashMap<Long, Comment> comments = new HashMap<Long, Comment>(); 
	private HashMap<Long, Comment> replies = new HashMap<Long, Comment>(); 
	
	public Triplet(Author author)
	{
		this.author = author;
	}
	
	public void addComment(Comment c){
		comments.put(c.getCreated_utc(), c);
	}
	public void addReply(Comment c){
		replies.put(c.getCreated_utc(), c);	
	}
	public CommentStringLog finish(){
		return new CommentStringLog(author, comments, replies);
	}
}
