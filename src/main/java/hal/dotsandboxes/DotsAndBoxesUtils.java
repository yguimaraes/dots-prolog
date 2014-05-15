package hal.dotsandboxes;

public final class DotsAndBoxesUtils {
	
	private DotsAndBoxesUtils() { throw new AssertionError(); }
	
	/**
	 * Calculates the number of edges in a grid of the specified size.
	 * 
	 * @param width The width of the grid.
	 * @param height The height of the grid.
	 * @return The number of edges in the grid.
	 */
	public static int gridEdgeCount(int width, int height) {
		return 2 * (width - 1) * (height - 1) + (width - 1) + (height - 1);
	}
	
	/**
	 * Checks if the given edge is in the bounds of a gamestate of the given 
	 * size.
	 * 
	 * @param state The gamestate to check for.
	 * @param edge The edge to check.
	 * @return {@code true} if the edge falls within the grid/board area of the
	 *         gamestate, {@code false} otherwise.
	 */
	public static boolean edgeInBounds(GameState state, Edge edge) {
		return edgeInBounds(state, edge.getX(), edge.getY(), 
				edge.getDirection());
	}
	
	/**
	 * Checks if the given edge is in the bounds of a gamestate of the given 
	 * size.
	 * 
	 * @param state The gamestate to check for.
	 * @param x The x coord of the edge to check.
	 * @param y The y coord of the edge to check.
	 * @param direction The direction of the edge to check.
	 * @return {@code true} if the edge falls within the grid/board area of the
	 *         gamestate, {@code false} otherwise.
	 */
	public static boolean edgeInBounds(GameState state, int x, int y, 
			Direction direction) {
		switch(direction) {
			case LEFT:
				return x > 0 && x < state.getNodeCountX() &&
					y >= 0 && y < state.getNodeCountY();
			case RIGHT:
				return x >= 0 && x < state.getNodeCountX() - 1 &&
					y >= 0 && y < state.getNodeCountY();
			case ABOVE:
				return x >= 0 && x < state.getNodeCountX() &&
					y > 0 && y < state.getNodeCountY();
			case BELOW:
				return x >= 0 && x < state.getNodeCountX() &&
					y >= 0 && y < state.getNodeCountY() - 1;
		}
		
		throw new AssertionError();
	}
}
