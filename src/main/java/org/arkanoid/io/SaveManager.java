package org.arkanoid.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkanoid.io.backend.FileSaveBackend;
import org.arkanoid.io.backend.SaveBackend;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SaveManager handles saving and loading player profiles and game data.
 * Stores data in the user's home directory for persistence across sessions.
 */
public class SaveManager {
    private static final Logger logger = LogManager.getLogger(SaveManager.class);
    private static SaveManager instance;

    private final SaveBackend backend;
    private List<PlayerProfile> playerProfiles;

    /**
     * Private constructor for singleton pattern.
     */
    private SaveManager() {
        this(new FileSaveBackend());
    }

    // Visible for testing/injection
    private SaveManager(SaveBackend backend) {
        this.backend = backend;
        this.playerProfiles = new ArrayList<>();
        loadProfiles();
    }

    /**
     * Get the singleton instance of SaveManager.
     * @return SaveManager instance
     */
    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }

    /**
     * Get the singleton instance with a custom backend (useful for testing).
     * The first invocation wins; subsequent calls return the existing instance.
     */
    public static SaveManager getInstance(SaveBackend backend) {
        if (instance == null) {
            instance = new SaveManager(backend);
        }
        return instance;
    }

    /**
     * Save a player profile. If the player already exists, update their profile.
     * @param profile PlayerProfile to save
     * @return true if save was successful, false otherwise
     */
    public boolean saveProfile(PlayerProfile profile) {
        if (profile == null || profile.getPlayerName() == null || profile.getPlayerName().trim().isEmpty()) {
            logger.warn("Cannot save null or invalid profile");
            return false;
        }

        Optional<PlayerProfile> existingProfile = findProfile(profile.getPlayerName());
        if (existingProfile.isPresent()) {
            PlayerProfile existing = existingProfile.get();
            if (profile.getHighScore() > existing.getHighScore()) {
                playerProfiles.remove(existing);
                playerProfiles.add(profile);
                logger.info("Updated profile for player: {}", profile.getPlayerName());
            } else {
                playerProfiles.remove(existing);
                PlayerProfile updated = new PlayerProfile(
                    existing.getPlayerName(),
                    existing.getHighScore(),
                    profile.getCurrentLevel()
                );
                playerProfiles.add(updated);
                logger.info("Updated current level for player: {}", profile.getPlayerName());
            }
        } else {
            // Add new profile
            playerProfiles.add(profile);
            logger.info("Added new profile for player: {}", profile.getPlayerName());
        }

        return saveAllProfiles();
    }

    /**
     * Save all profiles to disk.
     * @return true if save was successful, false otherwise
     */
    private boolean saveAllProfiles() {
        return backend.saveAll(playerProfiles);
    }

    /**
     * Load all player profiles from disk.
     */
    private void loadProfiles() {
        playerProfiles.clear();
        playerProfiles.addAll(backend.loadAll());
    }

    /**
     * Deserialize a profile from a string.
     * @param data Serialized profile data
     * @return PlayerProfile or null if deserialization fails
     */
    // Deserialization moved to backend

    /**
     * Find a player profile by name.
     * @param playerName Name of the player
     * @return Optional containing the profile if found
     */
    public Optional<PlayerProfile> findProfile(String playerName) {
        return playerProfiles.stream()
                .filter(p -> p.getPlayerName().equalsIgnoreCase(playerName))
                .findFirst();
    }

    /**
     * Get all player profiles.
     * @return List of all player profiles
     */
    public List<PlayerProfile> getAllProfiles() {
        return new ArrayList<>(playerProfiles);
    }

    /**
     * Get the top N profiles by high score.
     * @param limit Maximum number of profiles to return
     * @return List of top profiles sorted by high score (descending)
     */
    public List<PlayerProfile> getTopProfiles(int limit) {
        return playerProfiles.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getHighScore(), p1.getHighScore()))
                .limit(limit)
                .toList();
    }

    /**
     * Delete a player profile.
     * @param playerName Name of the player to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteProfile(String playerName) {
        Optional<PlayerProfile> profile = findProfile(playerName);
        if (profile.isPresent()) {
            playerProfiles.remove(profile.get());
            logger.info("Deleted profile for player: {}", playerName);
            return saveAllProfiles();
        }
        logger.warn("Profile not found for deletion: {}", playerName);
        return false;
    }

    /**
     * Update the high score for a player if the new score is higher.
     * @param playerName Name of the player
     * @param newScore New score to compare
     * @param currentLevel Current level the player is on
     * @return true if the high score was updated, false otherwise
     */
    public boolean updateHighScore(String playerName, int newScore, int currentLevel) {
        Optional<PlayerProfile> existing = findProfile(playerName);
        
        if (existing.isPresent()) {
            PlayerProfile profile = existing.get();
            if (newScore > profile.getHighScore()) {
                PlayerProfile updated = new PlayerProfile(playerName, newScore, currentLevel);
                return saveProfile(updated);
            }
        } else {
            // Create new profile
            PlayerProfile newProfile = new PlayerProfile(playerName, newScore, currentLevel);
            return saveProfile(newProfile);
        }
        
        return false;
    }

    /**
     * Clear all saved profiles (use with caution).
     * @return true if clearing was successful, false otherwise
     */
    public boolean clearAllProfiles() {
        playerProfiles.clear();
        return backend.deleteAll();
    }

    /**
     * Get the path to the save file.
     * @return Path to the save file
     */
    public Path getSaveFilePath() {
        return backend.getSaveFilePath();
    }

    /**
     * Reload profiles from disk (useful if external changes were made).
     */
    public void reloadProfiles() {
        loadProfiles();
    }
}
