package reddit_network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class CommentHistory {
	
	
	private ArrayList<Comment> dyadicActivityInThreadBeforeForward = new ArrayList<Comment>();
	private ArrayList<Comment> dyadicActivityInThreadAfterForward = new ArrayList<Comment>();
	private ArrayList<Comment> dyadicActivityBeforeForward = new ArrayList<Comment>();
	private ArrayList<Comment> dyadicActivityAfterForward = new ArrayList<Comment>();
	private ArrayList<Comment> dyadicActivityInThreadBeforeRev = new ArrayList<Comment>();
	private ArrayList<Comment> dyadicActivityInThreadAfterRev = new ArrayList<Comment>();
	private ArrayList<Comment> dyadicActivityBeforeRev = new ArrayList<Comment>();
	private ArrayList<Comment> dyadicActivityAfterRev = new ArrayList<Comment>();
	private ArrayList<Comment> parentActivityBefore = new ArrayList<Comment>();
	private ArrayList<Comment> parentActivityAfter = new ArrayList<Comment>();
	private ArrayList<Comment> replyAuthorActivityBefore = new ArrayList<Comment>();
	private ArrayList<Comment> replyAuthorActivityAfter = new ArrayList<Comment>();
	private ArrayList<Comment> parentActivityInThreadBefore = new ArrayList<Comment>();
	private ArrayList<Comment> parentActivityInThreadAfter = new ArrayList<Comment>();
	private ArrayList<Comment> replyActivityInThreadBefore = new ArrayList<Comment>();
	private ArrayList<Comment> replyActivityInThreadAfter = new ArrayList<Comment>();
	private String filepath;
	private Comment comment;
	
	
	public CommentHistory(Network network, Comment comment, String filePath) throws IOException {
		this.comment = comment;
		System.out.println(comment.getAuthor());
		//long minDate = comment.getCreated_utc() - Network.window;
		//long maxDate = comment.getCreated_utc() + Network.window;
		Iterator<Comment> threadIterator = network.getNetwork().values().iterator();
		int i = 0;
		while (threadIterator.hasNext()) {
			
			Comment nextComment = threadIterator.next();
			if (Network.isInTimeRange(nextComment)){
				if (nextComment.getAuthor().equals(comment.getParent_author())) {
					//System.out.println("parent");
					addBeforeAfter( 
							parentActivityBefore, 
							parentActivityAfter, 
							nextComment);
					boolean isDyadicReverse = nextComment.getParent_author().equals(comment.getAuthor());
					if (isDyadicReverse) 
						addBeforeAfter(
								dyadicActivityBeforeRev,
								dyadicActivityAfterRev,
								nextComment
								);
					if (comment.getLink_id() == nextComment.getLink_id()) {
						addBeforeAfter(
								parentActivityInThreadBefore,
								parentActivityInThreadAfter,
								nextComment
								);
						if (isDyadicReverse) 
							addBeforeAfter(
									dyadicActivityInThreadBeforeRev,
									dyadicActivityInThreadAfterRev,
									nextComment
									);
						}
					}
				else if (nextComment.getAuthor().equals(comment.getAuthor())) {
					addBeforeAfter(
							replyAuthorActivityBefore,
							replyAuthorActivityAfter,
							nextComment
							);
					
					boolean isDyadicForward = nextComment.getParent_author().equals(comment.getParent_author());
					if (isDyadicForward) 
						addBeforeAfter(
								dyadicActivityBeforeForward,
								dyadicActivityAfterForward,
								nextComment
								);
					if (comment.getLink_id() == nextComment.getLink_id()) {
						//System.out.println(comment.getLink_id() + comment.getUrl());
						//System.out.println(nextComment.getLink_id() + nextComment.getUrl());
						addBeforeAfter(
								replyActivityInThreadBefore,
								replyActivityInThreadAfter,
								nextComment
								);
						if (isDyadicForward) 
							addBeforeAfter(
									dyadicActivityInThreadBeforeForward,
									dyadicActivityInThreadAfterForward,
									nextComment
									);
						}
					
				}
			}
			}//end comment while loop
		
		this.filepath = filePath + File.separator + "comment_id_"  + comment.getId() + (comment.isSnope() ? "_snope" : "");
		new File(this.filepath).mkdir();
		commentListToFile("dyadicActivityInThreadBeforeForward", dyadicActivityInThreadBeforeForward);
		commentListToFile("dyadicActivityInThreadAfterForward", dyadicActivityInThreadAfterForward);
		commentListToFile("dyadicActivityBeforeForward", dyadicActivityBeforeForward);
		commentListToFile("dyadicActivityAfterForward", dyadicActivityAfterForward);
		commentListToFile("dyadicActivityInThreadBeforeRev", dyadicActivityInThreadBeforeRev);
		commentListToFile("dyadicActivityInThreadAfterRev", dyadicActivityInThreadAfterRev);
		commentListToFile("dyadicActivityBeforeRev", dyadicActivityBeforeRev);
		commentListToFile("dyadicActivityAfterRev", dyadicActivityAfterRev);
		commentListToFile("parentActivityBefore", parentActivityBefore);
		commentListToFile("parentActivityAfter", parentActivityAfter);
		commentListToFile("replyAuthorActivityBefore", replyAuthorActivityBefore);
		commentListToFile("replyAuthorActivityAfter", replyAuthorActivityAfter);
		commentListToFile("parentActivityInThreadBefore", parentActivityInThreadBefore);
		commentListToFile("parentActivityInThreadAfter", parentActivityInThreadAfter);
		commentListToFile("replyActivityInThreadBefore", replyActivityInThreadBefore);
		commentListToFile("replyActivityInThreadAfter", replyActivityInThreadAfter);
		System.out.println("sub files commited");
		BufferedWriter br = new BufferedWriter(new FileWriter(filepath + "/comment.txt"));
		br.write(Comment.tableHeader());
		br.write(comment.toString());
		br.close();
		br = new BufferedWriter(new FileWriter(filepath + "/comment_stats.txt"));
		br.write(fileHeader());
		br.write(toString());
		br.close();
		//System.out.println("left comment history");
	}
	private void addBeforeAfter(
						        ArrayList<Comment> addToIfBefore, 
						        ArrayList<Comment> addToIfAfter,
						        Comment commentToAdd) {
		if (comment.getCreated_utc() > commentToAdd.getCreated_utc()) 
			addToIfBefore.add(commentToAdd);
		else if (comment.getCreated_utc() < commentToAdd.getCreated_utc()) 
			addToIfAfter.add(commentToAdd);
	}
	private void commentListToFile(String fileName, ArrayList<Comment> commentList) throws IOException {
	
		//System.out.println("comment list to file");
		Iterator<Comment> comments = commentList.iterator();
		int c = 0;
		String fileText = "time_from_original_comment\t" + (Comment.tableHeader());
		while (comments.hasNext()) {
			//System.out.println(comment.getCreated_utc());
			c++;
			if (c %10000 == 0) {
				//System.out.println("comments commited to file" + c);
			}
			Comment comment = comments.next();
			//System.out.println(comment.getCreated_utc());
			//System.out.println((comment.getCreated_utc() - this.comment.getCreated_utc()));
			//System.out.println("ADDED: " + comment);
			long time = (comment.getCreated_utc() - this.comment.getCreated_utc());
			//System.out.println(time);
			String row = Long.toString(time) + "\t" + comment.toString();
			//System.out.println("row; " + row);
			fileText += row;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(this.filepath + File.separator + fileName +".txt"));
		bw.write(fileText);
		bw.close();
		//System.out.println("comments closed");
	}
	public static String fileHeader() {
		return "comment_id\t" +
			   "is_snope\t" +
			   "author\t" +
			   "parent_author\t" +
			   "cooment_created_utc\t" +
			   "link_id\t" +
			   "direct_replies\t" +
			   "url\t" +
			   "has_url_in_body\t" +
			   "dyadic_activity_in_thread_before_forward\t" +
			   "dyadic_activity_in_thread_after_forward\t" +
			   "dyadic_activity_before_forward\t"+ 
			   "dyadic_activity_after_forward\t" +
			   "dyadic_activity_in_thread_before_rev\t" +
			   "dyadic_activity_in_thread_after_rev\t" +
			   "dyadic_activity_before_rev\t"+ 
			   "dyadic_activity_after_rev\t" +
			   "parent_activity_before\t" +
			   "parent_activity_after\t" +
			   "reply_author_activity_before\t" +
			   "reply_author_activity_after\t" +
			   "parent_activity_in_thread_before\t" +
			   "parent_activity_in_thread_after\t" +
			   "reply_activity_in_thread_before\t" +
			   "reply_activity_in_thread_after\n";
	}
	public String toString() {
		return comment.getId() + "\t" +
			   comment.isSnope() + "\t" +
			   this.comment.getAuthor() + "\t" +
			   this.comment.getParent_author() + "\t" +
			   this.comment.getCreated_utc() + "\t" +
			   this.comment.getLink_id() + "\t" +
			   this.comment.getReplies() + "\t" +
			   this.comment.getUrl() + "\t" +
			   this.comment.hasUrl() + "\t" +
			   dyadicActivityInThreadBeforeForward.size() + "\t" +
			   dyadicActivityInThreadAfterForward.size() + "\t" +
			   dyadicActivityBeforeForward.size() + "\t" +
			   dyadicActivityAfterForward.size() + "\t" +
			   dyadicActivityInThreadBeforeRev.size() + "\t" +
			   dyadicActivityInThreadAfterRev.size() + "\t" +
			   dyadicActivityBeforeRev.size() + "\t" +
			   dyadicActivityAfterRev.size() + "\t" +
			   parentActivityBefore.size() + "\t" +
			   parentActivityAfter.size() + "\t" +
			   replyAuthorActivityBefore.size() + "\t" +
			   replyAuthorActivityAfter.size() + "\t" +
			   parentActivityInThreadBefore.size() + "\t" +
			   parentActivityInThreadAfter.size() + "\t" +
			   replyActivityInThreadBefore.size() + "\t" +
			   replyActivityInThreadAfter.size() + "\n";
	}

}
