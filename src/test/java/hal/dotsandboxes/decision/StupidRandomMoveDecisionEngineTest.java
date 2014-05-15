package hal.dotsandboxes.decision;

import static org.junit.Assert.*;
import hal.dotsandboxes.Game;
import hal.dotsandboxes.GameState;
import hal.dotsandboxes.Player;
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
		Player p = new Player("bob", new StupidRandomMoveDecisionEngine());
		GameState s = GameState.get(3, 3, ImmutableList.of(p));
		Game game = Game.INSTANCE;
		
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
