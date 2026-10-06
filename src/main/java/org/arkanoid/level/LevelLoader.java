package org.arkanoid.level;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.arkanoid.entity.Brick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The LevelLoader class is responsible for loading level data from a JSON file (levels.json)
 * and creating a list of Level objects for the Arkanoid game. It provides fallback behavior
 * by loading a default level if the JSON file cannot be read or parsed.
 */
public class LevelLoader {

    /**
     * Loads levels from the levels.json resource file.
     * If the file is not found, invalid, or an error occurs during loading, a default level is returned.
     *
     * @return A list of Level objects representing the game levels.
     */
    public static List<Level> loadLevels() {
        // Initialize an empty list to store levels
        List<Level> levels = new ArrayList<>();
        Gson gson = new Gson();

        try {
            // Load the levels.json file from resources
            InputStream inputStream = LevelLoader.class.getClassLoader().getResourceAsStream("org/levels.json");
            if (inputStream == null) {
                throw new IOException("File levels.json not found in resources");
            }

            // Read the JSON file content
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();

            // Parse JSON content into a list of Level objects
            levels = gson.fromJson(content.toString(), new TypeToken<List<Level>>(){}.getType());
            if (levels == null || levels.isEmpty()) {
                throw new IOException("JSON data is empty or invalid");
            }
        } catch (IOException e) {
            // Handle IO-related errors (e.g., file not found)
            System.err.println("Error reading levels.json: " + e.getMessage());
            e.printStackTrace();
        } catch (com.google.gson.JsonSyntaxException e) {
            // Handle JSON parsing errors
            System.err.println("Error parsing JSON in levels.json: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Handle unexpected errors
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }

        // If no levels were loaded, create a default level as a fallback
        if (levels.isEmpty()) {
            System.err.println("Loading default level due to failure in reading levels.json");

            // Define a simple default level layout
            String[] layout = new String[]{"..NNNNNNNN..",
                                           "...NN..NN...",
                                           "..NNNNNNNN.."};

            // Define a default palette with a single brick type
            Map<String, Level.BrickInfo> palette = new HashMap<>();
            palette.put("N", new Level.BrickInfo(Brick.NB, 1, "NB"));

            // Define default cell dimensions and padding
            Level.Cell cell = new Level.Cell(60, 30, 4);

            // Define default offset for rendering
            Level.Offset offset = new Level.Offset(50, 60);

            // Add the default level to the list
            levels.add(new Level(1, 0.2, layout, palette, cell, offset));
        }
        return levels;
    }
}
