package reddit_network;
import java.util.Comparator;

import reddit_network.Comment;


public class DateComparator implements Comparator<Comment> {
	 @Override
	    public int compare(Comment o1, Comment o2) {
	        return Long.compare(o1.getCreated_utc() , o2.getCreated_utc());
	    }
}
