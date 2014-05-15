package hal.dotsandboxes;

import hal.dotsandboxes.decision.DecisionEngine;

/**
 * Represents a player in a game of dots and boxes.
 * 
 * @author Hal
 */
public interface Player {
	
	/**
	 * @return the name of the player.
	 */
	String getName();
	
	/**
	 * Gets the player's decision engine. The decision engine is responsible for
	 * the moves the player makes.
	 * 
	 * @return The player's decision engine.
	 */
	DecisionEngine getDecisionEngine();
}
