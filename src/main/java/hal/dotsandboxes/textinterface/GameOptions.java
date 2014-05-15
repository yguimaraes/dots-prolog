/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hal.dotsandboxes.textinterface;

/**
 *
 * @author yago
 */
public class GameOptions {
    private final String mPlayerOneName = "player_1";
    //@Option(longName="player-one-name", defaultValue="you", description="The name of the first player. Default: you")
    private final String mPlayerTwoName = "player_2";
    //@Option(longName="player-two-name", defaultValue="cpu", description="The name of the second player. Default: cpu")
    private final PlayerType mPlayerOneType;
    //@Option(longName="player-one-type", defaultValue="prolog", pattern="(human)|(stupid)|(java)|(prolog)",description="the controller for the first player. Default: human")
    private final PlayerType mPlayerTwoType;
    private final int mWidth;
    private final int mHeight;
    private final int mP1Lookahead;
    private final int mP2Lookahead;
    
    public GameOptions(PlayerType playerOneType, PlayerType playerTwoType, int width, int height, int p1Lookahead, int p2Lookahead){
        mPlayerOneType = playerOneType;
        mPlayerTwoType = playerTwoType;
        mWidth = width;
        mHeight = height;
        mP1Lookahead = p1Lookahead;
        mP2Lookahead = p2Lookahead;
    }
    
    public String getPlayerOneName(){
        return mPlayerOneName;
    }
    
    public String getPlayerTwoName(){
        return mPlayerTwoName;
    }
    
    public PlayerType getPlayerOneType(){
        return mPlayerOneType;
    }
    
    public PlayerType getPlayerTwoType(){
        return mPlayerTwoType;
    }
    
    public int getWidth(){
        return mWidth;
    }
    
    public int getHeight(){
        return mHeight;
    }
    
    public int getP1Lookahead(){
        return mP1Lookahead;
    }
    
    public int getP2Lookahead(){
        return mP2Lookahead;
    }
}
