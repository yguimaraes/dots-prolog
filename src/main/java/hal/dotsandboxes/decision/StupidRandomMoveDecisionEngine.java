package hal.dotsandboxes.decision;

import static hal.dotsandboxes.DotsAndBoxesUtils.edgeInBounds;
import hal.dotsandboxes.DefaultEdge;
import hal.dotsandboxes.Direction;
import hal.dotsandboxes.Edge;
import hal.dotsandboxes.Game;
import hal.dotsandboxes.GameState;
import hal.dotsandboxes.Player;

import java.util.Random;

import com.google.common.collect.ImmutableList;

/**
 * A simple and rather stupid Player implementation. The player operates with 
 * the following rules:
 * 
 * <ol>
 * <li>If a box can be completed by adding an edge then the computer will add
 * that edge.</li>
 * <li>If no box can be immediately completed a random edge is added.</li>
 * </ol>
 * 
 * @author Hal
 */
public class StupidRandomMoveDecisionEngine implements DecisionEngine {
	
	// Don't care about replayability
	private static final Random RANDOM = new Random();
	
	@Override
	public Edge makeMove(GameState gameState, Player player, Game game) {
		
		// Try to find a move which completes a box immediately
		Edge result = findEdgeCompletingBox(gameState);
		if(result != null)
			return result;
		
		// Otherwise just randomly pick an edge to add;
		if(gameState.getMaxEdges() == gameState.getEdgeCount())
			throw new IllegalStateException("Grid is full.");
		
		// Pick a random starting point to search from.
		int startX = RANDOM.nextInt(gameState.getCellCountX());
		int startY = RANDOM.nextInt(gameState.getCellCountY());
		
		return firstMoveFrom(gameState, startX, startY);
	}
	
	@Override
	public String getName() {
		return "Stupid Random AI";
	}
	
	private static Edge findEdgeCompletingBox(GameState state) {
		
		for(int x = 0; x < state.getCellCountX(); ++x) {
			for(int y = 0; y < state.getCellCountY(); ++y) {
				
				final int left, above, right, below;
				
				left = state.containsEdge(x, y, Direction.BELOW) ? 1:0;
				above = state.containsEdge(x, y, Direction.RIGHT) ? 1:0;
				right = state.containsEdge(x + 1, y, Direction.BELOW) ? 1:0;
				below = state.containsEdge(x, y + 1, Direction.RIGHT) ? 1:0;
				
				// Are there 3 out of 4 sides already completed?
				if(left + above + right + below == 3){
					if(left == 0)
						return DefaultEdge.obtain(x, y, Direction.BELOW);
					else if(above == 0)
						return DefaultEdge.obtain(x, y, Direction.RIGHT);
					else if(right == 0)
						return DefaultEdge.obtain(x + 1, y, Direction.BELOW);
					else // below
						return DefaultEdge.obtain(x, y + 1, Direction.RIGHT);
				}
			}
		}
		
		return null;
	}
	
	private static final ImmutableList<Edge> DIRECTION_DELTAS = 
		ImmutableList.<Edge>of(
				DefaultEdge.obtain(0, 0, Direction.BELOW),
				DefaultEdge.obtain(0, 0, Direction.RIGHT),
				DefaultEdge.obtain(1, 0, Direction.BELOW),
				DefaultEdge.obtain(0, 1, Direction.RIGHT)
		);
	
	static Edge firstMoveFrom(GameState state, int startX, int startY) {
		int x = startX;
		int y = startY;
		do {
			do {
				final int len = DIRECTION_DELTAS.size();
				final int start = RANDOM.nextInt(len);
				int i = start;
				do {
					Edge delta = DIRECTION_DELTAS.get(i);
					int _x = x + delta.getX();
					int _y = y + delta.getY();
					Direction d = delta.getDirection();
					
					if(edgeInBounds(state, _x, _y, d) && 
							!state.containsEdge(_x, _y, d))
						return DefaultEdge.obtain(_x, _y, d);
					i = (i + 1) % len;
				} while(i != start);
				
				y = (y + 1) % (state.getCellCountY());
			} while(y != startY);
			x = (x + 1) % (state.getCellCountX());
		} while(x != startX);
		
		throw new AssertionError("Full Board.");
	}
}
