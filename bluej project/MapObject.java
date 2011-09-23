import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Random;
/**
 * Write a description of class MapObject here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class MapObject {

    public static final boolean DEBUG_DRAW_HULLS = false; //debugging to draw those darned collision hulls


    // instance variables - replace the example below with your own
    protected float x, y, width, height;
    protected RectangularShape hull; //collision detection, usually circle for easy elastic collision handling

    /**
     * Constructor for objects of class MapObject
     */
    public MapObject( float x, float y, float width, float height, RectangularShape newHull )
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        hull = newHull;
    }

    public RectangularShape getHull() { return hull; }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    
    public float getCenterX() { return (float)hull.getCenterX(); }
    public float getCenterY() { return (float)hull.getCenterY(); }

    public void setX(float newX) { x = newX; hull.setFrame(x,y,width,height);}
    public void setY(float newY) { y = newY; hull.setFrame(x,y,width,height); }
    public void setWidth(float newWidth) { width = newWidth; hull.setFrame(x,y,width,height); }
    public void setHeight(float newHeight) { height = newHeight; hull.setFrame(x,y,width,height); }

    
    public boolean intersects( MapObject o ) {
        //cheap detection, should short circuit more expensive stuff
        if( Math.abs( this.getX() - o.getX() ) > 100 || Math.abs( this.getY() - o.getY() ) > 100 ) { return false; }
        
        if( this instanceof MoveableObject && o instanceof MoveableObject ) {
            //distance between centers is less than their two radiuses, moveable objects are always circles, hopefully
            return  Math.pow( (this.getHull()).getCenterY()-(o.getHull()).getCenterY(),2 ) + Math.pow( (this.getHull()).getCenterX()-(o.getHull()).getCenterX(),2 )  < Math.pow(getWidth()/2+o.getWidth()/2,2);
        }
       
        //fallback
        //for  reasons of posRelativeTo, i'm now going to just use rectangular collision instead
        return getHull().getFrame().intersects( o.getHull().getFrame() );
        
    }
    
    //more detailed collision information, with probably more expensive processing
    // above returns 1;
    // left returns 2;
    // right returns 3;
    // bottom returns 4;
    // middle returns 0;
    // weird error returns -1;
    // this assumes the objects are in fact colliding.
    public abstract int posRelativeTo( MapObject o ); 
    
    public abstract void draw( Graphics2D g );
    
    public String toString() {
        return this.getClass().getName() + "( " + getX() + " , " + getY() + " ) ";
    }
    
}
