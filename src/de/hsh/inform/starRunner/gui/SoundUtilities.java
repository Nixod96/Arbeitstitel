package de.hsh.inform.starRunner.gui;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * StarRunner
 * Created by Florian on 11.01.2018.
 * Mit freundlicher Unterstüzung von Samuel Gläser
 */
class SoundUtilities {

    /**
     * Spielt einen unendlichen Loops des Sounds
     *
     * @param clip      clip des Sounds
     * @param volume    lautstärke (0.0-1.0)
     */
    static void playInfiniteLoop(Clip clip, double volume) {
        FloatControl gainControl  = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float db = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
        gainControl .setValue(db);

        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }

    /**
     * Spielt einen Sound vom Anfang
     *
     * @param clip      clip des Sounds
     * @param volume    lautstärke (0.0-1.0)
     */
    static void playFromStart(Clip clip, double volume) {
        FloatControl gainControl  = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float db = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
        gainControl .setValue(db);

        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    /**
     * Stoppt einen Clip
     *
     * @param clip  clip des Sounds
     */
    static void stop(Clip clip) {
        clip.stop();
    }
}