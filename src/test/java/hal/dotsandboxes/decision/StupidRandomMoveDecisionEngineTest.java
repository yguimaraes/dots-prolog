package hal.dotsandboxes.decision;

import static org.junit.Assert.*;
import hal.dotsandboxes.DefaultGame;
import hal.dotsandboxes.DefaultGameState;
import hal.dotsandboxes.DefaultPlayer;
import hal.dotsandboxes.DotsAndBoxesUtils;
import hal.dotsandboxes.Edge;
import hal.dotsandboxes.Game;
import hal.dotsandboxes.GameState;
import hal.dotsandboxes.Player;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class StupidRandomMoveDecisionEngineTest {

	@Test
	public void testFirstMoveFrom() {
		Player p = new DefaultPlayer("bob", new StupidRandomMoveDecisionEngine());
		GameState s = DefaultGameState.get(3, 3, ImmutableList.of(p));
		Game game = DefaultGame.INSTANCE;
		
		int iterations = 0;
		final int maxIterations = DotsAndBoxesUtils.gridEdgeCount(3, 3);
		while(!game.isGameCompleted(s)) {
			
			assertTrue(iterations < maxIterations);
			
			Edge e = StupidRandomMoveDecisionEngine.firstMoveFrom(s, 0, 0);
			s = s.withEdge(e, p);
			
			++iterations;			
		}
		
		assertTrue(game.isGameCompleted(s));
	}
	
}
