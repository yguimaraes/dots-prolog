package hal.dotsandboxes;

import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableList;
import hal.dotsandboxes.textinterface.Main;
import java.util.List;

public final class Game {

	private Game() {}
	
	public static final Game INSTANCE = new Game();
	
	public boolean isGameCompleted(GameState state) {
		return state.getEdgeCount() == state.getMaxEdges();
	}
	
	public GameState start(Player first, Player second, int width, int height) {
		checkNotNull(first);
		checkNotNull(second);
		checkArgument(width > 1, "width must be > 1");
		checkArgument(height > 1, "height must be > 1");
		
		return GameState.get(width, height, 
				ImmutableList.of(first, second));
	}

	public GameState nextMove(Edge lastEdge, GameState fromState) {
		Preconditions.checkState(!isGameCompleted(fromState), 
				"game is completed.");
		
		// Here is the logic behind the turn of a single player. We identify
		// the player who is to play next and ask them to decide which edge they
		// wish to add to the game's grid. We then return a version of the grid
		// with the new edge added.
		Player nextPlayer = getNextPlayer(fromState);
		
		Edge move = nextPlayer.getDecisionEngine().makeMove(lastEdge, fromState, 
				nextPlayer, this);
		
		// Ensure the edge does not already exist in the model
		if(fromState.containsEdge(move))
			throw new IllegalStateException(String.format("DecisionEngine %s," +
					"attempted to add pre existing edge: %s", 
					nextPlayer.getDecisionEngine(), move));
		
		return fromState.withEdge(move, nextPlayer);
	}
	
	public Player getNextPlayer(GameState state) {
		
		// If no moves have been made the first player in the list goes first.
		if(state.getEdgeCount() == 0)
			return state.getPlayers().get(0);
		
		final Player previousPlayer = state.getEdgeOwner(state.getNewestEdge());
		
		// If the player finished a square in the last move then they play 
		// again.
		if(lastMoveCompletedSquare(state)) {
			return previousPlayer;
		}
		
		// Otherwise the next player in the player list plays
		List<Player> players = state.getPlayers();
		
		assert state.getPlayers().contains(previousPlayer): "The state's " +
				"player list has to contain the previous player.";
		
		int nextIndex = (players.indexOf(previousPlayer) + 1) % players.size();
		return players.get(nextIndex);
	}
	
	/**
	 * Determines if the most recently added ege completed a cell on the board.
	 * 
	 * @param state Some gamestate.
	 * @return {@code true} if the state's most recent edge completed a cell.
	 */
	private static boolean lastMoveCompletedSquare(GameState state) {
		Edge newest = state.getNewestEdge();
		
		// Find the indexes of the cells on either side of the edge.
		final int x1, y1, x2, y2;
		
		switch(newest.getCanDirection()) {
			case RIGHT:
				x1 = newest.getCanX();
				y1 = newest.getCanY() - 1;
					
				x2 = newest.getCanX();
				y2 = newest.getCanY();
				break;
			case BELOW:
				x1 = newest.getCanX() - 1;
				y1 = newest.getCanY();
					
				x2 = newest.getCanX();
				y2 = newest.getCanY();
				break;
			default: throw new AssertionError();
		}
		
		// Check if a cell exists on either side of the edge. Because the edge 
		// is the newest edge in the grid, if a cell exists on either side it 
		// must have been finished by the placement of this edge.
		return cellExists(state, x1, y1) || cellExists(state, x2, y2);
	}
	
	private static boolean cellExists(GameState state, int cellX, int cellY) {
		return cellX >= 0 
		&& cellX < state.getCellCountX() 
		&& cellY >= 0 
		&& cellY < state.getCellCountY()
		&& state.isCellCompleted(cellX, cellY);
	}

	public int getScore(GameState state, Player player) {
		Preconditions.checkArgument(state.getPlayers().contains(player), 
				"The gamestate did not contain the player.");
		
		int score = 0;
		
		for(int x = 0; x < state.getCellCountX(); ++x)
			for(int y = 0; y < state.getCellCountY(); ++y)
				if(state.isCellCompleted(x, y))
					if(state.getCellOwner(x, y).equals(player))
						++score;
		
		return score;
	}

	public Player getWinner(GameState state) {
		checkState(isGameCompleted(state), "game was not completed");
		
		Player winner = null;
		
		// We maintain the top two scores so that we can check for ties between 
		// the top two players.
		int bestScore = -1;
		int secondBestScore = -1;
		
		for(Player p : state.getPlayers()) {
			int score = getScore(state, p);
			
			if(score > bestScore) {
				secondBestScore = bestScore;
				bestScore = score;
				winner = p;
			}
			else if(score > secondBestScore) {
				secondBestScore = score;
			}
		}
		
		// Check if the top two scores are tied.
		if(secondBestScore == bestScore)
			return null;
		
		return winner;
	}
}
