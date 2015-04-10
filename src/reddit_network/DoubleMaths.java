package reddit_network;

public class DoubleMaths  implements Maths<Double> {

	@Override
	public Double zero() {
		// TODO Auto-generated method stub
		return (double)0;
	}

	@Override
	public double add(double lhs, Double rhs) {
		// TODO Auto-generated method stub
		return lhs+rhs;
	}

	@Override
	public double sub(Double lhs, double rhs) {
		// TODO Auto-generated method stub
		return lhs-rhs;
	}

	@Override
	public double div(Double lhs, int rhs) {
		// TODO Auto-generated method stub
		if (rhs == 0)
				return 0;
		return lhs/(double) rhs;
	}

	@Override
	public double sqrt(double val) {
		// TODO Auto-generated method stub
		return Math.sqrt(val);
	}

	@Override
	public Double mult(Double lhs, Double rhs) {
		// TODO Auto-generated method stub
		return lhs * rhs;
	}

}
