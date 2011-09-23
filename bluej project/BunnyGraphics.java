import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Random;
/**
 * Write a description of class BunnyGraphics here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BunnyGraphics extends JPanel implements KeyListener
{
    

    public static final boolean DEBUG_TEXT = false; //print debug text



    //controls change here
    public static final int p1Up = KeyEvent.VK_UP;
    public static final int p1Down = KeyEvent.VK_DOWN;
    public static final int p1Left = KeyEvent.VK_LEFT;
    public static final int p1Right = KeyEvent.VK_RIGHT;
    public static final int p1Turbo = KeyEvent.VK_BACK_SLASH;
    
    public static final int p2Up = KeyEvent.VK_W;
    public static final int p2Down = KeyEvent.VK_S;
    public static final int p2Left = KeyEvent.VK_A;
    public static final int p2Right = KeyEvent.VK_D;
    public static final int p2Turbo = KeyEvent.VK_Q;
    
    public static final int p3Up = KeyEvent.VK_I;
    public static final int p3Down = KeyEvent.VK_K;
    public static final int p3Left = KeyEvent.VK_J;
    public static final int p3Right = KeyEvent.VK_L;
    public static final int p3Turbo = KeyEvent.VK_B;
    
    public static final int p4Up = KeyEvent.VK_NUMPAD8;
    public static final int p4Down = KeyEvent.VK_NUMPAD5;
    public static final int p4Left = KeyEvent.VK_NUMPAD4;
    public static final int p4Right = KeyEvent.VK_NUMPAD6;
    public static final int p4Turbo = KeyEvent.VK_NUMPAD0;

    
    public static final int NUM_GORES = 50; //number of giblets from a kill
    
    
    //some double buffering
    
    //with some processing saving map drawn image
    private Graphics2D dbg;
    //private BufferedImage mapImage;
    private BufferedImage dbImage = null;


    
    
    private ArrayList<Player> players;
    private Player leader;
    
    private int numPlayers;
    private ArrayList<Gore> gores; //giblets
    
    private int pointLimit;
    
    private Map map;
    
    private boolean p3enabled, p4enabled;
    
    //state variables
    private boolean isRunning,isGameOver;
    
    //embellishment
    Image logo = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/logo.png"));
    
    
    /**
     * Constructor for objects of class BunnyGraphics
     */
    public void BunnyGraphics() {
        isRunning = false;
        isGameOver = false;
    }
    
    
    public void init(String mapName, boolean p3en, boolean p4en,
        String p1name, String p2name, String p3name, String p4name, int points)
    {
        //keys
        this.setFocusable(true);
        this.addKeyListener(this);
        this.requestFocus();
        
        // initialise instance variables
        map = new Map(mapName);
        
        pointLimit = points;
        
        p3enabled = p3en;
        p4enabled = p4en;
        
        //initialize players
        Image p1LeftImage = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/p1LeftAvatar.gif"));
        Image p1RightImage = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/p1RightAvatar.gif"));
        Image p2LeftImage = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/p2LeftAvatar.gif"));
        Image p2RightImage = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/p2RightAvatar.gif"));
        Image p3LeftImage = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/p3LeftAvatar.gif"));
        Image p3RightImage = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/p3RightAvatar.gif"));
        Image p4LeftImage = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/p4LeftAvatar.gif"));
        Image p4RightImage = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/p4RightAvatar.gif"));
        
        numPlayers = 2;
        if( p3enabled ) { numPlayers++; }
        if( p4enabled ) { numPlayers++; }
        
        players = new ArrayList<Player>(numPlayers);
        
        
        //player 1
        players.add(0, new Player( map.getSpawnX(1), map.getSpawnY(1), p1name, 1, p1LeftImage, p1RightImage, numPlayers ) );
        //player 2
        players.add(1, new Player( map.getSpawnX(2), map.getSpawnY(2), p2name, 2, p2LeftImage, p2RightImage, numPlayers ) );
        
        //player 3
        if( p3enabled ) {
            players.add(2, new Player( map.getSpawnX(3), map.getSpawnY(3), p3name, 3, p3LeftImage, p3RightImage, numPlayers ) );
        }
        
        //player 4
        if ( p4enabled ) {
            players.add(3, new Player( map.getSpawnX(4), map.getSpawnY(4), p4name, 4, p4LeftImage, p4RightImage, numPlayers ) );
        }
        
        //start as first player
        leader = players.get(0);
        
        gores = new ArrayList<Gore>(NUM_GORES*4); // a reasonable amount

        
        //mapImage = new BufferedImage(Map.WINDOW_WIDTH, Map.WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
        //Graphics2D tempG = mapImage.createGraphics();
        //tempG.setPaint( Color.BLACK );
        //tempG.fill( new Rectangle( 0, 0, Map.WINDOW_WIDTH, Map.WINDOW_HEIGHT ) );
        //map.drawBackground( tempG );
        
        BattleBunnies.debug( "Game started with map " + mapName + " and " + numPlayers + " players.",1 );
        
        
        isRunning = true;
        
    }
    
    public void updateState() {
        
        if( isRunning ) {
        for( Player p : players ) {
 
            p.applyKeys(); //change vector based on input
            
            p.applyGravity();
            
            
            //wierd error checking when bunnies get stuck at in the exact same position, elastic collision forces NaN
            if( !(p.getY() >= 0) && !(p.getY() <= 0) && !(p.getX() >= 0) && !(p.getX() <= 0) ) {
                BattleBunnies.debug( "Got stuck in wierd error: " + p,1 );
                p.setX(map.getSpawnX( p.number() ) ); p.setY(map.getSpawnY(p.number() )); p.setVelocity( new Vector(0,0) ); 
            }
            
            
            p.updatePosition(); //update x,y based on velocity

            
            
            //out of map checking
            if( p.getY() > Map.WINDOW_HEIGHT ) { 
                BattleBunnies.debug( "Fell off bottom: " + p,1 );
                p.setX(map.getSpawnX( p.number() ) ); p.setY(map.getSpawnY( p.number() )); p.setVelocity( new Vector(0,0) ); 
            } //reset to spawn point, prevents falling off bottom of map
            if( p.getY() < -1000 ) {
                BattleBunnies.debug( "Went into orbit: " + p,1 ) ;
                p.setX(map.getSpawnX( p.number() ) ); p.setY(map.getSpawnY(p.number() )); p.setVelocity( new Vector(0,0) ); 
            }
            //out of map X checking, which resets their X position to in the range, and stops their X velocity
            if( ( p.getY() <= 0 || p.getY() >= Map.WINDOW_HEIGHT ) && p.getX() < 0 ) {
                BattleBunnies.debug( "Got stopped above left: " + p,1 );
                p.setX(0); p.setVelocity( new Vector( 0,  p.getVelocity().getY()) );
            }
            if( ( p.getY() <= 0 || p.getY() >= Map.WINDOW_HEIGHT ) && p.getX() > Map.WINDOW_WIDTH ) {
                BattleBunnies.debug( "Got stopped above right: " + p,1 );
                p.setX(Map.WINDOW_WIDTH); p.setVelocity( new Vector( 0,  p.getVelocity().getY() ) ); 
            }
            
            //in map space X checking, which would infinite the player within an edge block
            if( ( p.getY() >= 0 || p.getY() <= Map.WINDOW_HEIGHT ) && p.getX() < 0 ) {
                BattleBunnies.debug( "Got stuck left: " + p,1 );
                p.setX(map.getSpawnX( p.number() ) ); p.setY(map.getSpawnY(p.number() )); p.setVelocity( new Vector(0,0) ); 
            }
            if( ( p.getY() >= 0 || p.getY() <= Map.WINDOW_HEIGHT ) && p.getX() > Map.WINDOW_WIDTH ) {
                BattleBunnies.debug( "Got stuck right: " + p,1);
                p.setX(map.getSpawnX( p.number() ) ); p.setY(map.getSpawnY(p.number() )); p.setVelocity( new Vector(0,0) );
            }
            
            
            
            
            for( MapTile i : map.getTiles(p.getX(),p.getY()) ) {
                if( i.intersects(p) ) {
                    BattleBunnies.debug( p + " collides with " + i,3 );
                    int posRelTo = p.posRelativeTo(i); //prevents excessive cpu usage
                    if( posRelTo == 1 ) { 
                        p.returnToGround(); 
                        p.counteractGravity();
                    }
                    i.deflect(p, posRelTo);
                    p.simpleUncollide(i, posRelTo);
                }
            }
            
            for( Player i : players ) {
                if( !p.equals(i) && p.intersects(i) ) {
                    
                    BattleBunnies.debug( p + " collides with " + i,3 );
                    int posRelTo = p.posRelativeTo(i);
                    
                    
                    if( !i.isInvulnerable() && posRelTo == 1 ) { //this one gets a kill
                            //make gore appear
                            Random rand = new Random();
                            for( int j = 0; j < NUM_GORES; j++ ) {
                                float newX = rand.nextInt((int)i.getWidth()) + i.getX();
                                float newY = rand.nextInt((int)i.getHeight()) + i.getY();
                                Vector newVector = new Vector( newX - i.getHull().getCenterX() , i.getHull().getCenterY() - newY );
                                float newDiameter = rand.nextInt(5)+5;
                                gores.add( new Gore( newX, newY, newDiameter, newDiameter , newVector) );
                            }
                        
                            //move other player back to spawn point.
                            //random spawn
                            
                            i.respawn( map.getSpawnX( rand.nextInt(numPlayers-1) + 1 ), map.getSpawnY( rand.nextInt(numPlayers-1) + 1 ) );
                            
                            //add jump to this player
                            p.addVelocity( Player.KILL_BOUNCE );
                            
                            p.gotAKill( i.number() );
                            if( p.getTotalKills() > leader.getTotalKills() ) { leader = p; }
                            
                            //game over?
                            if( p.getTotalKills() >= pointLimit ) { 
                                isRunning = false;
                                isGameOver = true;
                            }
                            
                            
                            
                            
                        }
                        else if ( !p.isInvulnerable() && posRelTo == 4 ) { //this one dies, this i think prevents kill canceling better
                            
                            //rain giblets from the sky
                            Random rand = new Random();
                            for( int j = 0; j < NUM_GORES; j++ ) {
                                float newX = rand.nextInt((int)i.getWidth()) + i.getX();
                                float newY = rand.nextInt((int)i.getHeight()) + i.getY();
                                Vector newVector = new Vector( newX - i.getHull().getCenterX() , i.getHull().getCenterY() - newY );
                                float newDiameter = rand.nextInt(5)+5;;
                                gores.add( new Gore( newX, newY, newDiameter, newDiameter , newVector) );
                            }
                        
                            //move this player back to spawn
                            //randomly
                            p.respawn( map.getSpawnX( rand.nextInt(numPlayers-1) + 1 ), map.getSpawnY( rand.nextInt(numPlayers-1) + 1) );
                            //add jump to other player
                            i.addVelocity( Player.KILL_BOUNCE );
                            
                            
                            i.gotAKill( p.number() );
                            if( i.getTotalKills() > leader.getTotalKills() ) { leader = i; }
                            
                           //game over?
                            if( i.getTotalKills() >= pointLimit ) { 
                                isRunning = false;
                                isGameOver = true;
                            }
                            
                            
                        }
                        else {
                            p.simpleUncollide( i, posRelTo );
                            p.applyElasticCollision( i );
                        }   
                    
                    
                    //test for win
                }  
                
            }
            
            
        }
        
        //update gores
        //gnome for: in loop here due to co current modification
        for( int k = 0; k < gores.size(); k++ ) {
            
            Gore p = gores.get(k);
            
            p.deathCounter--;
            if( p.deathCounter < 0 ) {
                gores.remove( gores.indexOf( p ) );
                k--;
                continue;
            } 
            
            p.applyGravity();
            
            
            p.updatePosition(); //update x,y based on velocity

            for( MapTile i : map.getTiles(p.getX(),p.getY()) ) {
                if( i.intersects(p) ) {
                    int posRelTo = p.posRelativeTo(i);
                    if( posRelTo == 1 ) { 
                        p.counteractGravity();
                    }
                    i.deflect(p,posRelTo);
                    p.simpleUncollide(i,posRelTo);
                }
            }
            

            for( Gore i : gores ) {
                if( !i.equals(p) && i.intersects(p) ) {
                    int posRelTo = p.posRelativeTo(i);
                    p.simpleUncollide(i,posRelTo);
                    i.applyElasticCollision(p);
                }
           }


        }
        
        }//end of isRunning
    }
    
    //double buffered
    public void drawToBuffer() {
        
        
        
        if( dbImage == null ) { //create that
            dbImage = new BufferedImage(Map.WINDOW_WIDTH, Map.WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
            if( dbImage == null ) {
                BattleBunnies.debug( "funny. dbImage is still null. ",1 );
                return;
            }
            BattleBunnies.debug( "buffer created successfully",3 );
        } else {
            dbg = (Graphics2D) dbImage.createGraphics();
            if( dbg == null ) {
                BattleBunnies.debug( "OH NOES THE BUFFER GRAPHICS IS NULL ",1 );
                return;
            }
        }
        if( dbg == null ) {
                BattleBunnies.debug( "OH NOES THE BUFFER GRAPHICS IS NULL ",1 );
                return; //waiting once seems to help
        }
        //antialiasing
        RenderingHints rh = new RenderingHints(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
        dbg.setRenderingHints(rh);
        
        
        if(isRunning) {
        
        
        //draw premade tiles
        //dbg.drawImage( mapImage, 0, 0, null );
        
        
        
        map.drawBackground( dbg );
        
        //some sort of weird cocurrent modification exception here
         try {
         for( Gore i : gores ) {
            i.draw(dbg);   
         }
         } catch (Exception e ) { }
         
         for( Player i : players ) {
             i.draw(dbg);
         }
        
         //draw map foreground over everything else
         map.drawForeground(dbg);
         
         //debug text
         if( DEBUG_TEXT ) {
            dbg.setPaint( Color.WHITE );
            dbg.drawString( String.valueOf( players.get(0).getX() ), 100, 100 );
            dbg.drawString( String.valueOf( players.get(0).getY() ), 100, 120 );
            dbg.drawString( String.valueOf( players.get(1).getX() ), 700, 100 ); 
            dbg.drawString( String.valueOf( players.get(1).getY() ), 700, 120 );
            if( p3enabled ) {
                dbg.drawString( String.valueOf( players.get(2).getX() ), 100, 500 );
                dbg.drawString( String.valueOf( players.get(2).getY() ), 100, 520 );
            }
            if( p4enabled ) {
                dbg.drawString( String.valueOf( players.get(3).getX() ), 700, 500 ); 
                dbg.drawString( String.valueOf( players.get(3).getY() ), 700, 520 );
            }
         }

            //scores
            
            dbg.setColor( new Color( 0, 0, 0, 128 ) );
            if( players.get(0).equals( leader ) ) { dbg.setColor( new Color( 0,0,255, 128 ) ); }
            dbg.fill( new Rectangle( 30, 560, 150, 25 ) );
            
            dbg.setColor( new Color( 0, 0, 0, 128 ) );
            if( players.get(1).equals( leader ) ) { dbg.setColor( new Color( 0,0,255, 128 ) ); }
            dbg.fill( new Rectangle( 30+185, 560, 150, 25 ) );
            
            dbg.setColor( new Color( 0, 0, 0, 128 ) );        
            if( p3enabled ) { 
                if( players.get(2).equals( leader ) ) { dbg.setColor( new Color(0,0,255, 128 ) ); }
                dbg.fill( new Rectangle( 30+185+185, 560, 150, 25 ) ); 
            }
            
            dbg.setColor( new Color( 0, 0, 0, 128 ) );
            if( p4enabled ) { 
                if( players.get(3).equals( leader ) ) { dbg.setColor( new Color( 0,0,255, 128 ) ); }
                dbg.fill( new Rectangle( 30+185+185+185, 560, 150, 25 ) ); 
            }
            
            dbg.drawImage( players.get(0).getImage(), 31, 560, null );
            dbg.drawImage( players.get(1).getImage(), 31+185, 560, null );
            if( p3enabled ) { dbg.drawImage( players.get(2).getImage(), 31+185+185, 560, null ); }
            if( p4enabled ) { dbg.drawImage( players.get(3).getImage(), 31+185+185+185, 560, null ); }
            
            dbg.setColor( Color.WHITE );
            dbg.setFont( dbg.getFont().deriveFont((float)10.0) );
            
            dbg.drawString( players.get(0).getDeaths()+" DEATHS", 55, 583 );
            dbg.drawString( players.get(1).getDeaths()+" DEATHS", 55+185, 583 );
            if( p3enabled ) { dbg.drawString( players.get(2).getDeaths()+" DEATHS", 55+185+185, 583 ); }
            if( p4enabled ) { dbg.drawString( players.get(3).getDeaths()+" DEATHS", 55+185+185+185, 583 ); }
            
            dbg.drawString( players.get(0).getName(), 55, 570 );
            dbg.drawString( players.get(1).getName(), 55+185, 570 );
            if( p3enabled ) { dbg.drawString( players.get(2).getName(), 55+185+185, 570 ); }
            if( p4enabled ) { dbg.drawString( players.get(3).getName(), 55+185+185+185, 570 ); }
            
            if( players.get(0).getCurrentKillStreak() > 3 ) { dbg.setColor(Color.RED ); }
            if( players.get(0).getCurrentKillStreak() > 6 ) { dbg.setColor(Color.YELLOW ); }
            dbg.drawString( players.get(0).getCurrentKillStreak()+"X", 108, 570 );
            
            if( players.get(1).getCurrentKillStreak() > 3 ) { dbg.setColor(Color.RED ); }
            if( players.get(1).getCurrentKillStreak() > 6 ) { dbg.setColor(Color.YELLOW ); }
            dbg.drawString( players.get(1).getCurrentKillStreak()+"X", 108+185, 570 );
            
            if( p3enabled ) { 
                if( players.get(2).getCurrentKillStreak() > 3 ) { dbg.setColor(Color.RED ); }
                if( players.get(2).getCurrentKillStreak() > 6 ) { dbg.setColor(Color.YELLOW ); }
                dbg.drawString( players.get(2).getCurrentKillStreak()+"X", 108+185+185, 570 );
            }
            
            if( p4enabled ) { 
                if( players.get(3).getCurrentKillStreak() > 3 ) { dbg.setColor(Color.RED ); }
                if( players.get(3).getCurrentKillStreak() > 6 ) { dbg.setColor(Color.YELLOW ); }
                dbg.drawString( players.get(3).getCurrentKillStreak()+"X", 108+185+185+185, 570 );
            }
            
            dbg.setColor(Color.RED );
            dbg.setFont( dbg.getFont().deriveFont((float)24.0) );
            
            dbg.drawString( ""+players.get(0).getTotalKills(), 138, 580);
            dbg.drawString( ""+players.get(1).getTotalKills(), 138+185, 580);
            if( p3enabled ) { dbg.drawString( ""+players.get(2).getTotalKills(), 138+185+185, 580); }
            if( p4enabled ) { dbg.drawString( ""+players.get(3).getTotalKills(), 138+185+185+185, 580); }
            
            
        }//end isRunning
        
    }
    
    
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        RenderingHints rh = new RenderingHints(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);

        super.paintComponent( g );
        
        if( isRunning ) {
            //draw buffer
            if( dbImage != null ) {
                g.drawImage( dbImage, 0, 0, null );
            }
        }
        else if( isGameOver ) {
            
            map.drawBackground( g2 );
            map.drawForeground( g2 );
            g2.setPaint( new Color( 0, 0, 0, 169 ) );
            g2.fill( new Rectangle( 0, 0, Map.WINDOW_WIDTH, Map.WINDOW_HEIGHT ) );
            g2.setPaint( Color.WHITE );
            
            g2.setFont( dbg.getFont().deriveFont((float)48.0) );
            
            g2.drawString( " Its Over!" , 300, 100);
            g2.drawString( leader.getName() + " Wins!", 275, 150 ); 
            
            g2.setFont( dbg.getFont().deriveFont((float)14.0) );
            
            g2.drawString( players.get(0).getName() + " had " + players.get(0).getTotalKills() + " kills and "
                         + players.get(0).getDeaths() + " deaths, with at most "  + players.get(0).getHighestKillStreak() +
                         " kills in a row!", 100, 200 );
                         
            g2.drawString( players.get(1).getName() + " had " + players.get(1).getTotalKills() + " kills and "
                         + players.get(1).getDeaths() + " deaths, with at most "  + players.get(1).getHighestKillStreak() +
                         " kills in a row!", 100, 200+75 );
            
            if( p3enabled ) {             
            g2.drawString( players.get(2).getName() + " had " + players.get(2).getTotalKills() + " kills and "
                         + players.get(2).getDeaths() + " deaths, with at most "  + players.get(2).getHighestKillStreak() +
                         " kills in a row!", 100, 200+75+75 );
                        }
            
                        
            if( p4enabled ) {            
            g2.drawString( players.get(3).getName() + " had " + players.get(3).getTotalKills() + " kills and "
                         + players.get(3).getDeaths() + " deaths, with at most "  + players.get(3).getHighestKillStreak() +
                         " kills in a row!", 100, 200+75+75+75 );
            }            
            
            g2.setFont( dbg.getFont().deriveFont((float)12.0) );
            
            
            g2.drawString( players.get(0).getName() + " Killed " + players.get(1).getName() + " " +
                         + players.get(0).getKills(2) + " Times! " , 550, 200 );
            
            if( p3enabled ) {
            g2.drawString( players.get(0).getName() + " Killed " + players.get(2).getName() + " " +
                         + players.get(0).getKills(3) + " Times! " , 550, 200+20 );                 
                        } 
                        
            if( p4enabled ) {
            g2.drawString( players.get(0).getName() + " Killed " + players.get(3).getName() + " " +
                         + players.get(0).getKills(4) + " Times! " , 550, 200+20+20 );                 
                        } 
                        
                        
            g2.drawString( players.get(1).getName() + " Killed " + players.get(0).getName() + " " +
                         + players.get(1).getKills(1) + " Times! " , 550, 200+80 );
            
            if( p3enabled ) {
            g2.drawString( players.get(1).getName() + " Killed " + players.get(2).getName() + " " +
                         + players.get(1).getKills(3) + " Times! " , 550, 200+20+80 );                 
                        } 
                        
            if( p4enabled ) {
            g2.drawString( players.get(1).getName() + " Killed " + players.get(3).getName() + " " +
                         + players.get(1).getKills(4) + " Times! " , 550, 200+20+20+80 );                 
                        }
                        
                        
                        
            if( p3enabled ) {
            g2.drawString( players.get(2).getName() + " Killed " + players.get(0).getName() + " " +
                         + players.get(2).getKills(1) + " Times! " , 550, 200+80+80 );
            
            
            g2.drawString( players.get(2).getName() + " Killed " + players.get(1).getName() + " " +
                         + players.get(2).getKills(2) + " Times! " , 550, 200+20+80+80 );                 
                         
                        
            if( p4enabled ) {
            g2.drawString( players.get(2).getName() + " Killed " + players.get(3).getName() + " " +
                         + players.get(2).getKills(4) + " Times! " , 550, 200+20+20+80+80 );                 
                        }    
                
                
            }
            
            if( p4enabled ) {
            g2.drawString( players.get(3).getName() + " Killed " + players.get(0).getName() + " " +
                         + players.get(3).getKills(1) + " Times! " , 550, 200+80+80+80 );
            

            g2.drawString( players.get(3).getName() + " Killed " + players.get(1).getName() + " " +
                         + players.get(3).getKills(2) + " Times! " , 550, 200+20+80+80+80 );                 

                        
 
            g2.drawString( players.get(3).getName() + " Killed " + players.get(2).getName() + " " +
                         + players.get(3).getKills(4) + " Times! " , 550, 200+20+20+80+80+80 );                 
   
            }
            
            
            
        } else {
            //draw static logo in center
            g2.setPaint( Color.BLACK );
            g2.fill( new Rectangle( 0, 0, Map.WINDOW_WIDTH, Map.WINDOW_HEIGHT ) );
            g2.drawImage( logo, 0, 0, null );
        }
        
    }
    
    public void keyTyped(KeyEvent e){}
    
    public void keyPressed(KeyEvent e) {
        int i = e.getKeyCode();
        
        //System.out.println( "key pressed: " + i );
        
        if( isRunning ) {
        
        if( i == p1Up) { players.get(0).setUp( true ); }
        if( i == p1Down) { players.get(0).setDown( true ); }
        if( i == p1Left) { players.get(0).setLeft( true ); }
        if( i == p1Right) { players.get(0).setRight( true ); }
        if( i == p1Turbo) { players.get(0).setTurbo( true ); }
            
        if( i == p2Up) { players.get(1).setUp( true ); }
        if( i == p2Down) { players.get(1).setDown( true ); }
        if( i == p2Left) { players.get(1).setLeft( true ); }
        if( i == p2Right) { players.get(1).setRight( true ); }
        if( i == p2Turbo) { players.get(1).setTurbo( true ); }
        
        if( p3enabled ) {
            if( i == p3Up) { players.get(2).setUp( true ); }
            if( i == p3Down) {  players.get(2).setDown( true ); }
            if( i == p3Left) { players.get(2).setLeft( true ); }
            if( i == p3Right) { players.get(2).setRight( true ); }
            if( i == p3Turbo) { players.get(2).setTurbo( true ); }
        }
        
        if( p4enabled ) {
            if( i == p4Up) { players.get(3).setUp( true ); }
            if( i == p4Down) { players.get(3).setDown( true ); }
            if( i == p4Left) { players.get(3).setLeft( true ); }
            if( i == p4Right) { players.get(3).setRight( true ); }
            if( i == p4Turbo) { players.get(3).setTurbo( true ); }
        }
        
        }
    }
    
    public void keyReleased(KeyEvent e) {
        int i = e.getKeyCode();

        if( isRunning ) {
        
        if( i == p1Up) { players.get(0).setUp( false ); }
        if( i == p1Down) { players.get(0).setDown( false ); }
        if( i == p1Left) { players.get(0).setLeft( false ); }
        if( i == p1Right) { players.get(0).setRight( false ); }
        if( i == p1Turbo) { players.get(0).setTurbo( false ); }
            
        if( i == p2Up) { players.get(1).setUp( false ); }
        if( i == p2Down) { players.get(1).setDown( false ); }
        if( i == p2Left) { players.get(1).setLeft( false ); }
        if( i == p2Right) { players.get(1).setRight( false ); }
        if( i == p2Turbo) { players.get(1).setTurbo( false ); }
        
        if( p3enabled ) {
            if( i == p3Up) { players.get(2).setUp( false ); }
            if( i == p3Down) {  players.get(2).setDown( false ); }
            if( i == p3Left) { players.get(2).setLeft( false ); }
            if( i == p3Right) { players.get(2).setRight( false ); }
            if( i == p3Turbo) { players.get(2).setTurbo( false ); }
        }
        
        if( p4enabled ) {
            if( i == p4Up) { players.get(3).setUp( false ); }
            if( i == p4Down) { players.get(3).setDown( false ); }
            if( i == p4Left) { players.get(3).setLeft( false ); }
            if( i == p4Right) { players.get(3).setRight( false ); }
            if( i == p4Turbo) { players.get(3).setTurbo( false ); }
        }
       
        }
    }
}
