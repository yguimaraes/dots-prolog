package hal.dotsandboxes.textinterface;

import hal.dotsandboxes.Direction;
import hal.dotsandboxes.Edge;
import hal.dotsandboxes.GameState;
import hal.dotsandboxes.Player;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Board {
    private final String boxString = "box.png";
    
    private final String edgeBlueHstring = "side_blue_h.png";
    private final String edgeBlueVstring = "side_blue_v.png";
    private final String edgeRedHstring = "side_red_h.png";
    private final String edgeRedVstring = "side_red_v.png";
    
    private final String boxBluestring = "box_blue.png";
    private final String boxRedstring = "box_red.png";
    
    private final int box_side = 50;
    private final int dot_radius = 5;
    private final int width, heigth;
    private int lastX, lastY;
    
    public final int paddX = 10, paddY = 10;
    
    public Player p1, p2;    
    
    public Board(int width, int heigth, Player p1, Player p2){
        this.width = width; //in dots
        this.heigth = heigth; //in dots
        
        this.p1 = p1;
        this.p2 = p2;
    }
    
    public int getBoxSide(){
        return box_side;
    }
    
    public int getDotRadius(){
        return dot_radius;
    }
    
    public int getExtendedBoxSide(){
        return box_side + 2*dot_radius;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getHeigth(){
        return heigth;
    }
    
    public int getBoardWidth(){
        return (width-1)*box_side + 2*dot_radius;
    }
    
    public int getBoardHeigth(){
        return (heigth-1)*box_side + 2*dot_radius;
    }
    
     public int getPaddX(){
        return paddX;
    }
    
    public int getPaddY(){
        return paddY;
    }
    
    public ImageIcon getBoardImage(){
        try{            
            BufferedImage box = ImageIO.read(new File(boxString));
            BufferedImage line = box;
            for(int i=1; i<getWidth()-1; i++) //  The first box is already placed
                line = mergeAtSide(line, box);
            
            BufferedImage board = line;
            for(int j=1; j<getHeigth()-1; j++)
                board = mergeAtBottom(board, line);
            
            return new ImageIcon(board);            
        } catch (IOException e) {
            e.printStackTrace();
            return new ImageIcon();
        }
    }
    
    public ImageIcon getBoardImage(GameState state, ImageIcon oldBoardImage) throws IOException{
        BufferedImage oldBoard = iconToBuff(oldBoardImage);

        BufferedImage edgeBlueH = ImageIO.read(new File(edgeBlueHstring));
        BufferedImage edgeBlueV = ImageIO.read(new File(edgeBlueVstring));
        BufferedImage edgeRedH = ImageIO.read(new File(edgeRedHstring));
        BufferedImage edgeRedV = ImageIO.read(new File(edgeRedVstring));
                        
        //HORIZONTAL EDGES - All to the right, except for the last column
        for(int i=0; i<getWidth() - 1; i++){
            for(int j=0; j<getHeigth(); j++){
                Edge edgeRight = Edge.obtain(i, j, Direction.RIGHT);                
                                
                if(state.containsEdge(edgeRight)){
                    //Pattern - Player1 = red, Player2 = blue
                    Player player = state.getEdgeOwner(edgeRight);

                    int x = getBoxSide() * i;
                    int y = getBoxSide() * j;

                    if(player.equals(p1))
                        oldBoard = mergeAt(oldBoard, edgeRedH, x, y);
                    else if(player.equals(p2))
                        oldBoard = mergeAt(oldBoard, edgeBlueH, x, y);
                }
            }
        }
        
        //VERTICAL EDGES - All to down, except for the last row
        for(int i=0; i<getWidth(); i++){
            for(int j=0; j<getHeigth() - 1; j++){
                Edge edgeDown = Edge.obtain(i, j, Direction.BELOW);
                
                if(state.containsEdge(edgeDown)){
                    //Pattern - Player1 = red, Player2 = blue
                    Player player = state.getEdgeOwner(edgeDown);

                    int x = getBoxSide() * i;
                    int y = getBoxSide() * j;

                    if(player.equals(p1))
                        oldBoard = mergeAt(oldBoard, edgeRedV, x, y);
                    else if(player.equals(p2))
                        oldBoard = mergeAt(oldBoard, edgeBlueV, x, y);
                }
            }
        }
        
        BufferedImage boxBlue = ImageIO.read(new File(boxBluestring));
        BufferedImage boxRed = ImageIO.read(new File(boxRedstring));
        
        //CELLS
        for(int i=0; i<getWidth() - 1; i++){
            for(int j=0; j<getHeigth() - 1; j++){                
                if(state.containsCell(i, j)){
                    Player player = state.getCellOwner(i, j);
                    
                    int x = getBoxSide() * i + 2 * getDotRadius();
                    int y = getBoxSide() * j + 2 * getDotRadius();
                    
                    if(player.equals(p1))
                        oldBoard = mergeAt(oldBoard, boxRed, x, y);
                    else if(player.equals(p2))
                        oldBoard = mergeAt(oldBoard, boxBlue, x, y);                    
                }
            }
        }
        
        return new ImageIcon(oldBoard);
    }
    
    private BufferedImage iconToBuff(ImageIcon icon){
        BufferedImage bi = new BufferedImage(
            icon.getIconWidth(),
            icon.getIconHeight(),
            BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        // paint the Icon to the BufferedImage.
        icon.paintIcon(null, g, 0,0);
        g.dispose();
        return bi;
    }
    
    private BufferedImage mergeAtSide(BufferedImage img1, BufferedImage img2){
        int w = img1.getWidth() + img2.getWidth() - 2*dot_radius;
        int h = img1.getHeight();
        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        
        Graphics g = combined.getGraphics();
        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, img1.getWidth() - 2*dot_radius, 0, null);
    
        return combined;
    }
    
    private BufferedImage mergeAtBottom(BufferedImage img1, BufferedImage img2){
        int w = img1.getWidth();
        int h = img1.getHeight() + img2.getHeight() - 2*dot_radius;
        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        
        Graphics g = combined.getGraphics();
        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, 0, img1.getHeight() - 2*dot_radius, null);
    
        return combined;
    }
    
    private BufferedImage mergeAt(BufferedImage img1, BufferedImage img2, int x, int y){
        int w = img1.getWidth();
        int h = img1.getHeight();
        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        
        Graphics g = combined.getGraphics();
        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, x, y, null);
    
        return combined;
    }
    
    public Edge getEdge(int _lastX, int _lastY){
        System.out.println("getEdge()");
        
        Edge emptyEdge = null;
        
         //Get X and Y
        lastX = _lastX - paddX;
        lastY = _lastY - paddY;
        int sizeX = lastX, sizeY = lastY;
        int countX = 0, countY = 0;
        Direction direction;

        //X and Y must be inside the board
        if(lastX > getBoardWidth() || lastY > getBoardHeigth())
            return emptyEdge;

        //X and Y
        while(sizeX > getExtendedBoxSide()){
            sizeX = sizeX - getBoxSide();
            countX++;
        }

        while(sizeY > getExtendedBoxSide()){
            sizeY = sizeY - getBoxSide();
            countY++;
        }

        if(sizeX > 2*getDotRadius() && sizeX < getBoxSide()){
            if(sizeY > 0 && sizeY < 2*getDotRadius()){
                direction = Direction.RIGHT; //(UP)
            }else if(sizeY > getBoxSide() && sizeY < 2*getDotRadius() + getBoxSide()){
                direction = Direction.RIGHT; //(DOWN)
                countY++;
            }else{
                return emptyEdge;
            }
        }else if(sizeY > 2*getDotRadius() && sizeY < getBoxSide()){
            if(sizeX > 0 && sizeX < 2*getDotRadius()){
                direction = Direction.BELOW; //(LEFT)
            }else if(sizeX > getBoxSide() && sizeX < 2*getDotRadius() + getBoxSide()){
                direction = Direction.BELOW; //(RIGTH)
                countX++;
            }else{
                return emptyEdge;
            }
        }else{
            return emptyEdge;
        }

        //return edge
//        System.out.println("\nMYEDGE = (" + countX + ", " + 
//                countY + ", " + direction + ")\n"); 
        Edge returnEdge = Edge.obtain(countX, countY, direction);        
        return returnEdge;
    }
        
    public String toString(){
        return ("Board - " + getBoardWidth() + " x " + getBoardHeigth());
    }
}
