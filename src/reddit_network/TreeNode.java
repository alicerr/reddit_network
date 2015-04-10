package reddit_network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TreeNode {
	Comment comment = null;
	HashSet<TreeNode> children = new HashSet<TreeNode>();
	Tree root = null;
	
	public TreeNode(Thread thread, Comment comment, Tree root, ArrayList<Comment> commentSurvay) {
		this.comment = comment;
		this.root = root;
		commentSurvay.remove(comment);
		for (Comment c : thread.commentsInThread) {
			if (c.getParent_id() == comment.getId()) {
				children.add(new TreeNode(thread, c, root, commentSurvay));
			}
		}
		if (comment.getAuthor().equals(root.comment.getAuthor())) {
			root.rootAuthorAppearences += 1;
			root.repliesDirectlyToRootAuthor += children.size();
			root.snopesToRootAuthor += snopeCount();
			if (snopeCount() > 0) {
				System.out.println("FOUND SNOPE");
			}
		} else {
			root.snopesNotToRootAuthor += snopeCount();
		}
	}
	public int snopeCount() {
		int count = 0;
		for (TreeNode c : children) {
			if (c.comment.isSnope()) {
				count++;
			}
		}
		return count;
	}
	public int maxDepth(){
		int depth = 0;
		for (TreeNode t : children) {
			int tDepth = t.maxDepth();
			depth = tDepth > depth ? tDepth : depth;
		}
		return depth + 1;
	}
	public int maxBreadth() {
		int breadth = children.size();
		for (TreeNode t : children){
			int tBreadth = t.maxBreadth();
			breadth = breadth < tBreadth ? tBreadth : breadth;
		}
		return breadth;
	}
	public int[] depths(){
		int childrenCount = children.size();
		if (childrenCount == 0) {
			return new int[] {1};
		}
		int[][] depths = new int[childrenCount][];
		int length = 0;
		int i = 0;
		for (TreeNode t : children) {
			int[] childDepth = t.depths();
			length += childDepth.length;
			depths[i++] = childDepth;
		}
		return flatten(depths, length, true);
	}
	
	public int[] flatten(int[][] flattenable, int length, boolean add) {
		int[] flat = new int[length];
		int index = 0;
		for (int[] iList : flattenable)
			for (int i : iList)
				flat[index++] = i + (add ? 1 : 0);
		return flat;
	}
	public int[] flattenSnopeDepth(int[][] flattenable, int length, boolean isAuthor) {
		int[] flat = new int[length];
		int index = 0;
		for (int[] iList : flattenable)
			for (int i : iList)
				
					if (i > 0) {
						flat[index++] = i;
					} else {
						flat[index++] = i == 0 ? 0 : isAuthor? Math.abs(i) + 1 : i - 1;
					
				}
		return flat;
	}
	public int[] breadths(){
		int childrenCount = children.size();
		if (childrenCount == 0) {
			return new int[] {0};
		}
		int[][] breadths = new int[childrenCount + 1][];
		breadths[0] = new int[] {childrenCount};
		int length = 1;
		int i = 1;
		for (TreeNode t : children) {
			int[] childBreadth = t.breadths();
			length += childBreadth.length;
			breadths[i++] = childBreadth;
		}
		return flatten(breadths, length, false);
	}
	public int shallowestSnopeDepth() {
		if (comment.getAuthor().equals(root.comment.getAuthor()) && snopeCount() > 0) {
			return 2;
		} else {
			
			int depth = 0;
			for (TreeNode t : children) {
				int childVal = t.shallowestSnopeDepth();
				if (depth == 0 || (childVal != 0 && childVal < depth)) {
					depth = childVal;
				}
			}
			return depth == 0 ? 0 : 1 + depth;
		}
	}
	public int sumComments() {
		int count = 0;
		for (TreeNode t : children){
			count += t.sumComments();
		}
		return count + 1;
	}
	public int selfComments(){
		int selfComments = 0;
		for (TreeNode t : children){
			selfComments += t.comment.getAuthor().equalsIgnoreCase(comment.getAuthor()) ? 1 : 0;
		}
		return selfComments;
	}
	public HashSet<String> uniqueAuthors(){
		HashSet<String> authors = new HashSet<String>();
		authors.add(comment.getAuthor().toLowerCase());
		for (TreeNode t : children){
			authors.add(t.comment.getAuthor().toLowerCase());
		}
		return authors;
	}
	public HashSet<String> uniqueAuthorsInTree(){
		HashSet<String> authors = uniqueAuthors();
		for (TreeNode t : children){
			authors.addAll(t.uniqueAuthorsInTree());
		}
		return authors;
	}
	public int[] snopeDepthsToAuthor() {
		int childrenCount = children.size();
		int[] me;
		if (comment.isSnope() && !comment.getAuthor().equalsIgnoreCase(root.comment.getAuthor())) {
			me = new int[]{-1};
		} else {
			me = new int[0];
		}
		

		
		int[][] depths = new int[childrenCount + 1][];
		depths[0] = me;
		int length = me.length;
		int i = 1;
		for (TreeNode t : children) {
			int[] childDepth = t.snopeDepthsToAuthor();
			length += childDepth.length;
			depths[i++] = childDepth;
		}
		int[] toReturn = flattenSnopeDepth(depths, length, comment.getAuthor().equalsIgnoreCase(root.comment.getAuthor()));
		if (me.length >  0) toReturn[0] = me[0];
		return toReturn;
	}
	
	
	
}
