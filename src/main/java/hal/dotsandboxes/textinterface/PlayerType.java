package hal.dotsandboxes.textinterface;

/**
 * Enumerates the supported methods of making game play decisions.
 * 
 * <p>Note that lower case names are used for user friendliness as the names are
 * used directly by the command line argument parser.
 * 
 * @author Hal Blackburn
 */
public enum PlayerType {
	human, 
	prolog, 
	stupid, 
	java;
}
