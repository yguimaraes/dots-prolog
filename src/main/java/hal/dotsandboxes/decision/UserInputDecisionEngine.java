package hal.dotsandboxes.decision;


import static com.google.common.base.Preconditions.*;
import hal.dotsandboxes.Direction;
import static hal.dotsandboxes.DotsAndBoxesUtils.edgeInBounds;
import hal.dotsandboxes.Edge;
import hal.dotsandboxes.Game;
import hal.dotsandboxes.GameState;
import hal.dotsandboxes.Player;
import hal.dotsandboxes.textinterface.Values;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

/**
 * A decision engine which makes moves by receiving input from a human player.
 * 
 * @author Hal
 */
public class UserInputDecisionEngine implements DecisionEngine {

	/** 
	 * A regex which matches expected user input describing an edge in terms of
	 * two integer coordinates and a direction.
	 */
	private static final Pattern INPUT_PATTERN = Pattern.compile(
			"(\\d+) (\\d+) (left|l|right|r|up|u|down|d)");
	
	private final PrintStream mOut;
	
	private final InputStream mIn;
	
	public UserInputDecisionEngine(PrintStream out, InputStream in) {
		mOut = checkNotNull(out);
		mIn = checkNotNull(in);
	}
	
	@Override
	public Edge makeMove(Edge lastEdge, GameState gameState, Player player, Game game) {
		Edge move = null;
		
		do {                                       
                    Edge e = lastEdge;
                    
                    try{
                        if(!edgeInBounds(gameState, e)) {
                            return null;
                        }
                    			
                        if(gameState.containsEdge(e)) {
                            return null;
                        }

                    }catch(NullPointerException ex){
                        //System.out.println("Invalid Edge");
                        return null;
                    }
                    // All ok
                    move = e;
			
		} while(move == null);
		                
                //System.out.println("return move;");
		return move;
	}
	
	private void notifyBadInput() {
		mOut.println(Values.PROMPT_ERR_INVALID);
	}
	
	private void notifyMoveOutOfBounds() {
		mOut.println(Values.PROMPT_ERR_OUT_OF_BOUNDS);
	}
	
	private void notifyEdgeAlreadyExists() {
		mOut.println(Values.PROMPT_ERR_MOVE_ALREADY_MADE);
	}
	
	private static Direction directionFromWord(String direction) {
		switch(direction.charAt(0)) {
			case 'd': return Direction.BELOW;
			case 'u': return Direction.ABOVE;
			case 'r': return Direction.RIGHT;
			case 'l': return Direction.LEFT;
			default: throw new AssertionError("Unexpected input direction.");
		}
	}

	@Override
	public String getName() {
		return "Human";
	}
}
