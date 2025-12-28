package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import main.components.MainPanel;


public class App {
    public static void main(String[] args) throws Exception {
        

        JFrame Frame = new JFrame("MineSweeper");

        Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int Width = ScreenSize.width;
        int Height = ScreenSize.height;

        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Frame.setSize(Width, Height);
        Frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Frame.getContentPane().setBackground( new Color(0x1f1f1f) );
        Frame.setUndecorated(true);

        Frame.setLayout( new GridBagLayout() );
        GridBagConstraints Constr = new GridBagConstraints();

        Frame.add( new MainPanel() , Constr );

        Frame.setVisible(true);


        // KeyBinds ( No need for focus )
        JRootPane Root = Frame.getRootPane();
        InputMap IM = Root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap AM = Root.getActionMap();

        IM.put(KeyStroke.getKeyStroke("ESCAPE"), "ToggleExit");
        IM.put(KeyStroke.getKeyStroke("F11"), "ToggleExit");
        AM.put("ToggleExit", new AbstractAction() {
            boolean FullScreen = true;

            @Override
            public void actionPerformed(ActionEvent Event) {
                FullScreen = !FullScreen;

                Frame.dispose();
                Frame.setUndecorated(FullScreen);
                Frame.setVisible( true );

                Frame.requestFocusInWindow();
            }
        });

    }
}
