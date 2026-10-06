package org.arkanoid.core;

import com.sun.tools.jconsole.JConsoleContext;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.arkanoid.audio.AudioManager;
import org.arkanoid.audio.BgmId;
import org.arkanoid.audio.SfxId;
import org.arkanoid.entity.*;
import org.arkanoid.input.InputHandler;
import org.arkanoid.input.MouseHandler;
import org.arkanoid.io.PlayerProfile;
import org.arkanoid.powerup.Boom;
import org.arkanoid.powerup.ExpandPaddlePowerUp;
import org.arkanoid.powerup.ExtraLife;
import org.arkanoid.powerup.FastBallPowerUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The main game loop responsible for updating game logic and rendering each frame.
 * Uses JavaFX AnimationTimer for fixed-timestep updates.
 * Manages state transitions, audio, input, collisions, power-ups, items, and level progression.
 */
public class GameLoop {
    private final App app;
    private final GameStateManager stateManager;
    private final GraphicsContext gc;
    private final InputHandler inputHandler;
    private final MouseHandler mouseHandler;
    private final AudioManager audioManager;
    private long lastTime;
    private final List<Item> items;
    private boolean isTransitioning;
    private boolean isGameOverProcessed; //Flag to avoid multiple saves when game over
    private GameStateManager.GameState lastState;

    /**
     * Constructs the game loop with required dependencies.
     *
     * @param app           the main App instance
     * @param stateManager  manages current game state (MENU, PLAYING, etc.)
     * @param gc            graphics context for rendering
     * @param inputHandler  processes keyboard input
     * @param mouseHandler  processes mouse clicks/hover for UI buttons
     * @param audioManager  handles BGM and SFX playback
     */
    public GameLoop(App app, GameStateManager stateManager, GraphicsContext gc, InputHandler inputHandler, MouseHandler mouseHandler, AudioManager audioManager) {
        this.app = app;
        this.stateManager = stateManager;
        this.gc = gc;
        this.inputHandler = inputHandler;
        this.mouseHandler = mouseHandler;

        this.audioManager = audioManager;
        this.lastTime = 0;
        this.items = new ArrayList<>();
        this.isTransitioning = false;
        this.isGameOverProcessed = false;
        this.lastState = null;
    }

    /**
     * Starts the animation timer that drives the game loop.
     * Calculates delta time and calls update/render each frame.
     */
    public void start() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                double deltaTime;
                if (lastTime == 0) {
                    deltaTime = 0;
                } else {
                    deltaTime = (now - lastTime) / 1_000_000_000.0;
                }
                lastTime = now;

                update(deltaTime);
                render();
            }
        }.start();
    }

    /**
     * Stops currently playing background music.
     * Used during cleanup or state transitions.
     */
    public void stopAudio() {
        if (audioManager != null && audioManager.isBgmPlaying()) {
            audioManager.stopBgm();
        }
    }

    /**
     * Updates game logic based on current state and delta time.
     * Handles input, collisions, power-ups, items, scoring, and state transitions.
     *
     * @param deltaTime time elapsed since last frame (in seconds)
     */
    private void update(double deltaTime) {
        // Wait for level transition
        if (isTransitioning) {
            return;
        }

        // Processing input from the keyboard
        inputHandler.handleInput();

        // Only load and play new BGM if the state has changed
        if (lastState != stateManager.getCurrentState()) {
            if (audioManager != null) {
                boolean isPausing = lastState == GameStateManager.GameState.PLAYING && stateManager.getCurrentState() == GameStateManager.GameState.PAUSED;
                boolean isResuming = lastState == GameStateManager.GameState.PAUSED && stateManager.getCurrentState() == GameStateManager.GameState.PLAYING;
                if (!(isPausing || isResuming)) {
                    audioManager.stopBgm();
                }
            }

            // Play appropriate Background Music
            switch (stateManager.getCurrentState()) {
                case MENU:
                    audioManager.playBgm(BgmId.MENU, true);
                    break;
                case PLAYING:
                    if (lastState == GameStateManager.GameState.PAUSED) {
                        //audioManager.resumeBgm();
                        audioManager.playBgm(BgmId.PLAYING, true);
                    } else {
                        audioManager.playBgm(BgmId.PLAYING, true);
                    }

                    if (lastState == GameStateManager.GameState.MENU) {
                        app.resetGame();
                        isGameOverProcessed = false;
                    }
                    break;
                case PAUSED:
                    audioManager.pauseBgm();
                    break;
                case GAMEOVER:
                    audioManager.playBgm(BgmId.ENDING, true);
                    break;
            }
            lastState = stateManager.getCurrentState();
        }

        switch (stateManager.getCurrentState()) {
            case MENU:

                break;
            case PLAYING:

                if (app.getBall().isAlive()) {
                    // Handle Collisions
                    CollisionManager.handleBallBricksCollision(app.getBall(), app.getBricks());
                    CollisionManager.handleBallPaddleCollision(app.getBall(), app.getPaddle());
                    CollisionManager.handleBallWallCollision(app.getBall());

                    if (CollisionManager.isColliding(app.getBall(), app.getPaddle())) {
                        audioManager.playSfx(SfxId.PADDLE_HIT);
                    }

                    if (!app.getBall().isAlive()) {
                        app.addLives(-1);
                        app.getBall().reset(app.getPaddle().getPointX() + app.getPaddle().getWidth() / 2.0 - app.getBall().getWidth() / 2.0,
                                app.getPaddle().getPointY() - app.getBall().getHeight() - 5);
                    }
                }

                if(!app.getBall().isMoving()) {
                    app.getBall().setPointX(app.getPaddle().getPointX() + app.getPaddle().getWidth() / 2.0 - app.getBall().getWidth() / 2.0);
                    app.getBall().setPointY(app.getPaddle().getPointY() - app.getBall().getHeight() - 5);
                }

                List<Item> itemsToRemove = new ArrayList<>();
                for (Item item : items) {
                    if (item.getPointY() + item.getHeight() >= App.HEIGHT) {
                        itemsToRemove.add(item);
                        continue;
                    }
                    CollisionManager.handlePaddleItemCollision(item, app.getPaddle(), app);
                    if (CollisionManager.isColliding(item, app.getPaddle())) {
                        itemsToRemove.add(item);
                        audioManager.playSfx(SfxId.ITEM_COLLECTED);
                    }
                }
                items.removeAll(itemsToRemove);

                app.updateActivePowerUps(deltaTime);

                // Temporary list of bricks to be deleted
                List<Brick> toRemove = new ArrayList<>();
                for (Brick brick : app.getBricks()) {
                    if (CollisionManager.isColliding(app.getBall(), brick)) {
                        audioManager.playSfx(SfxId.BRICK_BREAK);
                        brick.hit();
                    }

                    if (brick.isDestroyed()) {
                        toRemove.add(brick);

                        Random rand = new Random();
                        int randomNumber = rand.nextInt(10 - 1 + 1) + 1;
                        Item item = null;
                        if (randomNumber == 2) {
                            ExpandPaddlePowerUp powerUp = new ExpandPaddlePowerUp(1.5, 5.0);
                            item = new Item(brick.getPointX() + brick.getWidth() / 2.0 - 10, brick.getPointY() + brick.getHeight() / 2.0 - 10, 20, 20, powerUp);
                        } else if (randomNumber == 4) {
                            FastBallPowerUp powerUp = new FastBallPowerUp(1.5, 5.0);
                            item = new Item(brick.getPointX() + brick.getWidth() / 2.0 - 10, brick.getPointY() + brick.getHeight() / 2.0 - 10, 40, 40, powerUp);
                        } else if (randomNumber == 6) {
                            ExtraLife powerUp = new ExtraLife();
                            item = new Item(brick.getPointX() + brick.getWidth() / 2.0 - 10, brick.getPointY() + brick.getHeight() / 2.0 - 10, 20, 20, powerUp);
                        } else if (randomNumber == 8) {
                            Boom powerUp = new Boom();
                            item = new Item(brick.getPointX() + brick.getWidth() / 2.0 - 10, brick.getPointY() + brick.getHeight() / 2.0 - 10, 20, 20, powerUp);
                        }

                        if (item != null) {
                            items.add(item);
                        }


                        // update score
                        int scoreToAdd = 0;
                        if (brick instanceof NormalBrick) {
                            scoreToAdd = 10;
                        } else if (brick instanceof StrongBrick) {
                            scoreToAdd = 50;
                        }
                        app.addScore(scoreToAdd);
                    }
                }

                app.getBricks().removeAll(toRemove);

                // Check if there are any BreakableBricks left
                boolean containsBreakableBrick = false;
                for (Brick brick : app.getBricks()) {
                    if ((brick instanceof NormalBrick || brick instanceof StrongBrick) && !brick.isDestroyed()) {
                        containsBreakableBrick = true;
                        break;
                    }
                }
                if (!containsBreakableBrick) {
                    app.getBricks().clear();
                    items.clear();
                }

                // If all breakable bricks are destroyed, move to next level
                if (app.getBricks().isEmpty()) {
                    isTransitioning = true;
                    new Thread(() -> {
                        try {
                            Thread.sleep(500); // Level Transition for 0.5 seconds
                            app.nextLevel();
                            isTransitioning = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    break;
                }

                app.getHud().update(app.getScore(), app.getLives(), app.getCurrentLevel());

                // GAMEOVER if lives is subtracted to 0
                if (app.getLives() == 0) {
                    stateManager.setState(GameStateManager.GameState.GAMEOVER);
                }

                // Update Game Objects
                app.getPaddle().update(deltaTime);
                app.getBall().update(deltaTime);

                for (Brick brick : app.getBricks()) {
                    brick.update(deltaTime);
                }

                for (Item item : items) {
                    item.update(deltaTime);
                }
                break;
            case PAUSED:

                break;
            case GAMEOVER:
                // Save profile when game over (only call once)
                if (!isGameOverProcessed) {
                    app.getSaveManager().saveProfile(new PlayerProfile(app.getPlayerName(), app.getScore(), app.getCurrentLevel()));
                    isGameOverProcessed = true;
                }
                break;
        }
    }

    /**
     * Renders the current frame based on game state.
     * Draws background, entities, UI, and overlays.
     */
    private void render() {
        // Clear Screen
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, App.WIDTH, App.HEIGHT);

        switch (stateManager.getCurrentState()) {
            case MENU:
                app.getMenu().render(gc);
                break;
            case PLAYING:
                gc.drawImage(app.getPlayingBackground(),0, 0, App.WIDTH, App.HEIGHT);
                app.getPaddle().render(gc);
                app.getBall().render(gc);

                for (Brick brick : app.getBricks()) {
                    brick.render(gc);
                }

                for (Item item : items) {
                    item.render(gc);
                }

                app.getHud().render(gc);
                break;
            case PAUSED:
                app.getPaddle().render(gc);
                app.getBall().render(gc);

                for (Brick brick : app.getBricks()) {
                    brick.render(gc);
                }

                for (Item item : items) {
                    item.render(gc);
                }

                app.getHud().render(gc);

                // Add semi-transparent overlay
                gc.setFill(Color.color(0,0,0,0.5)); // Black with 50% opacity
                gc.fillRect(0, 0, App.WIDTH, App.HEIGHT);

                gc.setFill(Color.WHITE);
                gc.setFont(new Font("Arial", 30));
                gc.fillText("Game Paused", App.WIDTH / 2.0 - 100, App.HEIGHT / 2.0 - 20);
                gc.setFont(new Font("Arial", 20));
                gc.fillText("Press P to Resume or ESCAPE to Quit",
                        App.WIDTH / 2.0 - 160, App.HEIGHT / 2.0 + 20);

                break;
            case GAMEOVER:
                app.getScoreBoard().render(gc);

                gc.setFill(Color.WHITE);
                gc.setFont(new Font("Arial", 30));
                gc.fillText("Your Score: " + app.getScore(), 30, 40);
                break;
        }

        // Render mouse-interactive buttons
        mouseHandler.render(gc, stateManager.getCurrentState());
    }
}