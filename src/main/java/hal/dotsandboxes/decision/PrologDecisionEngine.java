package hal.dotsandboxes.decision;

import java.io.File;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import hal.dotsandboxes.DefaultEdge;
import hal.dotsandboxes.Direction;
import hal.dotsandboxes.Edge;
import hal.dotsandboxes.Game;
import hal.dotsandboxes.GameState;
import hal.dotsandboxes.Player;
import hal.dotsandboxes.GameState.ItemType;
import hal.dotsandboxes.prolog.PrologRunner;
import hal.dotsandboxes.prolog.PrologRunner.PrologException;
import hal.dotsandboxes.textinterface.Values;
import static com.google.common.base.Preconditions.*;


/**
 * A decision engine which delegates to a prolog AI implementation.
 * 
 * @author Hal
 */
public class PrologDecisionEngine implements DecisionEngine {

	private static final Pattern MOVE_PATTERN = Pattern.compile(
			// Groups: 1=score, 2=x, 3=y, 4=direction
			"move\\(([-+]?[0-9]*\\.?[0-9]+)," +
			"edge\\(([0-9]+),([0-9]+),(down|right)\\)\\)");
	
	private final PrologRunner mRunner;
	
	private final int mDepth;
	
	private final File mPrologMinimaxSourceFile;
	
	public PrologDecisionEngine(int depth, PrologRunner runner, 
			File prologMinimaxSourceFile) {
		checkArgument(depth > 0);
		
		mRunner = checkNotNull(runner);
		mDepth = depth;
		mPrologMinimaxSourceFile = checkNotNull(prologMinimaxSourceFile);
	}
	
	@Override
	public Edge makeMove(GameState gameState, Player player, Game game) {
		
		System.out.format(Values.COMPUTER_THINKING + "\n", getName());
		long start = System.currentTimeMillis();
		String moveRequestTerm = buildGoalTerm(gameState, player, mDepth);
		
		String response;
		
		try {
			response = mRunner.execute(moveRequestTerm, 
					mPrologMinimaxSourceFile.getAbsolutePath());
		} catch (PrologException e) {
			System.err.println("Error running prolog code: " + e);
			System.exit(1);
			throw new AssertionError(
					"this won't be thrown but compiler complains without it");
		}
		Matcher matcher = MOVE_PATTERN.matcher(response);
		
		// Validate the response
		if(!matcher.find()) {
			System.err.println("ERROR: Cannot make move, malformed response " +
					"from prolog interpreter:");
			if(StringUtils.isEmpty(response))
				System.err.println("(empty string)");
			else
				System.err.println(response);
			System.exit(1);
		}
		
		Edge move = DefaultEdge.obtain(
				Integer.parseInt(matcher.group(2)), 
				Integer.parseInt(matcher.group(3)),
				matcher.group(4).equals(ATOM_RIGHT) ? 
						Direction.RIGHT : Direction.BELOW);
		
		// Fail hard if prolog code tries to make an existing move.
		checkState(!gameState.containsEdge(move), 
				"Prolog code made existing move");
		
		System.out.format("  Time taken: %s seconds.\n",
				(System.currentTimeMillis() - start) / 1000f);
		return move;
	}

	@Override
	public String getName() {
		return String.format("%s (%d)", "Prolog MiniMax", mDepth);
	}
	
	private static final String TERM_BASE = "main(%d, %d, %s, %d, %s)";
	private static final String EDGE_PLAYER_PAIR_BASE = "edge(%d, %d, %s)-%s";
	private static final String ATOM_RIGHT = "right";
	private static final String ATOM_DOWN = "down";
	private static final String ATOM_PLAYER_1 = "p1";
	private static final String ATOM_PLAYER_2 = "p2";
	private static final String LIST_START = "[";
	private static final String LIST_END = "]";
	private static final String LIST_ITEM_SEPARATOR = ", ";
	private static final String PLAYER_LIST = "[p1, p2]";
	
	private static String buildGoalTerm(GameState state, Player player, 
			int depth) {
		
		return String.format(TERM_BASE, 
				state.getNodeCountX(),
				state.getNodeCountY(),
				buildEdgeList(state),
				depth,
				PLAYER_LIST
				);
	}
	
	private static String buildEdgeList(GameState state) {
		StringBuilder sb = new StringBuilder(LIST_START);
		
		final Player firstPlayer = state.getPlayers().get(0);
		final Player secondPlayer = state.getPlayers().get(1);
		
		Iterator<Edge> edges = state.edges(ItemType.EXISTING).iterator();
		while(edges.hasNext()) {
			final Edge e = edges.next(); 
			Player p = state.getEdgeOwner(e);
			sb.append(String.format(EDGE_PLAYER_PAIR_BASE,
					e.getCanX(), e.getCanY(), 
					(e.getCanDirection() == Direction.RIGHT ?
							ATOM_RIGHT : ATOM_DOWN),
					p.equals(firstPlayer) ? ATOM_PLAYER_1 : ATOM_PLAYER_2));
			
			if(edges.hasNext()) {
				sb.append(LIST_ITEM_SEPARATOR);
			}
		}
		
		sb.append(LIST_END);
		return sb.toString();
	}
}

