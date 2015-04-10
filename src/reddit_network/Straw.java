package reddit_network;

public abstract class Straw {
	
	public static String fileHeader(){
		return "author_a" + '\t' 
				+ "author_b" + '\t' 
				+ "id_a" + '\t'
				+ "id_b" + '\t'
				+ "time_to_b1" + '\t'
				+ "time_to_a2" + '\t'
				+ "length_of_straw" + '\t'
				+ "min_snope_depth_in_straw" + '\t'
				+ "max_snope_depth_in_straw" + '\n';
	}
	private static int minSnopeDepth(CommentLinkedList cl){
		if (cl.self.isSnope())
			return 1;
		if (cl.next.size() >0) {
			int min = minSnopeDepth(cl.next.get(0));
			return min > 0 ? min + 1 : -1;
		}
		return -1;
	}
	private static int maxSnopeDepth(CommentLinkedList cl){
		if (cl.next.size() > 0){
			int max = maxSnopeDepth(cl.next.get(0));
			if (max > 0) {
				return max + 1;
			}
		}
		return cl.self.isSnope() ? 1 : -1;
	}
	public static String strawToFileString(CommentLinkedList cl){
		return cl.self.getAuthor() + '\t'
				+ cl.next.get(0).self.getAuthor() + '\t'
				+ cl.self.getId() + '\t'
				+ cl.next.get(0).self.getId() + '\t'
				+ (cl.next.get(0).self.getCreated_utc() - cl.self.getCreated_utc()) + '\t'
				+ (cl.next.get(0).next.size() > 0 ? cl.next.get(0).next.get(0).self.getCreated_utc()
						-cl.next.get(0).self.getCreated_utc() : "NA") + '\t'
				+ cl.getMaxDepth() + '\t'
				+ Dataflow.if0OrLessThenNA(minSnopeDepth(cl)) + '\t'
				+ Dataflow.if0OrLessThenNA(maxSnopeDepth(cl)) + '\n';
	}
}
