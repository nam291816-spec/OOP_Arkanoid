package org.arkanoid.io;

import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SaveManager.
 * Tests all core functionality including save, load, update, and delete operations.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SaveManagerTest {
    
    private SaveManager saveManager;
    private static final String TEST_PLAYER_1 = "TestPlayer1";
    private static final String TEST_PLAYER_2 = "TestPlayer2";
    private static final String TEST_PLAYER_3 = "TestPlayer3";
    
    @BeforeEach
    void setUp() {
        saveManager = SaveManager.getInstance();
        saveManager.clearAllProfiles();
    }
    
    @AfterEach
    void tearDown() {
        saveManager.clearAllProfiles();
    }
    
    @Test
    @Order(1)
    @DisplayName("Test SaveManager Singleton Pattern")
    void testSingleton() {
        SaveManager instance1 = SaveManager.getInstance();
        SaveManager instance2 = SaveManager.getInstance();
        
        assertNotNull(instance1, "SaveManager instance should not be null");
        assertSame(instance1, instance2, "Both instances should be the same object");
    }
    
    @Test
    @Order(2)
    @DisplayName("Test Save Directory Creation")
    void testSaveDirectoryCreation() {
        Path saveFilePath = saveManager.getSaveFilePath();
        Path directory = saveFilePath.getParent();
        
        assertTrue(Files.exists(directory), "Save directory should exist");
        assertTrue(Files.isDirectory(directory), "Save path should be a directory");
    }
    
    @Test
    @Order(3)
    @DisplayName("Test Save Single Profile")
    void testSaveSingleProfile() {
        PlayerProfile profile = new PlayerProfile(TEST_PLAYER_1, 1000, 1);
        boolean result = saveManager.saveProfile(profile);
        
        assertTrue(result, "Save operation should succeed");
        
        Optional<PlayerProfile> loaded = saveManager.findProfile(TEST_PLAYER_1);
        assertTrue(loaded.isPresent(), "Profile should be found");
        assertEquals(TEST_PLAYER_1, loaded.get().getPlayerName());
        assertEquals(1000, loaded.get().getHighScore());
        assertEquals(1, loaded.get().getCurrentLevel());
    }
    
    @Test
    @Order(4)
    @DisplayName("Test Save Multiple Profiles")
    void testSaveMultipleProfiles() {
        PlayerProfile profile1 = new PlayerProfile(TEST_PLAYER_1, 1000, 1);
        PlayerProfile profile2 = new PlayerProfile(TEST_PLAYER_2, 2000, 2);
        PlayerProfile profile3 = new PlayerProfile(TEST_PLAYER_3, 3000, 3);
        
        saveManager.saveProfile(profile1);
        saveManager.saveProfile(profile2);
        saveManager.saveProfile(profile3);
        
        List<PlayerProfile> allProfiles = saveManager.getAllProfiles();
        assertEquals(3, allProfiles.size(), "Should have 3 profiles");
    }
    
    @Test
    @Order(5)
    @DisplayName("Test Update Existing Profile with Higher Score")
    void testUpdateProfileHigherScore() {
        PlayerProfile profile1 = new PlayerProfile(TEST_PLAYER_1, 1000, 1);
        saveManager.saveProfile(profile1);
        
        PlayerProfile profile2 = new PlayerProfile(TEST_PLAYER_1, 2000, 2);
        saveManager.saveProfile(profile2);
        
        List<PlayerProfile> allProfiles = saveManager.getAllProfiles();
        assertEquals(1, allProfiles.size(), "Should still have only 1 profile");
        
        Optional<PlayerProfile> updated = saveManager.findProfile(TEST_PLAYER_1);
        assertTrue(updated.isPresent());
        assertEquals(2000, updated.get().getHighScore(), "High score should be updated");
        assertEquals(2, updated.get().getCurrentLevel(), "Level should be updated");
    }
    
    @Test
    @Order(6)
    @DisplayName("Test Update Existing Profile with Lower Score")
    void testUpdateProfileLowerScore() {
        PlayerProfile profile1 = new PlayerProfile(TEST_PLAYER_1, 2000, 2);
        saveManager.saveProfile(profile1);
        
        PlayerProfile profile2 = new PlayerProfile(TEST_PLAYER_1, 1000, 3);
        saveManager.saveProfile(profile2);
        
        Optional<PlayerProfile> updated = saveManager.findProfile(TEST_PLAYER_1);
        assertTrue(updated.isPresent());
        assertEquals(2000, updated.get().getHighScore(), "High score should NOT be updated");
        assertEquals(3, updated.get().getCurrentLevel(), "Level should be updated");
    }
    
    @Test
    @Order(7)
    @DisplayName("Test Update High Score Method")
    void testUpdateHighScoreMethod() {
        // Test with new player
        boolean updated1 = saveManager.updateHighScore(TEST_PLAYER_1, 1000, 1);
        assertTrue(updated1, "Should create new profile");
        
        // Test with higher score
        boolean updated2 = saveManager.updateHighScore(TEST_PLAYER_1, 2000, 2);
        assertTrue(updated2, "Should update high score");
        
        // Test with lower score
        boolean updated3 = saveManager.updateHighScore(TEST_PLAYER_1, 1500, 3);
        assertFalse(updated3, "Should NOT update high score");
        
        Optional<PlayerProfile> profile = saveManager.findProfile(TEST_PLAYER_1);
        assertTrue(profile.isPresent());
        assertEquals(2000, profile.get().getHighScore());
    }
    
    @Test
    @Order(8)
    @DisplayName("Test Find Profile Case Insensitive")
    void testFindProfileCaseInsensitive() {
        PlayerProfile profile = new PlayerProfile("TestPlayer", 1000, 1);
        saveManager.saveProfile(profile);
        
        Optional<PlayerProfile> found1 = saveManager.findProfile("TestPlayer");
        Optional<PlayerProfile> found2 = saveManager.findProfile("testplayer");
        Optional<PlayerProfile> found3 = saveManager.findProfile("TESTPLAYER");
        
        assertTrue(found1.isPresent(), "Should find with exact case");
        assertTrue(found2.isPresent(), "Should find with lowercase");
        assertTrue(found3.isPresent(), "Should find with uppercase");
    }
    
    @Test
    @Order(9)
    @DisplayName("Test Get Top Profiles")
    void testGetTopProfiles() {
        saveManager.saveProfile(new PlayerProfile("Player1", 1000, 1));
        saveManager.saveProfile(new PlayerProfile("Player2", 5000, 3));
        saveManager.saveProfile(new PlayerProfile("Player3", 3000, 2));
        saveManager.saveProfile(new PlayerProfile("Player4", 2000, 1));
        saveManager.saveProfile(new PlayerProfile("Player5", 4000, 2));
        
        List<PlayerProfile> top3 = saveManager.getTopProfiles(3);
        
        assertEquals(3, top3.size(), "Should return top 3 profiles");
        assertEquals(5000, top3.get(0).getHighScore(), "First should have highest score");
        assertEquals(4000, top3.get(1).getHighScore(), "Second should have second highest");
        assertEquals(3000, top3.get(2).getHighScore(), "Third should have third highest");
    }
    
    @Test
    @Order(10)
    @DisplayName("Test Delete Profile")
    void testDeleteProfile() {
        PlayerProfile profile = new PlayerProfile(TEST_PLAYER_1, 1000, 1);
        saveManager.saveProfile(profile);
        
        assertTrue(saveManager.findProfile(TEST_PLAYER_1).isPresent(), "Profile should exist");
        
        boolean deleted = saveManager.deleteProfile(TEST_PLAYER_1);
        assertTrue(deleted, "Delete operation should succeed");
        
        assertFalse(saveManager.findProfile(TEST_PLAYER_1).isPresent(), "Profile should not exist");
    }
    
    @Test
    @Order(11)
    @DisplayName("Test Delete Non-Existent Profile")
    void testDeleteNonExistentProfile() {
        boolean deleted = saveManager.deleteProfile("NonExistentPlayer");
        assertFalse(deleted, "Delete operation should fail for non-existent profile");
    }
    
    @Test
    @Order(12)
    @DisplayName("Test Save Null Profile")
    void testSaveNullProfile() {
        boolean result = saveManager.saveProfile(null);
        assertFalse(result, "Should not save null profile");
    }
    
    @Test
    @Order(13)
    @DisplayName("Test Save Profile with Empty Name")
    void testSaveProfileWithEmptyName() {
        PlayerProfile profile = new PlayerProfile("", 1000, 1);
        boolean result = saveManager.saveProfile(profile);
        assertFalse(result, "Should not save profile with empty name");
    }
    
    @Test
    @Order(14)
    @DisplayName("Test Clear All Profiles")
    void testClearAllProfiles() {
        saveManager.saveProfile(new PlayerProfile(TEST_PLAYER_1, 1000, 1));
        saveManager.saveProfile(new PlayerProfile(TEST_PLAYER_2, 2000, 2));
        
        assertEquals(2, saveManager.getAllProfiles().size(), "Should have 2 profiles");
        
        boolean cleared = saveManager.clearAllProfiles();
        assertTrue(cleared, "Clear operation should succeed");
        
        assertEquals(0, saveManager.getAllProfiles().size(), "Should have 0 profiles");
    }
    
    @Test
    @Order(15)
    @DisplayName("Test Persistence - Save and Reload")
    void testPersistence() {
        // Save profiles
        saveManager.saveProfile(new PlayerProfile(TEST_PLAYER_1, 1000, 1));
        saveManager.saveProfile(new PlayerProfile(TEST_PLAYER_2, 2000, 2));
        
        // Reload from disk
        saveManager.reloadProfiles();
        
        List<PlayerProfile> profiles = saveManager.getAllProfiles();
        assertEquals(2, profiles.size(), "Should load 2 profiles from disk");
        
        assertTrue(saveManager.findProfile(TEST_PLAYER_1).isPresent(), "Player 1 should exist");
        assertTrue(saveManager.findProfile(TEST_PLAYER_2).isPresent(), "Player 2 should exist");
    }
    
    @Test
    @Order(16)
    @DisplayName("Test Get All Profiles Returns Copy")
    void testGetAllProfilesReturnsCopy() {
        saveManager.saveProfile(new PlayerProfile(TEST_PLAYER_1, 1000, 1));
        
        List<PlayerProfile> profiles1 = saveManager.getAllProfiles();
        List<PlayerProfile> profiles2 = saveManager.getAllProfiles();
        
        assertNotSame(profiles1, profiles2, "Should return different list instances");
    }
    
    @Test
    @Order(17)
    @DisplayName("Test Save File Path")
    void testSaveFilePath() {
        Path savePath = saveManager.getSaveFilePath();
        
        assertNotNull(savePath, "Save path should not be null");
        assertTrue(savePath.toString().contains(".arkanoid"), "Path should contain .arkanoid directory");
        assertTrue(savePath.toString().endsWith("player_profiles.dat"), "Path should end with player_profiles.dat");
    }
}
