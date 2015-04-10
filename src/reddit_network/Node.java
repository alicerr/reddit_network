package reddit_network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Node {
	public int commentsMade = 0;
	public int replies = 0;
	public int snoper = 0;
	public int snopee = 0;
	public String name;
	public long firstActive = -1;
	public long lastActive = -1;
	public int urlsUsed = 0;
	public static HashMap<String, Node> allNodes = new HashMap<String, Node>();
	public static int added = 0;
	public static int maxComments = 0;
	public static int maxReplies = 0;
	
	public static void addCommentToNodes(Comment comment){
		if (!comment.getParent_author().equals("NA")&& !comment.getAuthor().equals("[deleted]") && !comment.getParent_author().equals("[deleted]") && Network.isInTimeRange(comment)) {
			try {
				allNodes.get(comment.getAuthor()).addComment(comment);
				allNodes.get(comment.getParent_author()).addReply(comment);
			} catch (Exception e){
				Node author = new Node(comment.getAuthor());
				Node parent = new Node(comment.getParent_author());
				author.addComment(comment);
				parent.addReply(comment);
				allNodes.put(comment.getAuthor(), author);
				allNodes.put(comment.getParent_author(), parent);
				added++;
				if (added % 100 == 0) {
					System.out.println("added to nodes: " + added);
				}
			}
		} else {
			//System.out.println("no parent");
		}
	}
	public Node(String name){
		this.name = name;	
	}
	public void addComment(Comment comment) {
		commentsMade++;
		maxComments += commentsMade > maxComments? 1 :0;
		snoper += comment.isSnope() ? 1 : 0;
		firstActive = firstActive != -1 && firstActive < comment.getCreated_utc() ? firstActive : comment.getCreated_utc();
		lastActive = lastActive != -1 && lastActive > comment.getCreated_utc() ? lastActive : comment.getCreated_utc();
		urlsUsed += comment.hasUrl() ? 1 : 0;
	}
	
	public void addReply(Comment comment){
		replies++;
		if (replies > maxReplies){
			maxReplies++;
		}
		snopee += comment.isSnope() ? 1 : 0;
	}
	public boolean equals(Object other) {
		return other instanceof Node && this.name.equals(((Node)other).name);
	}
	
	public String toString(){
		return name + "\t" +
			   commentsMade + "\t" +
			   replies + "\t" +
			   snoper + "\t" +
			   snopee + "\t" +
			   urlsUsed + "\t" +
			   firstActive + "\t" +
			   lastActive + "\n";
	}
	public static String header() {
		return "name" + "\t" +
				   "comments" + "\t" +
				   "replies" + "\t" +
				   "snoper" + "\t" +
				   "snopee" + "\t" +
				   "urls_used" + "\t" +
				   "first_active" + "\t" +
				   "last_active" + "\n";
	}
	
	public String toMlGraphNode(){
		return "    <node id=\"" + name + "\">\n" 
				+ Graph.dataKey("color", getColor()) 
				//+  Graph.dataKey("size", Double.toString(getSize())) 
				+ "    </node>";
	}
	
	public double getSize() {
		return Math.sqrt((replies + commentsMade) / (double)(maxComments + maxReplies)); 
	}
	public String getColor() {
		if (snopee > 0 && snoper > 0) {
			return "orange";
		} else if (snopee > 0) {
			return "green";
		} else if (snoper > 0) {
			return "red";
		} else {
			return "grey";
		}
		
	}
	public static void toFile(String filebase) throws IOException{
		new File(filebase).mkdirs();
		String name = filebase + "/nodes.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(name));
		bw.write(header());
		for(Node node : allNodes.values()){
			bw.write(node.toString());
		}
		bw.close();
	}

	public static BufferedWriter mlGraphComponent(BufferedWriter br) throws IOException{
		int i = 0;
		for(Node node: allNodes.values()){
			if (node.snopee + node.snoper > 3)
				br.write(node.toMlGraphNode() + "\n");
			i++;
			if (i % 1000 == 0) System.out.println("nodes added to graph: " + i);
		}
		return br;
	}
	

}
