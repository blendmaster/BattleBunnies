import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Random;
/**
 * Write a description of class Gore here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Gore extends MoveableObject
{
//     public static final float WIDTH = (float)20;
//     public static final float HEIGHT = (float)20;

    public static final float REGULAR_TILE_BOUNCINESS = (float).9;
    
    //public static final Image gore5 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/Gore5.png"));
   // public static final Image gore6 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/Gore6.png"));
   // public static final Image gore7 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/Gore7.png"));
   // public static final Image gore8 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/Gore8.png"));
  //  public static final Image gore9 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/Gore9.png"));
  //  public static final Image gore10 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/Gore10.png"));
    
    public int deathCounter; //goes down until the gore is removed from screen
    
    private Image goreImage;
    
    /**
     * Constructor for objects of class Gore
     */
    public Gore( float x, float y, float width, float height, Vector v )
    {
        // initialise instance variables
        super( x,y,width,height,v );
        deathCounter = 500;
        goreImage =  Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/Gore"+(int)width+".png"));
    }
    
    public void draw( Graphics2D g ) {
        g.drawImage( goreImage, (int)getX(), (int)getY(), null);
        if( DEBUG_DRAW_HULLS ) {
            g.setPaint( Color.RED );
            g.draw( getHull() );
        }
        if( DEBUG_DRAW_VECTORS ) {
            g.setPaint( Color.WHITE );
            g.draw( new Line2D.Double( getHull().getCenterX(), getHull().getCenterY(), getHull().getCenterX() + getVelocity().getX()*10 , getHull().getCenterY() - getVelocity().getY()*10 ) );
            
        }
    }
    
    public float getBounciness() { return Gore.REGULAR_TILE_BOUNCINESS; }
 
}
