package main.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.sound.sampled.Clip;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import main.assets.fonts.FontGetter;
import main.assets.images.ImageLoader;
import main.assets.sounds.SoundLoader;

public class Menu extends JPanel {

    private int BombAmount;

    private Game GamePanel;

    private final JLabel MenuLabel;
    private final JLabel FlagLabel;
    private final JLabel ClockLabel;
    private final Timer ClockTimer;
    private int CurrentTime = 0;

    private Clip Music;

    
    
    public Menu() throws Exception {

        setPreferredSize( new Dimension(900, 100) );
        setLayout( null );

        MenuLabel = new JLabel( ImageLoader.loadIcon("Menu", "Menu") );
        MenuLabel.setBounds( 0, 0, 900, 100);
        MenuLabel.setBackground( Color.red );
        add(MenuLabel);


        JLabel EasyLabel = new JLabel();
        EasyLabel.setBounds( 20, 29, 135, 42);
        EasyLabel.setCursor( new Cursor ( Cursor.HAND_CURSOR ) );
        EasyLabel.addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent E) {
                GamePanel.ResetGame("Easy");
            }
        });
        add( EasyLabel );


        JLabel MediumLabel = new JLabel();
        MediumLabel.setBounds( 170, 29, 135, 42);
        MediumLabel.setCursor( new Cursor ( Cursor.HAND_CURSOR ) );
        MediumLabel.addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent E) {
                GamePanel.ResetGame("Medium");
            }
        });
        add( MediumLabel );


        JLabel HardLabel = new JLabel();
        HardLabel.setBounds( 320, 29, 135, 42);
        HardLabel.setCursor( new Cursor ( Cursor.HAND_CURSOR ) );
        HardLabel.addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent E) {
                GamePanel.ResetGame("Hard");
            }
        });
        add( HardLabel );


        FlagLabel = new JLabel(" ");
        FlagLabel.setFont( FontGetter.GetFont("Georgia", 48) );
        FlagLabel.setHorizontalAlignment( SwingConstants.CENTER ); // Centering text
        FlagLabel.setVerticalAlignment( SwingConstants.CENTER );
        FlagLabel.setForeground( Color.gray );
        FlagLabel.setBounds(480, 22, 130, 50);
        add( FlagLabel );


        ClockLabel = new JLabel(CurrentTime + "");
        ClockLabel.setFont( FontGetter.GetFont("Georgia", 48) );
        ClockLabel.setHorizontalAlignment( SwingConstants.CENTER ); // Centering text
        ClockLabel.setVerticalAlignment( SwingConstants.CENTER );
        ClockLabel.setForeground( Color.gray );
        ClockLabel.setBounds(660, 15, 218, 63);
        add( ClockLabel );
        
      
        // Handling the Z Order
        setComponentZOrder(MenuLabel, 1);
        setComponentZOrder(EasyLabel, 0);
        setComponentZOrder(MediumLabel, 0);
        setComponentZOrder(HardLabel, 0);
        setComponentZOrder( FlagLabel, 0);
        setComponentZOrder( ClockLabel, 0);

        repaint();


        ClockTimer = new Timer( 1000, e -> {
            CurrentTime++;

            ClockLabel.setText(CurrentTime + "");
            ClockLabel.repaint();

        });


    }


    public void GetGame(Game GamePanel) { // For Communicating with the Game
        this.GamePanel = GamePanel;
    }

    public void ReceiveBombAmount (int Bomb) { // Also Serves as a Renderer for the Flag amounts
        this.BombAmount = Bomb;

        FlagLabel.setText(BombAmount + "");
        repaint();

    }

    public void Flagged() {
        FlagLabel.setText( (--BombAmount) + "");
        repaint();
    }

    public void UnFlagged() {
        FlagLabel.setText( (++BombAmount) + "");
        repaint();
    }

    public void AnnounceState(String Message) { // Uses the Flag Label to announce a Win or Lose
        ClockTimer.stop();
        FlagLabel.setText( Message );
        repaint();

        // Load music
        Music = SoundLoader.LoadMusic( (Message.equals("WIN")) ? "WinMusic" : "LoseMusic" );
        Music.loop(Clip.LOOP_CONTINUOUSLY);
        Music.setMicrosecondPosition(0);
        Music.start();
        
    }

    public void StopMusic() {
        if (Music == null) { return; }
        Music.setMicrosecondPosition(0);
        Music.stop();

    }

    public void StartTimer() {
        ClockTimer.start();
    }

    public void ResetTimer() {
        ClockTimer.stop();
        CurrentTime = 0;
        ClockLabel.setText("0");
        repaint();
    }


}
