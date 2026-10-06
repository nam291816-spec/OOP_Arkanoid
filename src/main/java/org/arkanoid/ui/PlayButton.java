package org.arkanoid.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.arkanoid.core.App;
import org.arkanoid.core.GameStateManager;

/**
 * Button that appears in the MENU screen and starts the game when clicked.
 */
public class PlayButton extends ImageButton {

    private final GameStateManager stateManager;

    /**
     * Creates a centered Play button for the MENU screen.
     *
     * @param stateManager reference to manage game states
     */
    public PlayButton(GameStateManager stateManager) {
        super(
                App.WIDTH / 2 - 100,
                App.HEIGHT / 2 + 50 ,
                200, 100,
                "/images/btn_toPlay.png",
                "/images/btn_toPlay_hover.png"
        );
        this.stateManager = stateManager;
    }

    /**
     * Starts the game from the MENU state.
     */
    @Override
    public void onClick() {
        if (stateManager.getCurrentState() == GameStateManager.GameState.MENU) {
            stateManager.setState(GameStateManager.GameState.PLAYING);
        }
    }

    /**
     * Renders the Play button (always same icon).
     */
    @Override
    public void render(GraphicsContext gc) {
        try {
            this.normalImage = new Image(getClass().getResourceAsStream("/images/btn_toPlay.png"));
            this.hoverImage = new Image(getClass().getResourceAsStream("/images/btn_toPlay_hover.png"));
        } catch (Exception e) {
            System.out.println("Failed to load image");
        }
        gc.drawImage(isHovered ? hoverImage : normalImage, x, y, width, height);
    }

    /**
     * Button only visible while in MENU state.
     */
    @Override
    public boolean shouldRender(GameStateManager.GameState currentState) {
        return currentState == GameStateManager.GameState.MENU;
    }
}
