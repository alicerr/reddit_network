package reddit_network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



public class CommentStringLog {
	
	public HashMap<Long, Comment> comments = new HashMap<Long, Comment>(); 
	public HashMap<Long, Comment> replies = new HashMap<Long, Comment>(); 
	public Author author = null;
	public float average_comment_window = 0;
	public float average_comment_window_without_reply = 0;
	public float average_comment_window_with_reply = 0;
	public float average_time_from_comment_to_first_reply = 0;
	public float average_time_from_first_reply_to_next_comment = 0;
	public int number_of_comment_windows = 0;
	public int number_of_comment_windows_with_reply = 0;
	public int number_of_comment_windows_with_more_than_one_reply = 0;
	public int maximum_replies_in_a_comment_window = 0;
	public int total_replies_in_comment_windows = 0;
	public float average_comment_window_with_snope = 0;
	public float average_time_from_comment_to_first_snope = 0;
	public float average_time_from_first_snope_to_next_comment = 0;
	public int number_of_comment_windows_with_snope = 0;
	public float average_comment_window_in_thread = 0;
	public float average_comment_window_without_reply_in_thread = 0;
	public float average_comment_window_with_reply_in_thread = 0;
	public float average_time_from_comment_to_first_reply_in_thread = 0;
	public float average_time_from_first_reply_to_next_comment_in_thread = 0;
	public int number_of_comment_windows_in_thread = 0;
	public int number_of_comment_windows_with_reply_in_thread = 0;
	public int number_of_comment_windows_with_more_than_one_reply_in_thread = 0;
	public int maximum_replies_in_a_comment_window_in_thread = 0;
	public int total_replies_in_comment_windows_in_thread = 0;
	public float average_comment_window_with_snope_in_thread = 0;
	public float average_time_from_comment_to_first_snope_in_thread = 0;
	public float average_time_from_first_snope_to_next_comment_in_thread = 0;
	public int number_of_comment_windows_with_snope_in_thread = 0;
	public static HashMap<Long, Integer> comment_window = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> comment_window_without_reply = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> comment_window_with_reply = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> time_from_comment_to_first_reply = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> time_from_first_reply_to_next_comment = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> comment_window_with_snope = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> time_from_comment_to_first_snope = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> time_from_first_snope_to_next_comment = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> comment_window_in_thread = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> comment_window_without_reply_in_thread = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> comment_window_with_reply_in_thread = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> time_from_comment_to_first_reply_in_thread = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> time_from_first_reply_to_next_comment_in_thread = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> comment_window_with_snope_in_thread = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> time_from_comment_to_first_snope_in_thread = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> time_from_first_snope_to_next_comment_in_thread = new HashMap<Long, Integer>();

	
	public CommentStringLog(Author author, HashMap<Long, Comment> comments, HashMap<Long, Comment> replies) {
		this.comments = comments;
		this.replies = replies;
		this.author = author;
		compute();
		
	}
	public static void CollectSlow(Network network, String filePath) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath + "/comments_round_1.txt"));
		bw.write(CommentString.fileHeader());
		bw.close();
		//HashMap<String, Triplet> collect = new HashMap<String, Triplet>();
		
		for (Author a : Author.allAuthors.values()){
			if (!a.name.equals("[deleted]") || !a.name.equals("NA")){
				Triplet t = new Triplet(a);
				for (Comment comment : network.network.values()){
					if (comment.getAuthor().equalsIgnoreCase(a.name)){
						t.addComment(comment);
					} 
					if (comment.getParent_author().equalsIgnoreCase(a.name))
						t.addReply(comment);
					} 
				//new File(filePath).mkdirs();
					CommentStringLog s = t.finish();
					bw = new BufferedWriter(new FileWriter(filePath + "/comments_round_1.txt", true));
					bw.append(s.toString());
					bw.close();
					System.out.println("To File: " + s);
				}
		}
		
	}
	public static void CollectAll(Network network, String filePath) throws IOException{
		HashMap<String, Triplet> collect = new HashMap<String, Triplet>();
		
		for (Author a : Author.allAuthors.values()){
			//System.out.println(a);
			collect.put(a.name.toLowerCase(), new Triplet(a));
			//System.out.println(collect.size());
			//System.out.println(a.name);
		}
		int i = 0;
		for (Comment comment : network.network.values()){
			i++;
			if (i % 1000 == 0){
				System.out.println("comments added to commentStrings: " + i);
			}
			try {
				//System.out.println(comment.getAuthor());
				collect.get(comment.getAuthor().toLowerCase()).addComment(comment);
			} catch (Exception e) {
				if (!comment.getAuthor().equals("[deleted]") && !comment.getAuthor().equals("NA")){
					System.out.println(comment.getAuthor());
					e.printStackTrace();
					System.out.println("Could not add comment: " + comment);
				}
			}
			try {
				//System.out.println(comment.getParent_author());
				collect.get(comment.getParent_author().toLowerCase()).addReply(comment);
			} catch (Exception e) {
				if (!comment.getParent_author().equals("[deleted]") && !comment.getParent_author().equals("NA")){
					e.printStackTrace();
					System.out.println(comment.getParent_author());
					System.out.println("Could not add comment: " + comment);
				}
			}
		}
		new File(filePath).mkdirs();
		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath + "/comments_round_1_log.txt"));
		bw.write(CommentStringLog.fileHeader());
		bw.close();
		for (Triplet t :  collect.values()){
			String s = t.finish().toString();
			collect.put(t.author.name.toLowerCase(), null);
			bw = new BufferedWriter(new FileWriter(filePath + "/comments_round_1_log.txt", true));
			bw.append(s);
			bw.close();
			System.out.println("To File: " + s);
		}
		new File(filePath).mkdirs();
		bw = new BufferedWriter(new FileWriter(filePath + "/comments_round_1_median_hour.txt"));
		bw.write(CommentStringLog.medianFileHeader());
		bw.write(CommentStringLog.medianFileData());
		bw.close();
		
	}
	public static String fileHeader() {
		return Author.fileHeader().substring(0, Author.fileHeader().length() - 1) + "\t" + 
				"average_comment_window\t" +
				"average_comment_window_without_reply\t" +
				"average_comment_window_with_reply\t" +
				"average_time_from_comment_to_first_reply\t" +
				"average_time_from_first_reply_to_next_comment\t" +
				"number_of_comment_windows\t" +
				"number_of_comment_windows_with_reply\t" +
				"number_of_comment_windows_with_more_than_one_reply\t" +
				"maximum_replies_in_a_comment_window\t" +
				"average_comment_window_with_snope\t" +
				"average_time_from_comment_to_first_snope\t" +
				"average_time_from_first_snope_to_next_comment\t" +
				"number_of_comment_windows_with_snope\t" +
				"total_replies_in_comment_windows\t" +
				"average_comment_window_in_thread\t" +
				"average_comment_window_without_reply_in_thread\t" +
				"average_comment_window_with_reply_in_thread\t" +
				"average_time_from_comment_to_first_reply_in_thread\t" +
				"average_time_from_first_reply_to_next_comment_in_thread\t" +
				"number_of_comment_windows_in_thread\t" +
				"number_of_comment_windows_with_reply_in_thread\t" +
				"number_of_comment_windows_with_more_than_one_reply_in_thread\t" +
				"average_comment_window_with_snope_in_thread\t" +
				"average_time_from_comment_to_first_snope_in_thread\t" +
				"average_time_from_first_snope_to_next_comment_in_thread\t" +
				"number_of_comment_windows_with_snope_in_thread\t" +
				"total_replies_in_comment_windows_in_thread\n";
	}
	public String toString(){
		return author.toString().substring(0, author.toString().length() - 1) + "\t" + 
				Dataflow.if0ThenNA(average_comment_window) + "\t" +
				Dataflow.if0ThenNA(average_comment_window_without_reply) + "\t" +
				Dataflow.if0ThenNA(average_comment_window_with_reply) + "\t" +
				Dataflow.if0ThenNA(average_time_from_comment_to_first_reply) + "\t" +
				Dataflow.if0ThenNA(average_time_from_first_reply_to_next_comment) + "\t" +
				number_of_comment_windows + "\t" +
				number_of_comment_windows_with_reply + "\t" +
				number_of_comment_windows_with_more_than_one_reply + "\t" +
				maximum_replies_in_a_comment_window + "\t" +
				Dataflow.if0ThenNA(average_comment_window_with_snope) + "\t" +
				Dataflow.if0ThenNA(average_time_from_comment_to_first_snope) + "\t" +
				Dataflow.if0ThenNA(average_time_from_first_snope_to_next_comment) + "\t" +
				number_of_comment_windows_with_snope + "\t" +
				total_replies_in_comment_windows + "\t" +
				Dataflow.if0ThenNA(average_comment_window_in_thread) + "\t" +
				Dataflow.if0ThenNA(average_comment_window_without_reply_in_thread) + "\t" +
				Dataflow.if0ThenNA(average_comment_window_with_reply_in_thread) + "\t" +
				Dataflow.if0ThenNA(average_time_from_comment_to_first_reply_in_thread) + "\t" +
				Dataflow.if0ThenNA(average_time_from_first_reply_to_next_comment_in_thread) + "\t" +
				number_of_comment_windows_in_thread + "\t" +
				number_of_comment_windows_with_reply_in_thread + "\t" +
				number_of_comment_windows_with_more_than_one_reply_in_thread + "\t" +
				Dataflow.if0ThenNA(average_comment_window_with_snope_in_thread) + "\t" +
				Dataflow.if0ThenNA(average_time_from_comment_to_first_snope_in_thread) + "\t" +
				Dataflow.if0ThenNA(average_time_from_first_snope_to_next_comment_in_thread) + "\t" +
				number_of_comment_windows_with_snope_in_thread + "\t" +
				total_replies_in_comment_windows_in_thread + "\n";
	}
		
	public void add(HashMap<Long, Integer> h, long l){
		if (h.containsKey(l)){
			h.put(l, h.get(l) + 1 );
		} else
			h.put(l, 1);
	}
	private void compute(){
		Comment last = null;
		Comment next = null;
		Comment nextReply = null;
		Long[] commentTimes = comments.keySet().toArray(new Long[comments.keySet().size()]);
		Arrays.sort(commentTimes);

		
		Long[] replyTimes = replies.keySet().toArray(new Long[replies.keySet().size()]);
		Arrays.sort(replyTimes);
		int t = replies.keySet().size();
		int f = comments.keySet().size();
		if (commentTimes.length > 0) next = comments.get(commentTimes[0]);
		if (replyTimes.length > 0) 
			nextReply = replies.get(replyTimes[0]);
		/*public long average_comment_window = 0;
		public long average_comment_window_without_reply = 0;
		public long average_comment_window_with_reply = 0;
		public long average_time_from_comment_to_first_reply = 0;
		public long average_time_from_first_reply_to_next_comment = 0;
		public int number_of_comment_windows = 0;
		public int number_of_comment_windows_with_reply = 0;
		public int number_of_comment_windows_with_more_than_one_reply = 0;
		public int maximum_replies_in_a_comment_window = 0;
		public int total_replies_in_comment_windows = 0;*/
		int commentIndex = 0;
		int replyIndex = 0;
		while (next != null){
			last = next;
			next = null;
			int replyCount = 0;
			Comment firstReply = null;
			Comment firstSnope = null;
			boolean hasReply = false;
			commentIndex++;
			boolean wasSnoped = false;
			boolean inThread = false;
			if (commentIndex < commentTimes.length){
				
				next = comments.get(commentTimes[commentIndex]);
				inThread = last.getLink_id() == next.getLink_id();
				long commentWindow = next.getCreated_utc() - last.getCreated_utc();
				long c = 0;
				if (nextReply != null){
					int o = 6+7;
					c = nextReply.getCreated_utc() - next.getCreated_utc();
				}
				
				while (nextReply != null && nextReply.getCreated_utc() < next.getCreated_utc()) {
					
					if (replyCount == 0){
						firstReply = nextReply;
					} 
					replyIndex++;
					if (nextReply.isSnope()){
						firstSnope = firstSnope == null? nextReply : firstSnope;
						wasSnoped = true;
					}
					nextReply = replyTimes.length > replyIndex ? replies.get(replyTimes[replyIndex]) : null;
					replyCount++;
					hasReply = true;
				}
				number_of_comment_windows++;
				average_comment_window += Math.log(commentWindow + 1);
				add(comment_window, commentWindow);
				if (!hasReply) {
					average_comment_window_without_reply += Math.log(commentWindow + 1);
					add(comment_window_without_reply, commentWindow);
				} else {
					
					average_comment_window_with_reply += Math.log(commentWindow + 1);
					
					average_time_from_comment_to_first_reply += Math.log(firstReply.getCreated_utc() - last.getCreated_utc() + 1);
					average_time_from_first_reply_to_next_comment += Math.log(next.getCreated_utc() - firstReply.getCreated_utc() + 1);
					
					add(comment_window_with_reply , commentWindow);
					
					add(time_from_comment_to_first_reply , firstReply.getCreated_utc() - last.getCreated_utc());
					add(time_from_first_reply_to_next_comment , next.getCreated_utc() - firstReply.getCreated_utc());
					
					number_of_comment_windows_with_reply++;
					number_of_comment_windows_with_more_than_one_reply += replyCount > 1 ? 1 : 0;
					maximum_replies_in_a_comment_window = replyCount > maximum_replies_in_a_comment_window? replyCount : maximum_replies_in_a_comment_window;
					total_replies_in_comment_windows += replyCount;
					
					if (wasSnoped){
						average_comment_window_with_snope += Math.log(commentWindow + 1);
						average_time_from_comment_to_first_snope += Math.log(firstSnope.getCreated_utc() - last.getCreated_utc() + 1);
						average_time_from_first_snope_to_next_comment += Math.log(next.getCreated_utc() - firstSnope.getCreated_utc() + 1);
						
						add(comment_window_with_snope, commentWindow);
						add(time_from_comment_to_first_snope, firstSnope.getCreated_utc() - last.getCreated_utc());
						add(time_from_first_snope_to_next_comment, next.getCreated_utc() - firstSnope.getCreated_utc());
						
						number_of_comment_windows_with_snope++;
						
					}

				}
				if (inThread){
					number_of_comment_windows_in_thread++;
					average_comment_window_in_thread += Math.log(commentWindow + 1);
					add(comment_window_in_thread, commentWindow);
					if (!hasReply) {
						average_comment_window_without_reply_in_thread += Math.log(commentWindow + 1);
						add(comment_window_without_reply_in_thread, commentWindow);
					} else {
						
						average_comment_window_with_reply_in_thread += Math.log(commentWindow + 1);
						average_time_from_comment_to_first_reply_in_thread += Math.log(firstReply.getCreated_utc() - last.getCreated_utc() + 1);
						average_time_from_first_reply_to_next_comment_in_thread += Math.log(next.getCreated_utc() - firstReply.getCreated_utc() + 1);
						
						add(comment_window_with_reply_in_thread, commentWindow);
						add(time_from_comment_to_first_reply_in_thread, firstReply.getCreated_utc() - last.getCreated_utc());
						add(time_from_first_reply_to_next_comment_in_thread, next.getCreated_utc() - firstReply.getCreated_utc());
						
						
						number_of_comment_windows_with_reply_in_thread++;
						number_of_comment_windows_with_more_than_one_reply_in_thread += replyCount > 1 ? 1 : 0;
						total_replies_in_comment_windows_in_thread += replyCount;
						
						if (wasSnoped){
							average_comment_window_with_snope_in_thread += Math.log(commentWindow + 1);
							average_time_from_comment_to_first_snope_in_thread += Math.log(firstSnope.getCreated_utc() - last.getCreated_utc() + 1);
							average_time_from_first_snope_to_next_comment_in_thread += Math.log(next.getCreated_utc() - firstSnope.getCreated_utc() + 1);
							add(comment_window_with_snope_in_thread, commentWindow);
							add(time_from_comment_to_first_snope_in_thread, firstSnope.getCreated_utc() - last.getCreated_utc());
							add(time_from_first_snope_to_next_comment_in_thread, next.getCreated_utc() - firstSnope.getCreated_utc());
							
							number_of_comment_windows_with_snope_in_thread++;
							
						}
	
					}
				}
				
			}
			
		}
		average_comment_window = number_of_comment_windows == 0 ? 0 : average_comment_window/number_of_comment_windows;
		average_comment_window_without_reply = number_of_comment_windows - number_of_comment_windows_with_reply == 0 ? 0 : average_comment_window_without_reply/(number_of_comment_windows - number_of_comment_windows_with_reply);
		average_comment_window_with_reply = number_of_comment_windows_with_reply == 0 ? 0 : average_comment_window_with_reply/number_of_comment_windows_with_reply;
		average_time_from_comment_to_first_reply = number_of_comment_windows_with_reply == 0 ? 0 : average_time_from_comment_to_first_reply/number_of_comment_windows_with_reply;
		average_time_from_first_reply_to_next_comment = number_of_comment_windows_with_reply == 0 ? 0 :  average_time_from_first_reply_to_next_comment/number_of_comment_windows_with_reply;
		average_comment_window_with_snope = number_of_comment_windows_with_snope == 0 ? 0 : average_comment_window_with_snope/number_of_comment_windows_with_snope;
		average_time_from_comment_to_first_snope = number_of_comment_windows_with_snope == 0 ? 0 : average_time_from_comment_to_first_snope/number_of_comment_windows_with_snope;
		average_time_from_first_snope_to_next_comment = number_of_comment_windows_with_snope == 0 ? 0 : average_time_from_first_snope_to_next_comment/number_of_comment_windows_with_snope;
		
		average_comment_window_in_thread = number_of_comment_windows_in_thread == 0 ? 0 : 
			average_comment_window_in_thread/number_of_comment_windows_in_thread;
		average_comment_window_without_reply_in_thread = 
				number_of_comment_windows_in_thread - number_of_comment_windows_with_reply_in_thread == 0 ? 0 : 
					average_comment_window_without_reply_in_thread/(number_of_comment_windows_in_thread - number_of_comment_windows_with_reply_in_thread);
		average_comment_window_with_reply_in_thread = number_of_comment_windows_with_reply_in_thread == 0 ? 0 : average_comment_window_with_reply_in_thread/number_of_comment_windows_with_reply_in_thread;
		average_time_from_comment_to_first_reply_in_thread = number_of_comment_windows_with_reply_in_thread == 0 ? 0 : average_time_from_comment_to_first_reply_in_thread/number_of_comment_windows_with_reply_in_thread;
		average_time_from_first_reply_to_next_comment_in_thread = number_of_comment_windows_with_reply_in_thread == 0 ? 0 :  average_time_from_first_reply_to_next_comment_in_thread/number_of_comment_windows_with_reply_in_thread;
		average_comment_window_with_snope_in_thread = number_of_comment_windows_with_snope_in_thread == 0 ? 0 : average_comment_window_with_snope_in_thread/number_of_comment_windows_with_snope_in_thread;
		average_time_from_comment_to_first_snope_in_thread = number_of_comment_windows_with_snope_in_thread == 0 ? 0 : average_time_from_comment_to_first_snope_in_thread/number_of_comment_windows_with_snope_in_thread;
		average_time_from_first_snope_to_next_comment_in_thread = number_of_comment_windows_with_snope_in_thread == 0 ? 0 : average_time_from_first_snope_to_next_comment_in_thread/number_of_comment_windows_with_snope_in_thread;
	}
	
	public static String medianFileHeader(){
		return "median_comment_window\t" +
				"median_comment_window_without_reply\t" +
				"median_comment_window_with_reply\t" +
				"median_time_from_comment_to_first_reply\t" +
				"median_time_from_first_reply_to_next_comment\t" +
				"median_comment_window_with_snope\t" +
				"median_time_from_comment_to_first_snope\t" +
				"median_time_from_first_snope_to_next_comment\t" +
				"median_comment_window_in_thread\t" +
				"median_comment_window_without_reply\t" +
				"median_comment_window_with_reply_in_thread\t" +
				"median_time_from_comment_to_first_reply_in_thread\t" +
				"median_time_from_first_reply_to_next_comment_in_thread\t" +
				"median_comment_window_with_snope_in_thread\t" +
				"median_time_from_comment_to_first_snope_in_thread\t" +
				"median_time_from_first_snope_to_next_comment_in_thread\n";
	}
	public static long medianR(HashMap<Long, Integer> h){
		long max = 0;
		int maxval = 0;
		for (Map.Entry<Long, Integer> e :h.entrySet()){
			if (e.getValue().compareTo(maxval) > 0){
				maxval = e.getValue();
				max = e.getKey();
			}
		}
		return max;
	}
	
	public static long median(HashMap<Long, Integer> h){
		HashMap<Integer, Integer> h2 = new HashMap<Integer, Integer>();
		
		long max = 0;
		int maxval = 0;
		for (Map.Entry<Long, Integer> e :h.entrySet()){
			int s = (int) ((e.getKey() + 59*60 + 59)/60/60);
			if (h2.containsKey(s)) {
				h2.put(s, h2.get(s) + e.getValue());
			} else {
				h2.put(s, e.getValue());
			}
		}
		for (Map.Entry<Integer, Integer> e :h2.entrySet()){
			if (e.getValue().compareTo(maxval) > 0){
				maxval = e.getValue();
				max = e.getKey();
			}
		}
		return max;
	}
	
	
	public static String medianFileData(){
		return median(comment_window) +"\t" +
				median(comment_window_without_reply)+"\t" +
				median(comment_window_with_reply)+"\t" +
				median(time_from_comment_to_first_reply)+"\t" +
				median(time_from_first_reply_to_next_comment)+"\t" +
				median(comment_window_with_snope)+"\t" +
				median(time_from_comment_to_first_snope)+"\t" +
				median(time_from_first_snope_to_next_comment)+"\t" +
				median(comment_window_in_thread)+"\t" +
				median(comment_window_without_reply)+"\t" +
				median(comment_window_with_reply_in_thread)+"\t" +
				median(time_from_comment_to_first_reply_in_thread)+"\t" +
				median(time_from_first_reply_to_next_comment_in_thread)+"\t" +
				median(comment_window_with_snope_in_thread)+"\t" +
				median(time_from_comment_to_first_snope_in_thread)+"\t" +
				median(time_from_first_snope_to_next_comment_in_thread)+"\n";
	}
}

