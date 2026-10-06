package org.arkanoid.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import org.arkanoid.io.PlayerProfile;
import org.arkanoid.io.SaveManager;

import java.util.List;

/**
 * The GameOver class is responsible for rendering the high score screen in the Arkanoid game.
 * It displays the top 5 players with their names and scores, retrieved from SaveManager.
 * The GameOver Screen includes a background image (if available), a semi-transparent table, and visual effects.
 */
public class GameOver {

    private static final int TOP_COUNT = 5;
    private final SaveManager saveManager;
    private Image background;

    /**
     * Constructor initializes the GameOver with a SaveManager instance and loads the background image.
     *
     * @param saveManager The SaveManager instance to retrieve high score data
     */
    public GameOver(SaveManager saveManager) {
        this.saveManager = saveManager;

        // Attempt to load the GameOver background image
        try {
            background = new Image(getClass().getResourceAsStream("/images/gameOverBg.png"));
        } catch (Exception e) {
            background = null;
            System.err.println("ScoreBoard: Error loading GameOver background: " + e.getMessage());
        }
    }

    /**
     * Renders the GameOver screen with background and score text.
     * Displays a title, a semi-transparent table with a yellow border, and the top 5 player scores.
     * The table includes a header and a return prompt, with visual distinctions for the top 3 ranks.
     *
     * @param gc The GraphicsContext used for rendering on the canvas
     */
    public void render(GraphicsContext gc) {
        // Draw the background image if available
        if (background != null) {
            gc.drawImage(background, 0, 0);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, 800, 600);
        }

        // Draw the title "HIGH SCORES" centered at the top
        gc.setFill(Color.rgb(255, 215, 0)); // Yellow
        gc.setFont(new Font("Arial Bold", 48));
        String title = "HIGH SCORES";
        Text titleText = new Text(title);
        titleText.setFont(new Font("Arial Bold", 48));
        double titleWidth = titleText.getLayoutBounds().getWidth();
        gc.fillText(title, (800 - titleWidth) / 2, 150);

        // Draw the table border
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(3);
        gc.strokeRect(150, 180, 500, 310);
        // Draw the semi-transparent table background
        gc.setFill(Color.rgb(0, 0, 0, 0.7)); // // Black with 70% opacity
        gc.fillRect(150, 180, 500, 310);

        // Draw the table header
        gc.setFill(Color.YELLOW);
        gc.setFont(new Font("Arial Bold", 28));
        gc.fillText("RANK", 160, 220); // Rank column header
        gc.fillText("PLAYER", 270, 220); // Player name column header
        gc.fillText("SCORE", 530, 220); // Score column header

        // Draw a horizontal separator line below the header
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        gc.strokeLine(150, 235, 650, 235);

        // Retrieve the top 5 player profiles from SaveManager
        List<PlayerProfile> topProfiles = saveManager.getTopProfiles(TOP_COUNT);

        // Render the list of top 5 players (or fewer if not enough profiles)
        gc.setFont(new Font("Arial", 26));
        int y = 270; // Starting y-coordinate for the first score entry
        for (int i = 0; i < Math.min(TOP_COUNT, topProfiles.size()); i++) {
            PlayerProfile profile = topProfiles.get(i);

            // Draw rank number (gold for top 3, white otherwise)
            gc.setFill(i < 3 ? Color.GOLD : Color.WHITE);
            gc.setFont(new Font("Arial Bold", 26));
            gc.fillText((i + 1) + ".", 190, y);

            // Draw player name (truncated if longer than 15 characters)
            gc.setFill(Color.CYAN);
            gc.setFont(new Font("Arial", 26));
            String nameText = profile.getPlayerName().length() > 15 ?
                    profile.getPlayerName().substring(0, 12) + "..." : profile.getPlayerName();
            gc.fillText(nameText, 270, y);

            // Draw score (gold for top 3, white otherwise)
            gc.setFill(i < 3 ? Color.GOLD : Color.WHITE);
            gc.setFont(new Font("Arial Bold", 26));
            gc.fillText(String.valueOf(profile.getHighScore()), 560, y);

            y += 50; // Move down for the next entry
        }

        // Draw the return prompt centered at the bottom
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontPosture.ITALIC, 24));
        String backText = "Press ESC to Return";
        Text backTextObj = new Text(backText);
        backTextObj.setFont(new Font("Arial", 24));
        double backWidth = backTextObj.getLayoutBounds().getWidth();
        gc.fillText(backText, (800 - backWidth) / 2, 550);
    }
}
