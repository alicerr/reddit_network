package reddit_network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class mean_dev_3g<T extends Number> {
	public ArrayList<T> groupAll = new ArrayList<T>();
	public ArrayList<T> groupSnope = new ArrayList<T>();
	public ArrayList<T> groupSnoped = new ArrayList<T>();
	public ArrayList<T> groupNuetral = new ArrayList<T>();
	public static int SNOPED = 3;
	public static int SNOPER = 2;
	public static int ALL = 1;
	public static int NUETRAL = 4;
	
	public void load(T value, int group){
		if (group == ALL) {
			groupAll.add(value);
		} else if (group == SNOPED) {
			groupSnope.add(value);
		} else if (group == SNOPER) {
			groupSnoped.add(value);
		} else if (group == NUETRAL) {
			groupNuetral.add(value);
		}
	}
	
	 public double mean(List<T> list, Maths<T> math) {
        T total = math.zero();
        int count = 0;
        for (T n : list){
            total = math.add(total, n);
            count++;
        }
        return math.div(total, count);
    }
	public T mode(List<T> list){
		HashMap<T, Integer> count = new HashMap<T, Integer>();
		for (T t :  list){
			if (count.containsKey(t)) {
				count.put(t, count.get(t) + 1);
			} else
				count.put(t,  1);
		}
		int max = 0;
		T medain = null;
		for (Entry<T, Integer> e : count.entrySet()){
			if (e.getValue() > max || max == 0){
				max = e.getValue();
				medain = e.getKey();
			}
		}
		return medain;
	}
	
	public double std(List<T> list, double mean, Maths<T> maths) {
		double sum = 0;
		int count = 0;
		for (T t : list){
			double diff = maths.sub(t, mean);
			double pow = Math.pow(diff, 2);
			sum += pow;
			count++;
		}
		double div = sum/(double)count;
		return Math.sqrt(div);
	}
	
	public String report(List<T> list, Maths<T> maths){
		double mean = mean(list, maths);
		T mode = mode(list);
		double std = std(list, mean, maths);
		return "\nMean: " + mean + "\nMode: " + mode + "\nStd: " + std;
		
	}
	public String fullReport(Maths<T> maths){
		return "All" + report(groupAll, maths) 
				+ "\nSnopes" + report(groupSnope, maths) 
				+ "\nSnoped" + report(groupSnoped, maths)
				+ "\nNuetral" + report(groupNuetral, maths);
	}

}
