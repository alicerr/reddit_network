package reddit_network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Author {
	public String name;
	public long createdUtc = 0;
	public int linkKarma = 0;
	public int commentKarma = 0;
	public long id = 0;
	public int comments = 0;
	public int replies = 0;
	public int snopes = 0;
	public int snoped = 0;
	public boolean isPrivate = false;
	public ArrayList<Long> commentTimes = new ArrayList<Long>();
	public ArrayList<Long> replyTimes = new ArrayList<Long>();
	public ArrayList<Long> snopeTimes = new ArrayList<Long>();
	public ArrayList<Long> snopedTimes = new ArrayList<Long>();
	public double average_time_before_snopes = 0;
	public double average_time_after_snopes = 0;
	public double average_time_before_comments = 0;
	public double average_time_after_comments = 0;
	public Author(String name){
		this.name = name;
	}
	public static HashMap<String, Author> allAuthors = new HashMap<String, Author>();
	
	
	public static void addComment(Comment comment){
		if (Network.isInTimeRange(comment)) {
			addCommentMade(comment);
			addReplyTo(comment);
		}
		
	}

	public static void addfromfile(String filename) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = br.readLine();
		int i = 0;
		while ((line = br.readLine()) != null) {
			i++;
			if (i % 10000 == 0){
				System.out.println("authors added: " + i);
			}
			String[] attribs = line.split("\t");
			Author a = new Author(attribs[0]);
			a.createdUtc = Long.parseLong(attribs[1]);
			a.linkKarma = Integer.parseInt(attribs[2]);
			a.commentKarma = Integer.parseInt(attribs[3]);
			a.id = Long.parseLong(attribs[4]);
			a.comments = Integer.parseInt(attribs[5]);
			a.replies = Integer.parseInt(attribs[6]);
			a.snopes = Integer.parseInt(attribs[7]);
			a.snoped = Integer.parseInt(attribs[8]);
			a.average_time_before_snopes = Double.parseDouble(attribs[9]);
			a.average_time_after_snopes = Double.parseDouble(attribs[10]);
			a.average_time_before_comments = Double.parseDouble(attribs[11]);
			a.average_time_after_comments = Double.parseDouble(attribs[12]);
			a.isPrivate = Boolean.parseBoolean(attribs[13]);
			allAuthors.put(attribs[0], a);
		}
		br.close();
	}
	public static void clear(){
		allAuthors = new HashMap<String, Author>();
	}
	public static void addCommentMade(Comment comment){
		String author = comment.getAuthor();
		if (!author.equals("NA") && !author.equals("[deleted]")) {
			try {
				Author a = allAuthors.get(author);
				if (!a.isPrivate) a.addCommentMadeToExistingAuthor(comment);
			} catch(Exception e) {
				Author a = new Author(author);
				try{
					
					
					a.addCommentMadeToExistingAuthor(comment);
					a.populateFromInternet();
					allAuthors.put(author, a);
				} catch (Exception e2) {
					allAuthors.put(author,  a);
				}
				
			}
		}
	}
	public static void addReplyTo(Comment comment){
		String author = comment.getParent_author();
		if (!author.equals("NA") && !author.equals("[deleted]")) {
			try {
				Author a = allAuthors.get(author);
				if (!a.isPrivate) a.addReplyMadeToExistingAuthor(comment);
			} catch(Exception e) {
				Author a = new Author(author);
				try{
					
					a.addReplyMadeToExistingAuthor(comment);
					a.populateFromInternet();
					allAuthors.put(author, a);
					//System.out.println("added reply author: " + a.replies);
				} catch (Exception e2) {
					allAuthors.put(author , a);
				}
				
			}
		}
	}
	private void addReplyMadeToExistingAuthor(Comment comment) {
		replies++;
		snoped += (comment.isSnope() ? 1 : 0);
		if (comment.isSnope()) {
			snopedTimes.add(comment.getCreated_utc());
		} else {
			replyTimes.add(comment.getCreated_utc());
		}
	
		
	}

	private void addCommentMadeToExistingAuthor(Comment comment) {
		comments++;
		
		snopes += (comment.isSnope() ? 1 : 0);
		if (comment.isSnope()){
			snopeTimes.add(comment.getCreated_utc());
		} else {
			commentTimes.add(comment.getCreated_utc());
		}
	}
	private void populateFromInternet() throws Exception{
		String url = "http://www.reddit.com/user/" + this.name + "/about.json";
		String jsonText;
		try {
			jsonText = UrlReader.read(url);
		
		JsonObject authorData = new JsonParser().parse(jsonText).getAsJsonObject().get("data").getAsJsonObject();
		linkKarma = Integer.parseInt(authorData.get("link_karma").getAsString());
		commentKarma = Integer.parseInt(authorData.get("comment_karma").getAsString());
		createdUtc = (long) Float.parseFloat(authorData.get("created_utc").toString());
		id = Long.parseLong(authorData.get("id").getAsString().replace("\"",  ""), 36);
		} catch (Exception e) {
			
			//e.printStackTrace();
			System.out.println(url);
			this.isPrivate = true;
			//throw new Exception();
		}
		
	}
	private double[] activityAroundComment(ArrayList<Long> commentTimes, long created){
		double before = 0;
		double after = 0;
		try {
	
			int commentsBefore = 0;
			int commentsAfter = 0;
			for(Long commentTime : commentTimes) {
				if (created < commentTime) {
					commentsBefore++;
				} else if (created > commentTime) {
					commentsAfter++;
				}
			}
			long earliest = Network.minDate < createdUtc ? createdUtc : Network.minDate;
			if (commentsBefore > 0) before = (double)(created - earliest) / commentsBefore;
			if (commentsAfter > 0) after = (double) (Network.maxDate - created)/ commentsAfter;
			
		} catch (Exception e){
			
		}
		
		return new double[] {before, after};
	}
	public String toString() {
		double averageTimeBeforeSnopes = 0;
		double averageTimeAfterSnopes = 0;
		double averageTimeBeforeComments = 0;
		double averageTimeAfterComments = 0;
		double averageTimeAfterReplies = 0;
		double averageTimeBeforeReplies = 0;
		double averageTimeAfterSnopeds = 0;
		double averageTimeBeforeSnopeds = 0;
	   // double averageCommentRateBeforeSnopes = 0;
		//double avergeCommentRateAfterSnopes = 0;
		//double avergeCommentRateBeforeComments = 0; 
		//double avergeCommentRateAfterComments = 0;
		int afterReplies = 0;
		int beforeReplies = 0;
		int beforeSnopeds = 0;
		int afterSnopeds = 0;
		int beforeSnopes = 0;
		int afterSnopes = 0;
		int beforeComments = 0;
		int afterComments = 0;
		ArrayList<Long> allComments = new ArrayList<Long>();
		allComments.addAll(commentTimes);
		allComments.addAll(snopeTimes);
		for (Long time : commentTimes){
			double[] avg = activityAroundComment(allComments, time);
			if (avg[0] > 0) {
				averageTimeBeforeComments += avg[0];
				beforeComments++;
			}
			if (avg[1] > 0) {
				averageTimeAfterComments += avg[1];
				afterComments++;
			}
		}
		if (beforeComments > 1) averageTimeBeforeComments = averageTimeBeforeComments / (double) beforeComments;
		if (afterComments > 1) averageTimeAfterComments = averageTimeAfterComments / (double) afterComments;
		//if (beforeComments > 0) averageCommentRateBeforeComments = 
		for (Long time : replyTimes){ 
			double[] avg = activityAroundComment(allComments, time);
			if (avg[0] > 0) {
				averageTimeBeforeReplies += avg[0];
				beforeReplies++;
			}
			if (avg[1] > 0) {
				averageTimeAfterReplies += avg[1];
				afterReplies++;
			}
		}
		if (beforeReplies > 1) averageTimeBeforeReplies = averageTimeBeforeReplies / (double) beforeReplies;
		if (afterComments > 1) averageTimeAfterReplies = averageTimeAfterReplies / (double) afterReplies;
		
		for (Long time : snopedTimes){
			double[] avg = activityAroundComment(allComments, time);
			if (avg[0] > 0) {
				averageTimeBeforeSnopeds += avg[0];
				beforeSnopeds++;
			}
			if (avg[1] > 0) {
				averageTimeAfterSnopeds += avg[1];
				afterSnopeds++;
			}
		}
		if (beforeSnopeds > 1) averageTimeBeforeSnopeds = averageTimeBeforeSnopeds / (double) beforeSnopeds;
		if (afterSnopeds > 1) averageTimeAfterSnopeds = averageTimeAfterSnopeds / (double) afterSnopeds;
		
		for (Long time : snopeTimes){
			double[] avg = activityAroundComment(allComments, time);
			if (avg[0] > 0) {
				averageTimeBeforeSnopes += avg[0];
				beforeComments++;
			}
			if (avg[1] > 0) {
				averageTimeAfterSnopes += avg[1];
				afterComments++;
			}
		}		
		if (beforeSnopes > 1) averageTimeBeforeSnopes = averageTimeBeforeSnopes / (double) beforeSnopes;
		if (afterSnopes > 1) averageTimeAfterSnopes = averageTimeAfterSnopes / (double) afterSnopes;
		//System.out.println(toString());
		String hold = name + "\t"
				+ createdUtc + "\t"
				+ linkKarma + "\t"
				+ commentKarma + "\t"
				+ id + "\t"
				+ comments + "\t"
				+ replies + "\t"
				+ snopes + "\t"
				+ snoped + "\t"
				+ Math.round(averageTimeBeforeSnopes) + "\t"
				+ Math.round(averageTimeAfterSnopes) + "\t"
				+ Math.round(averageTimeBeforeComments) + "\t"
				+ Math.round(averageTimeAfterComments) + "\t"
				+ isPrivate + "\n";
		//System.out.println(hold);
		return hold;
	}

	public static String fileHeader() {
		return "name" + "\t"
				+ "createdUtc" + "\t"
				+ "linkKarma" + "\t"
				+ "commentKarma" + "\t"
				+ "id" + "\t"
				+ "comments" + "\t"
				+ "replies" + "\t"
				+ "snopes" + "\t"
				+ "snoped" + "\t"
				+ "average_time_before_snopes" + "\t"
				+ "average_time_after_snopes" + "\t"
				+ "average_time_before_comments" + "\t"
				+ "average_time_after_comments" + "\t"
				//+ "average_comment_rate_before_snopes" + "\t"
				//+ "averge_comment_rate_after_snopes" + "\t"
				//+ "averge_comment_rate_before_comments" + "\t" 
				//+ "averge_comment_rate_after_comments" + "\t"
				+ "is_private_account" + "\n";
	}
	public static void toFile(String filebase) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filebase + "/authors2.txt"));
		System.out.println(bw.toString() + filebase + "/authors2.txt");
		
		bw.write(fileHeader());
		int i = 0;
		for (Author author : allAuthors.values()) {
			if (author != null) bw.write(author.toString());
			i++;
			if (i % 500 == 0 ) System.out.println("Authors to file:  + i");
		}
		bw.close();
	}
	
}
