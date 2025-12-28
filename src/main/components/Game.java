package main.components;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class Game extends JPanel {

    private final GridBagConstraints Constr = new GridBagConstraints();

    private final Cell[][] EasyArray = new Cell[10][10]; // Storing the Cells in a 2D array
    private final Cell[][] MediumArray = new Cell[20][20];
    private final Cell[][] HardArray = new Cell[30][30];

    private final Dimension EasyCellDimensions = new Dimension(90,90);
    private final Dimension MediumCellDimensions = new Dimension(45,45);
    private final Dimension HardCellDimensions = new Dimension(30,30);

    public int BombAmount; // Needed for MainPanel to give to Menu
    private int Size; // Needed for the Limits of the For Loops and Calculation of the Total Cells
    private String Difficulty; // For tracking current Difficulty
    private Cell[][] Arr; // Currently Used Array ( Either easy, medium, or hard array )
    public boolean EndGameState; // Used to toggle the Keybinds during win or loss to reset Game
    private boolean Started; // Used to toggle the clock only on the first occurence of Cell Reveal

    private int TotalCells;
    private int CellsRevealed;

    // DIRECTLY EDIT THE SIZE AND DIFFICULTY
    private final int EasySize = 10;
    private final int MediumSize = 20;
    private final int HardSize = 30;
    private final int EasyBomb = 15;
    private final int MediumBomb = 80;
    private final int HardBomb = 150;
    
    private Menu MenuPanel; // For Communicating with the Menu
    


    public Game() {

        setPreferredSize( new Dimension(900, 900) );

        setLayout( new GridBagLayout() );
        
        Constr.anchor = GridBagConstraints.CENTER;

        GenerateCells(); // Pre Generate Cells

        RenderCells("Easy"); // Default Mode on Start Up

        // KeyBinds
        InputMap IM = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap AM = getActionMap();

        IM.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "ResetGame");
        AM.put("ResetGame", new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent E) {
                if (EndGameState) {
                    EndGame();
                }
            }
        });



    }

    private void GenerateCells() { // Just initializes the Cell Arrays
        
        // Generating Cells
        for (int r = 0; r < HardSize; r++) {
            
            for (int c = 0; c < HardSize; c++) {

                // Only Adds a certain range of indices to Easy Array
                if ( r < EasySize && c < EasySize ) {
                    EasyArray[r][c] = new Cell(EasyCellDimensions, "Easy", this);
                }


                // Only Adds a certain range of indices to Easy Array
                if ( r < MediumSize && c < MediumSize ) {
                    MediumArray[r][c] = new Cell(MediumCellDimensions, "Medium", this);
                }

                // Adds automatically since the loop is set to the highest number of Cells posible which is the Hard
                HardArray[r][c] = new Cell(HardCellDimensions, "Hard", this);

            }
        }

        // Linking the Cells to their Neighbors
        for (int r = 0 ; r < HardSize; r++) {
            for (int c = 0; c < HardSize; c++) { // Generate Indices in a 2D Array

                for ( Cell[][] CurrArr : new Cell[][][]{ EasyArray, MediumArray, HardArray } ) { // Each Indices has an opportunity to be used in each Array

                    int ArrSize = CurrArr.length; // Gives the program a clue on what array is being presented

                    // If the number of Cells exceeds the amount of cells in Easy or Medium then continue to the next array
                    if ( (ArrSize == EasySize && (r >= EasySize || c >= EasySize) ) || (ArrSize == MediumSize && (r >= MediumSize || c >= MediumSize) ) ) {
                        continue;
                    }
                    
                    Cell CurrentCell = CurrArr[r][c];

                    // If the Row-1 is less than 0 then the North is null, Works with the other rows since theres always a Cell above them
                    CurrentCell.Neighbors.add ( (r-1 >= 0) ? CurrArr[r-1][c] : null ) ;

                    // If the Column+1 is 10 or greater then its the last one in the column and East is null
                    CurrentCell.Neighbors.add ( (c+1 < ArrSize) ? CurrArr[r][c+1] : null ) ; 

                    // If the Column-1 is less than 0 means that its the first in the column and West is null
                    CurrentCell.Neighbors.add ( (c-1 >= 0) ? CurrArr[r][c-1] : null ); 

                    // If the Row+1 is more than 9 then its the last row and south is null
                    CurrentCell.Neighbors.add ( (r+1 < ArrSize) ? CurrArr[r+1][c] : null ); 

                    CurrentCell.Neighbors.add ( (r-1 >= 0 && c+1 < ArrSize) ? CurrArr[r-1][c+1] : null );
                    CurrentCell.Neighbors.add ( (r-1 >= 0 && c-1 >= 0) ? CurrArr[r-1][c-1] : null );
                    CurrentCell.Neighbors.add ( (r+1 < ArrSize && c+1 < ArrSize) ? CurrArr[r+1][c+1] : null );
                    CurrentCell.Neighbors.add ( (r+1 < ArrSize && c-1 >= 0) ? CurrArr[r+1][c-1] : null );

                }
    
            }
        }

    }



    public final void RenderCells(String Difficulty) {

        // Tracking Current Difficulty globally
        this.Difficulty = Difficulty;

        // Reset Revealed Cells
        CellsRevealed = 0;

        // Reset Started
        Started = false;
        
        // Reset EndGameState , disabling the keybind
        EndGameState = false;

        
        switch(Difficulty) { // Sets the Arrays used, the Sizes of the Grid, and Bombs
            case "Easy":
                Arr = EasyArray;
                Size = EasySize;
                BombAmount = EasyBomb;
                break;
            
            case "Medium":
                Arr = MediumArray;
                Size = MediumSize;
                BombAmount = MediumBomb; 
                break;
            
            case "Hard":
                Arr = HardArray;
                Size = HardSize;
                BombAmount = HardBomb; 
                break;

            default:
                System.out.println("Error: Invalid Difficulty");
                return;
        }

        // Calculate the Total Cells
        CalculateCells();

        // Making random numbers corresponding to number of Cells which is Size * Size
        List<Integer> IntArr = new ArrayList<>();
        for (int i = 0; i < TotalCells ; i++) { IntArr.add(i+1); }
        Collections.shuffle(IntArr);
        // This will contain a list of random numbers of size Bomb amount
        IntArr = new ArrayList<>( IntArr.subList(0, BombAmount) );


        // For Tracking which Cell to put bomb
        int CellNumber = 1;

        // Put the New Bombs in and Place the Cell onto the screen
        for (int r = 0; r < Size; r++) {
            for (int c = 0; c < Size; c++) {

                Cell CurrCell = Arr[r][c];

                // Setting New Cell Properties, especially if this is not the first Render
                CurrCell.HasBomb = IntArr.contains(CellNumber++); // Returns true if the Cell Number is in the array of random numbers that correspond to cells that have bombs
                CurrCell.ResetCell();

                Constr.gridx = r;
                Constr.gridy = c;

                add(CurrCell, Constr);
            }
        }

        // Re Calculate the bombs in the Perimeter after they have been reset
        for (Cell[] Row : Arr) {
            for (Cell CL : Row) {
                CL.CalculateBombs();
            }
        }

    }


    // Restart Game with same Difficulty
    public void EndGame() {
        String OldDiff = Difficulty;
        Difficulty = ""; // Set it to blank since Resetgame ignores if its the same Difficulty
        ResetGame(OldDiff); 
    }

    // When theres a Difficulty Change or When there a reset due to win or loss
    public void ResetGame(String NewDifficulty) {
        // System.out.println("Old: " + Difficulty + "  New: " + NewDifficulty);
        if ( Difficulty.equals(NewDifficulty) ) { // If the new difficulty is the same as the current one
            return;
        } else { // If they are different

            for (int x = 0; x < Size; x++) { // Delete the Current Array Contents
                for (int y = 0; y < Size; y++) {
                    remove(Arr[x][y]);
                }
            }

        }

        RenderCells(NewDifficulty); // re Render
        
        MenuPanel.ResetTimer();
        MenuPanel.StopMusic();
        GiveBombAmount(); // Give new bomb amount to Menu

        validate();
        repaint();

    }



    
    public void CellRevealed() { // Logs the amount of Cells Revealed, Also serves as the Win Checker since every Cell revealed might be the last Cell needed to reveal
        
        if (!Started) { // When its the first Cell Revealed
            Started = true; // So it doesnt toggle again
            MenuPanel.StartTimer(); // Start Timer 
        }
        
        CellsRevealed++;

        if ( BombAmount == (TotalCells - CellsRevealed) ) { // If the Remaining Cells are equal to the amount of Bombs
            Win();
        }
    }


    public void CellFlagged() { // Logs the amount of Cells Flagged
        MenuPanel.Flagged();
    }

    public void CellUnFlagged() {
        MenuPanel.UnFlagged();
    }

    // When there is a loss
    public void Loss() {
        EndGameState = true; 
        // Toggle the EndGame state where the Keybind can activate 
        MenuPanel.AnnounceState("LOSS");
    }

    // When there is a win
    private void Win() {

        for (Cell[] CLArr : Arr) { // Remove the Mouse Listeners for Every Cell
            for (Cell CL : CLArr) {
                for (MouseListener listener : CL.getMouseListeners()) {
                    CL.removeMouseListener(listener);
                }
            }
        }

        EndGameState = true; // Set the end game state to true so that the keybind can work to reset the game
        MenuPanel.AnnounceState("WIN");
    }

    


    private void CalculateCells() { // For Calculating Cells
        TotalCells = Size * Size;
    }

    
    public void GetMenu(Menu MenuPanel) { // For Communication with the Menu Panel
        this.MenuPanel = MenuPanel;
    }

    public void GiveBombAmount() {
        MenuPanel.ReceiveBombAmount(BombAmount);
    }

}
