package org.arkanoid.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;

/**
 * The Menu class is responsible for displaying the main menu of the Arkanoid game before gameplay starts.
 * The menu includes a background (either an image or a solid color), the player's name, and a prompt to start the game.
 * The player's name is passed through the constructor and displayed centered horizontally on the screen.
 */
public class Menu {

    private Image backgroundImage;
    private String playerName;

    /**
     * Constructor for the Menu class.
     * Initializes the player's name and attempts to load the background image.
     * If the image is not found or fails to load, a fallback solid color will be used.
     *
     * @param playerName The name of the player to be displayed in the menu
     */
    public Menu(String playerName) {
        this.playerName = playerName;
        try {
            if (getClass().getResourceAsStream("/images/menu.png") != null) {
                backgroundImage = new Image(getClass().getResourceAsStream("/images/menu.png"));
            } else {
                backgroundImage = null;
                System.err.println("Menu: menu.png not found in /images/ (will draw plain color).");
            }
        } catch (Exception e) {
            backgroundImage = null;
            System.err.println("Menu: Error loading menu.png → " + e.getMessage());
        }
    }

    /**
     * Renders the menu on the provided GraphicsContext.
     * Draws the background (image or solid color), the player's name (centered horizontally),
     * and a prompt to press ENTER to start the game.
     *
     * @param gc The GraphicsContext used to draw the menu on the canvas
     */
    public void render(GraphicsContext gc) {
        // Draw the background
        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0);
        } else {
            gc.setFill(Color.DARKBLUE);
            gc.fillRect(0, 0, 800, 600);
        }

        String playerText = "Player: " + (playerName != null ? playerName : "Guest");
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 32));

        Text text = new Text(playerText);
        text.setFont(new Font("Arial", 32));
        double textWidth = text.getLayoutBounds().getWidth();

        int appWidth = 800;
        double x = (appWidth - textWidth) / 2;
        gc.fillText(playerText, x, 320);

        gc.setFill(Color.GRAY);
        gc.setFont(Font.font("Arial", FontPosture.ITALIC, 18));
        gc.fillText("Press ENTER to Start", 310, 510);
    }
}
