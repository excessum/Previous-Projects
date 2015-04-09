package games;
import java.util.ArrayList;
import random.RandomInterface;

/**
 * Dice game is a class that implements the functionality of the original dice 
 * game from the exercise. Although it has been modified to have more potently 
 * Functionality through number of decks and winning conditions, this is not 
 * demonstrated and not very well tested due to time constraints and being of 
 * low priority. 
 * Future improvements could include having a dice object,to further increase 
 * the OO and expandability of this program. 
 * @author Curtis Stokes
 *
 */
public class DiceGame extends RNGGame<Integer>{
	
	/**
	 * The default message to users when waiting for input, used in the example 
	 */
	private static String defaultInputString = "Hit <RETURN> to roll the die";
	
	/**
	 * Default constructor which uses a random number generator and then sets all
	 * values to those used in the original files. Used in the demonstration.
	 * @param rng A RandomInterface used for generating random numbers for this game
	 */
	public DiceGame(RandomInterface rng) {
		super(2, true, rng, "rolled");
		this.winningValues.add(new Integer(1));
		for(int i = 1; i <= 6; i++){
			this.possibleValues.add(new Integer(i));
		}
		this.inputString = defaultInputString;
	}
	
	/**
	 * Unused constructor which allows you to modify the game to change victory
	 * conditions or the number of decks.
	 * @param rng A RandomInterface used for generating random numbers for this game
	 * @param maxDiceValue The maximum number that can appear on a dice, i.e a 10 sided dice
	 * @param winningValues An ArrayList of Integers containing all cards which result in 
	 * a win.
	 */
	public DiceGame(RandomInterface rng, int maxDiceValue, ArrayList<Integer>winningValues) {
		super(2, true, rng, "rolled");
		this.winningValues.addAll(winningValues);
		for(int i = 1; i <= maxDiceValue; i++){
			this.possibleValues.add(new Integer(i));
		}
		this.inputString = defaultInputString;
	}
}
