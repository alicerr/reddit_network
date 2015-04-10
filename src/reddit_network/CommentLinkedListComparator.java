package reddit_network;

import java.util.Comparator;

public class CommentLinkedListComparator implements Comparator<CommentLinkedList> {

	@Override
	public int compare(CommentLinkedList arg0, CommentLinkedList arg1) {
		// TODO Auto-generated method stub
		return Long.compare(arg0.self.getCreated_utc(), arg1.self.getCreated_utc());
	}


}
