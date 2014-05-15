package hal.dotsandboxes;

/**
 * Represents a move made by a player of dots and boxes. The move adds a 
 * single edge to the board.
 * 
 * <p>Edges are represented by a starting point with a direction. All edges are
 * of unit length, that is they are always between horizontally or vertically 
 * adjacent nodes.
 * 
 * <p>The normXXX() methods provide access to the edge in canonical form in 
 * which the direction may only be right or down.
 */
public interface Edge {
	
	/**
	 * @return The x coodinate of the start of the edge added by this move.
	 */
	int getX();
	
	/**
	 * @return The y coodinate of the start of the edge added by this move.
	 */
	int getY();
	
	/**
	 * @return The direction the edge extends away from the x,y position in.
	 */
	Direction getDirection();
	
	/**
	 * Gets the x coordinate for a canonical representation of an edge where
	 * the direction can only be right or down.
	 * 
	 * @return The canonical x coordinate.
	 */
	int getCanX();
	
	/**
	 * Gets the y coordinate for a canonical representation of an edge where
	 * the direction can only be right or down.
	 * 
	 * @return The canonical y coordinate.
	 */
	int getCanY();
	
	/**
	 * Gets the canonical direction of the edge which must be either right or 
	 * down.
	 * 
	 * @return The canonical direction of this edge.
	 */
	Direction getCanDirection();
}