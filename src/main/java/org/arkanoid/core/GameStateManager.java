package org.arkanoid.core;

/**
 * Manages the current state of the game (MENU, PLAYING, PAUSED, GAMEOVER).
 * Provides methods to query and transition between states.
 * Uses a singleton reference to the main App instance.
 */
public class GameStateManager {

    /** Enumeration of possible game states */
    public enum GameState {
        MENU, PLAYING, PAUSED, GAMEOVER
    }

    private GameState currentState;
    private final App app;
    
    /**
     * GameStateManager Constructor.
     */
    public GameStateManager() {
        this.app = App.getInstance();
        this.currentState = GameState.MENU;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setState(GameState state) {
        if (this.currentState != state) {
            this.currentState = state;
        }
    }
}
