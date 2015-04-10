package reddit_network;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;

public class Comment implements Comparable<Comment>{
	private static String[] snope_criteria =
		{"snopes.com", "politifact.com", "factcheck.org"};
	private static SimpleDateFormat dateFormater = new SimpleDateFormat("dd/mm/yyyy");
	public Boolean is_snope_response;
	
	private String url, 
		author; 
	private long parent_id; 
	//private String  body; 
	private long link_id, 
		id; 
	public int wasSnoped = 0;
	public Boolean sister_to_snope;
	private long created_utc;  
	//private String link_title;  
	private int replies; 
	//private boolean edited; 
	//private boolean gilded; 
	//private int downs; 
	//private int ups;
	private String parent_author = "NA";
	public static final Pattern urlRegexPattern = 
			Pattern.compile(".*(http|ftp|www|\\[.+\\]\\w?\\(.+\\)).*");
	private boolean hasUrl;
	private boolean isSnope = false;
	public Boolean has_snope_aunt;
	public String stat;
	public String type = null;
	public String snope_id = "";
	public Comment(String row) throws Exception{
		//System.out.println(row);
		String[] attribs = row.split("\t");
		//for (int i = 0; i < attribs.length; i++){
			//System.out.println(i + ": " + attribs[i]);
		//}
		try{
			//System.out.println(attribs[5]);
			url = attribs[1].replace("\\", "").replace(" ", "");
			author = attribs[2].replace("\"", "");
			parent_id = idAsLong(attribs[3]);
			//body = attribs[4];
			link_id = idAsLong(attribs[5]);
			id = idAsLong(attribs[6]);
			created_utc = Long.parseLong(attribs[8]);
			//link_title = attribs[13];
			//replies = attribs[17] == null? 0 : Integer.parseInt(attribs[17]);
			//edited = attribs[19] getBool(attribs[19]);
			//downs = Integer.parseInt(attribs[20]);
			//ups = Integer.parseInt(attribs[21]);
			hasUrl = urlRegexPattern.matcher(attribs[4]).matches();
			//System.out.println(parent_id);
			//System.out.println(id);
			for (String crit : snope_criteria){
				isSnope = isSnope || (attribs[4].toLowerCase().indexOf(crit) >= 0);
			}
		
		}
		catch (Exception e){
			e.printStackTrace();
		
			throw new Exception("could not read line as comment");
		}
	//System.out.println(this);	
	}
	public Comment(String row, int overide) {
		String[] attribs = row.split("\t");
		this.id = Long.parseLong(attribs[0]);
		this.url = attribs[1].replace(" ", "");
		this.author = attribs[2].replace("\"", "");
		
		//System.out.println(this.author);
		this.parent_id = Long.parseLong(attribs[3]);
		this.parent_author = attribs[4];
		this.link_id = Long.parseLong(attribs[5]);
		this.created_utc = Long.parseLong(attribs[6]);
		this.replies = Integer.parseInt(attribs[7]);
		this.hasUrl = Boolean.parseBoolean(attribs[8]);
		this.isSnope = Boolean.parseBoolean(attribs[9]);
		this.link_id = (this.link_id == 0? this.id : this.link_id);
	
		
	}
	public Comment(JsonObject link, Long id, String url) {
		this.url = url;
		boolean isLink = link.get("name").toString().substring(1,3).equals("t3");
		author = link.get("author").toString().replace("\"", "");
		//System.out.println(this.author);
		parent_id = 0;
		String body = isLink ? "Linked content: " + link.get("url").toString() + " Title : " + link.get("title").toString() + " Text: " + link.get("selftext").toString() 
				: link.get("body").toString();
		created_utc = (long) Float.parseFloat(link.get("created_utc").toString());
		this.id = id;
		this.link_id = (isLink ? this.id : idAsLong(link.get("link_id").getAsString()));
		replies = 1;
		//edited = Boolean.getBoolean(link.get("edited").toString());
		//gilded = Boolean.getBoolean(link.get("gilded").toString());
		//downs = Integer.parseInt(link.get("downs").toString());
		//ups = Integer.parseInt(link.get("ups").toString());
		hasUrl = (isLink ? 
					link.get("is_self").getAsBoolean() ? 
				    hasUrl = urlRegexPattern.matcher(link.get("selftext").toString()).matches() :
				    	true :
				    urlRegexPattern.matcher(body).matches());
		for (String crit : snope_criteria){
			isSnope = isSnope || (body.toLowerCase().indexOf(crit) >= 0);
		}
		
	}
	
	public Comment(Comment c) {


		is_snope_response = c.is_snope_response;
		
		url = c.url; 
		author = c.author;; 
		parent_id = c.parent_id; 

		link_id = c.link_id; 
		id = c.id; 
		wasSnoped = c.wasSnoped;
		sister_to_snope = c.sister_to_snope;
		created_utc = c.created_utc;  
		//private String link_title;  
		replies = c.replies; 
		parent_author = c.parent_author;
		hasUrl = c.hasUrl;
		isSnope = c.isSnope;
		has_snope_aunt = c.has_snope_aunt;
		stat = c.stat;
		type = c.type;
		snope_id = c.snope_id;
	}
	private long idAsLong(String name){
		if (name.charAt(2) == '_' ){
			name = name.substring(3);
		}
		return Long.parseLong(name, 36);
	}
	public void addReply() {
		replies++;
	}
	public String getParent_author(){
		return parent_author;
	}
	public void setParentAuthor(String name){
		this.parent_author = name;
	}
	public String getUrl() {
		return url;
	}
	public String getAuthor() {
		return author;
	}
	public long getParent_id() {
		return parent_id;
	}
	//public String getBody() {
		//return body;
	//}
	public long getLink_id() {
		return link_id;
	}
	public long getId() {
		return id;
	}
	public long getCreated_utc() {
		return created_utc;
	}
	//public String getLink_title() {
	//	return link_title;
	//}
	public int getReplies() {
		return replies;
	}
	/*public boolean isEdited() {
		return edited;
	}
	public boolean isGilded() {
		return gilded;
	}
	public int getDowns() {
		return downs;
	}
	public int getUps() {
		return ups;
	}*/
	//helper method to read booleans
	private static boolean getBool(String bool){
		return (bool != "");
	}
	//helper method for converting dates
	public static long getDate(String date) throws ParseException{
		
		Date date1 = dateFormater.parse(date);
		return date1.getTime();
		
	}
	public boolean isSnope(){
		return isSnope;
	}
	public static String humanDate(Long unicodeDate){
		dateFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormater.format(new Date(unicodeDate));
		
	}
	public static String boolToString(boolean b){
		return (b? "true": "");
	}
	public static String tableHeader(){
		return "id\t" 
				+ "url\t"
				+ "author\t"
				+ "parent_id\t"
				+ "parent_author\t"
				//+ "body\t"
				+ "link_id\t"
				+ "created_utc\t"
				//+ "created_human_utc\t"
				//+ "link_title\t"
				+ "replies\t"
				//+ "edited\t"
				//+ "gilded\t"
				//+ "downs\t"
				//+ "ups\t"
				+ "has_url\t"
				+ "is_snope"
				+ "\n";
		
	}
	public String toString(){
		
			return  id + "\t"
					+ url + "\t"
					+ author + "\t"
					+ parent_id + "\t"
					+ parent_author + "\t"
					//+ body + "\t"
					+ link_id + "\t"
					+ created_utc + "\t"
					//+ humanDate(created_utc) + "\t"
					//+ link_title + "\t"
					+ replies + "\t"
					//+ edited + "\t"
					//+ boolToString(gilded) + "\t"
					//+ downs + "\t"
					//+ ups + "\t"
					+ hasUrl + "\t"
					+ isSnope  
					+ "\n";
			
		}
	public boolean hasUrl() {
	//www
	//.com
	//.edu
		//[...](...)
	return this.hasUrl;
	
	}
	public boolean equals(Object other){
		return other instanceof Comment && ((Comment)other).getId() == id;
	}

	public String getAuthorAddReply() {
		replies++;
		return author;
	}
	@Override
	public int compareTo(Comment o) {
		if (Long.compare(id, o.id) == 0) return 0;
		else if (Long.compare(created_utc, o.created_utc) == 0) return Long.compare(id, o.id);
		else return Long.compare(created_utc,  o.created_utc);
	}
	public Comment clone(){
		return new Comment(this);
}
	

	
}
	

	
	
	
	

