
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Toolkit;

// This class contains all the parts of the GUI interface
class BunnyGUI extends JPanel implements Runnable{
    
    //// GUI Declarations
    private BunnyGraphics bunnyGraphics;

    private Thread runner;
    
    //constructor
    public BunnyGUI()
    {
        // main graphics display
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem NGame = new JMenuItem("New Game");
        JMenu debug = new JMenu("Debug");
        menuBar.add(file);
        file.add (NGame);
        
        
        bunnyGraphics = new BunnyGraphics();
        
        
        NGame.addActionListener(
            new ActionListener() {
                //listener 
                public void actionPerformed(ActionEvent e) {
                    // action for New game button
                    
                    String newMap = JOptionPane.showInputDialog("New map name?");
                    
                    int p3e = JOptionPane.showOptionDialog(null, "Will there be a player 3?", " ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                    int p4e = 1;
                    if( p3e == 0 ) { p4e = JOptionPane.showOptionDialog(null,"Will there be a player 4?", " ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null); }
                    
                    
                    boolean p3en, p4en;
                    p3en = p4en = false;
                    if( p3e == 0 ) { p3en = true; } if( p4e == 0 ) { p4en = true; }
                    
                    String p1 = JOptionPane.showInputDialog("Player 1 name?");
                    String p2 = JOptionPane.showInputDialog("Player 2 name?");
                    String p3 = "";
                    String p4 = "";
                    if( p3en ) {p3 = JOptionPane.showInputDialog("Player 3 name?"); }
                    if( p4en ) {p4 = JOptionPane.showInputDialog("Player 4 name?"); }
                    
                    int kills = Integer.valueOf( JOptionPane.showInputDialog("Number of points to win?") );
                    
                    bunnyGraphics.init( newMap, p3en, p4en, p1, p2, p3, p4, kills);
       
                }
            }
        );
        
        
 
        
        
        //Set the layout and add the components
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);
        this.add(bunnyGraphics, BorderLayout.CENTER);
        this.add(menuBar, BorderLayout.NORTH);
        
        if (runner == null)
        {
            runner = new Thread (this);
            runner.start();
        }
    }
    
    public void run()
    {
        while(true)
        {
            try {runner.sleep(10);}
            catch (Exception e) { }

                bunnyGraphics.updateState();
                bunnyGraphics.drawToBuffer();
                bunnyGraphics.repaint();

        }
    }
}

