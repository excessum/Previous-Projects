package random;
import java.util.Arrays;

/**
 * RNGFactory is a class used purely for Object Orientated design,
 * and more specifically to return a RandomInterface interface dependent on users
 * input. It uses Singleton pattern itself to ensure there will only ever
 * be one RNGFactory. To improve this class i would give the users the ability
 * to customise the RNG before it returned, allowing all customisation to be 
 * done at this point before it is returned as a RandomInterface interface.
 * @author Curtis Stokes
 *
 */
public class RNGFactory {
	
	/**
	 * Instance of the RNGFactory, as there can only be one it is a static
	 * variable.
	 */
	private static RNGFactory instance;
	
	/**
	 * Arguments which are accepted by the getRNG method, any other
	 * argument throws an error.
	 */
	private static final String[] VALID_ARGUMENTS = {"j", "l"};
	
	/**
	 * The RNG selected by the user to return.
	 */
	private RandomInterface game;
	
	/**
	 * Default constructor, doesn't currently do or require anything
	 */
	private RNGFactory(){

	}
	
	/**
	 * Method to get the instance of RNGFactory, ensures that only one
	 * RNGFactory can exist at any time (Singleton design)
	 * @return RNGFactory object
	 */
	public static synchronized RNGFactory getInstance(){
		if (instance == null){
			instance = new RNGFactory();
		}
		return instance;
	}
	
	/**
	 * Method to get the RandomInterface interface the user requested, error 
	 * handling could still be done a little better. Furthermore the addition of 
	 * more games requires the editing of this file.
	 * @param type The type of RNG to use
	 * @return RandomInterface of the given RNG type
	 */
	public RandomInterface getRNG(String type){
		if (!Arrays.asList(VALID_ARGUMENTS).contains(type)){
			throw new IllegalArgumentException("Invalid type of RNG selected");
		}
		if(type.equalsIgnoreCase("j")){
			game = new JavaRandomNumberGenerator();
		}else if(type.equalsIgnoreCase("l")){
			game = new LinearCongruentialGenerator();
		}
		return this.game;
	}
}