package games;
import java.io.IOException;
import java.util.Arrays;

import random.RandomInterface;

/**
 * GameFactory is a class used purely for Object Orientated design,
 * and more specifically to return a Game interface dependent on users
 * input. It uses Singleton pattern itself to ensure there will only ever
 * be one GameFactory. To improve this class i would give the users the ability
 * to customise the game before it returned, allowing all customisation to be 
 * done at this point before it is returned as a Game interface.
 * @author Curtis Stokes
 *
 */
public class GameFactory {
	
	/**
	 * Instance of the GameFactory, as there can only be one it is a static
	 * variable.
	 */
	private static GameFactory instance;
	
	/**
	 * Arguments which are accepted by the getGame method, any other
	 * argument throws an error.
	 */
	private static final String[] VALID_ARGUMENTS = {"c", "d"};
	
	/**
	 * The game selected by the user to return.
	 */
	private Game game;
	
	/**
	 * Default constructor, doesn't currently do or require anything
	 */
	private GameFactory(){

	}
	
	/**
	 * Method to get the instance of GameFactory, ensures that only one
	 * GameFactory can exist at any time (Singleton design)
	 * @return GameFactory object
	 */
	public static synchronized GameFactory getInstance(){
		if (instance == null){
			instance = new GameFactory();
		}
		return instance;
	}
	
	/**
	 * Method to get the Game interface the user requested, error handling could 
	 * still be done a little better and currently assumes a RNG game is being
	 * selected. Furthermore the addition of more games requires the editing of 
	 * this file.
	 * @param type The game the user is requesting.
	 * @param rng The random number generator to be used in the game, if games
	 * that do not require a random number generator are added i will split the 
	 * method up and use an overloaded version.
	 * @return Game interface of chosen game
	 * @throws IOException If an invalid game is selected throws an error
	 */
	public Game getGame(String type, RandomInterface rng) throws IOException{
		if (!Arrays.asList(VALID_ARGUMENTS).contains(type)){
			throw new IllegalArgumentException("Invalid type of game selected");
		}
		if(type.equalsIgnoreCase("c")){
			game = new CardGame(rng);
			
		}else if(type.equalsIgnoreCase("d")){
			game = new DiceGame(rng);
		}
		return this.game;
	}
}
