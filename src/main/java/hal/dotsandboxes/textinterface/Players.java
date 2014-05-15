package hal.dotsandboxes.textinterface;

import hal.dotsandboxes.DefaultPlayer;
import hal.dotsandboxes.Player;
import hal.dotsandboxes.decision.DecisionEngine;
import hal.dotsandboxes.decision.JavaMinimaxDecisionEngine;
import hal.dotsandboxes.decision.JavaMinimaxDecisionEngine.MinimaxType;
import hal.dotsandboxes.decision.PrologDecisionEngine;
import hal.dotsandboxes.decision.StupidRandomMoveDecisionEngine;
import hal.dotsandboxes.decision.UserInputDecisionEngine;
import hal.dotsandboxes.prolog.PrologRunner;
import hal.dotsandboxes.prolog.SicstusPrologRunner;
import hal.dotsandboxes.prolog.SwiPrologRunner;
import hal.dotsandboxes.prolog.Utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.SystemUtils;

import com.google.common.collect.ImmutableList;

public final class Players {
    private Players() { throw new AssertionError(); }
	
    public static ImmutableList<Player> makePlayers(Options options) throws IOException {
		
        PrologRunner plRunner = null;
        File plFile = null;
        if(options.playerOneType() == PlayerType.prolog || options.playerTwoType() == PlayerType.prolog) {

            // Extract the prolog source to a temp file...
            plFile = Utils.extractResourceToTempFile(Utils.DOTS_AND_BOXES_PROLOG_FILE);
		
            //O executavel, ex: swipl
            File prologExecutable;
            if(options.getPrologPath().contains(SystemUtils.FILE_SEPARATOR))
                prologExecutable = new File(options.getPrologPath());
            else {
                prologExecutable = Utils.findProgramOnPath(options.getPrologPath());
                if(prologExecutable == null) {
                    System.err.println("Could not find program in the search path: " + options.getPrologPath());
                    System.exit(1);
                }
            }
			
            if(options.getPrologType() == PrologEngine.sicstus)
                    plRunner = new SicstusPrologRunner(prologExecutable);
            else
                    plRunner = new SwiPrologRunner(prologExecutable);
        }
		
        return ImmutableList.of(
            makePlayerOne(options, plRunner, plFile),
            makePlayerTwo(options, plRunner, plFile));
	}
	
	public static Player makePlayerOne(Options options, PrologRunner plRunner,
			File plFile) {
		return makePlayer(options.playerOneName(), options.playerOneType(), 
				options.getP1Lookahead(), plRunner, plFile);
	}
	
	public static Player makePlayerTwo(Options options, PrologRunner plRunner,
			File plFile) {
		return makePlayer(options.playerTwoName(), options.playerTwoType(), 
				options.getP2Lookahead(), plRunner, plFile);
	}
	
	public static Player makePlayer(String name, PlayerType controllerType, 
			int lookahead, PrologRunner plRunner, File plFile) {
		
		DecisionEngine de;
		switch(controllerType) {
			case java:
				de = new JavaMinimaxDecisionEngine(
						lookahead, MinimaxType.NORMAL_MINIMAX);
				break;
			case human:
				de = new UserInputDecisionEngine(System.out, System.in);
				break;
			case prolog:
				de = new PrologDecisionEngine(lookahead, plRunner, plFile);
				break;
			case stupid:
				de = new StupidRandomMoveDecisionEngine();
				break;
			default:
				throw new AssertionError();
		}
		
		return new DefaultPlayer(name, de);
	}
}
