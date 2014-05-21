package hal.dotsandboxes;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static hal.util.Comparables.max;
import hal.dotsandboxes.textinterface.Values.BoardRepresentation;
import hal.util.Pair;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import static hal.dotsandboxes.DotsAndBoxesUtils.gridEdgeCount;

/**
 * Implements a Dots and Boxes model.
 * 
 * @author Hal Blackburn
 */
public class GameState {
	/*
	 *  Note: We could share the mEdges array with child models created with
	 *  addEdge as long as we check that edges read are < the mEdgeCount in age. 
	 */
	
	private final int mWidth, mHeight;
	
	private final ImmutableList<Player> mPlayers;
	
	/**
	 * An array of all of the edges in the grid. Edges are stored according to 
	 * the indexes given by the {@link #indexFor(int, int, Direction)} method.
	 */
	private final EdgeData[] mEdges;
	
	/**
	 * The number of edges in the grid.
	 */
	private int mEdgeCount;
	
	private final Edge mNewestEdge;
        
        public enum ItemType { EXISTING, NON_EXISTING, ALL }
	
	/**
	 * Default constructor invoked publicly via {@link #get(int, int)}. Creates 
	 * an empty model.
	 * 
	 * @param width The number of nodes wide the grid is.
	 * @param height The number of nodes high the grid is.
	 */
	private GameState(int width, int height, 
			List<Player> players) {
		checkNotNull(players, "players collection was null");
		checkArgument(!players.isEmpty(), "players collection was empty");
		checkArgument(width > 1);
		checkArgument(height > 1);
		
		mWidth = width;
		mHeight = height;
		
		mEdgeCount = 0;
		mEdges = new EdgeData[gridEdgeCount(width, height)];
		mNewestEdge = null;
		
		mPlayers = ImmutableList.copyOf(players);
		if(mPlayers.size() != ImmutableSet.copyOf(players).size())
			throw new IllegalArgumentException("duplicate player in players " +
					"list");
	}
	
	/**
	 * Private constructor used when adding an edge.
	 * 
	 * @param base
	 * @param edge
	 * @param player
	 */
	private GameState(GameState base, Edge edge, Player player) {
		mWidth = base.mWidth;
		mHeight = base.mHeight;
		mPlayers = base.mPlayers;
		
		mEdges = Arrays.copyOf(base.mEdges, base.mEdges.length);
		mNewestEdge = edge;
		mEdges[indexFor(edge)] = new EdgeData(base.getEdgeCount() + 1, player);
		mEdgeCount = base.getEdgeCount() + 1;
	}
	
	/**
	 * Gets a new instance of a DbModel with no edges.
	 * 
	 * @param width The width of the grid.
	 * @param height The height of the grid.
	 * @return The empty DbModel.
	 */
	public static GameState get(int width, int height, 
			List<Player> players) {
		return new GameState(width, height, players);
	}
	
	public ImmutableList<Player> getPlayers() {
		return mPlayers;
	}
	
	public int getNodeCountY() {
		return mHeight;
	}

	public int getNodeCountX() {
		return mWidth;
	}
	
	public int getCellCountY() {
		return getNodeCountY() - 1;
	}
	
	public int getCellCountX() {
		return getNodeCountX() - 1;
	}
	
	public GameState withEdge(Edge edge, Player player) {
		
		checkNotNull(edge, "edge");
		checkNotNull(player, "player");
		if(!mPlayers.contains(player))
			throw new IllegalArgumentException("player is not part of this " +
					"game: " + player);
		
		// If the edge already exists then we can satisfy the request by simply
		// returning ourself.
		EdgeData edgeData;
		if((edgeData = getEdgeData(edge)) != null) {
			if(edgeData.getOwner().equals(player))
				return this;
			else
				throw new IllegalStateException(String.format("Edge already " +
						"exists with a different owner. edge: %s, owner: %s, " +
						"requested owner: %s", 
						edge, edgeData.getOwner(), player));
		}
		
		// Return a new model with the edge added using our private constructor 
		// for the job.
		return new GameState(this, edge, player);
	}
	
	public int getEdgeCount() {
		return mEdgeCount;
	}

	public int getMaxEdges() {
		return gridEdgeCount(getNodeCountX(), getNodeCountY());
	}
	
	public Edge getNewestEdge() {
		Preconditions.checkState(getEdgeCount() > 0, "No edges exist.");
		checkNotNull(mNewestEdge);
		
		return mNewestEdge;
	}
	
	public Set<Direction> getNodeEdges(int x, int y) {
		boolean left = false;
		if(x != 0)
			left = containsEdge(x, y, Direction.LEFT);
		
		boolean right = false;
		if(x < getNodeCountX() - 1)
			right = containsEdge(x, y, Direction.RIGHT);
		
		boolean up = false;
		if(y != 0)
			up = containsEdge(x, y, Direction.ABOVE);
		
		boolean down = false;
		if(y < getNodeCountY() - 1)
			down = containsEdge(x, y, Direction.BELOW);
		
		return Direction.directions(left, right, up, down);
	}

	public boolean containsEdge(int x, int y, Direction direction) {
		return getEdgeData(x, y, direction) != null;
	}
	
	public boolean containsEdge(Edge edge) {
		return getEdgeData(edge) != null;
	}

	public Player getEdgeOwner(Edge edge) {
		checkNotNull(edge, "edge was null");
		return requireEdgeData(edge).getOwner();
	}
        
        public boolean containsCell(int x, int y){
            Edge edgeTop = Edge.obtain(x, y, Direction.RIGHT);
            Edge edgeLeft = Edge.obtain(x, y, Direction.BELOW);
            Edge edgeRight = Edge.obtain(x+1, y+1, Direction.ABOVE);
            Edge edgeBottom = Edge.obtain(x+1, y+1, Direction.LEFT);
            
            //if it contains all four edges            
            if(containsEdge(edgeTop) && containsEdge(edgeLeft) &&
                    containsEdge(edgeRight) && containsEdge(edgeBottom))
                return true;
            else
                return false;
        }
	
	public Player getCellOwner(int x, int y) {
		return getEdgeOwner(getEdgeWhichCompletesCell(x, y));
	}

	public int getEdgeAge(Edge edge) {
		return requireEdgeData(edge).getAge();
	}
	
	private int indexFor(Edge edge) {
		return indexFor(edge.getX(), edge.getY(), edge.getDirection());
	}
	
	/**
	 * Defines the mapping of edge positions onto the backing flat array.
	 * 
	 * <p>Edges are defined as a node position with a direction.
	 * 
	 * <p>Horizontal edges are mapped onto the first half of the array, vertical 
	 * edges onto the second half.
	 * 
	 * @param x The x coord of the origin node.
	 * @param y The y coord of the origin node.
	 * @param direction The direction the edge emits from the node.
	 * @return The index of the node in the array.
	 */
	private int indexFor(int x, int y, Direction direction) {
		switch(direction) {
		case RIGHT:
			checkPositionIndex(x, getNodeCountX() - 1);
			checkPositionIndex(y, getNodeCountY());
			
			return x + (y * (getNodeCountX() - 1));
		case BELOW:
			checkPositionIndex(x, getNodeCountX());
			checkPositionIndex(y, getNodeCountY() - 1);
			
			int offset = (getNodeCountX() - 1) * getNodeCountY();
			return offset + x + (y * getNodeCountX());
		case LEFT:
			if(x < 1  || x >= getNodeCountX())
				throw new IndexOutOfBoundsException(String.format(
					"x (%d) out of range for direction (%s) at width (%d).", 
					x, direction, getNodeCountX()));
			if(y < 0  || y >= getNodeCountY())
				throw new IndexOutOfBoundsException(String.format(
					"y (%d) out of range for direction (%s) at height (%d).", 
					y, direction, getNodeCountY()));
			
			return indexFor(x - 1, y, Direction.RIGHT);
		case ABOVE:
			if(x < 0  || x >= getNodeCountX())
				throw new IndexOutOfBoundsException(String.format(
					"x (%d) out of range for direction (%s) at width (%d).", 
					x, direction, getNodeCountX()));
			if(y < 1  || y >= getNodeCountY())
				throw new IndexOutOfBoundsException(String.format(
					"y (%d) out of range for direction (%s) at height (%d).", 
					y, direction, getNodeCountY()));
			
			return indexFor(x, y - 1, Direction.BELOW);
		}
		throw new AssertionError("unknown direction: " + direction);
	}
	
	private Edge edgeForIndex(int index) {
		checkPositionIndex(index, mEdges.length);
		
		Direction direction;
		int x,y;
		if(index >= (getNodeCountX() - 1) * getNodeCountY()) {
			// Vertical edge
			direction = Direction.BELOW;
			index -= ((getNodeCountX() - 1) * getNodeCountY());
			
			x = index % (getNodeCountX());
			y = index / (getNodeCountX());
		}
		else {
			// Horizontal edge
			direction = Direction.RIGHT;
			x = index % (getNodeCountX() - 1);
			y = index / (getNodeCountX() - 1);
		}		
		
		return Edge.obtain(x, y, direction);
	}
	
	private EdgeData getEdgeData(Edge edge) {
		return getEdgeData(edge.getX(), edge.getY(), edge.getDirection());
	}
	
	private EdgeData getEdgeData(int x, int y, Direction direction) {
		int index = indexFor(x, y, direction);
		
		if(index < 0 || index >= mEdges.length)
			throw new IndexOutOfBoundsException(String.format("The specified " +
					"edge is outside the bounds of the board. width: %d, " +
					"height: %d, edge: %s", getNodeCountX(), getNodeCountY(), 
					Edge.obtain(x, y, direction)));
		
		return mEdges[index];
	}
	
	private EdgeData requireEdgeData(Edge edge) {
		return requireEdgeData(edge.getX(), edge.getY(), edge.getDirection());
	}
	
	private EdgeData requireEdgeData(int x, int y, Direction direction) {
		EdgeData edgeData = getEdgeData(x, y, direction);
		
		if(edgeData == null)
			throw new NoSuchElementException("Edge does not exist");
		
		return edgeData;
	}
	
	private static final class EdgeData implements Comparable<EdgeData> {

		private final int mAge;
		
		private final Player mOwner;
		
		public EdgeData(int age, Player owner) {
			mAge = age;
			mOwner = checkNotNull(owner);
		}
		
		public int getAge() {
			return mAge;
		}

		public Player getOwner() {
			return mOwner;
		}

		@Override
		public int compareTo(EdgeData o) {
			return mAge - o.mAge;
		}
	}

	public boolean isCellCompleted(int x, int y) {
		checkPositionIndex(x, getNodeCountX());
		checkPositionIndex(y, getNodeCountY());
		
		return containsEdge(x, y, Direction.RIGHT) &&
			   containsEdge(x, y, Direction.BELOW) &&
			   containsEdge(x, y + 1, Direction.RIGHT) &&
			   containsEdge(x + 1, y, Direction.BELOW);
	}

        
        //in order to get who completed the cell
	public Edge getEdgeWhichCompletesCell(int x, int y) {
		checkPositionIndex(x, getCellCountX());
		checkPositionIndex(y, getCellCountY());
		
		if(!isCellCompleted(x, y))
			throw new NoSuchElementException("The cell is not completed.");
		
		final EdgeData top = requireEdgeData(x, y, Direction.RIGHT),
				 left = requireEdgeData(x, y, Direction.BELOW),
				 bottom = requireEdgeData(x, y + 1, Direction.RIGHT),
				 right = requireEdgeData(x + 1, y, Direction.BELOW);
		
		EdgeData oldest = max(max(top, left), max(bottom, right));
		
		if(oldest == top)
			return Edge.obtain(x, y, Direction.RIGHT);
		else if(oldest == left)
			return Edge.obtain(x, y, Direction.BELOW);
		else if(oldest == bottom)
			return Edge.obtain(x, y + 1, Direction.RIGHT);
		else // right
			return Edge.obtain(x + 1, y, Direction.BELOW);
	}

	public Iterable<Edge> edges(final ItemType edgeType) {
		checkNotNull(edgeType);
		return new Iterable<Edge>() {
			@Override
			public Iterator<Edge> iterator() {
				switch(edgeType) {
					case ALL: return new AllEdgeIterator();
					case EXISTING: return new MadeEdgeIterator();
					case NON_EXISTING: return new UnMadeEdgeIterator();
					default: throw new AssertionError("can't happen");
				}
			}
		};
	}
	
	private abstract class AbstractEdgeIterator implements Iterator<Edge> {

		private int mPosition = 0;
		
		private Edge mNext = null;
		
		/**
		 * Decides if the iterator should return the edge at the provided index. 
		 * @param index The index of the edge in the edges array.
		 * @return {@code true} if the edge at the index should be returned from 
		 *         the iterator's {@link #next()} method.
		 */
		protected abstract boolean accept(int index);
		
		private Edge __next() {
			
			for(int i = mPosition; i < mEdges.length; ++i) {
				if(accept(i)) {
					mPosition = i + 1;
					return edgeForIndex(i);
				}
			}
			
			return null;
		}
		
		@Override
		public final boolean hasNext() {
			if(mNext != null)
				return true;
			
			mNext = __next();
			return mNext != null;
		}

		@Override
		public final Edge next() {
			if(hasNext()) {
				try {
					return mNext;
				} finally {
					mNext = null;
				}
			}
			throw new NoSuchElementException();
		}

		@Override
		public final void remove() {
			throw new UnsupportedOperationException("not supported");
		}
	}
	
	private final class MadeEdgeIterator extends AbstractEdgeIterator {
		@Override
		protected boolean accept(int index) {
			return mEdges[index] != null;
		}
	}
	
	private final class UnMadeEdgeIterator extends AbstractEdgeIterator {
		@Override
		protected boolean accept(int index) {
			return mEdges[index] == null;
		}
	}
	
	private final class AllEdgeIterator extends AbstractEdgeIterator {
		@Override
		protected boolean accept(int index) {
			return true;
		}
	}

	public Iterable<Pair<Integer, Integer>> cells() {
		return new Iterable<Pair<Integer,Integer>>() {
			@Override
			public Iterator<Pair<Integer, Integer>> iterator() {
				return new CellIterator();
			}
		};
	}
	
	private final class CellIterator implements 
			Iterator<Pair<Integer, Integer>> 
	{
		private int mX, mY;
		
		private Pair<Integer, Integer> mNext;
		
		private Pair<Integer, Integer> __next() {
			for(int y = mY; y < getCellCountY(); ++y) {
				for(int x = mX; x < getCellCountX(); ++x) {
				
					if(isCellCompleted(x, y)) {
						try {
							return Pair.of(x, y);
						} finally {
							mX = x + 1;
							mY = y;
						}
					}
				}
				mX = 0;
			}
			
			return null;
		}

		@Override
		public boolean hasNext() {
			if(mNext != null)
				return true;
			
			mNext = __next();
			return mNext != null;
		}

		@Override
		public Pair<Integer, Integer> next() {
			if(hasNext()) {
				try {
					return mNext;
				} finally {
					mNext = null;
				}
			}
			
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}

	public int getCompletedCellCount() {
		return Iterables.size(cells());
	}
	
	@Override
	public String toString() {
		// FIXME: do this properly
		BoardRepresentation.compact.getPrinter().printState(this, System.out);
		return super.toString();
	}
}
