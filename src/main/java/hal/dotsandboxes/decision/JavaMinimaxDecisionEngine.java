package hal.dotsandboxes.decision;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.max;
import static java.lang.Math.min;
import hal.dotsandboxes.Edge;
import hal.dotsandboxes.Game;
import hal.dotsandboxes.GameState;
import hal.dotsandboxes.GameState.ItemType;
import hal.dotsandboxes.textinterface.Values;
import hal.dotsandboxes.Player;
import hal.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import static com.google.common.collect.Collections2.filter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;

/**
 * A decision engine implementing the MiniMax algorithm in Java.
 * 
 * @author Hal
 */
public class JavaMinimaxDecisionEngine implements DecisionEngine {
	
	private static final Random RANDOM = new Random();
	
	private static Predicate<Pair<Edge, Float>> scoreEqualTo(
			final float score) {
		
		return new Predicate<Pair<Edge,Float>>() {
			@Override
			public boolean apply(Pair<Edge, Float> input) {
				return input.second().floatValue() == score;
			}
		};
	}
	
	private final int mLookAheadDepth;
	
	private final boolean mIsAlphaBetaPruningEnabled;
	
	public static enum MinimaxType { NORMAL_MINIMAX, ALPHA_BETA_PRUNING }
	
	public JavaMinimaxDecisionEngine(int lookaheadMoveDepth, 
			MinimaxType minimaxType) {
		checkArgument(lookaheadMoveDepth >= 0);
		checkNotNull(minimaxType);
		mLookAheadDepth = lookaheadMoveDepth;
		mIsAlphaBetaPruningEnabled = 
			minimaxType == MinimaxType.ALPHA_BETA_PRUNING;
	}
	
	@Override
	public Edge makeMove(GameState gameState, Player player, Game game) {
		
		System.out.format(Values.COMPUTER_THINKING + "\n", getName());
		
		checkState(gameState.getEdgeCount() < gameState.getMaxEdges(), 
				"board is full");
		long start = System.currentTimeMillis();
		try {
			return runMinimax(game, gameState, player, mLookAheadDepth, 
					mIsAlphaBetaPruningEnabled);
		}
		finally {
			System.out.format("  Time taken: %s seconds.\n",
					(System.currentTimeMillis() - start) / 1000f);
		}
	}
	
	@Override
	public String getName() {
		return String.format((mIsAlphaBetaPruningEnabled ? 
				"Java AlphaBeta (%d)" : "Java MiniMax (%d)"), mLookAheadDepth);
	}
	
	private static Edge runMinimax(Game game, GameState state, 
			Player favoredPlayer, int startDepth, boolean alphabetaEnabled) {
		checkNotNull(game);
		checkNotNull(state);
		checkNotNull(favoredPlayer);
		checkArgument(startDepth > 0);
		checkState(game.getNextPlayer(state).equals(favoredPlayer), 
			"Our player is not due to play!");
		
		Builder<Pair<Edge, Float>> moves = ImmutableList.builder();
		float best = Float.NEGATIVE_INFINITY;
		
		for(Edge e : state.edges(ItemType.NON_EXISTING)) {
			float score = alphabetaEnabled ?
					alphabeta(game, state.withEdge(e, favoredPlayer), 
							favoredPlayer, startDepth - 1, 
							Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY) :
					minimax(game, state.withEdge(e, favoredPlayer),
							favoredPlayer, startDepth - 1);
			
			if(score > best)
				best = score;
			
			moves.add(Pair.of(e, score));
		}
		
		List<Pair<Edge, Float>> bestMoves = ImmutableList.copyOf(
				filter(moves.build(), scoreEqualTo(best)));
		
		return bestMoves.get(RANDOM.nextInt(bestMoves.size())).first();
	}
	
	/**
	 * Determines the utility of a given game state by recursively applying the
	 * minimax algorithm. The player being maximised is {@link #mPlayer}.
	 *  
	 * @param state The gamestate to evaluate.
	 * @param depth The maximum number of times we can expand a state's 
	 *              children.
	 * @return How good the board is for {@link #mPlayer}. The return values are
	 *         the same as described in the {@link #estimateValue(GameState)} 
	 *         method.
	 * 
	 * @see #estimateValue(GameState)
	 */
	private static float minimax(
			final Game game, 
			final GameState state, 
			final Player favoredPlayer,
			final int depth) {
		
		// When depth is zero we are not allowed to expand any further children
		// so we must return the current estimated value of the board. Similarly
		// if the game is completed we obviously cannot continue as there are no
		// further moves to take.
		if(depth <= 0 || game.isGameCompleted(state)) {
			return estimateValue(game, state, favoredPlayer);
		}
		
		// Find the player who will be playing at this point in the game. If the
		// player is our mPlayer we try to maximise the score of the available 
		// options. If it is not us playing, we minimise the score of the 
		// options as we assume the opponent will attempt to choose the move 
		// worst for us.
		final Player current = game.getNextPlayer(state);
		
		// Initialise the 'best' score to a starting value to be replaced as we
		// evaluate the children.
		float bestScore = favoredPlayer.equals(current) ?
				Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
		
		// Now we evaluate each of the possible moves we can make from this 
		// state, determining how useful to us they are.
		for(Edge e : state.edges(ItemType.NON_EXISTING)) {
			
			// Evaluate the utility of the child
			float score = minimax(game, state.withEdge(e, current), 
					favoredPlayer, depth - 1);
			
			bestScore = favoredPlayer.equals(current) ? 
					max(bestScore, score) : min(bestScore, score);
		}
		
		return bestScore;
	}
	
	/**
	 * Determines the utility of a given game state by recursively applying the
	 * minimax algorithm. The player being maximised is {@link #mPlayer}.
	 *  
	 * @param state The gamestate to evaluate.
	 * @param depth The maximum number of times we can expand a state's 
	 *              children.
	 * @return How good the board is for {@link #mPlayer}. The return values are
	 *         the same as described in the {@link #estimateValue(GameState)} 
	 *         method.
	 * 
	 * @see #estimateValue(GameState)
	 */
	private static float alphabeta(
			final Game game, 
			final GameState state, 
			final Player favoredPlayer, 
			final int depth,
			float alpha,  // -inf
			float beta) { //  inf
		
		// When depth is zero we are not allowed to expand any further children
		// so we must return the current estimated value of the board. Similarly
		// if the game is completed we obviously cannot continue as there are no
		// further moves to take.
		if(depth <= 0 || game.isGameCompleted(state)) {
			return estimateValue(game, state, favoredPlayer);
		}
		
		// Find the player who will be playing at this point in the game. If the
		// player is our mPlayer we try to maximise the score of the available 
		// options. If it is not us playing, we minimise the score of the 
		// options as we assume the opponent will attempt to choose the move 
		// worst for us.
		final Player current = game.getNextPlayer(state);
		
		// Now we evaluate each of the possible moves we can make from this 
		// state, determining how useful to us they are.
		for(Edge e : state.edges(ItemType.NON_EXISTING)) {
			
			// Evaluate the utility of the child
			float score = alphabeta(game, state.withEdge(e, current), 
					favoredPlayer, depth - 1, alpha, beta);
			
			if(favoredPlayer.equals(current))
				alpha = max(alpha, score);
			else
				beta = min(beta, score);
			
			if(beta < alpha) {
				//System.out.println("a-b cutoff at depth: " + depth);
				break;
			}
		}
		
		return favoredPlayer.equals(current) ? alpha : beta;
	}
	
	/**
	 * Determines the utility of a given game state by recursively applying the
	 * minimax algorithm. The player being maximised is {@link #mPlayer}.
	 *  
	 * @param state The gamestate to evaluate.
	 * @param depth The maximum number of times we can expand a state's 
	 *              children.
	 * @return How good the board is for {@link #mPlayer}. The return values are
	 *         the same as described in the {@link #estimateValue(GameState)} 
	 *         method.
	 * 
	 * @see #estimateValue(GameState)
	 */
	private static float alphabetaSorting(
			final Game game, 
			final GameState state, 
			final Player favoredPlayer, 
			final int depth,
			float alpha,  // -inf
			float beta) { //  inf
		
		// When depth is zero we are not allowed to expand any further children
		// so we must return the current estimated value of the board. Similarly
		// if the game is completed we obviously cannot continue as there are no
		// further moves to take.
		if(depth <= 0 || game.isGameCompleted(state)) {
			return estimateValue(game, state, favoredPlayer);
		}
		
		// Find the player who will be playing at this point in the game. If the
		// player is our mPlayer we try to maximise the score of the available 
		// options. If it is not us playing, we minimise the score of the 
		// options as we assume the opponent will attempt to choose the move 
		// worst for us.
		final Player current = game.getNextPlayer(state);
		
		final List<GameState> states = buildSortedStates(state, current, 
				stateComparatorFor(game, favoredPlayer, 
						// we want higher scoring states first if we are the 
						// maxer, otherwise lower as we are minimising 
						current.equals(favoredPlayer)));
		
		// Now we evaluate each of the possible moves we can make from this 
		// state, determining how useful to us they are.
		for(GameState gs : states) {
			
			// Evaluate the utility of the child
			float score = alphabeta(game, gs, favoredPlayer, depth - 1, alpha, 
					beta);
			
			if(favoredPlayer.equals(current))
				alpha = max(alpha, score);
			else
				beta = min(beta, score);
			
			if(beta < alpha) {
				//System.out.println("a-b cutoff at depth: " + depth);
				break;
			}
		}
		
		return favoredPlayer.equals(current) ? alpha : beta;
	}
	
	private static List<GameState> buildSortedStates(GameState state, 
			Player currentPlayer, Comparator<GameState> stateOrderer) {
		
		// Allocate an array with space to fit all the edges.
		GameState[] childStates = new GameState[
				state.getMaxEdges() - state.getEdgeCount()];
		int i = 0;
		for(GameState childState : Iterables.transform(
				state.edges(ItemType.NON_EXISTING), 
				createStateBuilder(state, currentPlayer))) {
			
			childStates[i] = childState;
			++i;
		}
		
		Arrays.sort(childStates, stateOrderer);
		
		return Collections.unmodifiableList(Arrays.asList(childStates));
	}
	
	private static final Comparator<GameState> stateComparatorFor(
			final Game game, final Player maxer, final boolean highestFirst) {
		
		return new Comparator<GameState>() {
			
			@Override
			public int compare(GameState gs1, GameState gs2) {
				final float score1 = estimateValue(game, gs1, maxer);
				final float score2 = estimateValue(game, gs2, maxer);
				
				// comparison will be positive if gs1 has a larger heuristic 
				// value than gs2.
				float comparison = score1 - score2;
				
				int intComparison;
				if(comparison == 0)
					intComparison = 0;
				else if(comparison < 0)
					intComparison = -1;
				else 
					intComparison = 1;
				
				// if we want the highest scoring states to occur first we must
				// say that states with higher heuristic scores are < those with
				// lower scores
				//
				// note that negative return = first argument less than second
				return highestFirst ? -intComparison : intComparison;
				
				
			}
		};
	}
	
	private static final Function<Edge, GameState> createStateBuilder(
			final GameState state, final Player edgeOwner) {
		
		return new Function<Edge, GameState>() {
			public GameState apply(Edge input) {
				return state.withEdge(input, edgeOwner);
			}
		};
	}
	
	/**
	 * Calculates a heuristic estimate of how good the board is for the 
	 * {@link #mPlayer}.
	 * 
	 * <p>The values returned are in the interval [-2, 2], where 2 means the 
	 * player specified owns all the boards cells. -2 means the enemy player 
	 * owns all the cells. Values of 1 or more mean the player has won as they
	 * hold the majority of the cells. A draw will result in a 0. 
	 * 
	 * @param state The state of the game.
	 * @param maxer The player to estimate in favour of. i.e. if the board is 
	 *              beneficial for this player, the return value will be 
	 *              positive.
	 * @return A value representing the utility of the board for the given 
	 *         player. Boards in which the player is winning will return 
	 *         positive values.
	 */
	private static float estimateValue(Game game, GameState state, 
			Player maxer) {
		
		assert state != null;
		
		Player p1 = state.getPlayers().get(0);
		Player p2 = state.getPlayers().get(1);
		
		assert !p1.equals(p2);
		
		int maxScore = state.getCellCountX() * state.getCellCountY();
		
		// Find the scores in the interval [0, 2]. A score of 0 means the player
		// holds no cells, a score of 2 means the player holds all the cells. 
		// In this way a score > 1 means the player has won.
		float p1Score = game.getScore(state, p1) / (maxScore / 2.0f);
		float p2Score = game.getScore(state, p2) / (maxScore / 2.0f);
		
		assert maxer.equals(p1) || maxer.equals(p2);
		
		if(maxer.equals(p1)) {
			return p1Score - p2Score;
		}
		else {
			return p2Score - p1Score;
		}
	}
}
