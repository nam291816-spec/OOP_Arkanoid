package org.arkanoid.io;

import java.io.Serializable;

public class PlayerProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String playerName;
    private final int highScore;
    private final int currentLevel;

    public PlayerProfile(String playerName, int highScore, int currentLevel) {
        this.playerName = playerName;
        this.highScore = highScore;
        this.currentLevel = currentLevel;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
