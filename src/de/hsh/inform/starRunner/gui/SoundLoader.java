package de.hsh.inform.starRunner.gui;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.*;

/**
 * StarRunner
 * Created by Florian on 15.11.2017.
 * Mit freundlicher Unterstüzung von Samuel Gläser
 */
public class SoundLoader {
    private final HashMap<String,Clip> mCache;
    private static volatile transient SoundLoader mInstance;
    private boolean playSound = true, playMusic = true;

    static synchronized SoundLoader getInstance() {
        if (mInstance == null)
            mInstance = new SoundLoader();
        return mInstance;
    }

    /**
     * SoundLoader für die Sounds
     */
    private SoundLoader() {
        mCache = new HashMap<>();
    }

    /**
     * Gibt Sounds als Clips an das GamePanel zurücl
     *
     * @param soundName soundname
     * @param index     ggf. Index des Sounds (bmpact_big_1/2/3...)
     * @return          Clip des Sounds
     */
    Clip getSound(String soundName, int index) {
        if(playMusic) {
            switch (soundName) {
                case "menu":
                    return loadSound("menu");
                case "result_bad":
                    return loadSound("result_bad");
                case "result_good":
                    return loadSound("result_good");
                case "music_ingame":
                    return loadSound("music_ingame_2");
            }
        }
        if(playSound) {
            switch (soundName) {
                case "buttonclick":
                    return loadSound("buttonclick");
                case "buttonrelease":
                    return loadSound("buttonclickrelease");
                case "buttonrollover":
                    return loadSound("buttonrollover");
                case "explode":
                    return loadSound("explode_" + index);
                case "impact_big":
                    return loadSound("impact_big_" + index);
                case "impact_border":
                    return loadSound("impact_border_" + index);
                case "impact_shield":
                    return loadSound("impact_shield_" + index);
                case "impact_small":
                    return loadSound("impact_small_" + index);
                case "repair":
                    return loadSound("repair");
                case "shield_pickup":
                    return loadSound("shield_pickup_" + index);
                case "windloop":
                    return loadSound("windloop");
                case "ambience":
                    return loadSound("ambience");
                default:
                    return null;
            }
        }
        else return loadSound("empty");
    }

    /**
     * Sounds an-/ausschalten
     *
     * @param setting boolean
     */
    void setSound(boolean setting) {
        playSound = setting;
    }

    private final String[] musicList = {
            "menu","result_bad","result_good","music_ingame"
    };

    /**
     * musik ausschalten
     */
    void stopMusic() {
        for (String aMusicList : musicList) {
            SoundUtilities.stop(getSound(aMusicList, 0));
        }
    }

    /**
     * Musik an-/ausschalten
     *
     * @param setting boolean
     */
    void setMusic(boolean setting) {
        if(!setting) {
            stopMusic();
        }
        playMusic = setting;
    }

    /**
     * alle Sounds stoppen
     */
    public void stopAll() {
        for (Object o : mCache.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if (pair.getValue() instanceof Clip) {
                SoundUtilities.stop((Clip) pair.getValue());
            }
        }
    }

    /**
     * Lädt Sound für das GamePanel als Clip
     *
     * @param soundFile pfad des Sounds
     * @return          Clip des Sounds
     */
    private Clip loadSound(final String soundFile) {
        Clip result = mCache.get(soundFile);
        if (result == null) {
            try {
                final ClassLoader cl = getClass().getClassLoader();
                final URL imgResource = cl.getResource("resources/sounds/" + soundFile + ".wav");
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(imgResource);
                result = AudioSystem.getClip();
                result.open(audioIn);
            }catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
                e.printStackTrace();
            }
            mCache.put(soundFile, result);
        }
        return result;
    }
}