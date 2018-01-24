package de.hsh.inform.starRunner.app;

/**
 * StarRunner
 * Created by Florian on 15.11.2017.
 */
public class BorderModel extends LevelElement {

    private boolean moveVerticaly = false;
    private final int borderID;

    /**
     * Modell für die Wände des Tunnels. Dargestellt als 40x720 Block.
     *
     * @param x     x-Achse
     * @param y     y-Achse
     * @param ID    ID
     */
    public BorderModel(int x, int y, int ID) {
        super(x, y, new int[] {0, 0, 40, 40}, new int[] {0, 720, 720, 0}, 4);
        borderID = ID;
    }

    /**
     * @param xChange bewegung auf der x-Achse
     */
    public void moveBorder(int xChange) {
        this.moveHorizontal(xChange);
    }

    /**
     * @param input bewegung einstellen
     */
    public void setMovement(boolean input) {
        moveVerticaly = input;
    }

    /**
     * @return momentan in Bewegung
     */
    public boolean getMovement() {
        return moveVerticaly;
    }

    /**
     * @return ID
     */
    public int getBorderID() { return borderID; }
}