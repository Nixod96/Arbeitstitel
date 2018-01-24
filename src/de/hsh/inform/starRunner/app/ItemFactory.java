package de.hsh.inform.starRunner.app;

import java.util.Random;

/**
 * StarRunner
 * Created by Florian on 07.12.2017.
 */
class ItemFactory {

    private final int[][] polygonPoints = {
            {2,17,46,60,46,17},{27,2,2,27,53,53},     // länge 6
            {2,16,46,60,46,16},{39,2,2,39,76,76}      // länge 6
    };

    private final Random rand = new Random();

    /**
     * ItemFactory welche Powerups und Scheine erstellt
     */
    public ItemFactory() { }

    /**
     * Gibt ein Powerup zurück. Definiert durch Koordinaten, Effekt und Geschwindigkeit
     *
     * @param x     x-Achse
     * @param y     y-Achse
     * @param type  effekt
     * @param speed geschwindigkeit
     * @return      fertiges Powerup
     */
    public ItemModel createItem(int x, int y, Effekt type, int speed) {
        switch (type) {
            case REPARIEREN:
                return new ItemModel(x,y, polygonPoints[0], polygonPoints[1], 6, speed, Effekt.REPARIEREN);
            case SCHILD:
                return new ItemModel(x,y, polygonPoints[0], polygonPoints[1], 6, speed, Effekt.SCHILD);
            case SCHEIN:
                return new ItemModel(x,y, polygonPoints[2], polygonPoints[3], 6, speed, Effekt.SCHEIN);
            default:
                return null;
        }
    }

    /**
     * @return zufälliger Effekt
     */
    public Effekt randomEffekt() {
        int i = rand.nextInt(2) + 1;
        switch(i) {
            case 1:
                return Effekt.REPARIEREN;
            case 2:
                return Effekt.SCHILD;
            default:
                return null;
        }
    }
}
