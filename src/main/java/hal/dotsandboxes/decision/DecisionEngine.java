package hal.dotsandboxes.decision;

import hal.dotsandboxes.Edge;
import hal.dotsandboxes.Game;
import hal.dotsandboxes.GameState;
import hal.dotsandboxes.Player;

public interface DecisionEngine {
	    
        //For UserInput
	Edge makeMove(Edge lastEdge, GameState gameState, Player player, Game game);
	 
	String getName();
}
