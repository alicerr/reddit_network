package reddit_network;

import java.util.ArrayList;

public class Tree {

	public int rootAuthorAppearences = 0;
	public Comment comment;
	public int repliesDirectlyToRootAuthor = 0;
	public int snopesToRootAuthor = 0;
	public int snopesNotToRootAuthor = 0;
	public TreeNode root;
	
	public Tree(Comment comment, Thread thread, ArrayList<Comment> commentSurvay) {
		this.comment = comment;
		this.root = new TreeNode(thread, comment, this, commentSurvay);
	}
	
	public static String fileHeader(){
		return Comment.tableHeader().substring(0, Comment.tableHeader().lastIndexOf('\n')) + "\t"
				+ "max_breadth" + "\t"
				+ "max_depth" + "\t"
				+ "avg_breadth" + "\t"
				+ "median_breadth" + "\t"
				+ "avg_depth"  + "\t"
				+ "medain_depth" + "\t"
				+ "log_avg_breadth" + "\t"
				+ "log_avg_depth" + "\t"
				+ "count_root_author_comments" + "\t"
				+ "children_of_root_comment" + "\t"
				+ "sum_direct_replies_to_root_author" + "\t"
				+ "comments_in_tree" + "\t"
				+ "sum_snopes_diretly_to_root_author" + "\t"
				+ "shallowest_snope_depth_to_root_comment" + "\t"
				+ "shallowest_snope_depth_to_root_author" + "\t"
				+ "sum_snopes_not_to_root_author" + "\t" 
				+ "unique_authors_at_level_1_and_2" + "\t"
				+ "unique_authors_in_tree" + "\t"
				+ "self_comments_at_level_2" + "\n";
	}
	public String toString() {
		String commentString = comment.toString();
		int[] breadths = root.breadths();
		int[] depths = root.depths();
		return commentString.substring(0, commentString.lastIndexOf('\n')) + '\t'
				+ root.maxBreadth() + '\t'
				+ root.maxDepth() + '\t'
				+ Dataflow.if0ThenNA(Dataflow.getAvg(breadths)) + '\t'
				+ Dataflow.if0ThenNA(Dataflow.getMedian(breadths)) + '\t'
				+ Dataflow.if0ThenNA(Dataflow.getAvg(depths)) + '\t'
				+ Dataflow.if0ThenNA(Dataflow.getMedian(depths)) + '\t'
				+ Dataflow.if0ThenNA(Dataflow.getLogAvg(breadths)) + '\t'
				+ Dataflow.if0ThenNA(Dataflow.getLogAvg(depths)) + '\t'
				+ rootAuthorAppearences + '\t'
				+ root.children.size() + '\t'
				+ repliesDirectlyToRootAuthor + '\t' 
				+ root.sumComments() + '\t'
				+ snopesToRootAuthor + '\t'
				+ Dataflow.if0ThenNA(root.shallowestSnopeDepth()) + "\t"
				+ Dataflow.smallestPosValue(root.snopeDepthsToAuthor()) + "\t"
				+ snopesNotToRootAuthor + '\t'
				+ root.uniqueAuthors().size() + '\t'
				+ root.uniqueAuthorsInTree().size() + '\t'
				+ root.selfComments() + '\n';
				
		
	}


}
