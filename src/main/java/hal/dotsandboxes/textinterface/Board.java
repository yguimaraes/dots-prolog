package hal.dotsandboxes.textinterface;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Graphics;

public class Board {
    private final String boxString = "/media/vanzan/Files/Dropbox/Documentos/"
            + "IME/7_Periodo/Logica/Trabalho Prolog/DotsAndBoxes/box.png";
    private final int box_side = 50;
    private final int dot_radius = 5;
    private int n;
    
    
    public Board(int n){
        this.n = n;
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
    
    public int getBoardSide(){
        return n*box_side + 2*dot_radius;
    }
    
    public ImageIcon getBoardImage(){
        try{            
            BufferedImage box = ImageIO.read(new File(boxString));
            BufferedImage line = box;
            for(int i=1; i<n; i++) //  The first box is already placed
                line = mergeAtSide(line, box);
            
            BufferedImage board = line;
            for(int j=1; j<n; j++)
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
}