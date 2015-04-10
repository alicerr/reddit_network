package reddit_network;

public interface Maths<T> {
    T zero(); // Adding zero items
    T mult(T lhs, T rhs);
    double sub(T lhs, double rhs); // Adding two items
    double div(T lhs, int count); // Adding two items
    double sqrt(double val);
	double add(double lhs, T rhs);
    
}
