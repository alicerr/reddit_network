package reddit_network;

import java.util.HashMap;
import java.util.regex.Matcher;

import com.google.gson.JsonObject;

public class karmaComment implements Comparable<karmaComment>  {
	private CommentFetcher owner;
	public String json;
	public int ups;

	public Integer score;

	public String body;
	public boolean hasUrl;
	public boolean wasSnoped = false;
	public boolean isSnopeResponse = false;
	public Boolean hasSnopeAunt;
	public String qt;
	public String body_no_qt;
	

	public String text;
	public Comment comment;
	private Float upvote_ratio;
	public karmaComment(CommentFetcher commentFetcher, JsonObject data,
			Comment comment) {
		this.comment = comment;
		this.owner = commentFetcher;
		data.remove("replies");
		if (comment.snope_id.equals(""))
		{
			System.out.println("now");
		}
		this.json = data.toString();
		this.ups = data.get("ups").getAsInt();
		
		
		this.body = Dataflow.sanitize(data.get("body").getAsString());
		this.hasUrl = Dataflow.hasUrl(body);
		this.score = data.get("score").getAsInt();
		
		this.body_no_qt = data.get("body").getAsString();
		this.qt = "";
		
		
		Matcher m = Dataflow.qt(data.get("body").getAsString());
		while (m != null && m.find()){
			String s = m.group(1);
			body_no_qt = body_no_qt.replace(s.trim(), " ").replace("&gt;", "");
			qt += " " + s;
		}
		qt = Dataflow.sanitize(qt);
		body_no_qt = Dataflow.sanitize(body_no_qt);
	}

	public karmaComment(CommentFetcher commentFetcher, JsonObject data,
			Comment c, boolean b) {
		this.comment = c;
		this.owner = commentFetcher;
		this.json = data.toString();
		this.ups = data.get("ups").getAsInt();
		this.upvote_ratio = data.get("upvote_ratio").getAsFloat();
		this.hasUrl = !data.get("is_self").getAsBoolean();
		this.body = Dataflow.sanitize(data.get("url").getAsString() + " " + data.get("title").getAsString() + " " + data.get("selftext").getAsString());
		this.body_no_qt = data.get("url").getAsString() + " " + data.get("title").getAsString() + " " + data.get("selftext").getAsString();
		this.qt = "";
		
		
		Matcher m = Dataflow.qt(data.get("url").getAsString() + " " + data.get("title").getAsString() + " " + data.get("selftext").getAsString());
		while (m != null && m.find()){
			String s = m.group(1);
			body_no_qt = body_no_qt.replace(s, " ");
			qt += " " + s;
		}
		qt = Dataflow.sanitize(qt);
		body_no_qt = Dataflow.sanitize(body_no_qt);
		
	}
		

	public static String fileHeader(){
		return  Comment.tableHeader().substring(0, Comment.tableHeader().length() - 1) + '\t' +
				"is_snoped"    + '\t' +
				"status" + '\t' +
				"type" + "\t" +
				"has_snope_sibling" + '\t' +
				"has_snope_aunt" + '\t' +
				"is_snope_response" + '\t' +
				"has_url"      + '\t' +
				"body"         + '\t' +
				"quote" + '\t' +
				"body_no_quote" + '\t' +
				"upvote_ratio_for_links" + '\t' +
				"ups"          + '\t' +
				"score_for_comments"        + '\t'
				+"snope_id\n";
				
	}
	
	public String toString(){
		return  comment.toString().substring(0, comment.toString().length() - 1) + '\t' +
				comment.wasSnoped         + '\t' +
				Dataflow.checkNull(comment.stat) + '\t' +
				Dataflow.checkNull(comment.type) + "\t" +
				Dataflow.checkNull(comment.sister_to_snope) + '\t' +
				Dataflow.checkNull(comment.has_snope_aunt) + '\t' +
				Dataflow.checkNull(comment.is_snope_response) + '\t' +
				hasUrl            + '\t' +
				body              + '\t' +
				Dataflow.checkNull(qt) + '\t' +
				Dataflow.checkNull(body_no_qt) + '\t' +
				Dataflow.checkNull(upvote_ratio) + '\t' + 
				ups               + '\t' +
				Dataflow.checkNull(score)     + '\t' +
				comment.snope_id + '\n';
	}
	
	
	public int compareTo(karmaComment other){
		if (Long.compare(this.comment.getId(), other.comment.getId()) == 0) return 0;
		if (this.comment.getCreated_utc() != other.comment.getCreated_utc()) 
			return Long.compare(this.comment.getCreated_utc(), other.comment.getCreated_utc());
		return -1;
	}
	@Override
	public int hashCode(){
		return Long.valueOf(comment.getId()).hashCode();
	}
	
	public static String urlReducer(String url,Comment comment){
		url = url.replace(".json", "");
		int pos = url.lastIndexOf("?");
		String x = null;
		if (pos > -1) x = url.substring(0, pos) + ".json";
		else x = url + ".json";
		if (comment == null) return x;
		String y = Long.toString(comment.getParent_id(), 36);
		String z = Long.toString(comment.getId(), 36);
		if (comment.getParent_id() > 0 && x.indexOf(z) >= 0){
			
			if (comment.getParent_id() != comment.getLink_id())
				x = x.replace(z, y);
			
		}
		return x;
	}
	
	
}
