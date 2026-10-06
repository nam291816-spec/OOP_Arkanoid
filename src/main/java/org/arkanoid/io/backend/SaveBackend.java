package org.arkanoid.io.backend;

import org.arkanoid.io.PlayerProfile;

import java.nio.file.Path;
import java.util.List;

/**
 * Backend interface for saving/loading player profiles.
 * Implementations handle the persistence mechanism (e.g., file system, database).
 */
public interface SaveBackend {
    /**
     * Load all player profiles from the persistence store.
     * @return list of profiles, possibly empty, never null
     */
    List<PlayerProfile> loadAll();

    /**
     * Persist all given profiles to the persistence store.
     * @param profiles profiles to persist
     * @return true if persisted successfully, false otherwise
     */
    boolean saveAll(List<PlayerProfile> profiles);

    /**
     * Delete all persisted data for player profiles.
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteAll();

    /**
     * Get the path to the underlying persistence storage if applicable.
     * @return path to save file or root path; may be null for non-file backends
     */
    Path getSaveFilePath();
}
