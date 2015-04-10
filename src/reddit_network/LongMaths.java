package reddit_network;

public class LongMaths implements Maths<Long>{

	@Override
	public Long zero() {
		// TODO Auto-generated method stub
		return (long) 0;
	}

	@Override
	public double add(double lhs, Long rhs) {
		// TODO Auto-generated method stub
		return lhs + rhs;
	}

	@Override
	public double sub(Long lhs, double rhs) {
		// TODO Auto-generated method stub
		return lhs - rhs;
	}

	@Override
	public double div(Long lhs, int rhs) {
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
	public Long mult(Long lhs, Long rhs) {
		// TODO Auto-generated method stub
		return lhs * rhs;
	}

}
