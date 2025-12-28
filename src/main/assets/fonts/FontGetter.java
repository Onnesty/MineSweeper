package main.assets.fonts;

import java.awt.Font;

public class FontGetter {
    
    public static Font GetFont(String Name, int Size) throws Exception {
        // System.out.println( System.getProperty("user.dir") );
        Font CstFont = Font.createFont(Font.TRUETYPE_FONT, FontGetter.class.getResourceAsStream("/main/assets/fonts/" + Name.toLowerCase().strip() + ".ttf") );
        return CstFont.deriveFont(Font.PLAIN, Size);
    }
}
