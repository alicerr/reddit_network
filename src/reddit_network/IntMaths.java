package reddit_network;

public class IntMaths implements Maths<Integer> {

    public Integer zero(){
    	return 0; // Adding zero items
    }

	public double add(double lhs, Integer rhs) {
		// TODO Auto-generated method stub
		return lhs + rhs;
	}
	public double sub(Integer lhs, double rhs) {
		// TODO Auto-generated method stub
		return lhs - rhs;
	}
	@Override
	public double div(Integer lhs, int rhs) {
		// TODO Auto-generated method stub
		if (rhs == 0)
			return 0;
		return lhs / (double) rhs;
	}
	@Override
	public double sqrt(double val) {
		// TODO Auto-generated method stub
		return Math.sqrt(val);
	}

	@Override
	public Integer mult(Integer lhs, Integer rhs) {
		// TODO Auto-generated method stub
		return lhs * rhs;
	}


}
