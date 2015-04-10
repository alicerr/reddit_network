package reddit_network;




import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

public abstract class Dataflow {
	public static Pattern urlRegexPattern;
	public static Pattern qtMatcher;

	public static String if0ThenNA(int i){
		if (i == 0)
			return "NA";
		else
			return i + "";
	}
	public static String if0ThenNA(double i){
		if (i == 0)
			return "NA";
		else
			return i + "";
	}
	public static String if0ThenNA(long i){
		if (i == 0)
			return "NA";
		else
			return i + "";
	}
	public static String if0ThenNA(float i){
		if (i < .0001)
			return "NA";
		else
			return i + "";
	}
	public static String if0OrLessThenNA(int i){
		if (i < 1)
			return "NA";
		else
			return i + "";
	}
	public static double getAvg(int[] iArray) {
		long sum = 0;
		for (int i : iArray)
			sum += i;
		return sum / (double) iArray.length;
	}
	public static boolean hasUrl(String text){
		
		if (urlRegexPattern == null) urlRegexPattern = Pattern.compile(".*(http|ftp|www|\\[.+\\]\\w?\\(.+\\)).*");
		return urlRegexPattern.matcher(text).matches();
	}
	
	public static double getLogAvg(int[] iArray) {
		double sum = 0;
		for (int i : iArray)
			sum += Math.log(i + 1);
		return sum / (double) iArray.length;
	}
	public static String smallestPosValue(int[] depths){
		int min = 0;
		for (int i : depths)
			if (min == 0 || (i < min && i != 0)) {
				min = i;
			}
		return if0ThenNA(min);
	}
	public static double getAvg(ArrayList<Long> lArray) {
		long sum = 0;
		for (long i : lArray)
			sum += i;
		return sum / (double) lArray.size();
	}
	public static double getLogAvg(ArrayList<Long> lArray) {
		double sum = 0;
		for (long l : lArray)
			sum += Math.log(l + 1);
		return sum / (double) lArray.size();
	}
//
	public static double getMedian(int[] iArray) {
		Arrays.sort(iArray);
		if (iArray.length == 0)
			return 0;
		else if (iArray.length % 2 == 0)
			return (iArray[iArray.length/2 - 1] + iArray[iArray.length/2])/2.0;
		else
			return iArray[iArray.length/2];
	}
	

	public static double getMedian(ArrayList<Long> lArray) {
		lArray.sort(new Comparator<Long>() {
		    public int compare(Long o1, Long o2) {   
		        return (o1.compareTo(o2));
		    }
		});
		int l = lArray.size();
		if (l == 0){
			return 0;
		}else if (l % 2 == 0)
			return (lArray.get(l/2 - 1) + lArray.get(l/2))/2.0;
		else
			return lArray.get(l/2);
	}

	public static <T> ArrayList<T> asSortedList(ArrayList<T> c, Comparator<T> comp) {
		  java.util.Collections.sort(c, comp);
		  return c;
		}
	public static HashMap<Long, CommentLinkedList> threadCollectionToCommentLinkedListHashMap (ThreadCollection tc){
		HashMap<Long, CommentLinkedList> hold = new HashMap<Long, CommentLinkedList>();
		for (Entry<Long, Thread> t : tc.threads.entrySet()) {
			hold.put(t.getKey(), new CommentLinkedList(t.getValue()));
		}
		return hold;
		
	}
	protected static String sanitize(String body){
		
		return (body.replaceAll("\t", " ")).replaceAll("\n", " ").replaceAll("\\n", " ").replaceAll("\"", "").replaceAll("'", "").replaceAll("\\s+", " ");
	}
	protected static Matcher qt(String text){
		if (qtMatcher == null)
			qtMatcher = Pattern.compile("&gt;([\\s\\S]*?)\\n\\n");
		Matcher m = qtMatcher.matcher(text);
		
		return m;
		
	}
	public static String checkNull(Object stat) {
		if (stat == null) return "NA"; else return stat.toString();
	}
}
