package hal.dotsandboxes;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class DefaultEdgeTest {

	@Test
	public void testHashCodeEquals1() {
		DefaultEdge a = DefaultEdge.obtain(0, 0, Direction.RIGHT);
		DefaultEdge b = DefaultEdge.obtain(1, 0, Direction.LEFT);
		
		assertThat(a, equalTo(b));
		assertThat(a.hashCode(), equalTo(b.hashCode()));
	}
	
	@Test
	public void testHashCodeEquals2() {
		DefaultEdge a = DefaultEdge.obtain(0, 0, Direction.BELOW);
		DefaultEdge b = DefaultEdge.obtain(0, 1, Direction.ABOVE);
		
		assertThat(a, equalTo(b));
		assertThat(a.hashCode(), equalTo(b.hashCode()));
	}
	
	@Test
	public void testAddSet() {
		DefaultEdge a = DefaultEdge.obtain(0, 0, Direction.BELOW);
		DefaultEdge b = DefaultEdge.obtain(0, 1, Direction.ABOVE);
		
		DefaultEdge c = DefaultEdge.obtain(0, 0, Direction.RIGHT);
		DefaultEdge d = DefaultEdge.obtain(1, 0, Direction.LEFT);
		
		ImmutableSet<Edge> edges = ImmutableSet.<Edge>of(a, b, c, d);
		
		assertThat(edges.size(), equalTo(2));
		assertTrue(edges.contains(a));
		assertTrue(edges.contains(b));
		assertTrue(edges.contains(c));
		assertTrue(edges.contains(d));
	}
}

