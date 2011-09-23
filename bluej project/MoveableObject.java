import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Random;
/**
 * Write a description of class MoveableObject here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
//these objects always have circle hulls, and are not retangular
public abstract class MoveableObject extends MapObject
{
 
    public static final boolean DEBUG_DRAW_VECTORS = false;    
    
    public static final Vector GRAVITY = new Vector( 0.0 , -.25 );
    public static final float ELASTIC_DAMPENING = (float).9;
    public static final float VELOCITY_CAP = (float)40;

    private Vector velocity;

    /**
     * Constructor for objects of class MoveableObject
     */
    public MoveableObject( float x, float y, float width, float height, Vector v)
    {
        super( x, y, width, height, 
            new Ellipse2D.Float( x, y,width, height ) );
            
        velocity = v;
    }
    
    public void applyGravity() {
        velocity.add( GRAVITY );   
    }
    
    public void counteractGravity() {
        velocity.add( Vector.multiply( GRAVITY, -1 ) );   
    }
    
    public Vector getVelocity() { return velocity; }
    public void setVelocity( Vector v ) { velocity = v; }
    public void setVelocity( float x, float y ) { velocity = new Vector( x, y ); };
    
    public void addVelocity( Vector v ) { velocity.add(v); }
    
    
    

    public int posRelativeTo( MapObject o ) { 
        if( o instanceof MoveableObject ) {
            //center to center angle, in radians/PI, cartesian, not java
            double centerAngle = (Math.atan2( o.getCenterY() - getCenterY(), 
                                               getCenterX() - o.getCenterX() )/Math.PI);
                                               
                                   
            if( 1.0/4.0 < centerAngle && centerAngle < 3.0/4.0 ) { 
                BattleBunnies.debug( this + " is " + ( 1/4 < centerAngle && centerAngle < 3/4 ) + " above " + o + "\n\t with angle " + centerAngle + "*pi", 3 );
                return 1; 
            }
            if( -1.0/4.0 < centerAngle && centerAngle < 1.0/4.0 ) {
                BattleBunnies.debug( this + " is " + ( -1/4 < centerAngle && centerAngle < 1/4 ) + " right of" + o + "\n\t with angle " + centerAngle + "*pi", 3 );
                return 3; 
            }
            if( -3.0/4.0 < centerAngle && centerAngle < -1.0/4.0) { 
                BattleBunnies.debug( this + " is " + ( -3/4 < centerAngle && centerAngle < -1/4 ) + " below " + o + "\n\t with angle " + centerAngle + "*pi", 3 );
                return 4; 
            }
            if( -3.0/4.0 < centerAngle || centerAngle > 3.0/4.0 ) {
                BattleBunnies.debug( this + " is " + ( -3/4 > centerAngle || centerAngle > 3/4 ) + " left of " + o + "\n\t with angle " + centerAngle + "*pi", 3 );
                return 2; 
            }
            
        } else {
            
                
            
            
                float thisX = prevCenterX();
                float thisY = prevCenterY();
                
                float thisLeftX = prevX();
                float thisTopY = prevY();
                
                float thisRightX = prevX() + getWidth();
                float thisBottomY = prevY() + getHeight();
                
                float thatLeftX = o.getX();
                float thatTopY = o.getY();
                
                float thatRightX = o.getX() + o.getWidth();
                float thatBottomY = o.getY() + o.getHeight();
                
                //angle to corners from this
                float limitTopLeft = (float)Math.atan2( thisY - thatTopY, thatLeftX - thisX );
                float limitTopRight = (float)Math.atan2( thisY - thatTopY, thatRightX - thisX );
                float limitBottomLeft = (float)Math.atan2( thisY - thatBottomY, thatLeftX - thisX );
                float limitBottomRight = (float)Math.atan2( thisY - thatBottomY, thatRightX - thisX );
                
                float curAngle = getVelocity().getDirection();
                
                
                //determine octant
                // 1-8 clockwise from topleft
                int octant = 0;
                int col = 0, row = 0;
                
                /** This DOESN'T WORK BECAUSE IT DOESN"T DETECT THE DIAGONAL QUADRANTS CORRECTLY
                 * 
                 * HALP HALP HALP HALP HALP HALP HALP 
                 */
                if( thisY < thatTopY ) { row = 0; }
                if( thatTopY <= thisY && thisY <= thatBottomY ) { row = 1; }
                if( thatBottomY < thisY ) { row = 2; }
                    
                if( thisX < thatLeftX ) { col = 0; }
                if( thatLeftX <= thisX && thisX <= thatRightX ) { col = 1; }
                if( thatRightX < thisX ) { col = 2; }
                
                octant = col + 3 * row;
    
                //switch
                switch( octant ) {
                    case 1: BattleBunnies.debug( this + " is above " + o,3 );
                            return 1;
                            
                    case 3: BattleBunnies.debug( this + " is left of " + o,3 );
                            return 2;
                            
                    case 5: BattleBunnies.debug( this + " is right of " + o,3 );
                            return 3;
                            
                    case 7: BattleBunnies.debug( this + " is below " + o,3 );
                            return 4; 
                    
                    case 0: if( curAngle >= limitTopLeft ) { return 1; }
                            else                           { return 1; }
                            
                    case 2: if( curAngle <= limitTopRight ) { return 1; }
                            else                            { return 1; }
                            
                    case 8: if( curAngle >= limitBottomRight ) { return 3; }
                            else                               { return 4; }
                            
                    case 6: if( curAngle >= limitBottomLeft ) { return 2; }
                            else                           { return 4; }
                          
                    case 4: BattleBunnies.debug( this + " is INSIDE " + o,3 );
                            //push away from center
                            if( getCenterX() < o.getCenterX() ) {
                                return 2;     
                            }
                            if( getCenterY() > o.getCenterY() ) {
                                return 3;     
                            }
                            
                    default: BattleBunnies.debug( "OH NOES SOMETHING HAS GONE HORRIBLY WRONG AND THE OBJECT ISN\'T ANYWHERE TO BE FOUND",2 );
                }
                
        }
        BattleBunnies.debug( this + " is ERROR to " + o,2 );
            return -1;
    }
    
    // nudges this object out of the other object
    //other object is unchanged
    public void simpleUncollide( MapObject o, int posRelTo ) {
        if( o instanceof MoveableObject ) {
            Vector normal = new Vector( o.getCenterX() - this.getCenterX(), this.getCenterY() - o.getCenterY() );
            float distanceToMove = normal.getMagnitude()*-1 + this.getWidth()/2 + o.getWidth()/2; //change distance
            
            normal.setXY( normal.getX()*-1, normal.getY()*-1 ); //reverse away from other's center
            normal.setVector( normal.getUnitVector() );
            normal.multiply( distanceToMove ); //gets short vector
            
            this.setX( getX() + normal.getX() );
            this.setY( getY() - normal.getY() );
            
        } else {
            switch( posRelTo ) {
                case 1: setY( getY() - ( getY() + getHeight() - o.getY() ) ); break;
                case 2: setX( getX() - ( getX() + getWidth() - o.getX() ) );  break;
                case 3: setX( getX() + ( o.getX() + o.getWidth() - getX() ) ); break;
                case 4: setY( getY() + ( o.getY() + o.getHeight() - getY() ) ); break;
                default: setY( getY() - ( getY() + getHeight() - o.getY() ) ); //just move it up
            }
        }
    }
    
    //changes the velocity of this object _and_ the other object
    //doesn't change position
    public void applyElasticCollision( MoveableObject m ) {
        
              Vector normal = new Vector( m.getX() - this.getX(), this.getY() - m.getY() );
              normal.setVector( normal.getUnitVector() );
              Vector tangent = new Vector( normal.getY()*-1, normal.getX() ); 
              
              float v1n = Vector.dotProduct( normal, this.getVelocity() );
              float v1t = Vector.dotProduct( tangent, this.getVelocity() );
              float v2n = Vector.dotProduct( normal, m.getVelocity() );
              float v2t = Vector.dotProduct( tangent, m.getVelocity() );
              
              float v1tPrime = v1t;
              float v2tPrime = v2t;
              
              float v1nPrime = v2n;
              float v2nPrime = v1n;

              Vector vectorV1nPrime = Vector.multiply( normal, v1nPrime );
              Vector vectorV1tPrime = Vector.multiply( tangent, v1tPrime );
              
              Vector vectorV2nPrime = Vector.multiply( normal, v2nPrime );
              Vector vectorV2tPrime = Vector.multiply( tangent, v2tPrime );
              
              this.setVelocity( Vector.add( vectorV1nPrime, vectorV1tPrime ) );
              m.setVelocity( Vector.add( vectorV2nPrime, vectorV2tPrime ) );
        
    }
    
    
    //these two back apply the vector to find out where the object was
    public float prevX() {
        return getX() - velocity.getX();
    }
    
    public float prevY() {
        return getY() + velocity.getY();
    }
    
    public float prevCenterX() {
        return getX() - velocity.getX() + getWidth()/2;
    }
    
    public float prevCenterY() {
        return getY() + velocity.getY() + getWidth()/2;
    }
    
    public void updatePosition() {
        //limits for sanity and fast moving object collision detection
        //in other words, objects moving too fast to be inside an object and collide, 
        //and instead pass straight through
        if( velocity.getX() > VELOCITY_CAP ) {
            BattleBunnies.debug( "X Velocity truncated: " + this,2 );
            velocity.setX( VELOCITY_CAP );
        }
        if( velocity.getX() < -VELOCITY_CAP ) {
            BattleBunnies.debug( "X Velocity truncated: " + this,2 );
            velocity.setX( -VELOCITY_CAP );
        }
        if( velocity.getY() > VELOCITY_CAP ) {
            BattleBunnies.debug( "Y Velocity truncated: " + this,2 );
            velocity.setY( VELOCITY_CAP );
        }
        if( velocity.getY() < -VELOCITY_CAP ) {
            BattleBunnies.debug( "Y Velocity truncated: " + this,2 );
            velocity.setY( -VELOCITY_CAP );
        }
        
        setX( getX() + velocity.getX() );
        setY( getY() - velocity.getY() );
    }
    
    public String toString() {
        return super.toString() + ", vector ( " + velocity.getX() + " , " + velocity.getY() + " ) ";
    }
    
    public abstract float getBounciness();
}
