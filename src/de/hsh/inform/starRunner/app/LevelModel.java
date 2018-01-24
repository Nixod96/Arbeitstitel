package de.hsh.inform.starRunner.app;

import java.util.Observable;
import java.util.Random;

/**
 * StarRunner
 * Created by Florian on 15.11.2017.
 */
public class LevelModel extends Observable {

    private int nextElevation;
    private int nextGap;

    private final Random rand = new Random();

    private final boolean up = true;
    private final boolean down = false;

    private boolean dirBool;
    private boolean gapBool;

    private boolean midLevel = false;
    private boolean levelEnding = false;

    private int currentStage;

    private final int ASTEROID_COUNT = 15;
    private final int BORDER_COUNT = 40;
    private final int ITEM_COUNT = 1;

    private final BorderModel[][] mBorders = new BorderModel[BORDER_COUNT][2];
    private final AsteroidModel[] mAsteroids = new AsteroidModel[ASTEROID_COUNT];
    private final ItemModel[] mItems = new ItemModel[ITEM_COUNT];
    private ItemModel mSchein;

    private boolean scheinGeneriert = false;

    private final AsteroidFactory asteroidFactory = new AsteroidFactory();
    private final ItemFactory itemFactory = new ItemFactory();

    private boolean levelHalted = false;
    private double haltingModifier = 1;
    private boolean gameOver = false;


    /**
     * Erstellt ein LevelModel mit neuen Zufallswerten für den Abstand der Wände und deren Höhe
     */
    LevelModel() {
        nextElevation = rand.nextInt(321) + 160;
        nextGap = rand.nextInt(151) + 150;
        dirBool = rand.nextBoolean();
        gapBool = rand.nextBoolean();
    }

    /**
     * Startet die Gennerierung des Levels. Geschwindigkeiten der Items und Asteroiden werden nach Stage
     * angepasst
     *
     * @param stage momentane Stage
     */
    void startGen(int stage) {
        currentStage = stage;

        BORDER_SPEED = 12 + 2*currentStage;
        ASTEROID_SPEED  = 8 + 2*currentStage;

        midLevel = true;
        startBlock(FIRST_BLOCK);
    }

    /**
     * Beendet die Generierung
     */
    void stopGen() {
        midLevel = false;
        levelEnding = true;
    }

    /**
     * Pausiert die Generation
     *
     * @param status status
     */
    void haltGen(boolean status) {
        levelHalted = status;
    }

    /**
     * Führt einen Frame aus
     */
    void runFrame() {
        if (levelHalted) {
            haltingModifier += 0.1;
        }

        if (!levelEnding && midLevel) {
            updateGen();
            startBlock(MID_LEVEL_BLOCK);
            startAsteroids();
            startItems();
        }

        if((midLevel || levelEnding)) {
            moveBlocks();
            moveAsteroids();
            moveItems();

            checkBlocks();
            checkAsteroids();
            checkItems();
        }

        if(!midLevel && levelEnding) {
            if (!scheinGeneriert && !gameOver) {
                startSchein();
                scheinGeneriert = true;
            }
            if(mSchein != null) {
                checkSchein();
                moveSchein();
            }
        }
    }

    /**
     * setzt gameOver
     */
    public void gameOver() {
        gameOver = true;
    }

    //############################################
    // Generation Settings
    //############################################

    private double DIRECTION_LIMIT = 75;

    /**
     * prüft die Richtung der Wand-Generierung und kehrt diese ggf. um
     */
    private void checkDirection() {
        int MAX_ELEVATION = 600;
        int MIN_ELEVATION = 160;
        if(nextElevation < MIN_ELEVATION) {
            dirBool = up;
        } else if (nextElevation > MAX_ELEVATION) {
            dirBool = down;
        } else {
            if (rand.nextInt(100) + 1 > DIRECTION_LIMIT) {
                dirBool = !dirBool;
                DIRECTION_LIMIT = 75;
            } else {
                DIRECTION_LIMIT -= 4;
            }
        }
    }

    private double GAP_LIMIT = 75;

    /**
     * prüft die Lücke zwischen den Wänden und passt diese ggf. an
     */
    private void checkGap() {
        int MAX_GAP = 350;
        int MIN_GAP = 275;
        if(nextGap < MIN_GAP) {
            gapBool = up;
        } else if(nextGap > MAX_GAP) {
            gapBool = down;
        } else {
            if (rand.nextInt(100) + 1 > GAP_LIMIT) {
                gapBool = !gapBool;
                GAP_LIMIT = 75;
            } else {
                GAP_LIMIT -= 5;
            }
        }
    }


    /**
     * generiert neue Zufallszahlen für die Generierung
     */
    private void updateGen() {
        checkDirection();
        checkGap();
        int changeInElevation = rand.nextInt(20 + currentStage);
        int changeInGap = rand.nextInt(10);

        if(dirBool == up) {
            nextElevation += changeInElevation;
        } else if (dirBool == down) {
            nextElevation -= changeInElevation;
        }

        if(gapBool == up) {
            nextGap += changeInGap;
        } else if (dirBool == down) {
            nextGap -= changeInGap;
        }
    }

    //############################################
    // Schein Generation
    //############################################

    /**
     * erstellt einen neuen Schein
     */
    private void startSchein() {
        mSchein = itemFactory.createItem(1300, rand.nextInt(360)+ 180, Effekt.SCHEIN, 5);
    }

    /**
     * prüft ob der Schein eingesammelt wurde oder bewegt diesen zurück auf die rechte Seite des Fensters
     */
    private void checkSchein() {
        if(mSchein.getDestroyed()) {
            mSchein = null;
            scheinGeneriert = false;
            levelEnding = false;
        } else {
            if (mSchein.getCenteredX() <= -20 && !gameOver) {
                mSchein.moveItem(1320);
            }
        }
    }

    /**
     * bewegt den Schein nach links
     */
    private void moveSchein() {
        if (mSchein != null) {
            mSchein.moveItem(mSchein.getSpeed());
        }
    }

    //############################################
    // Item Generation
    //############################################

    private int nextItem = 0;

    private final int ITEM_MAX_LIMIT = 1000000;
    private int ITEM_LIMIT = ITEM_MAX_LIMIT - 1;

    /**
     * erstellt ein neues Item, sofern die Zufallsbedingung erfüllt ist
     */
    private void startItems() {
        if(mItems[nextItem] == null) {
            if (rand.nextInt(ITEM_MAX_LIMIT) > ITEM_LIMIT) {
                int ITEM_SPREAD = 20;
                int newY = nextElevation + rand.nextInt(ITEM_SPREAD) - ITEM_SPREAD /2;

                mItems[nextItem] = itemFactory.createItem(1300, newY, itemFactory.randomEffekt(), -BORDER_SPEED);
                mItems[nextItem].setMovement(true);

                if (nextItem == ITEM_COUNT - 1) {
                    nextItem = 0;
                } else {
                    nextItem++;
                }

                ITEM_LIMIT = ITEM_MAX_LIMIT - 1;
            } else {
                ITEM_LIMIT -= 1;
            }
        }
    }

    /**
     * prüft ob ein Item eingesammelt wurde und zerstört dieses dann
     */
    private void checkItems() {
        for(int i = 0; i < ITEM_COUNT; i++) {
            if (mItems[i] != null) {
                if (mItems[i].getCenteredX() <= -20 || mItems[i].getDestroyed()) {
                    mItems[i] = null;
                }
            }
        }
    }

    /**
     * bewegt Items nach links
     */
    private void moveItems() {
        for(ItemModel e: mItems) {
            if(e != null) {
                if (e.getMovement()) {
                    e.moveItem((int)((double)-e.getSpeed() / haltingModifier));
                }
            }
        }
    }

    //############################################
    // Border Generation
    //############################################

    private int nextBorderElement = 0;
    private int firstBorderElement = 0;
    private int currentBorderElement;

    private final int FIRST_BLOCK = 1;
    private final int MID_LEVEL_BLOCK = 2;
    private int BORDER_SPEED = 8;
    private int deltaX = 1300;

    /**
     * startet einen neuen Wand-Block. Der erste Block eines Levels wird speziell gestartet, da er nicht an einen vorherigen
     * Block anknüpft
     *
     * @param status erster Block
     */
    private void startBlock(int status) {
        if (status == MID_LEVEL_BLOCK && mBorders[currentBorderElement][0].getCenteredX() <= 1280 || status == FIRST_BLOCK) {

            if(status != FIRST_BLOCK) {
                deltaX = mBorders[currentBorderElement][0].getX() + 40;
            }

            mBorders[nextBorderElement][0] = new BorderModel(deltaX, (nextElevation - nextGap / 2 - 720), rand.nextInt(10) + 1);
            mBorders[nextBorderElement][1] = new BorderModel(deltaX, (nextElevation + nextGap / 2), rand.nextInt(10) + 1);

            mBorders[nextBorderElement][0].setMovement(true);
            mBorders[nextBorderElement][1].setMovement(true);

            currentBorderElement = nextBorderElement;

            if (nextBorderElement == BORDER_COUNT - 1) {
                nextBorderElement = 0;
            } else {
                nextBorderElement++;
            }
        }
    }

    /**
     * löscht einen Wand-Block
     */
    private void endBlock() {
        mBorders[firstBorderElement][0] = null;
        mBorders[firstBorderElement][1] = null;

        if(firstBorderElement == BORDER_COUNT - 1) {
            firstBorderElement = 0;
        } else {
            firstBorderElement++;
        }
    }

    /**
     * prüft ob ein Wand-Block das Fenster komplett durchlaufen hat
     */
    private void checkBlocks() {
        if (mBorders[firstBorderElement][0] != null) {
            if (mBorders[firstBorderElement][0].getCenteredX() <= -20) {
                endBlock();
            }
        }
    }

    /**
     * bewegt die Wände anhand der gegebenen Geschwindigkeit
     */
    private void moveBlocks() {
        for(BorderModel[] e: mBorders) {
            if(e[0] != null && e[1] != null) {
                if (e[0].getMovement()) {
                    e[0].moveBorder((int)((double)-BORDER_SPEED / haltingModifier));
                    e[1].moveBorder((int)((double)-BORDER_SPEED / haltingModifier));
                }
            }
        }
    }

    //############################################
    // Asteroid Generation
    //############################################

    private int nextAsteroid = 0;

    private double ASTEROID_LIMIT = 4999;
    private int ASTEROID_SPEED = 10;

    /**
     * erstellt einen neuen Asteroiden, sofern die Zufallsbedingung zutrifft
     */
    private void startAsteroids() {
        if(mAsteroids[nextAsteroid] == null && !levelHalted) {
            if (rand.nextInt(5000) > ASTEROID_LIMIT) {
                int ASTEROID_SPREAD = 20;
                int newY = nextElevation + rand.nextInt(ASTEROID_SPREAD) - ASTEROID_SPREAD /2;

                mAsteroids[nextAsteroid] = asteroidFactory.createAsteroid(1300, newY, rand.nextInt(10) + 1, BORDER_SPEED + rand.nextInt(ASTEROID_SPEED)+5);
                mAsteroids[nextAsteroid].setMovement(true);

                if (nextAsteroid == ASTEROID_COUNT - 1) {
                    nextAsteroid = 0;
                } else {
                    nextAsteroid++;
                }

                ASTEROID_LIMIT = 4999 - currentStage;
            } else {
                ASTEROID_LIMIT -= 1;
            }
        }
    }

    /**
     * überprüft ob ein Asteroid das Fenster durchlaufen hat und löscht ihn ggf.
     */
    private void checkAsteroids() {
        for(int i = 0; i < ASTEROID_COUNT; i++) {
            if (mAsteroids[i] != null) {
                if (mAsteroids[i].getCenteredX() <= -20 || mAsteroids[i].getDestroyed()) {
                    mAsteroids[i] = null;
                }
            }
        }
    }

    /**
     * bewegt die Asteroiden durch das Fenster. Geschwindigkeit hängt von der Größe ab
     */
    private void moveAsteroids() {
        for(AsteroidModel e: mAsteroids) {
            if(e != null) {
                if (e.getMovement()) {
                    if(e.getSize() == 1) {
                        e.moveAsteroid(-(e.getSpeed()+5));
                    } else {
                        e.moveAsteroid(-(e.getSpeed()));
                    }
                }
            }
        }
    }

    //############################################

    /**
     * @return BorderModel als zweidimensionales Array
     */
    public BorderModel[][] getmBorders() {
        return mBorders;
    }

    /**
     * @return AsteroideModel als Array
     */
    public AsteroidModel[] getmAsteroids() { return mAsteroids; }

    /**
     * @return ItemModel als Array
     */
    public ItemModel[] getmItems() { return mItems; }

    /**
     * @return schein
     */
    public ItemModel getmSchein() { return mSchein; }
}
