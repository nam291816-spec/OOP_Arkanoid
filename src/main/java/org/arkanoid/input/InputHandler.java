package org.arkanoid.input;

import org.arkanoid.core.GameStateManager;
import org.arkanoid.entity.Ball;
import org.arkanoid.entity.Paddle;

/**
 * Processes keyboard input and translates it into game actions.
 * Handles state transitions (MENU, PLAYING, PAUSED, GAMEOVER), paddle movement,
 * ball launch, and debug/reset commands.
 * Uses InputListener singleton for key state tracking.
 */
public class InputHandler {

    private final InputListener inputListener;
    private final Paddle paddle;
    private final Ball ball;
    private final GameStateManager stateManager;

    public InputHandler(Paddle paddle, Ball ball, GameStateManager stateManager) {
        this.inputListener = InputListener.getInstance();
        this.paddle = paddle;
        this.ball = ball;
        this.stateManager = stateManager;
    }

    public void handleInput() {
        // Press ENTER in MENU
        boolean enterPressed = inputListener.isJustPressed(Keybinds.NEXT_STATE);
        if (stateManager.getCurrentState() == GameStateManager.GameState.MENU
                && enterPressed) {
            System.out.println("pressed ENTER");
            stateManager.setState(GameStateManager.GameState.PLAYING);
        }

        // Press P to Pause/Resume
        boolean pPressed = inputListener.isJustPressed(Keybinds.PAUSE);
        if (pPressed) {
            GameStateManager.GameState currentState = stateManager.getCurrentState();
            if (currentState == GameStateManager.GameState.PLAYING) {
                System.out.println("Pausing game");
                stateManager.setState(GameStateManager.GameState.PAUSED);
            } else if (currentState == GameStateManager.GameState.PAUSED) {
                System.out.println("Resuming game");
                stateManager.setState(GameStateManager.GameState.PLAYING);
            }
        }

        // Press ESC to return MENU in PAUSED and GAMEOVER state
        boolean escapePressed = inputListener.isJustPressed(Keybinds.EXIT);
        if (escapePressed) {
            GameStateManager.GameState currentState = stateManager.getCurrentState();
            if (currentState == GameStateManager.GameState.PAUSED
                    || currentState == GameStateManager.GameState.GAMEOVER) {
                System.out.println("ESCAPE pressed: Returning to MENU");
                stateManager.setState(GameStateManager.GameState.MENU);
            }
        }

        // Moving Paddle in PLAYING
        if (stateManager.getCurrentState() == GameStateManager.GameState.PLAYING) {
            boolean leftPressed = inputListener.isKeyPressed(Keybinds.MOVE_LEFT);
            boolean rightPressed = inputListener.isKeyPressed(Keybinds.MOVE_RIGHT);

            if (leftPressed && !rightPressed) {
                paddle.moveLeft();
            } else if (rightPressed && !leftPressed) {
                paddle.moveRight();
            } else {
                paddle.stopMoving();
            }

            // launch the ball on the paddle
            if (!ball.isMoving() && inputListener.isKeyPressed(Keybinds.LAUNCH_BALL)) {
                ball.setDirection(0, -1);
            }
        }

        if (inputListener.isKeyPressed(Keybinds.RESET)) {
            // Implement reset functionality
        }

        // Clear per-frame transitions at the end of processing
        inputListener.endFrame();
    }
}