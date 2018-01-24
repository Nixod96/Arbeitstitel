package de.hsh.inform.starRunner.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * StarRunner
 * Created by Florian on 15.11.2017.
 */
public class GameController implements GameControllerFacade {

    private final GameModel mGameModel;

    private static Timer mFrameTimer;
    private boolean mPaused = false;

    private GameController() { //TODO change to private
        mGameModel = new GameModel();
        mFrameTimer = new Timer(16, new FrameCounter());
        start();
    }

    /**
     * Führt ein Frame alle 16ms aus (60FPS)
     */
    private final class FrameCounter implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            mGameModel.runFrame();
        }
    }

    /** SINGLETON */
    private static volatile transient GameControllerFacade mInstance;

    // ###########################################################################
    public static synchronized GameControllerFacade getInstance() {
        if (mInstance == null)
            mInstance = new GameController();
        return mInstance;
    }

    /**
     * Startet den Frame-Timer
     */
    public void start() {
        mFrameTimer.start();
    }

    /**
     * Stoppt den Frame-Timer
     */
    public void stop() {
        mFrameTimer.stop();
    }

    /**
     * Setzt das Spiel zurück
     * @param freshStart kompletter Neustart
     */
    public void restart(boolean freshStart) {
        stop();
        mGameModel.reset(freshStart);
        mGameModel.notifyObservers("RESTART");
        mPaused = true;
        start();
    }

    /**
     * @return gameModel
     */
    public GameModel getGameModel() {
        return mGameModel;
    }

    /**
     * Toggled die Pause
     */
    public void togglePause() {
        mPaused = !mPaused;
        if (mPaused) {
            stop();
        } else {
            start();
        }
    }
}
