package org.arkanoid.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * The HUD (Heads-Up Display) class is responsible for rendering user interface information
 * such as the player's score and lives in the Arkanoid game.
 * This class manages the drawing of UI elements like lives, score, and the HUD background.
 */
public class HUD {

    public static final int MAX_LIVES = 3;

    private int score;
    private int lives;
    private int currentLevel;

    private Image heartImage;
    private Image defaultBackground;
    private Image walls;

    /**
     * Default constructor for the HUD class.
     * Initializes the player's lives to MAX_LIVES and score to INIT_SCORE.
     * Loads the heart, background, and walls images, with fallback handling if resources are not found.
     */
    public HUD() {
        this.lives = MAX_LIVES;
        this.score = 0;
        currentLevel = 1;

        try {
            heartImage = new Image(getClass().getResourceAsStream("/images/heart.png"));
        } catch (Exception e) {
            heartImage = null;
            System.err.println("HUD: Error loading heart image: " + e.getMessage());
        }

        try {
            defaultBackground = new Image(getClass().getResourceAsStream("/images/hud_topBg.png"));
        } catch (Exception e) {
            defaultBackground = null;
            System.err.println("HUD: Error loading default background: " + e.getMessage());
        }

        try {
            walls = new Image(getClass().getResourceAsStream("/images/walls.png"));
        } catch (Exception e) {
            walls = null;
            System.err.println("HUD: Error loading walls: " + e.getMessage());
        }
    }

    /**
     * Constructor for the HUD class with specified lives and score.
     * @param lives The initial number of lives for the player.
     * @param score The initial score for the player.
     * @param currentLevel current level
     */
    public HUD(int lives,int score, int currentLevel) {
        this.lives = lives;
        this.score = score;
        this.currentLevel = currentLevel;
    }

    /**
     * Updates the HUD with updated score and lives values.
     * @param score The updated score to set.
     * @param lives The updated number of lives to set.
     * @param currentLevel current level
     */
    public void update(int score, int lives, int currentLevel) {
        this.score = score;
        this.lives = lives;
        this.currentLevel = currentLevel;
    }

    /**
     * Renders the HUD elements (background, score, and lives) on the provided GraphicsContext.
     * If the heart image is available, lives are displayed as heart icons; otherwise, lives
     * are displayed as text.
     *
     * @param gc The GraphicsContext used for rendering the HUD.
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(defaultBackground, 0, 0, 800, 30);
        gc.drawImage(walls, -20, 30, 840, 570);


        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("SCORE: " + score, 20, 25);
        gc.fillText("LIVES:", 230, 25);
        gc.fillText("LEVEL: " + currentLevel, 450, 25);

        double heartY = 8;
        double startX = 300;

        if (heartImage != null) {
            for (int i = 0; i < lives; i++) {
                gc.drawImage(heartImage, startX + i * 28, heartY, 20, 20);
            }
        } else {
            gc.fillText("x" + lives, startX, 25);
        }
    }
}
