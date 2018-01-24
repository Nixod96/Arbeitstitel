package de.hsh.inform.starRunner.app;

/**
 * StarRunner
 * Created by Florian on 15.11.2017.
 */
public class ItemModel extends LevelElement {

    private final Effekt effekt;

    private boolean moveVertical = false;
    private int size;
    private final int speed;

    /**
     * Erstellt ein Powerup anhand von Koordinaten, einem Effekt, Geschwindigkeit und Informationen über die Ausmaße
     * des Polygons
     *
     * @param x         x-Achse
     * @param y         y-Achse
     * @param xPoints   x-Punkte des Polygons
     * @param yPoints   y-Punkte des Polygons
     * @param nPoints   anzahl Punkte des Polygons
     * @param speed     geschwindigkeit
     * @param effekt    effekt
     */
    public ItemModel(int x, int y, int[] xPoints, int[] yPoints, int nPoints, int speed, Effekt effekt) {
        super(x, y, xPoints, yPoints, nPoints);
        this.speed = -speed;
        if (effekt != null) {
            this.effekt = effekt;
        } else {
            this.effekt = null;
        }
    }

    /**
     * @return effekt
     */
    public Effekt getEffekt() {
        return effekt;
    }

    /**
     * @return geschwindigkeit
     */
    int getSpeed() {
        return speed;
    }

    /**
     * @param xChange bewegt auf der x-Achse
     */
    void moveItem(int xChange) {
        this.moveHorizontal(xChange);
    }

    /**
     * @param input bewegung
     */
    void setMovement(boolean input) {
        moveVertical = input;
    }

    /**
     * @return in bewegung
     */
    boolean getMovement() {
        return moveVertical;
    }

}

