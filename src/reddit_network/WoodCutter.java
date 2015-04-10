package reddit_network;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class WoodCutter {
	
	public static void Cut(String fileBase, HashMap<String, Author> allAuthors, ThreadCollection threads, Network network) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileBase + '/' + fileName()));
		bw.write(Tree.fileHeader());
		bw.close();
		HashMap<String, Author> authorsToCut = new HashMap<String, Author>();
		for (Entry<String, Author> a : allAuthors.entrySet()) {
			if (meetCrteria(a.getValue())) {
				authorsToCut.put(a.getKey(), a.getValue());
			}
		}
		HashMap<String, ArrayList<Comment>> authorComments = new HashMap<String, ArrayList<Comment>>();
		for (Author a : authorsToCut.values()) {
			authorComments.put(a.name, new ArrayList<Comment>());
		}
		System.out.println("All authors added. Count: " + authorsToCut.size());
		int i = 0;
		int j = 0;
		for (Comment c : network.network.values()) {
			if (authorComments.containsKey(c.getAuthor())) {
				authorComments.get(c.getAuthor()).add(c);
				j++;
			}
			i++;
			if (i % 10000 == 0) {
				System.out.println("Looked at " + i + " comments, kept " + j + " comments");
			}
		}
		System.out.println("All comments collected. List Count: " + authorsToCut.size() + " CommentCount: " + j);
		for (Entry<String, ArrayList<Comment>> ca : authorComments.entrySet()) {
			ArrayList<Comment> c = ca.getValue();
			Collections.sort(c, new DateComparator());
			long l = 0;
			for (Comment com : c) {
				
				System.out.println(l - com.getCreated_utc() );
				l = com.getCreated_utc();
			}
			while (!c.isEmpty()) {
				Comment comment = c.get(0);
				Thread thread = threads.threads.get(comment.getLink_id());
				Tree tree = new Tree(comment, thread, c);
				String[] feilds = Tree.fileHeader().split("\t");
				String[] crit = tree.toString().split("\t");
				for (i = 0; i < feilds.length; i++) {
					System.out.println(feilds[i] + ": " + crit[i]);
				}
				bw = new BufferedWriter(new FileWriter(fileBase + '/' + fileName(), true));
				bw.append(tree.toString());
				bw.close();
			}
			
		}
	}

	private static boolean meetCrteria(Author a) {
		return a.snoped > 0 && a.comments > 4;
	}
	
	
	private static String fileName() {
		// TODO Auto-generated method stub
		return "tester.txt";
	}
}
