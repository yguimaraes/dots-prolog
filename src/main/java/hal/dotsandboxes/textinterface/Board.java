package hal.dotsandboxes.textinterface;

import hal.dotsandboxes.Direction;
import hal.dotsandboxes.Edge;
import hal.dotsandboxes.GameState;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Board {
    private final String boxString = "box.png";
    
    private final int box_side = 50;
    private final int dot_radius = 5;
    private int width, heigth;
    private int lastX, lastY;
    
    public final int paddX = 10, paddY = 10;
    
    
    public Board(int width, int heigth){
        this.width = width;
        this.heigth = heigth;
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
        return width*box_side + 2*dot_radius;
    }
    
    public int getBoardHeigth(){
        return heigth*box_side + 2*dot_radius;
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
            for(int i=1; i<width; i++) //  The first box is already placed
                line = mergeAtSide(line, box);
            
            BufferedImage board = line;
            for(int j=1; j<heigth; j++)
                board = mergeAtBottom(board, line);
            
            return new ImageIcon(board);            
        } catch (IOException e) {
            e.printStackTrace();
            return new ImageIcon();
        }
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
    
    public void printBoard(GameState state){
//        for
//                for
//                        getNodeEdges(int x, int y)
    }
    
    public String toString(){
        return ("Board - " + getBoardWidth() + " x " + getBoardHeigth());
    }
}
