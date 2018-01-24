package de.hsh.inform.starRunner.app;

/**
 * StarRunner
 * Created by Florian on 15.11.2017.
 */
public class PlayerModel extends LevelElement {

    private final double PLAYER_ACCELERATION_RATE = 0.3;
    private double acceleration;
    private double velocity;

    private int moveOffScreen;
    private int horizontalVelocity;
    private double horizontalChange;

    // Model Attribute
    private int[] xPoints = {
        0, 0, 50
    };
    private int[] yPoints = {
        0, 40, 20
    };

    // Gameplay Attribute
    private int playerHealth = 3;
    private boolean schild = false;

    /**
     * Erstellt einen neuen Spieler mit vordefinierter Position und Polygon
     */
    public PlayerModel() {
        super(-50, 360, new int[] {0,0,25}, new int[] {0,20,10}, 3); //new int[] {0,0,50}, new int[] {0,40,20}
    }

    /**
     * Frame run mit drei Modi. Einfahrt in das Level, Ausgang nach rechts und normale bewegung innerhalt des Spiels
     */
    public void runFrame() {
        switch (moveOffScreen) {
            case 1:
                if (this.getX() <= 50) {
                    moveHorizontal(horizontalVelocity);
                    if (horizontalChange > 0) {
                        horizontalChange--;
                    } else {
                        horizontalChange = 0;
                    }
                    horizontalVelocity -= horizontalChange;
                }
                if (this.getX() >= 50) {
                    horizontalVelocity = 0;
                    horizontalChange = 0;
                    this.moveHorizontal(50 - this.getX());
                    moveOffScreen = 0;
                    setChanged();
                    notifyObservers("PLAYER_READY");
                }
                break;
            case 2:
                moveHorizontal(horizontalVelocity);
                horizontalChange += 0.3;
                horizontalVelocity += horizontalChange;
                if (this.getX() >= 1350) {
                    horizontalVelocity = 0;
                    horizontalChange = 0;
                    moveOffScreen = 0;
                }
                break;
            case 0:
                if (!isBroken()) {
                    checkBorders();
                    velocity += acceleration;
                    if (velocity != 0) {
                        this.moveVertical((int) velocity);
                        setChanged();
                        notifyObservers("PLAYER_MOVED");
                    }
                    reduceVelocity();
                }
                break;
        }
    }

    private static final int UP = 1;
    private static final int DOWN = -1;
    private static final int STOP = 0;

    /**
     * verringert die vertikale Bewegung falls keine Taste gedrückt ist
     */
    private void reduceVelocity() {
        if(this.acceleration == 0 && this.velocity != 0) {
            if(this.velocity > 0) {
                this.velocity -= PLAYER_ACCELERATION_RATE/2;
            } else if(this.velocity < 0) {
                this.velocity += PLAYER_ACCELERATION_RATE/2;
            }
        }
    }

    /**
     * verhindert das der Spieler das Fenster verlässt
     */
    private void checkBorders() {
        int PLAYER_MIN_HEIGHT = 0;
        int PLAYER_MAX_HEIGHT = 720;
        if (this.getCenretedY() > PLAYER_MAX_HEIGHT - 70) {
            this.velocity = -1;
        } else if (this.getCenretedY() < PLAYER_MIN_HEIGHT) {
            this.velocity = +1;
        }
    }

    /**
     * bewegt den Spieler vertikal in dem Fenster
     *
     * @param direction y bewegung
     */
    public void movePlayer(int direction) {
        switch (direction) {
            case UP:
                if (velocity > 0) {
                    this.acceleration = -PLAYER_ACCELERATION_RATE * 1.5;
                } else {
                    this.acceleration = -PLAYER_ACCELERATION_RATE;
                }
                break;
            case DOWN:
                if (velocity < 0) {
                    this.acceleration = PLAYER_ACCELERATION_RATE * 1.5;
                } else {
                    this.acceleration = PLAYER_ACCELERATION_RATE;
                }
                break;
            case STOP:
                this.acceleration = 0;
                break;
        }
    }

    /**
     * @param index modus
     */
    void setMode(int index) {
        moveOffScreen = index;
        horizontalVelocity = 5;
    }

    /**
     * stellt das Schild ein
     */
    void applyShield() {
        schild = true;
        setChanged();
        notifyObservers("SHIELD_CHANGED");
    }

    /**
     * entfernt das Schild
     */
    private void removeShield() {
        schild = false;
        setChanged();
        notifyObservers("SHIELD_CHANGED");
    }

    /**
     * füht dem Spieler Lebenspunkte hinzu
     *
     * @param hp anzahl HP
     */
    void addHelath(int hp) {
        if (playerHealth < 3 && playerHealth > 0){
            playerHealth++;
        }
        setChanged();
        notifyObservers("HEALTH_CHANGED");
    }

    /**
     * füht dem Spieler Schaden hinzu und prüft ob dieser dadurch zerstört wird
     *
     * @param hp schaden
     */
    void damageHelath(int hp) {
        if(hp == 4) {
            playerHealth = 0;
            setChanged();
            notifyObservers("PLAYER_DESTROYED");
            notifyObservers("GAME_OVER");
        }
        if (schild) {
            removeShield();
            notifyObservers("PLAYER_DESTROYED");
            notifyObservers("GAME_OVER");
        } else if (playerHealth == 1 || playerHealth == 2 && hp == 2) {
            playerHealth = 0;
            setChanged();
            notifyObservers("PLAYER_DESTROYED");
            notifyObservers("GAME_OVER");
        } else {
            playerHealth -= hp;
            setChanged();
            notifyObservers("HEALTH_CHANGED");
        }
    }

    /**
     * setzt das Leben des Spielers auf den bestimmten Wert
     *
     * @param newPlayerHealth neue HP
     */
    void setPlayerHealth(int newPlayerHealth) {
        playerHealth = newPlayerHealth;
        setChanged();
        notifyObservers("HEALTH_CHANGED");
    }

    /**
     * @return HP
     */
    public int getPlayerHealth() {
        return  playerHealth;
    }

    /**
     * @return playerHealth
     */
    boolean isBroken() {
        return playerHealth < 1;
    }

    /**
     * @return schild
     */
    public boolean isShielded() { return schild; }
}
