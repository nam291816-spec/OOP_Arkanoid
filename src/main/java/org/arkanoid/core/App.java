package org.arkanoid.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.arkanoid.audio.AudioManager;
import org.arkanoid.entity.*;
import org.arkanoid.graphics.HUD;
import org.arkanoid.input.InputHandler;
import org.arkanoid.input.InputListener;
import org.arkanoid.input.MouseHandler;
import org.arkanoid.ui.PauseButton;
import org.arkanoid.io.PlayerProfile;
import org.arkanoid.io.SaveManager;
import org.arkanoid.level.Level;
import org.arkanoid.level.LevelLoader;
import org.arkanoid.powerup.PowerUp;
import org.arkanoid.powerup.TimedPowerUp;
import org.arkanoid.ui.Menu;
import org.arkanoid.ui.PlayButton;
import org.arkanoid.ui.GameOver;

import java.util.*;
import java.util.List;

/**
 * The main application class for the Arkanoid game.
 * Extends JavaFX Application and serves as the central controller
 * managing game state, entities, input, rendering, levels, scoring, and save/load.
 */
public class App extends Application {
    /** Game window dimensions */
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private static final int MAX_LIVES = HUD.MAX_LIVES;

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;
    private Map<TimedPowerUp, MovableObject> activePowerUps;
    private static App instance;
    private Canvas canvas;
    private GraphicsContext gc;
    private GameStateManager stateManager;
    private GameLoop gameLoop;

    private SaveManager saveManager;
    private AudioManager audioManager;

    private int currentLevel;
    private List<Level> levels;

    private Menu menu;
    private GameOver gameOver;
    private HUD hud;
    private Image playingBackground;

    private String playerName;
    private int score;
    private Map<Integer, Integer> levelScores;
    private int lives = MAX_LIVES;

    /**
     * Starts the JavaFX application.
     * Initializes the window, canvas, input, audio, save system, and game loop.
     *
     * @param stage the primary stage for this application
     */
    public void start(Stage stage) {
        if (instance != null) {
            throw new IllegalStateException("App already started!");
        }
        instance = this;

        // Interface Initialization
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // Set icon for stage
        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon_arkanoid.png")));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage() + ". Using default icon.");
        }

        stage.setTitle("Arkanoid");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        // Initialize managers
        audioManager = new AudioManager();
        saveManager = SaveManager.getInstance();
        activePowerUps = new HashMap<>();

        // Show player name dialog
        playerName = promptForPlayerName(stage);
        if (playerName.isEmpty() || playerName.trim().isEmpty()) {
            playerName = "Player";
        }

        levels = LevelLoader.loadLevels();
        levelScores = new HashMap<>();
        // load levelScores
        for (Level level : levels) {
            String[] layout = level.getLayout();
            Map<String, Level.BrickInfo> palette = level.getPalette();
            int normalBrickCount = 0;
            int strongBrickCount = 0;
            for (String rowData : layout) {
                for (int i = 0; i < rowData.length(); i++) {
                    char symbol = rowData.charAt(i);
                    if (symbol != '.') {
                        Level.BrickInfo brickInfo = palette.get(String.valueOf(symbol));
                        if (brickInfo != null) {
                            if (brickInfo.getType().equals("NormalBrick")) {
                                normalBrickCount++;
                            } else if (brickInfo.getType().equals("StrongBrick")) {
                                strongBrickCount++;
                            }
                        }
                    }
                }
            }
            int levelScore = normalBrickCount * 10 + strongBrickCount * 50;
            levelScores.put(level.getId(), levelScore);
        }

        // Load player profile
        Optional<PlayerProfile> profile = saveManager.findProfile(playerName);
        if (profile.isPresent()) {
            currentLevel = profile.get().getCurrentLevel();
            if (currentLevel < 1) {
                currentLevel = 1;
            } else if (currentLevel > levels.size()) {
                currentLevel = levels.size();
            }

            score = 0;
            for (Level level : levels) {
                if (level.getId() < currentLevel) {
                    score += levelScores.getOrDefault(level.getId(), 0);
                }
            }
            lives = MAX_LIVES;
        } else {
            currentLevel = 1;
            score = 0;
            lives = MAX_LIVES;
        }

        menu = new Menu(playerName);
        gameOver = new GameOver(saveManager);
        hud = new HUD();
        playingBackground = new Image(getClass().getResourceAsStream("/images/back_ground.png"));

        stateManager = new GameStateManager();
        initializeObjects(currentLevel);


        // Initialize InputHandler with Paddle
        InputHandler inputHandler = new InputHandler(paddle, ball, stateManager);

        // Set up input handling
        MouseHandler mouseHandler = new MouseHandler();
        PauseButton pausePlayButton = new PauseButton(stateManager);
        PlayButton toPlayButton = new PlayButton(stateManager);

        mouseHandler.addButton(pausePlayButton);
        mouseHandler.addButton(toPlayButton);

        scene.setOnMouseClicked(mouseHandler::handleClick);
        scene.setOnMouseMoved(mouseHandler::handleHover);

        InputListener inputListener = InputListener.getInstance();
        scene.setOnKeyPressed(event -> inputListener.pressKey(event.getCode()));
        scene.setOnKeyReleased(event -> inputListener.releaseKey(event.getCode()));

        // Start the game loop
        gameLoop = new GameLoop(this, stateManager, gc, inputHandler, mouseHandler, audioManager);
        gameLoop.start();

        // Clean up audio resources when the application closes
        stage.setOnCloseRequest(event -> {
            gameLoop.stopAudio();
            disposeAllSfx();
            javafx.application.Platform.exit();
        });
    }

    /**
     * Releases all sound effects and disposes the audio manager.
     */
    private void disposeAllSfx() {
        if (audioManager != null) {
            audioManager.dispose();
        }
    }

    /**
     * Initializes paddle, ball, and bricks for the given level.
     *
     * @param levelId the ID of the level to load
     */
    public void initializeObjects(int levelId) {
        paddle = new Paddle((int) (WIDTH / 2 - 50), HEIGHT - 30, Paddle.ORIGINAL_WIDTH, 20, 600);
        ball = new Ball((int)(WIDTH / 2), (int)(HEIGHT / 2), 10, 0, 0);
        bricks = new ArrayList<>();
        loadBricks(getLevelById(levelId));
    }

    /**
     * Resets paddle, ball, and bricks for the current level.
     *
     * @param levelId the level to reset
     */
    public void resetObjects(int levelId) {
        Level levelData = getLevelById(levelId);

        paddle.setPointX((int) (WIDTH / 2 - 20));
        ball.reset(paddle.getPointX() + paddle.getWidth() / 2.0 - ball.getWidth() / 2.0, paddle.getPointY() - ball.getHeight() - 5);

        activePowerUps.clear();
        bricks.clear();
        loadBricks(levelData);
    }

    /**
     * Loads brick entities from the level layout data.
     *
     * @param levelData the Level object containing layout and palette
     */
    private void loadBricks(Level levelData) {
        if (!bricks.isEmpty()) return;
        double cellWidth = levelData.getCell().getWidth();
        double cellHeight = levelData.getCell().getHeight();
        double padding = levelData.getCell().getPadding();
        double offSetX = levelData.getOffset().getX();
        double offSetY = levelData.getOffset().getY();

        String[] layout = levelData.getLayout();
        for (int row = 0; row < layout.length; row++) {
            String rowData = layout[row];
            for (int col = 0; col < rowData.length(); col++) {
                char symbol = rowData.charAt(col);
                if (symbol != '.') {
                    Level.BrickInfo brickInfo = levelData.getPalette().get(String.valueOf(symbol));
                    if (brickInfo != null) {
                        String type = brickInfo.getType();
                        int hp = brickInfo.getHp();
                        String id = brickInfo.getId();
                        double x = offSetX + col * (cellWidth + padding);
                        double y = offSetY + row * (cellHeight + padding);
                        if (type.equals("NormalBrick")) {
                            bricks.add(new NormalBrick(x, y, (int) cellWidth, (int) cellHeight, hp, id));
                        } else if (type.equals("StrongBrick")) {
                            bricks.add(new StrongBrick(x, y, (int) cellWidth, (int) cellHeight, hp, id));
                        } else if (type.equals("UnbreakableBrick")) {
                            bricks.add(new UnbreakableBrick(x, y, (int) cellWidth, (int) cellHeight, hp, id));
                        }
                    }
                }
            }
        }
    }

    /**
     * Advances to the next level if all bricks are destroyed.
     * Triggers game win if final level is completed.
     */
    public void nextLevel() {
        if (bricks.isEmpty()) {
            currentLevel++;
        }
        if (currentLevel > levels.size()) {
            // Won Game
            saveManager.updateHighScore(playerName, score, currentLevel);
            stateManager.setState(GameStateManager.GameState.GAMEOVER);
        } else {
            // Next Level
            resetObjects(currentLevel);
        }
    }

    /**
     * Resets the game.
     * Keeps accumulated score from previous levels.
     */
    public void resetGame() {
        score = 0;
        for (Level level : levels) {
            if (level.getId() < currentLevel) {
                score += levelScores.getOrDefault(level.getId(), 0);
            }
        }
        lives = MAX_LIVES;
        resetObjects(currentLevel);

    }

    /**
     * Retrieves the Level object by its ID.
     * Falls back to the first level if not found.
     *
     * @param levelId the level ID
     * @return the corresponding Level object
     */
    public Level getLevelById(int levelId) {
        Level levelData = null;
        for (Level lvl : levels) {
            if (lvl.getId() == levelId) {
                levelData = lvl;
                break;
            }
        }
        if (levelData == null) {
            levelData = levels.getFirst();
            currentLevel = levelData.getId();
        }
        return levelData;
    }

    /**
     * Registers a timed power-up and removes any existing one of the same type.
     *
     * @param powerUp the power-up to activate
     * @param target  the object affected by the power-up (paddle or ball)
     */
    public void addActivePowerUp(TimedPowerUp powerUp, MovableObject target) {

        activePowerUps.entrySet().removeIf(entry -> entry.getKey().getType().equals(powerUp.getType()));
        activePowerUps.put(powerUp, target);
    }

    /**
     * Updates all active timed power-ups.
     *
     * @param deltaTime time elapsed since last frame
     */
    public void updateActivePowerUps(double deltaTime) {
        List<PowerUp> toRemove = new ArrayList<>();
        for (Map.Entry<TimedPowerUp, MovableObject> entry : activePowerUps.entrySet()) {
            TimedPowerUp powerUp = entry.getKey();
            MovableObject target = entry.getValue();
            powerUp.update(deltaTime, target);
            if (!powerUp.isActive()) {
                toRemove.add(powerUp);
            }
        }

        for (PowerUp powerUp : toRemove) {
            activePowerUps.remove(powerUp);
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getLives() {
        return lives;
    }

    public void addLives(int x) {
        if (this.lives + x <= MAX_LIVES && this.lives + x >= 0) {
            this.lives += x;
        }
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public Ball getBall() {
        return ball;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public GraphicsContext getGraphicsContext() {
        return gc;
    }

    public Menu getMenu() {
        return menu;
    }

    public GameOver getScoreBoard() {
        return gameOver;
    }

    public HUD getHud() {
        return hud;
    }

    public Image getPlayingBackground() {
        return playingBackground;
    }

    /**
     * Returns the singleton instance of the App.
     *
     * @return the current App instance
     * @throws IllegalStateException if App has not been initialized
     */
    public static App getInstance() {
        if (instance == null) {
            throw new IllegalStateException("App has not been initialized");
        }
        return instance;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    /**
     * Shows a dialog to input the player name.
     *
     * @param stage the owner stage
     * @return the entered name (trimmed)
     */
    private String promptForPlayerName(Stage stage) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Get Player Name");
        dialog.setHeaderText("Welcome to Arena of Valor!");
        dialog.setContentText("Please Enter Your Name:");
        dialog.initOwner(stage);

        Optional<String> result = dialog.showAndWait();
        return result.orElse("").trim();
    }
}