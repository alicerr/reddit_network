package reddit_network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CommentLinkedList implements Comparable{
	public ArrayList<CommentLinkedList> next = new ArrayList<CommentLinkedList>();
	public CommentLinkedList prev = null;
	Comment self = null;
	String snope_id = "";
	public CommentLinkedList(CommentLinkedList parent, Comment c, Thread t){
		assert(c.getParent_author().equalsIgnoreCase(parent.self.getAuthor()));
		prev = parent;
		self = c;
		
		for (Comment ci : t.commentsInThread) {
			if (ci.getParent_id() == c.getId()) {
				next.add(new CommentLinkedList(this, ci, t));
			}
		}
		List<CommentLinkedList> next2 = next;
		java.util.Collections.sort(next2, new CommentLinkedListComparator());
		
	}
	public CommentLinkedList(Thread t){
		java.util.Collections.sort(t.commentsInThread, new DateComparator());

		self = t.commentsInThread.get(0);
		t.commentsInThread.remove(self);
		for (Comment ci : t.commentsInThread) {
			if (ci.getParent_id() == self.getId()) {
				next.add(new CommentLinkedList(this, ci, t));
			}
		}
		List<CommentLinkedList> next2 = next;
		java.util.Collections.sort(next2, new CommentLinkedListComparator());
	}
	public CommentLinkedList(CommentLinkedList prev, Comment self) {
		this.prev = prev;
		this.self = self;
	}
	public long getLinkId() {
		return self.getLink_id();
	}
	public static HashMap<Long, CommentLinkedList> threadCollectionToCommentLinkedLists(ThreadCollection tc) {
		HashMap<Long, CommentLinkedList> hm = new HashMap<Long, CommentLinkedList>();
		for (Thread t : tc.threads.values()){
			CommentLinkedList cl = new CommentLinkedList(t);
			hm.put(cl.getLinkId(), cl);
			
		}
		return hm;
	}
	public ArrayList<CommentLinkedList> commentSets(Comment c, ArrayList<Long> blackList, CommentReplyComment crc) {
		if (c.getId() == self.getId()) {
			ArrayList<CommentLinkedList> straws = new ArrayList<CommentLinkedList>();
			for (CommentLinkedList cl : next) {
				if (!blackList.contains(cl.self.getId()) && !cl.self.getAuthor().equalsIgnoreCase(c.getAuthor()) ){
					if (crc != null){
						crc.replies++;
						crc.snopeReplies += cl.self.isSnope() ? 1 : 0;
					}
					CommentLinkedList cll = cl.searcher(new CommentLinkedList(null, c), blackList, c);
					if (crc != null && cll.next.get(0).next.size() == 0) {
						crc.repliesTotallyIgnored++;
						crc.snopeRepliesTotallyIgnored += cl.self.isSnope() ? 1 : 0;
					}
					straws.add(cl.searcher(new CommentLinkedList(null, c), blackList, c));
					
				} 
			}
			System.out.println(straws.size());
			for (CommentLinkedList cll : straws) {
				System.out.print(cll.self.getAuthor());
				while (cll.next.size() > 0){
					System.out.print("<--" + cll.next.get(0).self.getAuthor());
					cll = cll.next.get(0);
				}
				System.out.print("\n");
			}
			return straws;
		} else {
			ArrayList<CommentLinkedList> response = null;
			for (CommentLinkedList cl : next) {
				response = cl.commentSets(c, blackList, crc);
				if (response != null )
					return response;
			}
			return response;
		}
	}
	public HashSet<String> uniqueAuthors(){
		HashSet<String> hs = new HashSet<String>();
		for (CommentLinkedList c : next){
			hs.add(c.self.getAuthor().toLowerCase());
		}
		return hs;
	}
	public HashSet<String> uniqueAuthorsInTree(){
		HashSet<String> hs = uniqueAuthors();
		for (CommentLinkedList c : next){
			hs.addAll(c.uniqueAuthorsInTree());
		}
		return hs;
	}
	public int selfComments(){
		int selfComment = 0;
		for (CommentLinkedList c : next){
			if (c.self.getAuthor().equalsIgnoreCase(self.getAuthor()))
				selfComment++;
		}
		return selfComment;
	}

	public CommentLinkedList searcher(CommentLinkedList seed, ArrayList<Long> blackList, Comment model) {
			CommentLinkedList newSeed = new CommentLinkedList(seed, self);
			//Collection<CommentLinkedList> seeds = new Collection<CommentLinkedList>();
			for (CommentLinkedList cl : next) {
				if (cl.self.getAuthor().equalsIgnoreCase(self.getParent_author())) {
					cl.searcher(newSeed, blackList, model);
					if (!self.getAuthor().equalsIgnoreCase(model.getAuthor())) {
						blackList.add(self.getId());
					}
				} 
			}
			seed.next.add(newSeed);
			return seed;
	}
	public void getCommentWindows(
			ArrayList<Long> commentWindowTracker, 
			ArrayList<Long> snopeCommentWindowTracker,
			ArrayList<Long> commentToReplyTracker, 
			ArrayList<Long> snopeCommentToReplyTracker,
			ArrayList<Long> replyToCommentTracker,
			ArrayList<Long> snopeReplyToCommentTracker,
			Comment model
			){
		if (model == null) {
			model = self;
		} else if (self.getAuthor().equalsIgnoreCase(model.getAuthor())) {
			//assert(self.getAuthor().equalsIgnoreCase(prev.prev.self.getAuthor()));
			//if (prev.prev != null)
			commentWindowTracker.add(self.getCreated_utc() - prev.prev.self.getCreated_utc());
			replyToCommentTracker.add(self.getCreated_utc() - prev.self.getCreated_utc());
			if (prev.self.isSnope() && prev.prev != null) {
				snopeCommentWindowTracker.add(self.getCreated_utc() - prev.prev.self.getCreated_utc());
				snopeReplyToCommentTracker.add(self.getCreated_utc() - prev.self.getCreated_utc());
			}
		} else if (!self.getAuthor().equalsIgnoreCase(model.getAuthor()) /*&& next.size() > 0*/) {
			assert(self.getParent_author().equalsIgnoreCase(prev.self.getAuthor()));
			commentToReplyTracker.add(self.getCreated_utc() - prev.self.getCreated_utc());
			if (self.isSnope()) {
				snopeCommentToReplyTracker.add(self.getCreated_utc() - prev.self.getCreated_utc());
			}
			
		}
		
		if (next.size() > 0){
			next.get(0).getCommentWindows(
					commentWindowTracker, 
					snopeCommentWindowTracker,
					commentToReplyTracker, 
					snopeCommentToReplyTracker,
					replyToCommentTracker,
					snopeReplyToCommentTracker,
					model);
		}
		
		
	}
	public static void printCRC(CommentLinkedList cll){
		System.out.print(cll.self.getAuthor());
		while(cll.next.size() > 0) {
			System.out.print("<--" + cll.next.get(0).self.getAuthor());
			cll = cll.next.get(0);
		}
		System.out.print("\n");
	}
	public int getMaxDepth() {
		int max = 0;
		for (CommentLinkedList cl : next) {
			int newMax = cl.getMaxDepth();
			if (newMax > max) 
				max = newMax;
		}
		return max + 1;
	}
	public boolean hasSnope() {
		boolean snope = self.isSnope();
		for (CommentLinkedList cl : next) {
			if (snope)
				return true;
			else
				snope = cl.hasSnope();
		}
		return snope;
	}
	public boolean firstAuthorIsSnoped(Comment model) {
		boolean snope = self.isSnope() && self.getParent_author().equalsIgnoreCase(model.getAuthor());
		for (CommentLinkedList cl : next) {
			if (snope)
				return true;
			else
				snope = cl.firstAuthorIsSnoped(model);
		}
		return snope;
}
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return Long.compare(self.getCreated_utc(), ((CommentLinkedList)arg0).self.getCreated_utc());
	}
	
	
	
}
