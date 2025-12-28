package main.assets.images;

import javax.swing.ImageIcon;

public class ImageLoader {

    public static ImageIcon loadIcon( String Type, String Name ) {
        try {
            return new ImageIcon( ImageLoader.class.getResource("/main/assets/images/" + Type + "/" + Name + ".png") );

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;

        }
    }

    public static ImageIcon loadIcon( String Difficulty, String Type, String Name ) {
        try {
            return new ImageIcon( ImageLoader.class.getResource("/main/assets/images/" + Difficulty + "/" + Type + "/" + Name + ".png") );

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;

        }
    }
}
