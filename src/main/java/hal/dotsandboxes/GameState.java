package hal.dotsandboxes;

import hal.util.Pair;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Represents a regular grid used to play Dots and Boxes.
 *  
 * <p>The grid is comprised of dots (nodes). Horizontally or vertically 
 * adjacent nodes may be joined with an edge.
 * 
 * <p>Nodes are indexed from 0 to width-1 horizontally on the x axis, and to 
 * height-1 vertically on the y axis.
 * 
 * <p>Attempts to access nodes or edges outside the grid will result in an 
 * {@link IndexOutOfBoundsException} being thrown.
 * 
 * <p>The following is an example of a grid of width 4, height 3 with two edges:
 * <pre>
 * x   x   x   x
 * 
 * x   x---x   x
 *         |
 * x   x   x   x
 * </pre>
 * 
 * In this example there is an edge from node (1, 1) to (2, 1) and from (2, 1) 
 * to (2, 2).
 * 
 * @author Hal Blackburn
 */
public interface GameState {
	
	List<Player> getPlayers();
	
	/**
	 * Gets the number of nodes wide the model's grid is.
	 * 
	 * @return The width of the model.
	 */
	int getNodeCountX();
	
	/**
	 * Gets the number of nodes high the model's grid is.
	 * @return
	 */
	int getNodeCountY();
	
	/**
	 * @return the number of cells wide the grid is.
	 */
	int getCellCountX();
	
	/**
	 * @return the number of cells high the grid is.
	 */
	int getCellCountY();
	
	/**
	 * Gets the number of edges the model currently contains.
	 * @return The current edge count.
	 */
	int getEdgeCount();
	
	/**
	 * Gets the number of edges the grid can contain.
	 * 
	 * @return The total number of edges in the model's grid. 
	 */
	int getMaxEdges();
	
	/**
	 * @return The number of completed cells in the board.
	 */
	int getCompletedCellCount();
	
	/**
	 * Gets the set of directions representing the edges which are connected to 
	 * the node specified.
	 * 
	 * <p><strong>Examples:</strong>
	 * <ul>
	 * <li>A node with no edges returns the empty set: {}
	 * <li>A node with one edge to the right returns: {Right}
	 * <li>A node with two edges, one above and one to the left returns: {Above, 
	 * Left}
	 * </ul>
	 * 
	 * <p>In this manner the number of elements in the returned set is the 
	 * number of edges connected to the node.
	 * 
	 * @param x The x coordinate of the node whose edges to find.
	 * @param y The y coordinate of the node whose edges to find.
	 * @return The set directions for which there is a node originating at this 
	 * node.
	 */
	Set<Direction> getNodeEdges(int x, int y);
	
	boolean containsEdge(Edge edge);
	
	boolean containsEdge(int x, int y, Direction direction);
	
	boolean isCellCompleted(int x, int y);
	
	/**
	 * Gets a version of this model with an edge added from the specified node
	 * in the direction given.
	 * 
	 * <p><strong>Contract:</strong> This method must not modify this model, it
	 * must return the result of the changes in a new model instance.
	 * 
	 * @param nodeX The x coordinate of the node to start the edge from.
	 * @param nodeY The y coordinate of the node to start the edge from.
	 * @param direction The direction the edge should extend in.
	 * @param player The player who is adding the edge.
	 * @return A model with this model's edges, plus the edge specified.
	 */
	GameState withEdge(Edge edge, Player player);
	
	Player getEdgeOwner(Edge edge);
	
	int getEdgeAge(Edge edge);
	
	/**
	 * Gets the owner of a cell if the cell is completed.
	 * 
	 * @param x The x coordinate of the cell.
	 * @param y The y coordinate of the cell.
	 * @return The player who completed the edges around the cell, or null if 
	 *         the cell has not been completed.
	 */
	Player getCellOwner(int x, int y);

	/**
	 * Gets the edge which completed the specified cell.
	 * 
	 * @param cellX
	 * @param cellY
	 * @return The edge which finished the specified cel.
	 * 
	 * @see #containsCell(int, int)
	 * @throws NoSuchElementException if the cell has not been completed.
	 */
	Edge getEdgeWhichCompletesCell(int cellX, int cellY);
	
	/**
	 * Gets the edge most recently added to the model.
	 * 
	 * @return The newest edge.
	 */
	Edge getNewestEdge();
	
	/**
	 * Enumerates edges associated with the board. Either existing, non existing 
	 * or all (existing or otherwise) edges can be selected for enumeration.
	 * 
	 * <p>For example, specifying {@link ItemType#EXISTING} will return an 
	 * iterator which will only give edges that have been added to the board.
	 * Specifying {@link ItemType#NON_EXISTING} will result in the iterator 
	 * returning edges that have not been (but could be) added to the board.
	 * 
	 * @param edgeType The category of edges to enumerate.
	 * @return An iterable yielding an iterator over the edges of the given 
	 *         classification.
	 */
	Iterable<Edge> edges(ItemType edgeType);
	
	/**
	 * Gets an iterable which returns an iterator over the completed cells in 
	 * the board. The iterator will yeild a pair of integers containing the x 
	 * and y coordinates of the cell.
	 * 
	 * @return An iterable yielding the completed cells in the board.
	 */
	Iterable<Pair<Integer, Integer>> cells();
	
	/**
	 * Enumerates the classification of items an iterator should return.
	 */
	public enum ItemType { EXISTING, NON_EXISTING, ALL }
}
