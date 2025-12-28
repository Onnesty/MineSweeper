package main.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

public class MainPanel extends JPanel {

    private final Menu MenuPanel;
    private final Game GamePanel; 


    public MainPanel() throws Exception {
        
        setPreferredSize( new Dimension(900, 1000) );
        setBackground( Color.gray );
        setLayout( new GridBagLayout() );
        GridBagConstraints Constr = new GridBagConstraints();

        MenuPanel = new Menu();
        add( MenuPanel, Constr );


        GamePanel = new Game();
        Constr.gridy = 1;
        add( GamePanel, Constr );

        GamePanel.GetMenu(MenuPanel);
        GamePanel.GiveBombAmount(); // Do this only after Game is done intializing, not during

        MenuPanel.GetGame(GamePanel);
        
    }
    
}
