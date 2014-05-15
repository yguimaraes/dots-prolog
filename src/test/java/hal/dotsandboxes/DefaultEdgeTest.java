package hal.dotsandboxes;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class DefaultEdgeTest {

	@Test
	public void testHashCodeEquals1() {
		Edge a = Edge.obtain(0, 0, Direction.RIGHT);
		Edge b = Edge.obtain(1, 0, Direction.LEFT);
		
		assertThat(a, equalTo(b));
		assertThat(a.hashCode(), equalTo(b.hashCode()));
	}
	
	@Test
	public void testHashCodeEquals2() {
		Edge a = Edge.obtain(0, 0, Direction.BELOW);
		Edge b = Edge.obtain(0, 1, Direction.ABOVE);
		
		assertThat(a, equalTo(b));
		assertThat(a.hashCode(), equalTo(b.hashCode()));
	}
	
	@Test
	public void testAddSet() {
		Edge a = Edge.obtain(0, 0, Direction.BELOW);
		Edge b = Edge.obtain(0, 1, Direction.ABOVE);
		
		Edge c = Edge.obtain(0, 0, Direction.RIGHT);
		Edge d = Edge.obtain(1, 0, Direction.LEFT);
		
		ImmutableSet<Edge> edges = ImmutableSet.<Edge>of(a, b, c, d);
		
		assertThat(edges.size(), equalTo(2));
		assertTrue(edges.contains(a));
		assertTrue(edges.contains(b));
		assertTrue(edges.contains(c));
		assertTrue(edges.contains(d));
	}
}

