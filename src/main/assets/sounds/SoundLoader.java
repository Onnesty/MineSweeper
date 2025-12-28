package main.assets.sounds;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundLoader {
    

    public static Clip LoadMusic(String Name) {
        
        try {
            AudioInputStream AudioFile = AudioSystem.getAudioInputStream( SoundLoader.class.getResource( "/main/assets/sounds/" + Name + ".wav" ) );
            Clip Clip = AudioSystem.getClip();
            Clip.open(AudioFile);
            return Clip;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        

    }


}
