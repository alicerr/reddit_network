package reddit_network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CommentReplyComment {



	public ArrayList<Long> commentWindowTracker = new ArrayList<Long>();
	public ArrayList<Long> snopeCommentWindowTracker = new ArrayList<Long>();
	public ArrayList<Long> commentToReplyTracker = new ArrayList<Long>();
	public ArrayList<Long> snopeCommentToReplyTracker = new ArrayList<Long>();
	public ArrayList<Long> replyToCommentTracker = new ArrayList<Long>();
	public ArrayList<Long> snopeReplyToCommentTracker = new ArrayList<Long>();
	public ArrayList<Long> blackList = new ArrayList<Long>();
	public ArrayList<Long> depthTracker = new ArrayList<Long>();
	public ArrayList<Long> depthWithSnopeTracker = new ArrayList<Long>();
	public ArrayList<Long> depthAuthorIsSnoped = new ArrayList<Long>();
	public ArrayList<Long> commentsA1 = new ArrayList<Long>();
	public ArrayList<Long> commentsA2 = new ArrayList<Long>();
	public ArrayList<Integer> uniqueAuthorsAt2 = new ArrayList<Integer>();
	public ArrayList<Integer> uniqueAuthorsAt2IfSnopedAtLevel2 = new ArrayList<Integer>();
	public ArrayList<Integer> uniqueAuthorsInTree = new ArrayList<Integer>();
	public ArrayList<Integer> uniqueAuthorsInTreeIfSnopedAtLevel2 = new ArrayList<Integer>();
	public ArrayList<Integer> selfCommentsAtDepth2 = new ArrayList<Integer>();
	public Author author = null;
	public int repliesTotallyIgnored;
	public int replies;
	public int snopeRepliesTotallyIgnored;
	public int snopeReplies;
	public ArrayList<CommentLinkedList> straws = new ArrayList<CommentLinkedList>();
	
	public CommentReplyComment(ArrayList<Comment> comments, HashMap<Long, CommentLinkedList> threadTrees) {
		java.util.Collections.sort(comments, new DateComparator());
		for (Comment c : comments) {
			ArrayList<CommentLinkedList >straws = threadTrees.get(c.getLink_id()).commentSets(c, blackList, this);
			if (straws != null ){
				//System.out.println(straws.size() + " straws at " + c);
				
				for (CommentLinkedList straw : straws) {
					CommentLinkedList.printCRC(straw);
					straw.getCommentWindows(
							commentWindowTracker, 
							snopeCommentWindowTracker, 
							commentToReplyTracker, 
							snopeCommentToReplyTracker, 
							replyToCommentTracker, 
							snopeReplyToCommentTracker,
							null);
					long depth = (long)straw.getMaxDepth();
					depthTracker.add(depth);
					if (straw.hasSnope()) {
						depthWithSnopeTracker.add(depth);
					}
					if (straw.firstAuthorIsSnoped(c)) {
						depthAuthorIsSnoped.add(depth);
					}
					commentsA1.add((depth + 1)/2);
					commentsA2.add((depth/2));
					
				}
			this.straws.addAll(straws);
			} else {
				//System.out.println("Null straws at: " + c.toString());
			}
		}
		
	}
	public String toString(){
		double avgCommentWindow = Dataflow.getAvg(commentWindowTracker);
		double medianCommentWindow = Dataflow.getMedian(commentWindowTracker);
		double logAvgCommentWindow = Dataflow.getLogAvg(commentWindowTracker);
		int numCommentWindow = commentWindowTracker.size();
		
		//public ArrayList<Long> comment_windows = new ArrayList<Long>();
		double avgSnopeCommentWindow = Dataflow.getAvg(snopeCommentWindowTracker);
		double medianSnopeCommentWindow = Dataflow.getMedian(snopeCommentWindowTracker);
		double logSnopeAvgCommentWindow = Dataflow.getLogAvg(snopeCommentWindowTracker);
		int numSnopeCommentWindow = snopeCommentWindowTracker.size();
		//public ArrayList<Long> commentWindowTracker = new ArrayList<Long>();
		//public ArrayList<Long> snopeCommentWindowTracker = new ArrayList<Long>();
		
		//public ArrayList<Long> commentToReplyTracker = new ArrayList<Long>();
		double avgCommentToReply = Dataflow.getAvg(commentToReplyTracker);
		double medianCommentToReply = Dataflow.getMedian(commentToReplyTracker);
		double logAvgCommentToReply = Dataflow.getLogAvg(commentToReplyTracker);
		int numCommentToReply = commentToReplyTracker.size();
		
		//public ArrayList<Long> snopeCommentToReplyTracker = new ArrayList<Long>();
		double avgsnopeCommentToReply = Dataflow.getAvg(snopeCommentToReplyTracker);
		double mediansnopeCommentToReply = Dataflow.getMedian(snopeCommentToReplyTracker);
		double logAvgsnopeCommentToReply = Dataflow.getLogAvg(snopeCommentToReplyTracker);
		int numsnopeCommentToReply = snopeCommentToReplyTracker.size();
		
		//public ArrayList<Long> replyToCommentTracker = new ArrayList<Long>();
		double avgreplyToComment = Dataflow.getAvg(replyToCommentTracker);
		double medianreplyToComment = Dataflow.getMedian(replyToCommentTracker);
		double logAvgreplyToComment = Dataflow.getLogAvg(replyToCommentTracker);
		int numreplyToComment = replyToCommentTracker.size();
		
		
		//public ArrayList<Long> snopeReplyToCommentTracker = new ArrayList<Long>();
		double avgsnopeReplyToComment = Dataflow.getAvg(snopeReplyToCommentTracker);
		double mediansnopeReplyToComment = Dataflow.getMedian(snopeReplyToCommentTracker);
		double logAvgsnopeReplyToComment = Dataflow.getLogAvg(snopeReplyToCommentTracker);
		int numsnopeReplyToComment = snopeReplyToCommentTracker.size();
		
		
		
		//public ArrayList<Long> depthTracker = new ArrayList<Long>();
		double avgDepth = Dataflow.getAvg(depthTracker);
		double medianDepth = Dataflow.getMedian(depthTracker);
		double logAvgDepth = Dataflow.getLogAvg(depthTracker);
		int numDepth = depthTracker.size();
		
		
		//public ArrayList<Long> depthWithSnopeTracker = new ArrayList<Long>();
		double avgdepthWithSnope = Dataflow.getAvg(depthWithSnopeTracker);
		double mediandepthWithSnope = Dataflow.getMedian(depthWithSnopeTracker);
		double logAvgdepthWithSnope = Dataflow.getLogAvg(depthWithSnopeTracker);
		int numdepthWithSnope = depthWithSnopeTracker.size();
		
		
		//public ArrayList<Long> depthAuthorIsSnoped = new ArrayList<Long>();
		double avgdepthAuthorIsSnoped = Dataflow.getAvg(depthAuthorIsSnoped);
		double mediandepthAuthorIsSnoped = Dataflow.getMedian(depthAuthorIsSnoped);
		double logAvgdepthAuthorIsSnoped = Dataflow.getLogAvg(depthAuthorIsSnoped);
		int numdepthAuthorIsSnoped = depthAuthorIsSnoped.size();
		
		
		//public ArrayList<Long> commentsA1 = new ArrayList<Long>();
		double avgcommentsA1 = Dataflow.getAvg(commentsA1);
		double mediancommentsA1 = Dataflow.getMedian(commentsA1);
		double logAvgcommentsA1 = Dataflow.getLogAvg(commentsA1);
		int numcommentsA1 = commentsA1.size();
		
		
		//public ArrayList<Long> commentsA2 = new ArrayList<Long>();
		double avgcommentsA2 = Dataflow.getAvg(commentsA2);
		double mediancommentsA2 = Dataflow.getMedian(commentsA2);
		double logAvgcommentsA2 = Dataflow.getLogAvg(commentsA2);
		int numcommentsA2 = commentsA2.size();
		
		
		
		String hold = "" +
				Dataflow.if0ThenNA(avgCommentWindow) + '\t' +
				Dataflow.if0ThenNA(medianCommentWindow) + '\t' +
				Dataflow.if0ThenNA(logAvgCommentWindow) + '\t' +
				numCommentWindow + '\t' +
				Dataflow.if0ThenNA(avgSnopeCommentWindow) + '\t' +
				Dataflow.if0ThenNA(medianSnopeCommentWindow) + '\t' +
				Dataflow.if0ThenNA(logSnopeAvgCommentWindow) + '\t' +
				numSnopeCommentWindow + '\t' +
				Dataflow.if0ThenNA(avgCommentToReply) + '\t' +
				Dataflow.if0ThenNA(medianCommentToReply) + '\t' +
				Dataflow.if0ThenNA(logAvgCommentToReply) + '\t' +
				numCommentToReply + '\t' +
				Dataflow.if0ThenNA(avgsnopeCommentToReply) + '\t' +
				Dataflow.if0ThenNA(mediansnopeCommentToReply) + '\t' +
				Dataflow.if0ThenNA(logAvgsnopeCommentToReply) + '\t' +
				numsnopeCommentToReply + '\t' +
				Dataflow.if0ThenNA(avgreplyToComment) + '\t' +
				Dataflow.if0ThenNA(medianreplyToComment) + '\t' +
				Dataflow.if0ThenNA(logAvgreplyToComment) + '\t' +
				numreplyToComment + '\t' +
				Dataflow.if0ThenNA(avgsnopeReplyToComment) + '\t' +
				Dataflow.if0ThenNA(mediansnopeReplyToComment) + '\t' +
				Dataflow.if0ThenNA(logAvgsnopeReplyToComment) + '\t' +
				numsnopeReplyToComment + '\t' +
				Dataflow.if0ThenNA(avgDepth) + '\t' +
				Dataflow.if0ThenNA(medianDepth) + '\t' +
				Dataflow.if0ThenNA(logAvgDepth) + '\t' +
				numDepth + '\t' +
				Dataflow.if0ThenNA(avgdepthWithSnope) + '\t' +
				Dataflow.if0ThenNA(mediandepthWithSnope) + '\t' +
				Dataflow.if0ThenNA(logAvgdepthWithSnope) + '\t' +
				numdepthWithSnope + '\t' +
				Dataflow.if0ThenNA(avgdepthAuthorIsSnoped) + '\t' +
				Dataflow.if0ThenNA(mediandepthAuthorIsSnoped) + '\t' +
				Dataflow.if0ThenNA(logAvgdepthAuthorIsSnoped) + '\t' +
				numdepthAuthorIsSnoped + '\t' +
				Dataflow.if0ThenNA(avgcommentsA1) + '\t' +
				Dataflow.if0ThenNA(mediancommentsA1) + '\t' +
				Dataflow.if0ThenNA(logAvgcommentsA1) + '\t' +
				numcommentsA1 + '\t' +
				Dataflow.if0ThenNA(avgcommentsA2) + '\t' +
				Dataflow.if0ThenNA(mediancommentsA2) + '\t' +
				Dataflow.if0ThenNA(logAvgcommentsA2) + '\t' +
				numcommentsA2 + '\t'+
				replies + '\t' +
				snopeReplies + '\t' +
				repliesTotallyIgnored + '\t'+
				snopeRepliesTotallyIgnored +/* '\t' +
				"self_comments_at_depth_2" + '\t' +
				"unique_authors_at_depth_2" + '\t' +
				"unique_authors_in_subtree" +*/ '\n';
		return hold;
	
	}
	public static String fileHeader() {
		return 	
				"avg_comment_window" + '\t' +
				"median_comment_window" + '\t' +
				"log_avg_comment_window" + '\t' +
				"num_comment_window" + '\t' +
				"avg_snope_comment_window" + '\t' +
				"median_snope_comment_window" + '\t' +
				"log_avg_snope_comment_window" + '\t' +
				"num_snope_comment_window" + '\t' +
				"avg_comment_to_reply_time" + '\t' +
				"median_comment_to_reply_time" + '\t' +
				"log_avg_comment_to_reply_time" + '\t' +
				"num_comment_to_reply_time" + '\t' +
				"avg_snope_comment_to_reply_time" + '\t' +
				"median_snope_comment_to_reply_time" + '\t' +
				"log_avg_snope_comment_to_reply_time" + '\t' +
				"num_snope_comment_to_reply_time" + '\t' +
				"avg_reply_to_comment_time" + '\t' +
				"median_reply_to_comment_time" + '\t' +
				"log_avg_reply_to_comment_time" + '\t' +
				"num_replyto_comment_time" + '\t' +
				"avg_snope_reply_to_comment_time" + '\t' +
				"median_snope_reply_to_comment_time" + '\t' +
				"log_avg_snope_reply_to_comment_time" + '\t' +
				"num_snope_reply_to_comment_time" + '\t' +
				"avg_depth" + '\t' +
				"median_depth" + '\t' +
				"log_avg_depth" + '\t' +
				"num_depth" + '\t' +
				"avg_depth_with_snope" + '\t' +
				"median_depth_with_snope" + '\t' +
				"log_avg_depth_with_snope" + '\t' +
				"num_depth_with_snope" + '\t' +
				"avg_depth_author_is_snoped" + '\t' +
				"median_depth_author_is_snoped" + '\t' +
				"log_avg_depth_author_is_snoped" + '\t' +
				"num_depth_author_is_snoped" + '\t' +
				"avg_comments_original_author" + '\t' +
				"median_comments_original_author" + '\t' +
				"log_avg_comments_original_author" + '\t' +
				"num_comment_original_author" + '\t' +
				"avg_comments_replying_authors" + '\t' +
				"median_comments_replying_authors" + '\t' +
				"log_avg_comment_replying_authors" + '\t' +
				"num_comment_replying_authors" + '\t' + 
				"first_step_replies" + '\t' +
				"first_step_snopes" + '\t' +
				"first_step_replies_ignored" + '\t' +
				"first_step_snopes_ignored" +/* '\t' +
				"self_comments_at_depth_2" + '\t' +
				"unique_authors_at_depth_2" + '\t' +
				"unique_authors_in_subtree" + */'\n';
				
	
	
	
	}
}
