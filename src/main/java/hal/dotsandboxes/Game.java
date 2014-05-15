package hal.dotsandboxes;


/**
 * Encapsulates the game logic of a Dots and Boxes game, representing the game
 * as a series of non destructive translations of an old game state to a new 
 * one.
 */
public interface Game {
	
	/**
	 * Starts a game with the specified players on a board of the given number 
	 * of cells in width and height.
	 * 
	 */
	GameState start(Player first, Player second, int width, int height);
	
	/**
	 * Calculates the next move to be made based on the provided previous move.
	 * 
	 * <p>At the start of a 
	 */
	GameState nextMove(GameState fromState);
	
	Player getNextPlayer(GameState state);
	
	boolean isGameCompleted(GameState state);
	
	/**
	 * Finds the score of a player. The score of the player is given by the 
	 * number of completed cells the player has personally completed.
	 * 
	 * @param game The gamestate to calculate the score for.
	 * @param player The player to calculate the score for.
	 * @return The number of completed cells the player has.
	 * 
	 * @throws IllegalArgumentException if the state does not contain the 
	 * player.
	 */
	int getScore(GameState game, Player player);
	
	/**
	 * Finds the winner (if any) of the game state. The game must be in a 
	 * completed state.
	 * 
	 * @param state The game state to find the winner of.
	 * @return The winning player, <strong>or null</strong> if the game ended in 
	 *         a tie.
	 * 
	 * @see #isGameCompleted(GameState)
	 */
	Player getWinner(GameState state);
}
