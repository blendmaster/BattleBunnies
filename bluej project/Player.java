import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Random;
/**
 * Write a description of class Player here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Player extends MoveableObject
{

    public static final boolean DEBUG_DRAW_NAMES = true;
    
    public static final int START_GAME_PROTECTION = 300;
    public static final int SPAWN_PROTECTION = 100;

    public static final float REGULAR_TILE_BOUNCINESS = (float)0;
    

    public static final Vector LEFT = new Vector( -.1, 0.0 );
    public static final Vector LEFT_TURBO = new Vector( -.3, 0.0 );
    public static final Vector RIGHT = new Vector( .1, 0.0 );
    public static final Vector RIGHT_TURBO = new Vector( .3, 0.0 );
    public static final Vector UP = new Vector( 0.0, 1 );
    public static final Vector DOWN = new Vector ( 0.0, -.3 );
    public static final Vector RIGHT_BRAKE = new Vector( -.5, 0.0 );
    public static final Vector LEFT_BRAKE = new Vector( .5, 0.0 );
    
    public static final Vector KILL_BOUNCE = new Vector( 0.0, 5 );

    public static final float FLAT_TOLERANCE = (float).001;

    public static final float WIDTH = (float)24;
    public static final float HEIGHT = (float)24;
    
   //spawn protection
    private boolean isInvulnerable;
    private int invulnCounter;
    
    private String name;
    private int playerNumber;
    private Image leftAvatar;
    private Image rightAvatar;
    
    //stat tracking
    private int[] kills; //kills to different players
    private int totalKills;
    private int deaths;
    private int currentKillStreak; //how many kills in a row
    private int highestKillStreak; //highest kill streak
    
    private boolean leftPressed, downPressed, rightPressed, upPressed, turboPressed;
    
    private boolean jumpReady;
    private int jumpCounter;
    
    private int facing; // -1 for left, 1 for right

    /**
     * Constructor for objects of class Player
     */
    public Player( float x, float y, String name, int num, Image leftA, Image rightA, int numPlayers)
    {
        super( x,y,WIDTH,HEIGHT, new Vector( 0, 0) );
        
        this.name = name;
        
        leftAvatar = leftA;
        rightAvatar = rightA;
        facing = -1;
        
        playerNumber = num;
        kills = new int[numPlayers];
        for( int i : kills ) { i = 0; } //initialization
        
        invulnCounter = START_GAME_PROTECTION;
        isInvulnerable = true;
        
        deaths = currentKillStreak = highestKillStreak = 0;
        leftPressed = downPressed = rightPressed = upPressed = turboPressed =false;
        
        jumpReady = true;
        jumpCounter = 0;
    }

    public int number() { return playerNumber; }
    
    public boolean isInvulnerable() { return isInvulnerable; }
    
    public void updatePosition() {
        super.updatePosition();
        invulnCounter--;
        if( invulnCounter < 0 ) { isInvulnerable = false; }
    }
    
    public void setUp(boolean b) { upPressed = b; }
    public void setDown(boolean b) { downPressed = b; }
    public void setRight(boolean b) { rightPressed = b; }
    public void setLeft(boolean b) { leftPressed = b; }
    public void setTurbo(boolean b) { turboPressed = b; }
    
    //this hopefully will work
    public void returnToGround() { jumpReady = true; }
    public void isInAir() { jumpReady = false; jumpCounter = 0;}
    
//     public static boolean isClose( float a, float b ) {
//         return Math.abs( a - b ) < FLAT_TOLERANCE;   
//     }
    
    public void applyKeys() { 
            
            if (upPressed && jumpReady) {
                jumpCounter++;
                
                //here's the assume-you're-on-a-platform
                //if-your-velocity-is-flat trick
                
                //if( Player.isClose(getVelocity().getY(), 0 ) ) {
                    addVelocity( UP );
                //}
                if(jumpCounter > 10 ) {
                 jumpReady = false;
                 jumpCounter = 0;
                }
             }
           
            if (downPressed) {
//                 if( Player.isClose(getVelocity().getY(), 0 ) ) {
                    //braking
//                     if( facing == -1 ) { addVelocity( LEFT_BRAKE ); }
//                     else { addVelocity( RIGHT_BRAKE ); }
//                 } else { 
                    addVelocity( DOWN ); 
//                 } 
            }
            
            if (leftPressed) {
                    facing = -1;
                    if(turboPressed) {
                        addVelocity( LEFT_TURBO );
                    }
                    else { addVelocity( LEFT ); }
            }
            
            if (rightPressed) {
                facing = 1;
                if(turboPressed) {
                    addVelocity( RIGHT );
                }
                else { addVelocity( RIGHT ); }
            }
            
            
    }
    
    public void respawn( float x, float y ) {
        setX( x );
        setY( y );
        setVelocity( new Vector() );
        invulnCounter = SPAWN_PROTECTION;
        isInvulnerable = true;
        
        //scorekeeping
        currentKillStreak = 0;
        deaths++;
    }
    
    public void gotAKill( int num ) {
        currentKillStreak++;
        if( currentKillStreak > highestKillStreak ) {
            highestKillStreak = currentKillStreak;   
        }
        
        kills[num-1]++;
        totalKills++;
    }
    
    //for score drawing
    public Image getImage() {
        return leftAvatar;   
    }
    
    public int getTotalKills() { return totalKills; }
    public int getKills( int num ) { return kills[num-1]; }
    public int getCurrentKillStreak() { return currentKillStreak; }
    public int getHighestKillStreak() { return highestKillStreak; }
    public int getDeaths() { return deaths; }
    
    public String getName() { return name; }
    
    public void draw( Graphics2D g ) { 
        if( MapObject.DEBUG_DRAW_HULLS ) { 
            g.setPaint( Color.WHITE );
            g.draw( getHull() ); 
        }
        if( DEBUG_DRAW_VECTORS ) {
            g.setPaint( Color.WHITE );
            g.draw( new Line2D.Double( getHull().getCenterX(), getHull().getCenterY(), getHull().getCenterX() + getVelocity().getX()*10 , getHull().getCenterY() - getVelocity().getY()*10 ) );
            
        }
        if( DEBUG_DRAW_NAMES ) {
            g.setPaint( Color.WHITE );
            g.setFont( g.getFont().deriveFont((float)10.0) );
            g.drawString( name , getX(), getY() - 12 );   
        }
        
        if( facing == 1 ) {
            g.drawImage( rightAvatar, (int)getX(), (int)getY(), null);
        } else {
            g.drawImage( leftAvatar, (int)getX(), (int)getY(), null);
        }
        
        //clever little invincibility field
        if( isInvulnerable ) {
            g.setPaint( Color.GREEN );
            g.draw( getHull() ); 
        }
        
    }
    
    public String toString() {
        return "Player(" + number() + ") " + name + "( " + getX() + " , " + getY() + " ), vector ( " + getVelocity().getX() + " , " + getVelocity().getY() + " ) ";
    }

     public float getBounciness() { return Player.REGULAR_TILE_BOUNCINESS; }
}

