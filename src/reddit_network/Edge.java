package reddit_network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Edge {
	public String target;
	public String source;
	public int comments = 0;
	public int snoper = 0;
	public int snopee = 0;
	public int replies = 0;
	public long firstActive = -1;
	public long lastActive = -1;
	public static HashMap<String, Edge> allEdges = new HashMap<String, Edge>();
	public static int added = 0;
	public static int maxComments = 0;
	public static int maxReplies = 0;
	
	
	public static void addCommentToEdges(Comment comment){
		if (!comment.getParent_author().equals("NA") && !comment.getAuthor().equals("[deleted]") && !comment.getParent_author().equals("[deleted]") && Network.isInTimeRange(comment)) {
			String key = Edge.makeKey(comment);
			String reverseKey = Edge.makeReverseKey(comment);
			try {
				allEdges.get(key).addComment(comment);
				allEdges.get(reverseKey).addReply(comment);
			} catch (Exception e) {
				Edge edge = new Edge(comment.getAuthor(), comment.getParent_author());
				Edge revEdge = new Edge(comment.getParent_author(), comment.getAuthor());
				edge.addComment(comment);
				revEdge.addComment(comment);
				allEdges.put(key, edge);
				allEdges.put(reverseKey, revEdge);
				added++;
				if (added % 100 == 0) {
					System.out.println("added to edges: " + added);
				}
			}
		}
	}
	public String toMlGraphEdge(){
		return "    <edge id=\"edge_" + source + "_" + target  + "\" source=\"" + source + "\" target=\"" + target + "\">\n" 
				+ Graph.dataKey("color", getColor()) 
				//+ Graph.dataKey("weight", Double.toString(getSize())) 
				+ "    </edge>";
	}
	
	public double getSize() {
		return Math.sqrt(comments / (double)maxComments); 
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
	public Edge(String source, String target){
		this.source = source;
		this.target = target;
	}
	public void addComment(Comment comment) {
	   comments++;
	   if (comments > maxComments) maxComments++;
	   snoper += comment.isSnope() ? 1 : 0;
	   firstActive = firstActive != -1 && firstActive < comment.getCreated_utc() ? firstActive : comment.getCreated_utc();
	   lastActive = lastActive != -1 && lastActive > comment.getCreated_utc() ? lastActive : comment.getCreated_utc();
	}
	public void addReply(Comment comment) {
		replies++;
		if (replies > maxReplies) maxReplies++;
		snopee += comment.isSnope() ? 1 : 0;
	}
	public static String makeKey(Comment comment){
		return "e" + comment.getAuthor() + "_" + comment.getParent_author();
	}
	public static String makeReverseKey(Comment comment){
		return "e" + comment.getParent_author() + "_" + comment.getAuthor();
	}
	public boolean equals(Object other){
		return other instanceof Edge && ((Edge)other).target.equals(target) && ((Edge)other).source.equals(source);
	}
	public String toString() {
		return target + "\t" +
			   source + "\t" +
			   comments + "\t" +
			   snoper + "\t" +
			   snopee + "\t" +
			   replies + "\t" +
			   firstActive + "\t" +
			   lastActive + "\n";
	}

	public static String header(){
		return "target" + "\t" +
				   "source" + "\t" +
				   "comments" + "\t" +
				   "snoper" + "\t" +
				   "snopee" + "\t" +
				   "replies" + "\t" +
				   "firstActive" + "\t" +
				   "lastActive" + "\n";
	}

	public static void toFile(String filebase) throws IOException{
		new File(filebase).mkdirs();
		String name = filebase + "/edges.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(name));
		bw.write(header());
		for(Edge edge : allEdges.values()){
			bw.write(edge.toString());
		}
		bw.close();
	}

	public static BufferedWriter mlGraphComponent(BufferedWriter br) throws IOException{
		int i = 0;
		for(Edge edge: allEdges.values()){
			if (edge.snopee + edge.snoper > 3){
				br.write(edge.toMlGraphEdge() + "\n");
				i++;
				if (i % 1000 == 0) System.out.println("edges added to graph: " + i);
			}
		}
		System.out.println(maxReplies + " maximum replies");
		System.out.println(maxReplies + " maximum comments");
		return br;
	}
	
	
}
