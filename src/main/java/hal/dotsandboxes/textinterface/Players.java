package hal.dotsandboxes.textinterface;

import hal.dotsandboxes.Player;
import hal.dotsandboxes.decision.DecisionEngine;
import hal.dotsandboxes.decision.PrologDecisionEngine;
import hal.dotsandboxes.decision.UserInputDecisionEngine;
import hal.dotsandboxes.prolog.PrologRunner;
import hal.dotsandboxes.prolog.Utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.SystemUtils;

import com.google.common.collect.ImmutableList;

public final class Players {
    private Players() { throw new AssertionError(); }
	
    public static ImmutableList<Player> makePlayers(GameOptions options) throws IOException {
		
        PrologRunner plRunner = null;
        File plFile = null;
        if(options.getPlayerOneType() == PlayerType.prolog || options.getPlayerTwoType() == PlayerType.prolog) {
            // Extract the prolog source to a temp file...
            plFile = Utils.extractResourceToTempFile(Utils.DOTS_AND_BOXES_PROLOG_FILE);
		
            //O executavel, ex: swipl
            File prologExecutable;
            if("swipl".contains(SystemUtils.FILE_SEPARATOR))
                
                prologExecutable = new File("swipl");
            else {
                prologExecutable = Utils.findProgramOnPath("swipl");
                if(prologExecutable == null) {
                    System.err.println("Could not find program in the search path: " + "swipl");
                    System.exit(1);
                }
            }
			

            plRunner = new PrologRunner(prologExecutable);
        }
		
        return ImmutableList.of(
            makePlayerOne(options, plRunner, plFile),
            makePlayerTwo(options, plRunner, plFile));
	}
	
	public static Player makePlayerOne(GameOptions options, PrologRunner plRunner,
			File plFile) {
		return makePlayer(options.getPlayerOneName(), options.getPlayerOneType(), 
				options.getP1Lookahead(), plRunner, plFile);
	}
	
	public static Player makePlayerTwo(GameOptions options, PrologRunner plRunner,
			File plFile) {
		return makePlayer(options.getPlayerTwoName(), options.getPlayerTwoType(), 
				options.getP2Lookahead(), plRunner, plFile);
	}
	
	public static Player makePlayer(String name, PlayerType controllerType, 
			int lookahead, PrologRunner plRunner, File plFile) {
		
		DecisionEngine de;
		switch(controllerType) {
			case human:
				de = new UserInputDecisionEngine(System.out, System.in);
				break;
			case prolog:
				de = new PrologDecisionEngine(lookahead, plRunner, plFile);
				break;
			default:
				throw new AssertionError();
		}
		
		return new Player(name, de);
	}
}
