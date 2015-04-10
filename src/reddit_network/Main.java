package reddit_network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TimeZone;

public class Main {
	
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		//System.out.println("File base name: ");
		
		//String filebase = sc.nextLine();
		String filebase = "C:\\Users\\Alice\\research\\summer_2014_subreddits-2014-09-12\\summer_2014_subreddits";
		sc.close();
		File dir = new File(filebase);
		File[] directoryListing = dir.listFiles();

		if (directoryListing != null) {
		    for (File child : directoryListing) {
		    		
			    	System.out.println(child.getPath().indexOf("r_"));
			    	Author.clear();
			    	System.gc();
			    	if (new File(child.getPath()).isDirectory() && child.getPath().indexOf("r_") >= 0 && child.getPath().indexOf("r_politics") >= 0) { 
				    	filebase = child.getPath();
				    	System.out.println(filebase);
				      // Do something with child
				    	//new File(filebase).mkdirs();
				    	//System.out.println(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() / 1000 - 1354320000);
				//Network network = new Network (1367366400, 1398902400 , filebase, 
				//				filebase + "/comments.txt");
				    	Network network = new Network(filebase, filebase + "/networkdata.txt");
				    	ThreadCollection threadCollection = new ThreadCollection(network);
				    	HashMap<Long, CommentLinkedList> trees = CommentLinkedList.threadCollectionToCommentLinkedLists(threadCollection);
				    	
				    	
				    	/*long count = 0;
				    	int tCount = 0;
				    	long sCount = 0;
				    	for (Thread t : threadCollection.threads.values()) {
				    		count += t.commentsInThread.size() - 1;
				    		tCount++;
				    		for (Comment c : t.commentsInThread) {
				    			sCount += c.isSnope() ? 1 : 0;
				    		}
				    	}*/
				    	Network.loadAuthorsFromFile(filebase);
				    	for (Comment c : network.getNetwork().values()){
				    		if (c.isSnope() && network.getNetwork().containsKey(c.getParent_id())){
				    			network.getNetwork().get(c.getParent_id()).wasSnoped++;
				    		}
				    	}
				    	//new CommentPatternsDoubleStep(Author.allAuthors, trees, filebase, network);
				    	//CommentStringLog.CollectAll(network, filebase);
				    	//WoodCutter.Cut(filebase, Author.allAuthors, threadCollection, network);
				    	StringCutter.Cut(filebase, Author.allAuthors, threadCollection, network, trees);
				    	/*System.out.println("total non link comments: " + count);
				    	System.out.println("total links: " + tCount);
				    	System.out.println("total snopes: " + sCount);*/
			    	}
			    	
		    	
		    }
		}

		//new Network (1354320000, Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() / 1000 , "../../../testing_authors", 
				//"../ZOMBIE.txt");
		//Graph.graphNetwork("../../../testing_snope_8_5_2014", new Network("../../../testing_snope_8_5_2014", "../networkdata.txt"));
		//new Network (1354320000, Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() / 1000 , "../r_politics_7_2014", 
		//"../ZOMBIE.txt");
		//Network network = new Network("../../../r_politics_7_2014", "../../../r_politics_7_2014/networkdata.txt");
		//network.repopAuthors("../../../r_politics_7_2014");
        //Network.authors(filebase, network);
        
		//network.report(filebase+"/report");
		
		//Graph.graphNetwork(filebase, network);
	}

}
