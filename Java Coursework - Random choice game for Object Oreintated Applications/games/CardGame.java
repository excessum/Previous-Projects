package games;
import java.util.ArrayList;
import java.util.Arrays;

import random.RandomInterface;

/**
 * Card game is a class that implements the functionality of the original card 
 * game from the exercise. Although it has been modified to have more potently 
 * Functionality through number of decks and winning conditions, this is not 
 * demonstrated and not very well tested due to time constraints and being of 
 * low priority. 
 * Future improvements could include having a deck object, with even the potently
 * for card objects to further increase the OO and expandability of this program. 
 * @author Curtis Stokes
 *
 */
public class CardGame extends RNGGame<String> {
	
	/**
	 * A static variable holding all the values of a standard pack of cards as a 
	 * string.
	 */
	private static String deckOfcards[]={"AHrts", "2Hrts", "3Hrts", "4Hrts", "5Hrts", "6Hrts",
            "7Hrts", "8Hrts", "9Hrts", "10Hrts", "JHrts",
            "QHrts", "KHrts",
            "ADmnds", "2Dmnds", "3Dmnds", "4Dmnds", "5Dmnds",
            "6Dmnds", "7Dmnds", "8Dmnds", "9Dmnds", "10Dmnds",
            "JDmnds", "QDmnds", "KDmnds",
            "ASpds", "2Spds", "3Spds", "4Spds", "5Spds", "6Spds",
            "7Spds", "8Spds", "9Spds", "10Spds", "JSpds",
            "QSpds", "KSpds",
            "AClbs", "2Clbs", "3Clbs", "4Clbs", "5Clbs", "6Clbs",
            "7Clbs", "8Clbs", "9Clbs", "10Clbs", "JClbs",
            "QClbs", "KClbs"};
	/**
	 * The default values which result in a win, used in the demonstration
	 */
	public static final String[] defaultWinningValues = {"AHrts", "ADmnds", "ASpds", "AClbs"};
	
	/**
	 * The default message to users when waiting for input, used in the example 
	 */
	private static String defaultInputString = "Hit <RETURN> to choose a card";
	
	/**
	 * Default constructor which uses a random number generator and then sets all
	 * values to those used in the original files. Used in the demonstration.
	 * @param rng A RandomInterface used for generating random numbers for this game
	 */
	public CardGame(RandomInterface rng){
		super(2, false, rng, "chose");
		this.possibleValues.addAll(Arrays.asList(deckOfcards));
		this.winningValues.addAll(Arrays.asList(defaultWinningValues));
		this.inputString = defaultInputString;
	}
	
	/**
	 * Unused constructor which allows you to modify the game to change victory
	 * conditions or the number of decks.
	 * @param rng A RandomInterface used for generating random numbers for this game
	 * @param noOfDecks The number of full decks to use
	 * @param winningValuesIn An ArrayList of Strings containing all cards which result in a win
	 */
	public CardGame(RandomInterface rng, int noOfDecks, ArrayList<String> winningValuesIn){
		super(2, false, rng, "chose");
		for(int i = 0; i < noOfDecks ; i++){
			this.possibleValues.addAll(Arrays.asList(deckOfcards));
		}
		this.winningValues.addAll(winningValuesIn);
		this.inputString = defaultInputString;
	}

}
