package reddit_network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class ThreadCollection {
	
	public HashMap<Long, Thread> threads = new HashMap<Long, Thread>();
	
	public ThreadCollection(Network network, String filePath) throws IOException {
		
		this(network);
		
		String fileText = Thread.fileHeader();
		for (Thread thread : threads.values()) {
			fileText += thread.toString();
		}
		new File(filePath + "/").mkdirs();
		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath + File.separator + "threadData.txt"));
		bw.write(fileText);
		bw.close();
	}
	public ThreadCollection(Network network) {
		Iterator<Comment> comments = network.getNetwork().values().iterator();
		
		while (comments.hasNext()) {
			
			Comment comment = comments.next();
			if (comment.getLink_id() != 0) {
			//System.out.println(comment.getLink_id());
			if (threads.containsKey(comment.getLink_id()) && Network.isInTimeRange(comment)) 
				threads.get(comment.getLink_id()).addComment(comment);
			else if (Network.isInTimeRange(comment))
				threads.put(comment.getLink_id(), new Thread(comment));
			}
		}

	}
	


	public HashMap<Long, Thread> getThreads() { return threads; }
}

