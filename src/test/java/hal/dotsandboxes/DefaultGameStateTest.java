package hal.dotsandboxes;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import hal.dotsandboxes.GameState.ItemType;
import hal.dotsandboxes.decision.StupidRandomMoveDecisionEngine;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class DefaultGameStateTest {
	
	private static final List<Player> PLAYERS = ImmutableList.<Player>of(
			new Player("you", new StupidRandomMoveDecisionEngine()),
			new Player("cpu", new StupidRandomMoveDecisionEngine())
		);
	
	@Test
	public void testIteration() {
		ImmutableSet<Edge> edges = ImmutableSet.<Edge>of(
				Edge.obtain(0, 0, Direction.RIGHT),
				Edge.obtain(2, 0, Direction.RIGHT),
				Edge.obtain(5, 0, Direction.RIGHT),
				Edge.obtain(6, 1, Direction.BELOW),
				Edge.obtain(6, 1, Direction.ABOVE)
		);
		
		GameState model = GameState.get(7, 3, PLAYERS);
		for(Edge e : edges)
			model = model.withEdge(e, PLAYERS.get(0));
		
		ImmutableSet<Edge> reportedEdges = 
			ImmutableSet.copyOf(model.edges(ItemType.EXISTING));
		
		assertThat(reportedEdges, equalTo(edges));
	}
	
	@Test
	public void testSize() {
		final int width = 32;
		final int height = 12;
		
		GameState m = GameState.get(width, height, PLAYERS);
		
		assertThat(width, equalTo(m.getNodeCountX()));
		assertThat(height, equalTo(m.getNodeCountY()));
	}
	
	@Test
	public void testDefaultModelIsEmpty() {
		GameState m = GameState.get(10, 10, PLAYERS);
		
		assertThat(0, equalTo(m.getEdgeCount()));
	}
	
	@Test
	public void testAddEdge() {
		GameState empty = GameState.get(10, 10, PLAYERS);
		GameState withEdge = empty.withEdge(
				Edge.obtain(0, 0, Direction.RIGHT), 
				PLAYERS.get(0));
		
		assertThat(empty, not(equalTo(withEdge)));
		assertFalse(empty.containsEdge(0, 0, Direction.RIGHT));
		assertTrue(withEdge.containsEdge(0, 0, Direction.RIGHT));
	}
	
	@Test
	public void testCellOwner() {
		GameState model = GameState.get(10, 10, PLAYERS);
		model = model.withEdge(Edge.obtain(4, 2, Direction.RIGHT), PLAYERS.get(0))
			.withEdge(Edge.obtain(4, 3, Direction.ABOVE), PLAYERS.get(1))
			.withEdge(Edge.obtain(5, 3, Direction.LEFT), PLAYERS.get(0))
			.withEdge(Edge.obtain(5, 2, Direction.BELOW), PLAYERS.get(1));
		
		assertThat(4, equalTo(model.getEdgeCount()));
		assertThat(PLAYERS.get(1), equalTo(model.getCellOwner(4, 2)));
	}
	
	@Test(expected=RuntimeException.class)
	public void testDoubleAddFails() {
		GameState model = GameState.get(10, 10, PLAYERS);
		model = model.withEdge(Edge.obtain(0, 0, Direction.BELOW), 
				PLAYERS.get(0));
		
		// Should fail:
		model = model.withEdge(Edge.obtain(0, 1, Direction.ABOVE), PLAYERS.get(1));
	}
	
	@Test
	public void testMaxEdges() {
		GameState model = GameState.get(2, 2, PLAYERS);
		
		assertThat(4, equalTo(model.getMaxEdges()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorSizeBounds1() {
		GameState.get(1, 2, PLAYERS);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorSizeBounds2() {
		GameState.get(0, 2, PLAYERS);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorSizeBounds3() {
		GameState.get(-1, 2, PLAYERS);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorSizeBounds4() {
		GameState.get(2, 1, PLAYERS);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorSizeBounds5() {
		GameState.get(2, 0, PLAYERS);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorSizeBounds6() {
		GameState.get(2, -1, PLAYERS);
	}
}
