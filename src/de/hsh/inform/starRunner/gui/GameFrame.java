package de.hsh.inform.starRunner.gui;

import de.hsh.inform.starRunner.app.GameControllerFacade;
import de.hsh.inform.starRunner.app.GameModel;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Objects;

import static de.hsh.inform.starRunner.gui.ImageLoader.toBufferedImage;

/**
 * StarRunner
 * Created by Florian on 16.11.2017.
 */
public class GameFrame extends JFrame {
	
	private GamePanel mGamePanel;

    /**
     * erstellt ein neues GamePanel
     *
     * @param gameCtrl gameCtrl
     */
    public GameFrame(final GameControllerFacade gameCtrl) {
        super("forsenE");
        setLocation(0, 0);
        setSize(1280, 720);
        setResizable(false);

        final GameModel gameModel = gameCtrl.getGameModel();
        GamePanel mGamePanel = new GamePanel(gameModel, gameCtrl, this);
        getContentPane().add(mGamePanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        try {
            final ClassLoader cl = getClass().getClassLoader();
            final URL imgResource = cl.getResource("forsenE.jpg");
            this.setIconImage(new ImageIcon(imgResource).getImage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * minimiert das Spiel falls ein anderes ausgeführt werden soll
     */
    public void toggleGame() {
    	mGamePanel.toggleGame();
    }
}
