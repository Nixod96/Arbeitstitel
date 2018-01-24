package de.hsh.inform.starRunner.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

/**
 * StarRunner
 * Created by Florian on 16.11.2017.
 */

public class ImageLoader {
    /**
     * SINGLETON
     */
    private static volatile transient ImageLoader mInstance;

    private final HashMap<String, BufferedImage> mCache;

    public static synchronized ImageLoader getInstance() {
        if (mInstance == null)
            mInstance = new ImageLoader();
        return mInstance;
    }

    /**
     * ImageLoader zum buffern der Bilder
     */
    private ImageLoader() {
        // singleton-Instanz
        mCache = new HashMap<>();
    }

    /**
     * Gibt Bilder an das GamePanel zurück
     *
     * @param imageName name des Bildes
     * @param index     ggf. Index des Bildes (asteroid_1/2/3...)
     * @return          bufferedImage des Bildes
     */
    public BufferedImage getImage(String imageName, int index) {
        switch (imageName) {
            case "asteroid":
                return loadImage("asteroid_" + index);
            case "border":
                return loadImage("border_" + index) ;
            case "level_progress":
                return loadImage("level_progress");
            case "level_progressbar":
                return loadImage("level_progressbar");
            case "level_progressbar_pixel":
                return loadImage("level_progressbar_pixel");
            case "life":
                return loadImage("life");
            case "lifebar":
                return loadImage("lifebar");
            case "schild":
                return loadImage("shield");
            case "powerup_reparieren":
                return loadImage("powerup_reparieren");
            case "powerup_schein":
                return loadImage("powerup_schein");
            case "powerup_schild":
                return loadImage("powerup_schild");
            case "schein":
                return loadImage("schein_" + index);
            case "raumschiff_normal":
                return loadImage("raumschiff_normal");
            case "raumschiff_schild":
                return loadImage("raumschiff_schild");
            case "background_dark":
                return  loadImage("background_dark");
            case "background_dark_inverted":
                return  loadImage("background_dark_inverted");
            case "background_light":
                return  loadImage("background_light");
            case "background_light_inverted":
                return  loadImage("background_light_inverted");
            case "background_stars":
                return loadImage("background_stars");
            case "explosion":
                return loadImage("explosion/explosion_" + index);
            case "blende":
                return loadImage("blende/blende_" + index);
            case "note":
                return loadImage("Note_" + index);
            case "fail":
                return loadImage("fail");
            case "fog":
                return loadImage("fog");
            case "menu":
                return loadImage("menu/menu");
            case "menu_exit":
                return loadImage("menu/menu_exit");
            case "menu_musik":
                return loadImage("menu/menu_musik");
            case "menu_sound":
                return loadImage("menu/menu_sound");
            case "menu_start":
                return loadImage("menu/menu_start");
            case "menu_steuerung":
                return loadImage("menu/menu_steuerung");
            case "marker_music":
                return loadImage("menu/marker_music");
            case "marker_sound":
                return loadImage("menu/marker_sound");
            case "steuerung":
                return loadImage("menu/steuerung");
            default:
                return null;
        }
    }

    /**
     * konvertierung von ImageIcon zu BufferedImage
     *
     * @param img   Image
     * @return      BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    /**
     * Lädt ein Bild
     *
     * @param imgFile   pfad des Bildes als String
     * @return          BufferedImage des Bildes
     */
    private BufferedImage loadImage(final String imgFile) {
        BufferedImage result = mCache.get(imgFile);
        if (result == null) {
            try {
                final ClassLoader cl = getClass().getClassLoader();
                final URL imgResource = cl.getResource("resources/" + imgFile + ".png");
                result = toBufferedImage(new ImageIcon(Objects.requireNonNull(imgResource)).getImage());
                mCache.put(imgFile, result);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } return result;
    }
}