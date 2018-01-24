package de.hsh.inform.starRunner.app;

/**
 * StarRunner
 * Created by Florian on 15.11.2017.
 */
public class AsteroidModel extends LevelElement {

    private boolean moveVerticaly = false;
    private final int size;
    private final int speed;
    private final int ID;

    /**
     * Model der Asteroiden. Wird gebildet mit einem Punkt auf der Oberfläche, wo der Asteroid erscheinen soll, einer festen
     * Geschwindigkeit, Größe und einer ID. Die Maße des Asteroiden werden als Polygon gespeichert, welches auch zur
     * Kollisionsabfrage genutzt wird.
     *
     * @param x         x-Achse
     * @param y         y-Achse
     * @param xPoints   x-Koordinaten des Polygons als Array
     * @param yPoints   y-Koordinaten des Polygons als Array
     * @param nPoints   anzahl Punkte des Polygons
     * @param size      größe
     * @param speed     geschwindigkeit
     * @param ID        ID
     */
    AsteroidModel(int x, int y, int[] xPoints, int[] yPoints, int nPoints, int size, int speed, int ID) {
        super(x, y, xPoints, yPoints, nPoints);
        this.size = size;
        this.speed = speed;
        this.ID = ID;
    }

    /**
     * @return größe des Asteroiden
     */
    int getSize() {
        return size;
    }

    /**
     * @return gschwindigkeit des Asteroiden
     */
    int getSpeed() {
        return speed;
    }

    /**
     * @param xChange bewegt den Asteroiden auf der x-Achse
     */
    void moveAsteroid(int xChange) {
        this.moveHorizontal(xChange);
    }

    /**
     * @param input stellt bewegung ein
     */
    void setMovement(boolean input) {
        moveVerticaly = input;
    }

    /**
     * @return ID
     */
    public int getID() { return ID; }

    /**
     * @return momentan in Bewegung
     */
    boolean getMovement() {
        return moveVerticaly;
    }
}
