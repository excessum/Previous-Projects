import games.Game;
import games.GameFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import random.RNGFactory;
import random.RandomInterface;

/**
 * Simple script demonstrating the functionality of my classes. Could
 * be improved to show all functionality and be made in a more OO way.
 * Currently any new RandomInterfaces or Game interfaces must be added
 * @author Curtis Stokes
 *
 */
public class Main {
	
	public static void main(String[] args) throws IOException{
		//Initialise all objects that are going to be used and create if required
		RNGFactory rngf = RNGFactory.getInstance();
		GameFactory gf = GameFactory.getInstance();
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		RandomInterface rng;
		Game game;
		//Allows the user to play muiltiple games without restarting
		boolean playAgain = false;
		do{
			//Check if the user wishes to use a specified RandomInterface or the default
			//LCG method from the original files
			System.out.println("Hello would you like to choose a Random Number Generator? (y/n)");
			if(br.readLine().equalsIgnoreCase("y")){
				System.out.println("Would you like to use Java's RNG (j) or Linear Congruential Method(l) ? (j/l)");
				rng = rngf.getRNG(br.readLine());
			}else{
				rng = rngf.getRNG("l");
			}
			//Check which game the user wants to play
			System.out.print("Card (c) or Die (d) game? ");
			game = gf.getGame(br.readLine(), rng);
			
			//While the game still requires user input ask them for the input and
			//display relevant information
			while(game.requiresInput()){
				System.out.println(game.askForInput());
				System.out.println(game.nextMove(br.readLine()));
			}
			
			//Print results of game out to the user
			System.out.println(game.getResults());	
			
			//Check if they would like to continue playing games
			System.out.println("Would you like to play again? (y/n)");
			if(br.readLine().equalsIgnoreCase("y")){
				playAgain = true;
			}else{
				playAgain = false;
			}
		}while(playAgain);
	}	
}
