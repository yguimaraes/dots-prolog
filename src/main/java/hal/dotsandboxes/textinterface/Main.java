package hal.dotsandboxes.textinterface;

import com.google.common.base.Preconditions;
import hal.dotsandboxes.Edge;
import hal.dotsandboxes.Player;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

public class Main extends javax.swing.JFrame {
    //Class Variables    
    public Board board;
    public int currentPlayer; //1 ou 2
    public DotsAndBoxesText dotsAndBoxesText;
    public int lastX, lastY;
    public Edge lastEdge;
    
    public Main(){
        initComponents();
        
        //PreStart the game
        Options old_options;
        GameOptions  options;

        try {
            Cli<Options> cli = CliFactory.createCli(Options.class);

            String args[];
            args = new String[]{
                            "--player-one-type", "human",
                            "--player-two-type", "prolog",
                            "--prolog-executable", "swipl",
                            "--prolog-type", "swi",
                            "--p2-lookahead", "1",
                            "--p1-lookahead", "2",
                            "--width", "4",
                            "--height", "4",
                            "--yes"
            };

            if(Arrays.asList(args).contains("--help")) {
                    System.out.println(cli.getHelpMessage());
                    System.exit(0);
            }

            old_options = cli.parseArguments(args);
            options = new GameOptions(old_options.playerOneType(), old_options.playerTwoType(), old_options.getWidth(), old_options.getHeight(), old_options.getP1Lookahead(), old_options.getP2Lookahead());

        }catch (ArgumentValidationException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            throw new AssertionError("never happens");
        }

        // Start the game
        try{
            List<Player> players = Players.makePlayers(options);
            Preconditions.checkState(players.size() == 2, "unexpected player " +
                            "count received from Players.makePlayers");
            Player p1 = players.get(0);
            Player p2 = players.get(1);

            Values.BoardRepresentation boardRepresentation = Values.BoardRepresentation.normal;

            dotsAndBoxesText = new DotsAndBoxesText(this,
                                    options.getWidth(), 
                                    options.getHeight(), 
                                    p1, p2,
                                    boardRepresentation.getPrinter(), 
                            true);
        }catch(IOException e){

        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelTitle = new javax.swing.JLabel();
        jLabelTable = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabelPlayer1 = new javax.swing.JLabel();
        jComboBoxPlayer1 = new javax.swing.JComboBox();
        jLabelPlayer2 = new javax.swing.JLabel();
        jComboBoxPlayer2 = new javax.swing.JComboBox();
        jComboBoxGridWidth = new javax.swing.JComboBox();
        jLabelGridSize = new javax.swing.JLabel();
        jButtonStartGame = new javax.swing.JButton();
        jLabelBoard = new javax.swing.JLabel();
        jComboBoxGridHeigth = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jLabelTeste = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabelTitle.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        jLabelTitle.setText("Dots and Boxes Game");

        jLabelTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabelTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabelTableMousePressed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("New Game"));

        jLabelPlayer1.setText("Player 1");

        jComboBoxPlayer1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CPU", "Human" }));
        jComboBoxPlayer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxPlayer1ActionPerformed(evt);
            }
        });

        jLabelPlayer2.setText("Player 2");

        jComboBoxPlayer2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CPU", "Human" }));
        jComboBoxPlayer2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxPlayer2ActionPerformed(evt);
            }
        });

        jComboBoxGridWidth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        jComboBoxGridWidth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxGridWidthActionPerformed(evt);
            }
        });

        jLabelGridSize.setText("Grid Size");

        jButtonStartGame.setText("Start Game");
        jButtonStartGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartGameActionPerformed(evt);
            }
        });

        jLabelBoard.setText("jLabel1");

        jComboBoxGridHeigth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        jComboBoxGridHeigth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxGridHeigthActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelPlayer1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxPlayer1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelPlayer2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelGridSize)
                                .addGap(8, 8, 8)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxPlayer2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxGridWidth, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxGridHeigth, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelBoard)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonStartGame)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPlayer1)
                    .addComponent(jComboBoxPlayer1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxPlayer2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPlayer2))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxGridWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelGridSize))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxGridHeigth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBoard)
                    .addComponent(jButtonStartGame))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));

        jLabelTeste.setText("Teste:");

        jButton1.setText("Test user input");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTeste)
                    .addComponent(jButton1))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelTeste)
                .addGap(73, 73, 73))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabelTable, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(253, 253, 253)
                .addComponent(jLabelTitle)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabelTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelTable, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxPlayer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxPlayer1ActionPerformed
        // Set Player 1 value
    }//GEN-LAST:event_jComboBoxPlayer1ActionPerformed

    private void jComboBoxPlayer2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxPlayer2ActionPerformed
        // Set Player 1 value
    }//GEN-LAST:event_jComboBoxPlayer2ActionPerformed

    private void jComboBoxGridWidthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxGridWidthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxGridWidthActionPerformed

    private void jButtonStartGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartGameActionPerformed
        //Set new game parameters
        board = new Board(jComboBoxGridWidth.getSelectedIndex()+1, 
                          jComboBoxGridHeigth.getSelectedIndex()+1);
        
        //Create board
        jLabelBoard.setIcon(board.getBoardImage());
        jLabelBoard.setText("");           
        jLabelTable.add(jLabelBoard);
        jLabelBoard.setSize(board.getBoardWidth(), board.getBoardHeigth());
        jLabelBoard.setLocation(board.getPaddX(), board.getPaddY());
        
        //Really start game
        dotsAndBoxesText.play();
    }//GEN-LAST:event_jButtonStartGameActionPerformed

    private void jLabelTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelTableMousePressed
    }//GEN-LAST:event_jLabelTableMousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        getBoardInput();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBoxGridHeigthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxGridHeigthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxGridHeigthActionPerformed

    public Edge getBoardInput(){     
        System.out.println("Inside getBoardInput()");
        //Add action listener to label        
        jLabelTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){   
                System.out.println("mousePressed()");
                
                lastEdge = board.getEdge(e.getX(), e.getY());
                
                jLabelTable.removeMouseListener(this);
            }
        });
        System.out.println("return lastEdge;");
        return lastEdge;
    }
    
    public static void main(String args[]){
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
                
        // Create and display the form
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonStartGame;
    private javax.swing.JComboBox jComboBoxGridHeigth;
    private javax.swing.JComboBox jComboBoxGridWidth;
    private javax.swing.JComboBox jComboBoxPlayer1;
    private javax.swing.JComboBox jComboBoxPlayer2;
    private javax.swing.JLabel jLabelBoard;
    private javax.swing.JLabel jLabelGridSize;
    private javax.swing.JLabel jLabelPlayer1;
    private javax.swing.JLabel jLabelPlayer2;
    private javax.swing.JLabel jLabelTable;
    private javax.swing.JLabel jLabelTeste;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables

    public void setJTest(String text){
        jLabelTeste.setText(text);
    }
}
