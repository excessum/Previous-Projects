package random;

/**
 * Very basic random number generator class created purely to show
 * why there is a RNGFactory. Uses the Math.Random() function. 
 * @author Curtis Stokes
 *
 */
public class JavaRandomNumberGenerator implements RandomInterface{
	public JavaRandomNumberGenerator(){
	}
	
	@Override
	public double next() {
		return Math.random();
	}
	
}
