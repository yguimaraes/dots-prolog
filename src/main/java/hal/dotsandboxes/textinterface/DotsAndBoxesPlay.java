package hal.dotsandboxes.textinterface;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import hal.dotsandboxes.Edge;
import hal.dotsandboxes.Game;
import hal.dotsandboxes.GameState;
import hal.dotsandboxes.Player;
import hal.util.Pair;

import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class DotsAndBoxesPlay {
	private final int mOutWidth = 80;
	
	private final PrintStream mOut = System.out;
	
	private final int mBoardWidth;
	private final int mBoardHeight;
	
	private final Game mGame;	
	private final Player mPlayer1;
	private final Player mPlayer2;
	
	private final GameGridPrinter mPrinter;
        
        private int turn;
        
        GameState mState;
	
	public DotsAndBoxesPlay(int width, int height, Player p1, Player p2, 
			GameGridPrinter printer, boolean alwaysShowBoard) {
		checkNotNull(p1, "player1 was null");
		checkNotNull(p2, "player2 was null");
		checkArgument(width > 1, "width was < 2");
		checkArgument(height > 1, "height was < 2");
		checkArgument(!p1.equals(p2), "player 1 and 2 are the same");
		checkNotNull(printer);
                
		mPlayer1 = p1;
		mPlayer2 = p2;
		
		mBoardWidth = width;
		mBoardHeight = height;
		
		mGame = Game.INSTANCE;
		mPrinter = printer;
	}
	
        public void start(){ //initialize game
//            // Print the title with an underline below.
//            mOut.println(StringUtils.center(Values.TITLE, mOutWidth));
//            mOut.println(StringUtils.center(StringUtils.repeat(
//                            Values.TITLE_UNDERSCORE, Values.TITLE.length()), mOutWidth));
//            mOut.println();
//
//            // Print a message introducing the game
//            mOut.println(Values.INTRO_MSG);
//            mOut.println();
//
//            // Print a header identifying the two players
//            printPlayerAnnounce();
//            mOut.println();

            //User (mPlayer1) starts 
            final List<Player> players = ImmutableList.of(mPlayer1, mPlayer2);

            mState = GameState.get(mBoardWidth, mBoardHeight, players);

            turn = 1;
        }
        
	public GameState play(Edge lastEdge) { //each time the screen is pressed
            if(mGame.isGameCompleted(mState)){
                finish();
            }else{
			
                // Find who's gonna play
                final Player currentPlayer = mGame.getNextPlayer(mState);

                // Print the turn start message
                //mOut.format(Values.TURN_START + "\n", turn, currentPlayer.getName());

                // Let them take their turn
                GameState newState = mGame.nextMove(lastEdge, mState);

                // Print last move...
                Edge move = newState.getNewestEdge();
                System.out.print("Turn " + turn + ": "
                    + currentPlayer.getName() + " - "
                    + move.getCanX() + " " + move.getCanY() + " " 
                    + move.getCanDirection().toString());

                // Print a message if the move completed any cells
                if(newState.getCompletedCellCount() - mState.getCompletedCellCount() > 0) {
                        System.out.print(" -> Completed cell");
                }
                System.out.println();
                                
                mState = newState;
                ++turn;
                
                //if the next currentPlayer player is the machine, we call the play again
                final Player nextPlayer = mGame.getNextPlayer(mState);
                if(currentPlayer.equals(mPlayer2) && 
                        nextPlayer.equals(mPlayer2)){
                    Edge nullEdge = null;
                    play(nullEdge);
                }
            }
            return mState;
        }
                    
        public boolean checkFinish(){
            if(mGame.isGameCompleted(mState)){
                //finish();
                return true;
            }else{
                return false;
            }
        }
        
        public void finish(){ //print scores
            System.out.println();
            
            Player p1 = mState.getPlayers().get(0);
            Player p2 = mState.getPlayers().get(1);
            int score1 = mGame.getScore(mState, p1);
            int score2 = mGame.getScore(mState, p2);
                      

            if(score1 == score2)
                    mOut.println(Values.RESULT_DRAW);
            else
                    mOut.println(String.format(Values.RESULT_WINNER, 
                            (score1 > score2 ? 
                            p1 : p2).getName()));

            // Print final scores
            mOut.format(Values.SCORE + "\n", p1.getName(), 
                            mGame.getScore(mState, p1));
            mOut.format(Values.SCORE + "\n", p2.getName(), 
                            mGame.getScore(mState, p2));
	}
        
        public String getResults(){
            String answer = "";
            
            Player p1 = mState.getPlayers().get(0);
            Player p2 = mState.getPlayers().get(1);
            int score1 = mGame.getScore(mState, p1);
            int score2 = mGame.getScore(mState, p2);
            
            if(score1 == score2)
                answer += "Empate! \n";
            else
                answer += "Jogador " + (score1 > score2 ? "1" : "2") + " venceu! \n";

            // Print final scores
            answer += "  - Jogador 1: "
                    + mGame.getScore(mState, p1) + "\n";
            answer += "  - Jogador 2: "
                    + mGame.getScore(mState, p2) + "\n";
            
            return answer;
        }
	
	/**
	 * Gives the spacing required on either side of str to centre it in an area 
	 * of the specified width.
	 *  
	 * @param str The string to centre.
	 * @param width The width of the area to centre the string in.
	 * @return The left and right padding required.
	 */
	private static Pair<Integer, Integer> centerPad(String str, int width) {
		return centerPad(str.length(), width);
	}
	
	/**
	 * Gives the spacing required on either side of a string of width 
	 * {@code strWidth} to centre it in an area of the specified width.
	 *  
	 * @param strWidth The width of the string to centre.
	 * @param width The width of the area to centre the string in.
	 * @return The left and right padding required.
	 */
	private static Pair<Integer, Integer> centerPad(int strWidth, 
			int width) {
		Preconditions.checkArgument(strWidth >= 0, "strWidth must be positive");
		final int totalPad = Math.max(0, width - strWidth);
		
		return Pair.of(
				totalPad / 2, 
				totalPad % 2 == 0 ? totalPad / 2 : (totalPad / 2) + 1);
	}
	
	/**
	 * Tries to format the name and score into a string of availableWidth.
	 *  
	 * @param availableWidth
	 * @param name
	 * @param score
	 * @return
	 */
	private static String makeScoreStr(int availableWidth, String name, 
			int score) {
		
		String min = String.format(Values.SCORE, "", score);
		int nameSpace = Math.max(0, availableWidth - min.length());
		
		String compressedName = StringUtils.abbreviate(name, nameSpace);
		final String out = String.format(Values.SCORE, compressedName, score);
		
		// Ensure the output is at least availableWidth in length
		return out;
	}
	
	private String makeScoreAndTitleUnderlineLine(GameState gamestate) {
		
		Preconditions.checkArgument(gamestate.getPlayers().size() == 2, 
				"Two players expected in gamestate.");
		
		Player p1 = gamestate.getPlayers().get(0);
		int p1Score = mGame.getScore(gamestate, p1);
		Player p2 = gamestate.getPlayers().get(1);
		int p2Score = mGame.getScore(gamestate, p2);
		
		final String underline = StringUtils.repeat(
				Values.TITLE_UNDERSCORE, Values.SHOW_BOARD_TITLE.length());
		final Pair<Integer, Integer> pad = centerPad(underline, mOutWidth);
		
		String p1Str = makeScoreStr(pad.first() - 1, p1.getName(), p1Score);
		String p2Str = makeScoreStr(pad.second() - 1, p2.getName(), p2Score);
		
		return StringUtils.rightPad(p1Str, pad.first() - 1) 
				+ " " + underline + " " 
				+ StringUtils.leftPad(p2Str, pad.second() - 1);
	}
	
	private String makeTitleLine() {
		final Pair<Integer, Integer> pad = centerPad(Values.SHOW_BOARD_TITLE, 
				mOutWidth);
		return StringUtils.repeat(" ", pad.first()) + Values.SHOW_BOARD_TITLE + 
			StringUtils.repeat(" ", pad.second());
	}
	
	private void printBoard(GameState state) {
		
		mOut.println(makeTitleLine());
		mOut.println(makeScoreAndTitleUnderlineLine(state));
		mOut.println();
		mPrinter.printState(state, mOut);
		mOut.println();
	}
	
	private void printPlayerAnnounce() {
		String left = String.format(Values.PLAYER_ANNOUNCE, 
				1, mPlayer1.getName(), mPlayer1.getDecisionEngine().getName());
		
		String right = String.format(Values.PLAYER_ANNOUNCE, 
				2, mPlayer2.getName(), mPlayer2.getDecisionEngine().getName());
		
		String middle = Values.PLAYER_ANNOUNCE_SEPARATOR;
		
		int len = left.length() + middle.length() + right.length();
		int pad = Math.max(2, mOutWidth - len);
		
		final int padLeft, padRight;
		
		if(pad % 2 == 0)
			padLeft = padRight = pad / 2;
		else {
			padLeft = pad / 2; padRight = padLeft + 1;
		}
		
		mOut.println(left + 
				   StringUtils.repeat(" ", padLeft) + 
				   middle + 
				   StringUtils.repeat(" ", padRight) + 
				   right);
	}
}
