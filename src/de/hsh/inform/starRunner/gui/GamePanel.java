package de.hsh.inform.starRunner.gui;

//import de.hsh.inform.MainMenu;
import de.hsh.inform.starRunner.app.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * StarRunner
 * Created by Florian on 16.11.2017.
 */
class GamePanel extends JPanel implements Observer{

    private GameModel mModel;
    private final GameControllerFacade mGameFacade;

    private int currentHealth = 3;
    private boolean currentShield = false;

    private double currentProgressPercentage;
    private int currentScheine = 0;
    private int currentScore;
    private int currentStage = 0;
    private int currentPlayerY;

    private boolean gifAbspielen1 = false;
    private int gifTimehelper1 = 0;
    private int gifIndex1 = 1;

    private int timehelper2 = 0;
    private int gifIndex2 = 1;

    private boolean endcard = false;
    private boolean mainMenu = true;
    private boolean steuerung = false;

    private boolean started;
    private boolean musicPlaying = false;

    private boolean playSound = true;
    private boolean playMusic = true;

    private boolean switchBackground = false;
    private boolean switchBackgroundLight = false;
    private boolean switchBackgroundDark = false;

    private GameFrame mFrame;

    /**
     * erstellt ein neues gamePanel
     *
     * @param gameModel gameModel
     * @param gameCtrl  gameCtrl
     * @param mFrame    mFrame
     */
    public GamePanel(GameModel gameModel, GameControllerFacade gameCtrl, GameFrame mFrame) {
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addKeyListener(new ControlListener());
        addMouseListener(new MouseListener());
        requestFocusInWindow();
        grabFocus();
        mGameFacade = gameCtrl;
        this.mFrame = mFrame;
        setModel(gameModel);
    }

    private boolean toggled = true;
    private boolean tempPlaySound;
    private boolean tempPlayMusic;

    /**
     * speichert Zustände des Spiels und minimiert es/setzt es fort
     *
     * @return status
     */
    public int toggleGame() {
    	if (toggled) {
    		soundLoader.stopAll();
    		tempPlayMusic = playMusic;
    		tempPlaySound = playSound;
    		playSound = false;
    		playMusic = false;
    		mFrame.setVisible(false);
    		//MainMenu.getInstance().setVisible(true);
    	} else {
    		playSound = tempPlaySound;
    		playMusic = tempPlayMusic;
    		mFrame.setVisible(true);
    	}
    	return 0;
    }

    /**
     * setzt mModel
     *
     * @param model GameModel
     */
    private void setModel(GameModel model) {
        if (mModel!=null)
            mModel.deleteObserver(this);
        mModel = model;
        if (mModel!=null)
            mModel.addObserver(this);
    }

    /**
     * Erstellt einen MouseListener um Tastenanschläge zu erkennen und so das Menu zu bedienen
     */
    private final class MouseListener extends MouseAdapter {
        /**
         * zur bedienung des Menus und um die Musik zu starten
         *
         * @param e tastenanschlag
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if(mainMenu && !steuerung) {
                if (mouseX > 454 && mouseX < 825) {
                    if (mouseY > 275 && mouseY < 344) {
                        SoundUtilities.playFromStart(soundLoader.getSound("buttonclick", 0 ), 0.1);
                        mainMenu = false;
                        mModel.startPlayer();
                        SoundUtilities.stop(soundLoader.getSound("menu",0));
                        SoundUtilities.playInfiniteLoop(soundLoader.getSound("music_ingame", 2), 0.2);
                    } else if (mouseY > 371 && mouseY < 439) {
                        if (mouseX < 620) {
                            SoundUtilities.playFromStart(soundLoader.getSound("buttonclick", 0 ), 0.1);
                            playSound = !playSound;
                            soundLoader.setSound(playSound);
                        } else if (mouseX > 659) {
                            SoundUtilities.playFromStart(soundLoader.getSound("buttonclick", 0 ), 0.1);
                            playMusic = !playMusic;
                            soundLoader.setMusic(playMusic);
                        }
                    } else if (mouseY > 464 && mouseY < 532) {
                        SoundUtilities.playFromStart(soundLoader.getSound("buttonclick", 0 ), 0.1);
                        steuerung = true;
                    } else if (mouseY > 557 && mouseY < 625) {
                        SoundUtilities.playFromStart(soundLoader.getSound("buttonclick", 0 ), 0.1);
                        toggleGame();
                    }
                }
            }
        }
    }

    /**
     * Erstellt einen ControlListener um Tastenanschläge zu erkennen und so das Spiel zu steuern
     */
    private final class ControlListener extends KeyAdapter {
        /**
         * zur bedienung des Menus und um das Spiel zu steuern
         *
         * @param e tastenanschlag
         */
        public void keyPressed(KeyEvent e) {
            final int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
                mModel.movePlayer(1);
            } else if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
                mModel.movePlayer(-1);
            } else if (keyCode == KeyEvent.VK_P && !mainMenu) {
                mGameFacade.togglePause();
            } else if (keyCode == KeyEvent.VK_R && !mainMenu) {
                mGameFacade.restart(false);
                SoundUtilities.playInfiniteLoop(soundLoader.getSound("music_ingame", 2), 0.2);
            } else if (keyCode == KeyEvent.VK_ESCAPE) {
                if(steuerung) {
                    steuerung = false;
                } else {
                    mainMenu = true;
                    mGameFacade.restart(true);
                    SoundUtilities.stop(soundLoader.getSound("music_ingame",0));
                    SoundUtilities.playFromStart(soundLoader.getSound("menu", rand.nextInt(3)+1), 0.2);
                }
            }
        }

        /**
         * zur bedienung des Menus und um das Spiel zu steuern
         *
         * @param e tastenanschlag
         */
        @Override
        public void keyReleased(KeyEvent e) {
            final int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
                mModel.movePlayer(0);
            } else if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
                mModel.movePlayer(0);
            }
        }
    }

    /**
     * prüft Updates der App und reagiert ggf.
     *
     * @param o     observable
     * @param arg   argument
     */
    @Override
    public void update(Observable o, Object arg) {
        getScore();
        currentPlayerY = mModel.getPlayer().getY();
        repaint(getVisibleRect());
        gifTimehelper1++;
        timehelper2++;

        //Für Variablen-Updates


        if (arg != null && arg.equals("HEALTH_CHANGED")) {
            currentHealth = mModel.getPlayer().getPlayerHealth();
        }
        if (arg != null && arg.equals("SHIELD_CHANGED")) {
            currentShield = mModel.getPlayer().isShielded();
        }
        if (arg != null && arg.equals("SCHEIN_GESAMMELT")) {
            currentScheine = mModel.getSchein();
            currentStage = mModel.getStage();
        }
        if (arg != null && arg.equals("PLAYER_DESTROYED")) {
            gifAbspielen1 = true;
            gifTimehelper1 = 1;
            currentHealth = 0;
        }
        if (arg != null && arg.equals("GAME_OVER")) {
            endcard = true;
        }
        if (arg != null && arg.equals("RESTART")) {
            reset();
        }

        //Sound Updates

        if (arg != null && arg.equals("HIT_BORDER")) {
            SoundUtilities.playFromStart(soundLoader.getSound("impact_border", rand.nextInt(3)+1), 0.2);
        }
        if (arg != null && arg.equals("HIT_ASTEROID_1")) {
            SoundUtilities.playFromStart(soundLoader.getSound("impact_small", rand.nextInt(3)+1), 0.2);
        }
        if (arg != null && arg.equals("HIT_ASTEROID_2")) {
            SoundUtilities.playFromStart(soundLoader.getSound("impact_big", rand.nextInt(5)+1), 0.2);
        }
        if (arg != null && arg.equals("PLAYER_DESTROYED")) {
            SoundUtilities.playFromStart(soundLoader.getSound("explode", rand.nextInt(2)+3), 0.2);
        }
        if (arg != null && arg.equals("HIT_ASTEROID_SHIELDED")) {
            SoundUtilities.playFromStart(soundLoader.getSound("impact_shield", rand.nextInt(1)+1), 0.2);
        }
        if (arg != null && arg.equals("POWERUP_REPAIR")) {
            SoundUtilities.playFromStart(soundLoader.getSound("repair", 0 ), 0.2);
        }
        if (arg != null && arg.equals("POWERUP_SHIELD")) {
            SoundUtilities.playFromStart(soundLoader.getSound("shield_pickup", 1 ), 0.05);
        }
        if (arg != null && arg.equals("SCHEIN_GESAMMELT")) {
            SoundUtilities.playFromStart(soundLoader.getSound("shield_pickup", 2 ), 0.2);
        }
    }

    /**
     * setzt Variablen zurück und startet das Spiel neu
     */
    private void reset() {
        soundLoader.stopMusic();
        currentHealth = mModel.getPlayer().getPlayerHealth();
        currentScheine = mModel.getSchein();
        currentStage = mModel.getStage();
        gifAbspielen1 = false;
        gifTimehelper1 = 0;
        gifIndex1 = 1;
        backgroundSpeed = 2;
        darkBackgroundSpeed = 5;
        lightBackgroundSpeed = 8;
        endcard = false;
        musicPlaying = false;
        gifIndex2 = 1;
        boolean gifAbspielen2 = false;
    }

    /**
     * sammelt Fortschritte des Spiels
     */
    private void getScore() {
        currentProgressPercentage = mModel.getProgressPercentage();
        currentScore = mModel.getScore();
    }

    private static final ImageLoader imageLoader = ImageLoader.getInstance();
    private static final SoundLoader soundLoader = SoundLoader.getInstance();

    private final Random rand = new Random();

    //Hintergrund Koordianten;

    private int mainBackground_1 = 0;
    private int mainBackground_2 = 2780;

    private int darkBackground_1 = 0;
    private int darkBackground_2 = 2780;

    private int lightBackground_1 = 0;
    private int lightBackground_2 = 2780;

    private double backgroundSpeed = 1;
    private double darkBackgroundSpeed = 5; //5
    private double lightBackgroundSpeed = 8;

    private long endcardTime = Long.MAX_VALUE;

    private double mouseX;
    private double mouseY;

    /**
     * zeichnet die gesamte Spieloberfläche und das Menu
     *
     * @param graphics graphics
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (!started) {
            started = true;
            SoundUtilities.playFromStart(soundLoader.getSound("menu", rand.nextInt(3) + 1), 0.2);
            SoundUtilities.playInfiniteLoop(soundLoader.getSound("windloop", 0), 0.01);
            SoundUtilities.playInfiniteLoop(soundLoader.getSound("ambience", 0), 0.1);
        }

        final Graphics2D g = (Graphics2D) graphics;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));

        if (mainMenu) {
            mouseX = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
            mouseY = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();
        }

        if (gifAbspielen1) {
            if (backgroundSpeed > 0) {
                backgroundSpeed -= 0.1;
            } else {
                backgroundSpeed = 0;
            }
            if (darkBackgroundSpeed > 0) {
                darkBackgroundSpeed -= 0.1;
            } else {
                darkBackgroundSpeed = 0;
            }
            if (lightBackgroundSpeed > 0) {
                lightBackgroundSpeed -= 0.1;
            } else {
                lightBackgroundSpeed = 0;
            }
        }

        if (gifAbspielen1 && darkBackgroundSpeed == 0) {
            SoundUtilities.stop(soundLoader.getSound("windloop", 0));
            endcard = true;
        }

        //############################################
        // Hintergrund
        //############################################

        int playerOffset_lightWall = (int) ((double) (currentPlayerY - 360) / 4.0);
        int playerOffset_darkWall = (int) ((double) (currentPlayerY - 360) / 6.0);
        int playerOffset_Background = (int) ((double) (currentPlayerY - 360) / 15.0);

        //Sterne (Haupthintergrund)

        g.drawImage(imageLoader.getImage("background_stars", 0), mainBackground_1, playerOffset_Background, null);
        g.drawImage(imageLoader.getImage("background_stars", 0), mainBackground_2, playerOffset_Background, null);

        //Berge 1

        g.drawImage(imageLoader.getImage("background_dark_inverted", 0), darkBackground_1, 25 + playerOffset_darkWall, null);
        g.drawImage(imageLoader.getImage("background_dark_inverted", 0), darkBackground_2, 25 + playerOffset_darkWall, null);
        g.drawImage(imageLoader.getImage("background_dark", 0), darkBackground_1, 500 + playerOffset_darkWall, null);
        g.drawImage(imageLoader.getImage("background_dark", 0), darkBackground_2, 500 + playerOffset_darkWall, null);

        if(switchBackgroundDark) {
            darkBackground_2 -= darkBackgroundSpeed;
            darkBackground_1 = darkBackground_2 + 2780;
        } else {
            darkBackground_1 -= darkBackgroundSpeed;
            darkBackground_2 = darkBackground_1 + 2780;
        }

        if (darkBackground_1 <= -2780 || darkBackground_2 <= -2780) {
            switchBackgroundDark = !switchBackgroundDark;
        }

        //Berge 2

        g.drawImage(imageLoader.getImage("background_light_inverted", 0), lightBackground_1, -75 + playerOffset_lightWall, null);
        g.drawImage(imageLoader.getImage("background_light_inverted", 0), lightBackground_2, -75 + playerOffset_lightWall, null);
        g.drawImage(imageLoader.getImage("background_light", 0), lightBackground_1, 520 + playerOffset_lightWall, null);
        g.drawImage(imageLoader.getImage("background_light", 0), lightBackground_2, 520 + playerOffset_lightWall, null);

        if(switchBackgroundLight) {
            lightBackground_2 -= lightBackgroundSpeed;
            lightBackground_1 = lightBackground_2 + 2780;
        } else {
            lightBackground_1 -= lightBackgroundSpeed;
            lightBackground_2 = lightBackground_1 + 2780;
        }

        if (lightBackground_1 <= -2780 || lightBackground_2 <= -2780) {
           switchBackgroundLight = !switchBackgroundLight;
        }

        //Nebel

        g.drawImage(imageLoader.getImage("fog", 0), mainBackground_1, playerOffset_Background, null);
        g.drawImage(imageLoader.getImage("fog", 0), mainBackground_2, playerOffset_Background, null);

        if(switchBackground) {
            mainBackground_2 -= backgroundSpeed;
            mainBackground_1 = mainBackground_2 + 2780;
        } else {
            mainBackground_1 -= backgroundSpeed;
            mainBackground_2 = mainBackground_1 + 2780;
        }

        if (mainBackground_1 <= -2780 || mainBackground_2 <= -2780) {
            switchBackground = !switchBackground;
        }

        //############################################
        // Borders
        //############################################

        for (BorderModel[] b : mModel.getLevel().getmBorders()) {
            for (BorderModel e : b) {
                if (e != null) {
                    //g.drawPolygon(e.getPolygon());
                    g.drawImage(imageLoader.getImage("border", e.getBorderID()), e.getX(), e.getY(), null);
                }
            }
        }

        //############################################
        // Spieler
        //############################################


        //g.drawPolygon(mModel.getPlayer().getPolygon());

        if (!gifAbspielen1) {
            if (currentShield) {
                g.drawImage(imageLoader.getImage("raumschiff_schild", 0), mModel.getPlayer().getX() - 68, mModel.getPlayer().getY() - 3, null);
            } else {
                g.drawImage(imageLoader.getImage("raumschiff_normal", 0), mModel.getPlayer().getX() - 68, mModel.getPlayer().getY() - 3, null);
            }
        } else if (gifIndex1 <= 13) {
            g.drawImage(imageLoader.getImage("explosion", gifIndex1), mModel.getPlayer().getX() - 68 - 70, mModel.getPlayer().getY() - 3 - 77, null);
            if (gifTimehelper1 % 5 == 0) {
                gifIndex1++;
            }
        }

        //############################################
        // Scheine
        //############################################

        if (mModel.getLevel().getmSchein() != null) {
            //g.drawPolygon(mModel.getLevel().getmSchein().getPolygon());
            g.drawImage(imageLoader.getImage("powerup_schein", 0), mModel.getLevel().getmSchein().getX(), mModel.getLevel().getmSchein().getY(), null);
        }

        //############################################
        // Powerups
        //############################################

        for (ItemModel i : mModel.getLevel().getmItems()) {
            if (i != null) {
                //g.drawPolygon(i.getPolygon());
                if (i.getEffekt() == Effekt.REPARIEREN) {
                    g.drawImage(imageLoader.getImage("powerup_reparieren", 0), i.getX(), i.getY(), null);
                } else {
                    g.drawImage(imageLoader.getImage("powerup_schild", 0), i.getX(), i.getY(), null);
                }
            }
        }

        //############################################
        // Asteroiden
        //############################################

        for (AsteroidModel a : mModel.getLevel().getmAsteroids()) {
            if (a != null) {
                //g.drawPolygon(a.getPolygon());
                g.drawImage(imageLoader.getImage("asteroid", a.getID()), a.getX(), a.getY(), null);
            }
        }

        //############################################
        // HUD
        //############################################
        if (!mainMenu) {
            for (int i = 0; i < currentHealth; i++) {
                g.drawImage(imageLoader.getImage("life", 0), 30 + 70 * i, 30, null);
            }

            if (currentShield) {
                g.drawImage(imageLoader.getImage("schild", 0), 241, 30, null);
            }

            g.drawImage(imageLoader.getImage("lifebar", 0), 30, 30, null);

            for (int i = 1; i <= currentScheine; i++) {
                g.drawImage(imageLoader.getImage("schein", 1), 1195 + i * 5, 5 + i * 5, null);
            }

            g.drawString(Integer.toString(currentScore), 1100, 30);
            g.drawString(currentScheine + "/6", 1100, 55);

            if (currentStage == 5) {
                currentStage = 4;
            }

            g.drawString(Integer.toString(currentStage), 380, 43);
            g.drawString(Integer.toString(currentStage + 1), 890, 43);

            int progressPixels = (int) (481 * currentProgressPercentage);

            for (int i = 1; i < progressPixels; i++) {
                g.drawImage(imageLoader.getImage("level_progressbar_pixel", 0), 400 + i, 31, null);
            }
            g.drawImage(imageLoader.getImage("level_progressbar", 0), 400, 30, null);
        }
        //############################################
        // ENDSCREEN
        //############################################

        if (endcard) {

            if (!musicPlaying) {
                SoundUtilities.stop(soundLoader.getSound("music_ingame", 0));
                if (currentScheine < 3) {
                    SoundUtilities.playFromStart(soundLoader.getSound("result_bad", 0), 0.2);
                } else {
                    SoundUtilities.playFromStart(soundLoader.getSound("result_good", 0), 0.2);
                }
                musicPlaying = true;
            }

            if (gifIndex2 <= 8) {
                g.drawImage(imageLoader.getImage("blende", gifIndex2), 0, 0, null);
                if (timehelper2 % 10 == 0) {
                    if (gifIndex2 < 8) {
                        gifIndex2++;
                    }
                }
            }

            if (gifIndex2 == 7) {
                endcardTime = System.currentTimeMillis();
            }

            if (gifIndex2 == 8) {

                if (System.currentTimeMillis() > endcardTime + 500) {
                    g.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 80));
                    g.drawString("Ergebnis", 450, 150);

                }
                if (System.currentTimeMillis() > endcardTime + 1500) {
                    g.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 30));
                    g.drawString("Punkte    :", 400, 250);
                    g.drawString("Scheine   :", 400, 350);

                }
                if (System.currentTimeMillis() > endcardTime + 2500) {
                    g.drawString(Integer.toString(currentScore), 700, 250);

                }
                if (System.currentTimeMillis() > endcardTime + 3500) {
                    g.drawString(Integer.toString(currentScheine) + "/6", 700, 350);
                    for (int i = 1; i <= currentScheine; i++) {
                        g.drawImage(imageLoader.getImage("schein", 1), 800 + i * 5, 300 + i * 5, null);
                    }

                }
                if (System.currentTimeMillis() > endcardTime + 5000) {
                    g.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 80));
                    g.drawString("NOTE:", 400, 550);

                }
                if (System.currentTimeMillis() > endcardTime + 6000) {
                    if (mModel.getNoHits() && currentScheine == 6) {
                        g.drawImage(imageLoader.getImage("note", 6), 750, 460, null);
                    } else {
                        g.drawImage(imageLoader.getImage("note", currentScheine), 730, 460, null);
                    }

                }
                if (System.currentTimeMillis() > endcardTime + 7500) {
                    if (currentScheine < 3) {
                        g.drawImage(imageLoader.getImage("fail", 0), 200, 100, null);
                    }
                }
            }
        }

        //############################################
        // MAIN MENU
        //############################################

        if (mainMenu) {

            if (!steuerung) {
                g.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 60));
                g.drawString("\"Arbeitstitel\"", 420, 150);

                g.drawImage(imageLoader.getImage("menu", 0), 0, 0, null);
                if (mouseX > 454 && mouseX < 825) {
                    if (mouseY > 275 && mouseY < 344) {
                        g.drawImage(imageLoader.getImage("menu_start", 0), 0, 0, null);
                    } else if (mouseY > 371 && mouseY < 439) {
                        if (mouseX < 620) {
                            g.drawImage(imageLoader.getImage("menu_sound", 0), 0, 0, null);
                        } else if (mouseX > 659) {
                            g.drawImage(imageLoader.getImage("menu_musik", 0), 0, 0, null);
                        }
                    } else if (mouseY > 464 && mouseY < 532) {
                        g.drawImage(imageLoader.getImage("menu_steuerung", 0), 0, 0, null);
                    } else if (mouseY > 557 && mouseY < 625) {
                        g.drawImage(imageLoader.getImage("menu_exit", 0), 0, 0, null);
                    }
                }
                if (!playSound) {
                    g.drawImage(imageLoader.getImage("marker_sound", 0), 0, 0, null);
                }
                if (!playMusic) {
                    g.drawImage(imageLoader.getImage("marker_music", 0), 0, 0, null);
                }
            } else {
                g.drawImage(imageLoader.getImage("steuerung", 0), 0, 0, null);
            }
        }

        Toolkit.getDefaultToolkit().sync();


        //##########################
        // FPS COUNTER
        //##########################

        /*
        frames++;
        long currentFrame = System.currentTimeMillis();
        if (currentFrame > firstFrame + 1000) {
            firstFrame = currentFrame;
            fps = frames;
            frames = 0;
        }
        g.drawString(Integer.toString(fps) + " fps", 50, 50);
        */

    }

    /*
    private long firstFrame;
    private int frames;
    private int fps;
    */
}

