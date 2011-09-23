import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Random;
/**
 * Runner for teh bunniez.
 * 
 * @author Steven
 * @version (a version number or a date)
 */
public class BattleBunnies {

    public static final int DEBUG_TEXT_LEVEL = 1;
    // 0 = nothing
    // 1 = killer
    // 2 = mostly
    // 3 = all those annoying posRelTo messages

    public static void debug( String str, int n ) {
        if( n <= DEBUG_TEXT_LEVEL ) {
            System.out.println( str );   
        }
    }

    private static void createAndShowGUI() {
        JFrame window = new JFrame("BUNNY BATTLE 4 PLAYER EDITION");
        BunnyGUI itGUI = new BunnyGUI();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().add(itGUI);
        window.setPreferredSize(new Dimension(800, 648));
        window.pack();  // finalize layout
        window.setVisible(true);
        window.setResizable(false);
        window.toFront();
     
    }
    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}