import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Random;
/**
 * Write a description of class MapTile here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MapTile extends MapObject
{

    public static final boolean DEBUG_DRAW_INFO = false;
    
    public static final float FRICTION = (float).95;
    
    public static final float MIN_BOUNCINESS = (float)5;


    private char type;
    // 1 = regular
    // 2 = bouncy
    
    // implemented in map:
    // a = player 1 spawn
    // b = player 2 spawn
    // c = player 3 spawn
    // d = player 4 spawn
    
    //Image tile; //fancy
    
    private int xPos;
    private int yPos;
    
    /**
     * Constructor for objects of class MapTile
     */
    public MapTile( char type, int xpos, int ypos, float width, float height)
    {
        super( xpos*width, ypos*height, width, height, 
            new Rectangle2D.Float( xpos*width, ypos*height,width, height ) );
        this.type = type;
        this.xPos = xpos;
        this.yPos = ypos;
    }
    
    public void draw( Graphics2D g ) { 
        
        if( type == '1' ) {
                g.setPaint( Color.RED );
                g.fill( getHull() ); // hull is the same as the block here, at least until images, so no debug code
       }  
        else {
                g.setPaint( Color.BLUE );
                g.fill( getHull() ); // hull is the same as the block here, at least until images, so no debug code
        }
        
        if( DEBUG_DRAW_INFO ) {
            g.setPaint( Color.WHITE );
            g.drawString( "("+xPos+","+yPos+")", (float)getHull().getX() + 2, (float)getHull().getY() + 12 );
            g.drawString( "("+getHull().getX()+","+getHull().getY()+")", (float)getHull().getX() + 2, (float)getHull().getY() + 24 );
        }
        
    }
    public int posRelativeTo( MapObject o ) { 
        //THIS DOESN"T GET USED ANYWAY
        
        ///CAUTION THIS DOESN'T WORK
        Rectangle2D otherHull = o.getHull().getFrame();
        if ( getHull().getCenterY() < otherHull.getCenterY() ) { return 1; }
        if ( getHull().getCenterX() < otherHull.getCenterX() ) { return 2; }
        if ( getHull().getCenterX() > otherHull.getCenterX() ) { return 3; }
        if ( getHull().getCenterY() > otherHull.getCenterY() ) { return 4; }
        if ( getHull().getCenterY() == otherHull.getCenterY() && getHull().getCenterX() == otherHull.getCenterX() ) {
            return 0; 
        }
        return -1; //this shouldn't happen
    }
    
    
    //depending on if this tile is bouncy or not, it changes the vector of the incoming object to either
    // horizontal or vertical along the surface of this tile, or 
    //the bounce, which is the vector reflected just like light.
    public void deflect( MoveableObject o, int posRelTo ) {
        BattleBunnies.debug( this + " deflected object: \n\t" + o,3 );
        
        
        if( type=='1' ) { //regular, flatten and dampen
            switch( posRelTo ) {
                case 1: o.getVelocity().setY(o.getVelocity().getY()*o.getBounciness() ); o.getVelocity().setX(o.getVelocity().getX()*FRICTION); break;
                case 2: o.getVelocity().setX(o.getVelocity().getX()*o.getBounciness() ); o.getVelocity().setY(o.getVelocity().getY()*FRICTION); break;
                case 3: o.getVelocity().setX(o.getVelocity().getX()*o.getBounciness() ); o.getVelocity().setY(o.getVelocity().getY()*FRICTION); break;
                case 4: o.getVelocity().setY(o.getVelocity().getY()*o.getBounciness() ); o.getVelocity().setX(o.getVelocity().getX()*FRICTION); break;
            }
        }
        if( type=='2' ) { //bouncy
            switch( posRelTo ) {
                case 1: 
                    o.getVelocity().setY(-1*o.getVelocity().getY()); BattleBunnies.debug( "reversing Y",3 );
                    if( o.getVelocity().getY() < MIN_BOUNCINESS ) { o.addVelocity( new Vector( 0, MIN_BOUNCINESS ) ); }
                    if( o instanceof Player ) {
                        Player p = (Player) o;
                        p.isInAir(); 
                    }
                    break;
                case 2: 
                    o.getVelocity().setX(-1*o.getVelocity().getX()); BattleBunnies.debug( "reversing X",3 );
                    //if( o.getVelocity().getX() < -1*MIN_BOUNCINESS ) { o.addVelocity( new Vector( -1*MIN_BOUNCINESS, 0 ) ); }
                    break;
                case 3: 
                    o.getVelocity().setX(-1*o.getVelocity().getX()); BattleBunnies.debug( "reversing X",3 );
                    //if( o.getVelocity().getX() < MIN_BOUNCINESS ) { o.addVelocity( new Vector( 1*MIN_BOUNCINESS, 0 ) ); }
                    break;
                case 4: 
                    o.getVelocity().setY(-1*o.getVelocity().getY()); BattleBunnies.debug( "reversing Y",3 );
                    //if( o.getVelocity().getY() < -1*MIN_BOUNCINESS ) { o.addVelocity( new Vector( 0, -1*MIN_BOUNCINESS ) ); }
                    break;
            }
            
            
        }
        BattleBunnies.debug( "to: \t" + o.toString(),3 );
    }
    
    public String toString() {
        return "Tile (" + String.valueOf( type ) + ") at ( " + getX() + " , " + getY() + " ) ";
    }
    
    
}
