package hal.dotsandboxes.textinterface;

import java.util.ResourceBundle;

import com.google.common.base.Preconditions;

public final class Values {
	private Values() { throw new AssertionError(); }
	
	private static final String BUNDLE_NAME = 
		"hal.dotsandboxes.textinterface.messages";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);
	
	private static String load(String key) {
		return RESOURCE_BUNDLE.getString(key);
	}
	
	private static final int loadInt(String key) {
		return Integer.parseInt(load(key));
	}
	
	private static final char loadChar(String key) {
		String s = load(key);
		return s.charAt(0);
	}
	
	/** The title of the game. */
	public static final String TITLE = load("Title");
	
	public static final String TITLE_UNDERSCORE = load("TitleUnderscore");
	
	public static final String INTRO_MSG = load("IntroMessage");
	
	public static final String PLAYER_ANNOUNCE = load("PlayerAnnounce");
	public static final String PLAYER_ANNOUNCE_SEPARATOR = 
		load("PlayerAnnounceSeparator");
	
	public static final String TURN_START = load("TurnStart");
	
	public static final String ENTER_MOVE_PROMPT = load("EnterMovePrompt");
	
	public static final String PROMPT_ERR_MOVE_ALREADY_MADE = 
		load("PromptError.MoveAlreadyMade");
	
	public static final String PROMPT_ERR_INVALID = 
		load("PromptError.InvalidMove");
	
	public static final String PROMPT_ERR_OUT_OF_BOUNDS = 
		load("PromptError.MoveOutOfBounds");
	
	public static final String TURN_RESULT = load("TurnResultInfo");
	
	public static final String TURN_RESULT_COMPLETED_1 = 
		load("TurnResultCellsCompleted1");
	
	public static final String TURN_RESULT_COMPLETED_2 = 
		load("TurnResultCellsCompleted2");
	
	public static final String PROMPT_SHOW_BOARD = load("Prompt.ShowBoard");

	public static final String SHOW_BOARD_TITLE = load("ShowBoardTitle");
	
	public static final String SCORE = load("Score");
	
	public static final String COMPUTER_THINKING = load("ComputerThinking");
	
	public static final String RESULT_WINNER = load("Result.Winner");
	public static final String RESULT_DRAW = load("Result.Draw");
	
	static final char VERTICAL_EDGE_CHAR_NEWEST = loadChar("Game.Compact.VerticalEdgeCharNewest");
	
	public enum BoardRepresentation {
		normal(new GameGridPrinter(
				loadInt("Game.BoardPadLeft"), 
				loadInt("Game.BoardPadAbove"),
				loadChar("Game.NodeChar"),
				loadChar("Game.HorizontalEdgeChar"),
				loadChar("Game.VerticalEdgeChar"),
				loadChar("Game.HorizontalEdgeCharNewest"),
				loadChar("Game.VerticalEdgeCharNewest"),
				loadInt("Game.EdgeWidth"),
				loadInt("Game.EdgeHeight"))),
				
		compact(new GameGridPrinter(
				loadInt("Game.Compact.BoardPadLeft"), 
				loadInt("Game.Compact.BoardPadAbove"),
				loadChar("Game.Compact.NodeChar"),
				loadChar("Game.Compact.HorizontalEdgeChar"),
				loadChar("Game.Compact.VerticalEdgeChar"),
				loadChar("Game.Compact.HorizontalEdgeCharNewest"),
				loadChar("Game.Compact.VerticalEdgeCharNewest"),
				loadInt("Game.Compact.EdgeWidth"),
				loadInt("Game.Compact.EdgeHeight")))
		;
		
		private final GameGridPrinter mPrinter;
		
		private BoardRepresentation(GameGridPrinter printer) {
			mPrinter = Preconditions.checkNotNull(printer);
		}
		
		public GameGridPrinter getPrinter() {
			return mPrinter;
		}
	}
}
