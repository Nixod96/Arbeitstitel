package de.hsh.inform.starRunner;

import de.hsh.inform.starRunner.app.GameController;
import de.hsh.inform.starRunner.app.GameControllerFacade;
import de.hsh.inform.starRunner.gui.GameFrame;

/**
 * StarRunner
 * Created by Florian on 15.11.2017.
 */
class main {

    public static void main(String[] args) {
        new Thread(() -> {
            final GameControllerFacade gameCtrl = GameController.getInstance();
            final GameFrame gameFrame = new GameFrame(gameCtrl);
            gameCtrl.start();
            gameFrame.setVisible(true);
        }).start();
    }
}
