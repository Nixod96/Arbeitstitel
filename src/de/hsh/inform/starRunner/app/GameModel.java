package de.hsh.inform.starRunner.app;

import java.util.Observable;
import java.util.Observer;

/**
 * StarRunner
 * Created by Florian on 15.11.2017.
 */
public class GameModel extends Observable implements Observer {

    private static final int LEVEL_LENGHT = 2500;

    private int currentProgress;
    private int currentStage = 1;
    private int currentScheine;
    private int currentScore;

    private boolean levelStarting = false;
    private boolean midLevel = false;

    private long startFrame;

    private PlayerModel mPlayer;
    private LevelModel mLevel;

    private boolean started = false;
    private boolean notHitOnce = true;


    /**
     * Erstellt ein neues GameModel
     */
    public GameModel() {
        reset(true);
    }

    /**
     * Erzeugt ein neues Level und einen neuen Spieler. Je nach Art des Neustarts wird der
     * Modus des Spielers angepasst
     *
     * @param freshStart kompletter Neustart
     */
    public void reset(boolean freshStart) {
        mLevel = new LevelModel();
        mLevel.addObserver(this);
        mPlayer = new PlayerModel();
        mPlayer.addObserver(this);
        if(freshStart) {
            mPlayer.setMode(4);
        } else {
            mPlayer.setMode(1);
        }

        currentProgress = 0;
        currentStage = 1;
        currentScheine = 0;
        currentScore = 0;
        levelStarting = false;
        midLevel = false;
    }

    /**
     * Führt einen Frame aus. Hierzu zählen Intros in die Level, das einstellen der Lebenspunkte,
     * Zählen des Fortschritts und abfragen von Kollisionen.
     */
    public void runFrame() {

        if (levelStarting) {
            long currentFrame = System.currentTimeMillis();
            if(currentFrame > startFrame + 5000) {
                midLevel = true;
                levelStarting = false;

                mPlayer.setPlayerHealth(3);
                startLevel();
            }
        }

        if(midLevel) {
            if(!mPlayer.isBroken()) {
                currentProgress++;
                currentScore++;
            }
        }

        checkProgress();

        mPlayer.runFrame();
        mLevel.runFrame();
        checkCollisions();

        notifyObservers("SINGLE_FRAME");
        setChanged();
    }

    /**
     * Startet die Einleitung eines Levels
     */
    private void startIntro() {
        startFrame = System.currentTimeMillis();
        levelStarting = true;
        if (!started) {
            setChanged();
            notifyObservers("STARTED");
            started = true;
        }
    }

    /**
     * Startet das eigentliche Level
     */
    private void startLevel() {
        mLevel.startGen(currentStage);
    }

    /**
     * Beendet ein Level und überprüft ob ob das Spiel vorbei ist
     *
     * @param endGame checkt ob GameOver
     */
    private void endLevel(boolean endGame) {
        if(endGame) {
            setChanged();
            notifyObservers("GAME_OVER");
            mPlayer.setMode(2);
            mLevel.gameOver();
        } else {
            currentProgress = 0;
            currentStage++;
            startIntro();
        }
    }

    /**
     * Überprüft den Fortschritt
     */
    private void checkProgress() {
        if(currentProgress >= LEVEL_LENGHT) {
            currentProgress = LEVEL_LENGHT;
            midLevel = false;
            mLevel.stopGen();
        }
    }

    /**
     * Abfragung der Kollisionen des Spielers mit allen relevanten Objekten des Levels. Bei der Kollision mit der Wand
     * verliert der Spieler sofort alle Lebenspunkte. Treffer mit Asteroiden werden je nach Schild und Größe des Asteroiden
     * entschieden. Die Powerups werden dem Spieler nur gutgeschrieben, wenn dieser die Effekte auch nutzen kann
     */
    private void checkCollisions() {
        for (BorderModel[] b : mLevel.getmBorders()) {
            for (BorderModel e : b) {
                if (e != null) {
                    if (e.getCenteredX() < 150 && e.getCenteredX() > 50) {
                        if(mPlayer.intersects(e) && mPlayer.getPlayerHealth() > 0) {
                            playerHit(4);
                            setChanged();
                            notifyObservers("HIT_BORDER");
                        }
                    }
                }
            }
        }


        for (AsteroidModel e : mLevel.getmAsteroids()) {
            if (e != null && !e.getDestroyed()) {
                if (e.getCenteredX() < 150 && e.getCenteredX() > 50) {
                    if(mPlayer.intersects(e)) {
                        if(mPlayer.isShielded()) {
                            setChanged();
                            notifyObservers("HIT_ASTEROID_SHIELDED");
                        } else {
                            setChanged();
                            notifyObservers("HIT_ASTEROID_" + e.getSize());
                            notHitOnce = false;
                        }
                        playerHit(e.getSize());
                        e.setDestroyed(true);
                    }
                }
            }
        }

        for (ItemModel e : mLevel.getmItems()) {
            if (e != null && !e.getDestroyed()) {
                if (e.getCenteredX() < 150 && e.getCenteredX() > 50) {
                    if(mPlayer.intersects(e)) {
                        if(mPlayer.getPlayerHealth() <= 3) {
                            if(mPlayer.getPlayerHealth() < 3 && e.getEffekt() == Effekt.REPARIEREN) {
                                setChanged();
                                notifyObservers("POWERUP_REPAIR");
                            }

                            if(!mPlayer.isShielded() && e.getEffekt() == Effekt.SCHILD) {
                                setChanged();
                                notifyObservers("POWERUP_SHIELD");
                            }

                            applyPowerUp(e.getEffekt());
                            e.setDestroyed(true);
                        }
                    }
                }
            }
        }

        if(mLevel.getmSchein() != null) {
            if(mLevel.getmSchein().getCenteredX() < 150 && mLevel.getmSchein().getCenteredX() > 50) {
                if(mPlayer.intersects(mLevel.getmSchein())) {
                    collectSchein();
                    mLevel.getmSchein().setDestroyed(true);
                }
            }
        }
    }


    /**
     * Fügt dem Spieler schaden zu und prüft ob
     * dieser dadurch verliert
     *
     * @param damage schaden
     */
    private void playerHit(int damage){
        mPlayer.damageHelath(damage);
        if(mPlayer.isBroken()) {
            setChanged();
            notifyObservers("PLAYER_DESTROYED");
            mLevel.gameOver();
        }
    }

    /**
     * Fügt dem Spieler den Effekt eines Powerups hinzu
     *
     * @param type powerup Effekt
     */
    private void applyPowerUp(Effekt type) {

        if(type == Effekt.REPARIEREN) {
            mPlayer.addHelath(1);
        } else if (type == Effekt.SCHILD) {
            mPlayer.applyShield();
        }
    }


    /**
     * Erhöht anzahl Scheine und überprüft, ob der Spieler alle möglichen Scheine gesammelt hat
     */
    private void collectSchein() {
        currentScheine++;
        setChanged();
        notifyObservers("SCHEIN_GESAMMELT");
        if(currentScheine == 6) {
            endLevel(true);
        } else {
            currentScore = currentScore + 500;
            mPlayer.setPlayerHealth(3);
            endLevel(false);
        }
    }

    /**
     * Startet den Spiler falls dieser das Spiel aus dem Hauptmenu aus startet
     */
    public void startPlayer() {
        mPlayer.setMode(1);
    }

    /**
     * Bewegt den Spieler
     *
     * @param direction auf- und abwärts Bewegung als Integer
     */
    public void movePlayer(int direction) {
        mPlayer.movePlayer(direction);
    }

    /**
     * @return playerModel
     */
    public PlayerModel getPlayer() {
        return mPlayer;
    }

    /**
     * @return levelModel
     */
    public LevelModel getLevel() { return mLevel; }

    /**
     * @return fortschritt des Levels in Prozent
     */
    public double getProgressPercentage() {
        return (double)currentProgress/(double)LEVEL_LENGHT;
    }

    /**
     * @return gesamtpunktestand
     */
    public int getScore() { return currentScore; }

    /**
     * @return stage
     */
    public int getStage() { return currentStage; }

    /**
     * @return anzahl Scheine
     */
    public int getSchein() {
        return currentScheine;
    }

    /**
     * @return wurde der Spieler schon getroffen
     */
    public boolean getNoHits() { return notHitOnce; }

    /**
     * Führt ein Update aus und prüft, ob dre Spieler zerstört wurde, oder bereit für das Level ist
     *
     * @param o     observable
     * @param arg   argument
     */
    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);

        if (arg != null && arg.equals("PLAYER_DESTROYED")) {
            mLevel.haltGen(true);
        }
        if (arg != null && arg.equals("PLAYER_READY")) {
            startIntro();
        }
    }
}
