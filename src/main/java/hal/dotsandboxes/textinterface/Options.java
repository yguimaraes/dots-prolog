package hal.dotsandboxes.textinterface;

import hal.dotsandboxes.textinterface.Values.BoardRepresentation;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

@CommandLineInterface(application="boxes")
public interface Options {
	
	@Option(longName="player-one-name", defaultValue="you", 
			description="The name of the first player. Default: you")
	String playerOneName();
	
	@Option(longName="player-two-name", defaultValue="cpu", 
			description="The name of the second player. Default: cpu")
	String playerTwoName();
	
	@Option(longName="player-one-type", defaultValue="prolog", 
			pattern="(human)|(stupid)|(java)|(prolog)",
			description="the controller for the first player. Default: human")
	PlayerType playerOneType();
	
	// TODO: make prolog default when finished
	@Option(longName="player-two-type", defaultValue="prolog", 
			pattern="(human)|(stupid)|(java)|(prolog)",
			description="the controller for the second player. Default: java")
	PlayerType playerTwoType();
	
	@Option(longName="width", defaultValue="4", 
			description="The number of dots wide the board is. Default: 4")
	int getWidth();
	
	@Option(longName="height", defaultValue="4", 
			description="The number of dots high the board is. Default: 4")
	int getHeight();
	
	@Option(longName="board-style", defaultValue="normal", 
			description="How the board should be displayed. Default: normal")
	BoardRepresentation getBoardRepresentation();
	
	@Option(longName="p1-lookahead", defaultValue="2",
			description="The search depth for player 1. Default: 4")
	int getP1Lookahead();
	
	@Option(longName="p2-lookahead", defaultValue="3", 
			description="The search depth for player 2. Default: 4")
	int getP2Lookahead();
	
	@Option(helpRequest=false)
	boolean getHelp();
	
	@Option(longName="prolog-type", defaultValue="swi",
			description="The type of Prolog interpreter to use. Must be one " +
					"of 'swi' or 'sicstus'. Default: swi")
	PrologEngine getPrologType();
	
	@Option(longName="prolog-executable", defaultValue="swipl",
			description="The name of the Prolog interpreter executable to " +
					"run. Can either be an absolute path, or a single word " +
					"that gets resolved from the system's standard executable" +
					" search path.")
	String getPrologPath();
	
	@Option(longName="yes", defaultValue="false", 
			description="Always show the board after each move.")
	boolean getAlwaysShowBoardAndContinue();
}
