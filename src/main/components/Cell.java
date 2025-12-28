package main.components;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.Clip;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import main.assets.images.ImageLoader;
import main.assets.sounds.SoundLoader;

public class Cell extends JPanel{

    public final List<Cell> Neighbors = new ArrayList<>(); // For Auto Revealing Neighbors when current cell is 0 Bombs

    public int BombAmountInPerimeter = 0;

    public boolean Started = false; /*  This is to know if the game has been started, 
    the first Cell reveal will remove an area of Cells and then this will be true for all cellsm, 
    meaning that area reveal will only work for the first Cell*/

    public boolean Flagged = false;
    public boolean HasBomb;
    public boolean Revealed = false; // For optimization, preventing multiple Reveals
    private final Game Game;
    public boolean RevealedViaBomb = false; // To prevent infinite looping when revealing all Cells due to bomb reveal,cant use Revealed since it may block

    private final JLabel ImageLabel;
    private final String Difficulty;
    private final int TileVariations = 5; // Number of Different Tiles
    private final Clip Music;


    private final Random Rnd = new Random();
    private int RandomNum;




    public Cell(Dimension Dimensions, String Difficulty, Game Game) {

        this.Difficulty = Difficulty;
        this.Game = Game;

        setPreferredSize( Dimensions ); 
        setLayout( new GridBagLayout() );
        setCursor( new Cursor(Cursor.HAND_CURSOR) );
        GridBagConstraints Constr = new GridBagConstraints();

        RandomNum = Rnd.nextInt(TileVariations) + 1;  // Change according to number of images

        ImageLabel = new JLabel( ImageLoader.loadIcon( Difficulty, "Tile" + RandomNum ) );
        ImageLabel.setPreferredSize( Dimensions );

        // For Music
        Music = SoundLoader.LoadMusic( "Explosion");
        Music.setMicrosecondPosition(0); 

        // For Hover Animation
        AddListener();

        add(ImageLabel, Constr);

    }


    // Calculate the bombs in perimeter based on the neighbors
    public void CalculateBombs() {
        
        for (Cell CL : Neighbors) {

            if (CL != null && CL.HasBomb) {
                BombAmountInPerimeter++;
            }
    
        }
    }

    private void BombRevealed() {
        RevealedViaBomb = true; // Set True first to prevent infinite looping between two cells

        if (HasBomb) {
            ImageLabel.setIcon( ImageLoader.loadIcon( Difficulty, "Number", "Bomb" ) );
        } else {
            ImageLabel.setIcon( ImageLoader.loadIcon( Difficulty, "Number", "Tile" + "0" ) );
        }
       
        for (Cell CL : Neighbors) { // This will not block already revealed Cells since if it does it may stop the algorithm prematurely ( if bomb is boxed in naturally revealed Cells )
            if ( CL != null && !CL.RevealedViaBomb) { // If Neighbor Cell hasnt been revealed via bomb
                CL.BombRevealed();
            }
        }

        for (MouseListener listener : getMouseListeners()) { // Remove mouselistener
            removeMouseListener(listener);
        }

    }


    // Reveals the Tile
    private void Reveal() {
        if (Flagged ) { return; } // Prevent any area revealing from deleting flags
        if (Revealed) { return; } // For some reason this is still getting called even though Started and Revealed is true

        Game.CellRevealed(); // Tell the Game that a Cell is revealed

        
        // This must come first to prevent "0" Cells from calling each other back and forth endlessly
        Revealed = true; // Set it to true such that any Neightbor reveals by neighboring Cells with 0 Bomb perimeter wont stack
    

        ImageLabel.setIcon( ImageLoader.loadIcon( Difficulty, "Number", "Tile" + BombAmountInPerimeter ) ); // Reveal Self

        if (BombAmountInPerimeter == 0) { // If the Cell bomb in perimter is 0, then reveal the neighbors, if the neighbors are also 0 then this will chain

            for (Cell CL : Neighbors) {
                if ( CL != null && !CL.HasBomb && !CL.Revealed ) { // If Neighbor Cell doest have a bomb and is not revealed
                    CL.Reveal();
                }
            }

        }

        for (MouseListener listener : getMouseListeners()) { // Remove all mouse listeners
            removeMouseListener(listener);
        }

        if (!Started) { // If the game has not started meaning this is the first Cell to be shown
            FirstCellRevealed();
        }
  
    }


    

    private void FirstCellRevealed() {
        AlertCellsOfGameStart();

        for (Cell CL : Neighbors) {
            if (CL != null && !CL.HasBomb) { 
                CL.Reveal();
            }

        }
    }

    // Tells all the Cells that the game has started and when they are pressed they are not to reveal their Neighbors just like what the first revealed Cell does
    private void AlertCellsOfGameStart() {
        Started = true;
        for (Cell CL : Neighbors) {
            if (CL != null && !CL.Started) {
                CL.AlertCellsOfGameStart();
            }
        }

    }

    public final void ResetCell() {

        RandomNum = Rnd.nextInt(TileVariations) + 1; // New Random Number

        ImageLabel.setIcon( ImageLoader.loadIcon( Difficulty, "Tile" + RandomNum ) ); // New Random Design

        BombAmountInPerimeter = 0;

        Started = false; 
        Flagged = false;
        Revealed = false; 
        RevealedViaBomb = false; 

        for (MouseListener listener : getMouseListeners()) { // Remove Old listeners
            removeMouseListener(listener);
        }

        AddListener();
        
    }




    // Just some helper function to Add the Mouse Listeners
    private void AddListener() {
        MouseAdapter MListener = new MouseAdapter() {
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (Flagged) {
                    ImageLabel.setIcon( ImageLoader.loadIcon(Difficulty, "Flagged", "Tile" + RandomNum + "Selected" ) );
                } else {
                    ImageLabel.setIcon( ImageLoader.loadIcon( Difficulty, "Tile" + RandomNum + "Selected" ) );
                }
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (Flagged) {
                    ImageLabel.setIcon( ImageLoader.loadIcon( Difficulty, "Flagged", "Tile" + RandomNum ) );

                } else {
                    ImageLabel.setIcon( ImageLoader.loadIcon( Difficulty, "Tile" + RandomNum ) );
                }
                
            }

            @Override
            public void mousePressed(MouseEvent E) {

                if (SwingUtilities.isLeftMouseButton(E)) {
                    if (Flagged) { return; } // Return Early if this is flagged
                    
                    if (HasBomb) {
                        if (!Started) { FirstCellRevealed(); return; } // If the first Cell clicked is a bomb then it will just reveal the Neighbors
                        BombRevealed();
                        Music.start();

                        Game.Loss();
                        
                    } else {
                        Reveal();
                    }
                    removeMouseListener(this);
                   
                } else if (SwingUtilities.isRightMouseButton(E) && Started) { // Only allow flagging when Started

                    
                    if (!Flagged) {
                        ImageLabel.setIcon( ImageLoader.loadIcon(Difficulty, "Flagged", "Tile" + RandomNum + "Selected" ) );
                        Game.CellFlagged(); // Tell the Game Panel that a Cell is Flagged
                    } else {
                        ImageLabel.setIcon( ImageLoader.loadIcon( Difficulty, "Tile" + RandomNum + "Selected" ) );
                        Game.CellUnFlagged(); // Tell the Game that the Cell is unFlagged
                    }

                    Flagged = !Flagged;

                }      

            }
        };

        addMouseListener( MListener );

    }



    
}
