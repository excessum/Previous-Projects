package games;
import java.util.ArrayList;
import java.util.HashSet;

import random.RandomInterface;

/**
 * RNGGame is very comprehensive abstract class covering all possibilities with the
 * same format as the examples of games given. It uses generics to allow it to take
 * a random item from a set of any objects, (with either replacing it back into 
 * possible values, or removing it). Implements the Game interface allowing the 
 * main class to play any game created from it. Only real improvements come from
 * victory conditions such as allowing compound conditions(rolling 6 twice) or
 * more complex conditions such as a scoring system against a computer. Due to 
 * the examples given i felt this unnecessary for this class. 
 * @author Curtis Stokes
 *
 * @param <A> The Object type that is avaliable for the player to randomly recieve
 */
public abstract class RNGGame<A> implements Game{
	
	/**
	 * Holds all the possible values for the player to receive
	 */
	protected ArrayList<A> possibleValues = new ArrayList<A>();
	
	/**
	 * Holds all the values required for a player to have to win
	 */
	protected ArrayList<A> winningValues = new ArrayList<A>();
	
	/**
	 * Holds all values the player currently owns
	 */
	private HashSet<A> playerValues = new HashSet<A>();
	
	/**
	 * The number of objects the player will receive
	 */
	private int numberOfTurns;
	
	/**
	 * The current turn
	 */
	private int turnNumber = 0;
	
	/**
	 * Whether to replace a value back into possibleValues once a player
	 * has received it (allow duplicate draws/rolls)
	 */
	private boolean replaceValues;
	
	/**
	 * The RandomInterface responsible for generating pseudo random numbers
	 */
	private RandomInterface rng;
	
	/**
	 * Currently unused functionality allowing the modification of input
	 * request strings, if they are enables this is true. 
	 */
	private boolean uniqueAskStrings = false;
	
	/**
	 * List of all input request strings to use if uniqueAskStrings is true.
	 * Must be equal in length to the number of turns.
	 */
	private ArrayList<String> askForInputStrings;
	
	/**
	 * Adjective used for string formatting such as rolled
	 */
	private String adjective;
	
	/**
	 * String to use if uniqueAskStrings is not enabled
	 */
	protected String inputString; 
	
	/**
	 * Default constructor of an RNG game, called from the subclasses that
	 * extend it and used to configure the game.
	 * @param noOfTurns The number of turns the user gets to draw an item.
	 * @param replaceValues Whether values can be drawn more than once.
	 * @param rng The RandomInterface responsible for RNG
	 * @param adjective Adjective used for string formatting such as rolled for
	 * a dice.
	 */
	public RNGGame(int noOfTurns, boolean replaceValues, RandomInterface rng, String adjective){
		this.numberOfTurns = noOfTurns;
		this.replaceValues = replaceValues;
		this.rng = rng;
		this.adjective = adjective;
	}
	
	/**
	 * Method using generic's to randomly receive an item from a set, removing it
	 * if required based on the replaceValues value.
	 * @return RandomValue given to player
	 */
	private <A> A givePlayerRandomValue(){
		int position = (int)(rng.next() * this.possibleValues.size()); 
		A toReturn = (A) this.possibleValues.get(position);
		playerValues.add(this.possibleValues.get(position));
		if(!replaceValues){
			this.possibleValues.remove(position);
		}
		return toReturn;
	}
	
	/**
	 * Currently unused method that allows the addition of custom
	 * input request strings
	 * @param inputList ArrayList<String> Containing all the input request
	 * strings. Must be equal in length to numberOfTurns.
	 */
	public void addUniqueInputStrings(ArrayList<String> inputList){
		this.uniqueAskStrings = true;
		this.askForInputStrings = inputList;
	}
	
	/**
	 * Locally used method to determine if the player has won the game.
	 * @return true if the player has won, false otherwise
	 */
	private boolean playerWins() {
		for(A value : this.playerValues){
			if(this.winningValues.contains(value)){
				return true;
			}
		}
		return false;	
	}
	
	@Override
	public String nextMove(String input) {
		return "You " +this.adjective +" : " + this.givePlayerRandomValue();
	}
	
	@Override
	public boolean requiresInput() {
		if (this.turnNumber < this.numberOfTurns){
			this.turnNumber++;
			return true;
		}
		return false;
	}
	
	@Override
	public String getResults() {
		String toReturn = "You "+this.adjective+" : " + this.playerValues.toString() +"\n";
		if(this.playerWins()){
			toReturn += "You win!";
		}else{
			toReturn += "You lose!";
		}
		return toReturn;
	}
	
	@Override
	public String askForInput() {
		if(!this.uniqueAskStrings){
			return inputString;
		}else{
			return this.askForInputStrings.get(this.turnNumber);
		}
	}
	
	/**
	 * Method to change the number of turns in the game.
	 * Currently unused but can be implemented into GameFactory
	 * with future improvements.
	 * @param numberOfTurns The numberOfTurns the user gets to randomly
	 * get an item.
	 */
	public void setNumberOfTurns(int numberOfTurns){
		this.numberOfTurns = numberOfTurns;
	}
	
}

