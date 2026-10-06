package org.arkanoid.io.backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkanoid.io.PlayerProfile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based implementation of SaveBackend.
 * Stores data under user's home directory in a hidden '.arkanoid' folder.
 */
public class FileSaveBackend implements SaveBackend {
    private static final Logger logger = LogManager.getLogger(FileSaveBackend.class);
    private static final String SAVE_DIRECTORY = System.getProperty("user.home") + File.separator + ".arkanoid";
    private static final String SAVE_FILE = "player_profiles.dat";

    private final Path saveFilePath;

    public FileSaveBackend() {
        this.saveFilePath = Paths.get(SAVE_DIRECTORY, SAVE_FILE);
        initializeSaveDirectory();
    }

    private void initializeSaveDirectory() {
        try {
            Path directory = Paths.get(SAVE_DIRECTORY);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                logger.info("Created save directory: {}", SAVE_DIRECTORY);
            }
        } catch (IOException e) {
            logger.error("Failed to create save directory: {}", e.getMessage());
        }
    }

    @Override
    public List<PlayerProfile> loadAll() {
        if (!Files.exists(saveFilePath)) {
            logger.info("No save file found. Starting with empty profile list.");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFilePath.toFile()))) {
            @SuppressWarnings("unchecked")
            List<PlayerProfile> profiles = (List<PlayerProfile>) ois.readObject();
            logger.info("Loaded {} profiles from disk", profiles.size());
            return profiles;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Failed to load profiles: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean saveAll(List<PlayerProfile> profiles) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFilePath.toFile()))) {
            oos.writeObject(profiles);
            logger.info("Successfully saved {} profiles", profiles.size());
            return true;
        } catch (IOException e) {
            logger.error("Failed to save profiles: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteAll() {
        try {
            if (Files.exists(saveFilePath)) {
                Files.delete(saveFilePath);
            }
            logger.info("Cleared all profiles");
            return true;
        } catch (IOException e) {
            logger.error("Failed to delete save file: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Path getSaveFilePath() {
        return saveFilePath;
    }
}
