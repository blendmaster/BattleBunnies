import java.io.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Random;
/**
 * Write a description of class Map here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Map
{

    public static final boolean DEBUG_DRAW_TILES = false;
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;

     private MapTile[][] tiles; //da tiles
     
     private Image background;
     private Image foreground;
     
     //spawn positions
     private float spawn1x,spawn1y;
     private float spawn2x,spawn2y;
     private float spawn3x,spawn3y;
     private float spawn4x,spawn4y;
     
     private float corWidth, corHeight;
     
     private BufferedReader in;

    /**
     * Constructor for objects of class Map
     */
    public Map( String filename )
    {
        try {
            in = new BufferedReader( new InputStreamReader(getClass().getClassLoader().getResourceAsStream("maps/"+filename+".map") ) );
            
            int width = Integer.valueOf( in.readLine() ); //first line is width
            int height = Integer.valueOf( in.readLine() ); //second line is height
            
            tiles = new MapTile[height][width]; //initialize
            corWidth = WINDOW_WIDTH / width; // corrected tile width according to window
            corHeight = WINDOW_HEIGHT / height; //samey samey
            for( int i = 0; i < height; i++ ) { 
                for ( int j = 0; j < width; j++ ) {
                    char k = (char)in.read();
                    while( !Character.isLetterOrDigit(k) ) { k = new Character( (char)in.read() ); } //skips over whitespace
                    if( k != '0' ) { //we don't need tiles for emptiness, so they will just be null
                        if( k == 'a' ) { spawn1x = j * corWidth; spawn1y = i * corHeight; }
                        else if( k == 'b' ) { spawn2x = j * corWidth; spawn2y = i * corHeight; }
                        else if( k == 'c' ) { spawn3x = j * corWidth; spawn3y = i * corHeight; }
                        else if( k == 'd' ) { spawn4x = j * corWidth; spawn4y = i * corHeight; }
                        else { tiles[i][j] = new MapTile( k, j, i, corWidth, corHeight ); }
                    }
                }
            }
            
            in.close();
        } catch ( Exception e ) {
            System.out.println( "sigh, try again." );
            System.out.print( e );
        }
        //fetch background images, gnome tile images here, just big ones.
        background = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/"+filename+"-b.png"));
        foreground = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/"+filename+"-f.png"));
        
    }
    
    //gets all tiles for drawing
    public ArrayList<MapTile> getTiles() {
        ArrayList<MapTile> toReturn = new ArrayList<MapTile>(tiles.length*tiles[0].length);
        for( int i = 0; i < tiles.length; i++ ) { 
                for ( int j = 0; j < tiles[i].length; j++ ) {
                    if( tiles[i][j] != null ) {
                        toReturn.add( tiles[i][j] );
                    }
                }
        }
        return toReturn;
    }
    
    
    //gets tiles in the area for processing
    //Caution: no checking for out of range x and y's
    public ArrayList<MapTile> getTiles(float x, float y) {
        ArrayList<MapTile> toReturn = new ArrayList<MapTile>(9); //at most, 9
        
        int corX = (int)(x / corWidth) ; //num rows from 0 x is;
        int corY = (int)(y / corHeight); //samey
        
        //bind them within the array
        int startX, endX, startY, endY;
        if( corX - 1 < 0 ) { startX = 0; } else { startX = corX - 1; }
        if( corX + 1 >= tiles[0].length ) { endX = tiles[0].length - 1; } else { endX = corX + 1; }
        if( corY - 1 < 0 ) { startY = 0; } else { startY = corY - 1; }
        if( corY + 1 >= tiles.length ) { endY = tiles.length - 1; } else { endY = corY + 1; }
        
        for( int i = startY; i <= endY; i++ ) { //from 3 tiles within range of the index
            for( int j = startX; j <= endX; j++ ) {
                if( tiles[i][j] != null ) {
                    toReturn.add( tiles[i][j] );
                }
            }
        }
        
        return toReturn;
    }
    
    public float getSpawnX(int num) { 
        switch(num) {
            case 1: return spawn1x; 
            case 2: return spawn2x; 
            case 3: return spawn3x; 
            case 4: return spawn4x; 
        }
        return 0;
    }
    public float getSpawnY(int num) { 
            switch(num) {
            case 1: return spawn1y; 
            case 2: return spawn2y; 
            case 3: return spawn3y; 
            case 4: return spawn4y; 
        }
        return 0;
    }
    
    public String toString() {
        String toReturn = new String();
        for( MapTile[] i : tiles ) {
            for( MapTile j : i ) {
                if( j != null ) {
                    toReturn += j.toString();
                } else { toReturn += " "; }
            }
            toReturn += "\n";
        }
        return toReturn;
    }
    
    public void drawBackground( Graphics2D g ) {
        g.drawImage( background, 0, 0, null );
        
        
        if( DEBUG_DRAW_TILES || background == null ) { //if there's no background image
            for( int i = 0; i <= tiles.length; i++ ) { //from 3 tiles within range of the index
                for( int j = 0; j <= tiles[0].length; j++ ) {
                    if( tiles[i][j] != null ) {
                        tiles[i][j].draw(g);
                    }
                }
            }
        }
    }
    
    public void drawForeground( Graphics2D g ) {
        g.drawImage( foreground, 0, 0, null );   
    }
}
