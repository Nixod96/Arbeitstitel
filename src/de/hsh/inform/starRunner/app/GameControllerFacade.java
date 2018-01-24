package de.hsh.inform.starRunner.app;

/**
 * StarRunner
 * Created by Florian on 16.11.2017.
 */
public interface GameControllerFacade {

    // ###########################################################################
    void start();

    // ###########################################################################
    void stop();

    // ###########################################################################
    void restart(boolean i);

    // ###########################################################################
    GameModel getGameModel();

    // ###########################################################################
    void togglePause();

}

