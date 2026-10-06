package org.arkanoid.level;

import java.util.Map;

/**
 * The Level class represents a single level in the Arkanoid game.
 * It contains information about the level's layout, brick properties, and rendering details.
 */
public class Level {
    private int id;
    private double dropRate;
    private String[] layout;
    private Map<String, BrickInfo> palette;
    private Cell cell;
    private Offset offset;

    /**
     * Constructor for the Level class with specified parameters.
     *
     * @param id       The unique identifier for the level.
     * @param dropRate The probability rate for power-up drops.
     * @param layout   The level layout as an array of strings.
     * @param palette  The map of brick identifiers to their properties.
     * @param cell     The cell dimensions and padding for the level grid.
     * @param offset   The offset coordinates for rendering the level.
     */
    public Level(int id, double dropRate, String[] layout, Map<String, BrickInfo> palette, Cell cell, Offset offset) {
        this.id = id;
        this.dropRate = dropRate;
        this.layout = layout;
        this.palette = palette;
        this.cell = cell;
        this.offset = offset;
    }

    public int getId() {
        return id;
    }

    public double getDropRate() {
        return dropRate;
    }

    public String[] getLayout() {
        return layout;
    }

    public Map<String, BrickInfo> getPalette() {
        return palette;
    }

    public Cell getCell() {
        return cell;
    }

    public Offset getOffset() {
        return offset;
    }

    /**
     * The BrickInfo class represents the properties of a brick in the level.
     * It includes the brick type, hit points, and identifier.
     */
    public static class BrickInfo {
        private String type;
        private int hp;
        String id;

        /**
         * Constructor for the BrickInfo class with specified parameters.
         * @param type The type of the brick.
         * @param hp   The hit points of the brick.
         * @param id   The unique identifier for the brick.
         */
        public BrickInfo(String type, int hp, String id) {
            this.type = type;
            this.hp = hp;
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public int getHp() {
            return hp;
        }

        public String getId() {
            return id;
        }
    }

    /**
     * The Cell class defines the dimensions and padding of a grid cell in the level.
     */
    public static class Cell {
        private double width;
        private double height;
        private double padding;

        /**
         * Constructor for the Cell class with specified parameters.
         * @param width   The width of the cell.
         * @param height  The height of the cell.
         * @param padding The padding between cells.
         */
        public Cell(double width, double height, double padding) {
            this.width = width;
            this.height = height;
            this.padding = padding;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        public double getPadding() {
            return padding;
        }
    }

    /**
     * The Offset class defines the x and y coordinates for rendering the level on the canvas.
     */
    public static class Offset {
        private double x;
        private double y;

        /**
         * Constructor for the Offset class with specified parameters.
         * @param x The x-coordinate offset.
         * @param y The y-coordinate offset.
         */
        public Offset(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}