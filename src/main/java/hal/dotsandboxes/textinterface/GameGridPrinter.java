package hal.dotsandboxes.textinterface;

import hal.dotsandboxes.Edge;
import hal.dotsandboxes.GameState;
import hal.dotsandboxes.GameState.ItemType;
import hal.dotsandboxes.Player;
import hal.util.Pair;

import java.io.PrintStream;
import java.util.Arrays;

public class GameGridPrinter {
	
	private static final boolean DEBUG = true;
	
	/** The number of blank lines of spacing between the left of the board and 
	 * the Y index numbers. */
	public final int boardPaddingLeft;
	
	/** The number of blank lines of spacing between the top of the board and 
	 * the X index numbers. */
	public final int boardPaddingAbove;
	
	/** The char to use to depict the nodes of boards. */
	public final char boardNodeChar;
	
	/** The char to use to depict horizontal edges. */
	public final char boardHorizontalEdgeChar;
	
	/** The char to use to depict vertical edges. */
	public final char boardVerticalEdgeChar;
	
	/** The char to use to depict the most recently added edge horizontally. */
	public final char boardHorizontalEdgeNewestChar;
	
	/** The char to use to depict the most recently added edge vertically. */
	public final char boardVerticalEdgeNewestChar;
	
	/** The number of characters between nodes horizontally.*/
	public final int nodeSpacingHorizontal;
	
	/** The number of lines between nodes vertically. */
	public final int nodeSpacingVertical;
	
	
	
	public GameGridPrinter(int boardPaddingLeft, int boardPaddingAbove,
			char boardNodeChar, char boardHorizontalEdgeChar,
			char boardVerticalEdgeChar, char boardHorizontalEdgeNewestChar,
			char boardVerticalEdgeNewestChar, int nodeSpacingHorizontal,
			int nodeSpacingVertical) {
		super();
		this.boardPaddingLeft = boardPaddingLeft;
		this.boardPaddingAbove = boardPaddingAbove;
		this.boardNodeChar = boardNodeChar;
		this.boardHorizontalEdgeChar = boardHorizontalEdgeChar;
		this.boardVerticalEdgeChar = boardVerticalEdgeChar;
		this.boardHorizontalEdgeNewestChar = boardHorizontalEdgeNewestChar;
		this.boardVerticalEdgeNewestChar = boardVerticalEdgeNewestChar;
		this.nodeSpacingHorizontal = nodeSpacingHorizontal;
		this.nodeSpacingVertical = nodeSpacingVertical;
	}

	public void printState(GameState state, PrintStream dest) {
		// I'm going to allocate a char buffer and write to that in
		// stages rather than taking the more efficient route of doing it char 
		// by char with individual print statements. Should make things more 
		// readable and modifiable. 
		
		final int xAxisLabelLen = Integer.toString(state.getNodeCountX())
			.length();
		final int yAxisLabelLen = Integer.toString(state.getNodeCountY())
			.length();
		
		final int outputWidth = yAxisLabelLen + boardPaddingLeft + 
			state.getNodeCountX() + ((state.getNodeCountX() - 1) * nodeSpacingHorizontal);
		final int outputHeight = xAxisLabelLen + boardPaddingAbove + 
			state.getNodeCountY() + ((state.getNodeCountY() - 1) * nodeSpacingVertical);
		
		final char[][] buffer = new char[outputHeight][outputWidth];
		for(char[] line : buffer)
			Arrays.fill(line, ' ');
		
		final int xOffset = yAxisLabelLen + boardPaddingLeft;
		final int yOffset = xAxisLabelLen + boardPaddingAbove;
		
		writeHorizontalIndicies(state, buffer, xAxisLabelLen, yAxisLabelLen);
		writeVerticalIndices(buffer, state.getNodeCountY(), yAxisLabelLen, 
				xAxisLabelLen + boardPaddingAbove);
		writeNodes(state, buffer, xOffset, yOffset);
		writeEdges(state, buffer, xOffset, yOffset);
		writeCells(state, buffer, xOffset, yOffset);
		
		for(int i = 0; i < buffer.length; ++i)
			dest.println(buffer[i]);
	}
	
	private void writeHorizontalIndicies(GameState state, char[][] buffer, 
			int xAxisLabelLen, int yAxisLabelLen) {
		
		for(int i = 0; i < state.getNodeCountX(); ++i) {
			writeHorizontalIndex(buffer, i, xAxisLabelLen, 
					yAxisLabelLen + boardPaddingLeft);
		}
	}
	
	private void writeHorizontalIndex(char[][] buffer, int index, int len, 
			int xoffset) {
		final int x = xoffset + index + (index * nodeSpacingHorizontal);
		
		String number = Integer.toString(index);
		
		for(int i = 0; i < number.length(); ++i) {
			int y = len - number.length() + i;
			put(buffer, x, y, number.charAt(i));
		}
	}
	
	private void writeVerticalIndices(char[][] buffer, int count, int maxLen, 
			int yOffset) {
		
		for(int i = 0; i < count; ++i) {
			int y = yOffset + i + (i * nodeSpacingVertical);
			String number = Integer.toString(i);
			
			for(int j = 0; j < number.length(); ++j) {
				int x = j + (maxLen - number.length());
				put(buffer, x, y, number.charAt(j));
			}
		}
	}
	
	/**
	 * Writes a char to the buffer, ensuring nothing is overwritten if 
	 * {@link #DEBUG} is true.
	 * 
	 * @param buffer The buffer to write to.
	 * @param x The x index to write to.
	 * @param y The y index to write to.
	 * @param c The char to write.
	 */
	private static void put(char[][] buffer, int x, int y, char c) {
		
		if(DEBUG && buffer[y][x] != ' ')
			throw new AssertionError(String.format(
					"Error: attempted to overwrite existing char %s at index " +
					"(%d, %d) with char: %s", buffer[y][x], x, y, c));
		buffer[y][x] = c;
	}
	
	private void writeNodes(GameState state, char[][] buffer, int xOffset, 
			int yOffset) {
		
		for(int xi = 0; xi < state.getNodeCountX(); xi++) {
			for(int yi = 0; yi < state.getNodeCountY(); ++yi) {
				
				int x = xOffset + xi + (nodeSpacingHorizontal * xi);
				int y = yOffset + yi + (nodeSpacingVertical * yi);
				put(buffer, x, y, boardNodeChar);
			}
		}
	}
	
	private void writeEdges(GameState state, char[][] buffer, int xOffset, 
			int yOffset) {
		
		final Edge newest = state.getNewestEdge();
		
		for(Edge edge : state.edges(ItemType.EXISTING)) {
			final int x, y;
			switch(edge.getCanDirection()) {
				case RIGHT:
					x = xOffset + edge.getCanX() + 1 + 
						(edge.getCanX() * nodeSpacingHorizontal);
					y = yOffset + edge.getCanY() + 
						(edge.getCanY() * nodeSpacingVertical);
					
					for(int i = 0; i < nodeSpacingHorizontal; ++i)
						put(buffer, x + i, y, edge.equals(newest) ? 
								boardHorizontalEdgeNewestChar : 
								boardHorizontalEdgeChar);
					break;
				case BELOW:
					x = xOffset + edge.getCanX() + 
						(edge.getCanX() * nodeSpacingHorizontal);
					y = yOffset + edge.getCanY() + 1 + 
							(edge.getCanY() * nodeSpacingVertical);
					
					for(int i = 0; i < nodeSpacingVertical; ++i)
						put(buffer, x, y + i, edge.equals(newest) ? 
								boardVerticalEdgeNewestChar : 
								boardVerticalEdgeChar);
					break;
			}
		}
	}
	
	private void writeCells(GameState state, char[][] buffer, int xOffset, 
			int yOffset) {
		for(Pair<Integer, Integer> coord : state.cells()) {
			Player owner = state.getCellOwner(coord.first(), coord.second());
			
			int x = xOffset + coord.first() + 1 + 
					(coord.first() * nodeSpacingHorizontal);
			int y = yOffset + coord.second() + 1 + 
					(coord.second() * nodeSpacingVertical);
			
			String name = owner.getName();
			for(int i = 0; i < Math.min(nodeSpacingHorizontal, name.length()); 
					++i) {
				put(buffer, x + i, y, name.charAt(i));
			}
		}
	}
}
