package games;

/**
 * A basic interface used by every game, these methods are used to run
 * a game no matter what its subtype is. 
 * @author Curtis Stokes
 *
 */
public interface Game {
	
	/**
	 * Method to check if the game is waiting for input from the user
	 * @return true if it is waiting for input
	 */
	public boolean requiresInput();
	
	/**
	 * Method to get the final results of the game as a String to print
	 * to the user.
	 * @return String to print to the user
	 */
	public String getResults();
	
	/**
	 * Method used to get the message to the user of what the game is requiring
	 * Although not necessary in the original implementation, this will allow 
	 * more interactive games such as hangman to be made. 
	 * @return String instructing the user on what to input
	 */
	public String askForInput();
	/**
	 * Perform the next action in the game, although input is not used in this 
	 * scenario as the games are relatively uninteractive, it will allow for more
	 * interactive games in the future. NOTE: It is assumed all RNG games are not
	 * interactive hence why the input is never used 
	 * @param input Command from user
	 * @return Result of the command to print for user.
	 */
	public String nextMove(String input);
}
