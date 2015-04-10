package reddit_network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class Graph {
	

	
	public static void graphNetwork(String fileBase, Network network) throws IOException{
		for (Comment comment : network.network.values()){
			Node.addCommentToNodes(comment);
			Edge.addCommentToEdges(comment);
		}
		toFile(fileBase);
		mlGraphComponent(fileBase);
	}
	public static void mlGraphComponent(String fileBase) throws IOException{
		
		List<String> file = Files.readAllLines(Paths.get("../graph_ml_template.txt"));
		String graph = "";
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileBase + "/graph.txt"));
		for (String line : file) {
			
				bw.write(line + "\n");
			
		}
		bw = Node.mlGraphComponent(bw);
		bw = Edge.mlGraphComponent(bw);
		bw.write("  </graph>\n</graphml>");
		//System.out.println(graph);
		
		bw.close();
	}
	
	public static void toFile(String fileBase) throws IOException{
		Edge.toFile(fileBase);
		Node.toFile(fileBase);
	}
	public static String dataKey(String name, String value){
		return "      <data key=\"" + name + "\">" + value + "</data>\n";
	}


}
