package de.hsh.inform.starRunner.app;

import java.awt.*;
import java.awt.geom.Area;
import java.util.Observable;

/**
 * StarRunner
 * Created by Florian on 15.11.2017.
 */
public class LevelElement extends Observable{

    private final Polygon mPolygon;
    private boolean destroyed = false;

    /**
     * Grundklasse aller LevelElemente. Basiert auf einem Polygon.
     *
     * @param x         x-Achse
     * @param y         y-Achse
     * @param xPoints   x-Punkte des Polygons
     * @param yPoints   y-Punkte des Polygons
     * @param nPoints   Anzahl Punkte des Polygons
     */
    LevelElement(int x, int y, int[] xPoints, int[] yPoints, int nPoints) {
        this.mPolygon = new Polygon(xPoints, yPoints, nPoints);
        mPolygon.translate(x, y);
    }

    /**
     * @return x-Zentrum des Polygons
     */
    int getCenteredX() {
        return mPolygon.getBounds().x + (mPolygon.getBounds().width/2);
    }

    /**
     * @return y-Zentrum des Polygons
     */
    int getCenretedY() {
        return mPolygon.getBounds().y + (mPolygon.getBounds().height/2);
    }

    /**
     * @return x-Achse (obere linke Ecke des Polygons)
     */
    public int getX() { return mPolygon.getBounds().x; }

    /**
     * @return y-Achse (obere linke Ecke des Polygons)
     */
    public int getY() { return mPolygon.getBounds().y; }

    /**
     * bewegung auf der x-Achse
     *
     * @param x distanz
     */
    void moveHorizontal(int x) {
        mPolygon.translate(x,0);
    }

    /**
     * bewegung auf der y-Achse
     *
     * @param y distanz
     */
    void moveVertical(int y) {
        mPolygon.translate(0, y);
    }

    /**
     * stellt ein ob zerstört
     *
     * @param status status
     */
    void setDestroyed(boolean status) { destroyed = status; }

    /**
     * @return zerstört
     */
    boolean getDestroyed() { return destroyed; }

    /**
     * @return polygon
     */
    private Polygon getPolygon() { return this.mPolygon; }

    /**
     * Überprüft die Kollision zweier Polygone, indem jene Polygone in Areas konvertiert werden, welche
     * dann via Area.intersect auf Kollision überprüft werden
     *
     * @param element   zweites Polygon
     * @return          kollision
     */
    boolean intersects(LevelElement element) {
        Area tempAera = new Area(this.getPolygon());
        tempAera.intersect(new Area(element.getPolygon()));
        return !tempAera.isEmpty();
    }

}
