package Lab08_CreationalDesignPattern.FactoryMethod.interfaceVersion;

import java.util.*;

// PUBLIC CLASS (as instructed)
public class InterfaceVersion {

    public static void main(String[] args) {

        LevelFactory level;

        level = new ForestLevelFactory();
        level.loadLevel(3);

        level = new DesertLevelFactory();
        level.loadLevel(7);

        level = new OceanLevelFactory();
        level.loadLevel(5);

        // NEW LEVEL (no changes anywhere else)
        level = new SpaceLevelFactory();
        level.loadLevel(6);
    }
}


//PRODUCT (Interface)

interface LevelData {
    String createTerrain();
    List<String> spawnEnemies(int difficulty);
    List<String> placePowerUps();
}


// CREATOR (ABSTRACT CLASS)

abstract class LevelFactory {

    // Factory Method (subclass will override)
    abstract LevelData createLevelData();

    // Template Method (workflow controlled here)
    public void loadLevel(int difficulty) {

        System.out.println("\n=== Loading Level (difficulty: " + difficulty + ") ===");

        // Step 1: Validate (shared)
        if (difficulty < 1 || difficulty > 10) {
            throw new IllegalArgumentException("Difficulty must be 1-10");
        }

        // Creation delegated
        LevelData data = createLevelData();

        // Step 2: Terrain
        String terrain = data.createTerrain();
        System.out.println("[TERRAIN] " + terrain);

        // Step 3: Enemies
        List<String> enemies = data.spawnEnemies(difficulty);
        System.out.println("[ENEMIES] " + enemies.size() + ": " + enemies);

        // Step 4: PowerUps
        List<String> powerUps = data.placePowerUps();
        System.out.println("[POWER-UPS] " + powerUps);

        // Step 5: Music (shared)
        System.out.println("[MUSIC] Playing ambient track");

        // Step 6: Countdown (shared)
        System.out.println("[START] 3... 2... 1... GO!");

        System.out.println("Level loaded with "
                + enemies.size() + " enemies and "
                + powerUps.size() + " power-ups");
    }
}


// CONCRETE PRODUCTS

class ForestLevelData implements LevelData {

    public String createTerrain() {
        return "Trees, rivers, muddy paths";
    }

    public List<String> spawnEnemies(int difficulty) {
        List<String> enemies = new ArrayList<>();
        int count = difficulty * 3;

        for (int i = 0; i < count; i++) {
            enemies.add(i % 2 == 0 ? "Wolf" : "Bear");
        }
        return enemies;
    }

    public List<String> placePowerUps() {
        return Arrays.asList("Healing Herb", "Camouflage Cloak");
    }
}


class DesertLevelData implements LevelData {

    public String createTerrain() {
        return "Sand dunes, oasis, quicksand";
    }

    public List<String> spawnEnemies(int difficulty) {
        List<String> enemies = new ArrayList<>();
        int count = difficulty * 3;

        for (int i = 0; i < count; i++) {
            enemies.add(i % 3 == 0 ? "Scorpion" : "Bandit");
        }
        return enemies;
    }

    public List<String> placePowerUps() {
        return Arrays.asList("Water Flask", "Sand Shield");
    }
}


class OceanLevelData implements LevelData {

    public String createTerrain() {
        return "Coral reefs, underwater caves";
    }

    public List<String> spawnEnemies(int difficulty) {
        List<String> enemies = new ArrayList<>();
        int count = difficulty * 3;

        for (int i = 0; i < count; i++) {
            enemies.add(i % 2 == 0 ? "Shark" : "Jellyfish");
        }
        return enemies;
    }

    public List<String> placePowerUps() {
        return Arrays.asList("Oxygen Tank", "Trident");
    }
}


// NEW LEVEL (NO CHANGES ANYWHERE ELSE)

class SpaceLevelData implements LevelData {

    public String createTerrain() {
        return "Asteroids, space station, zero gravity";
    }

    public List<String> spawnEnemies(int difficulty) {
        List<String> enemies = new ArrayList<>();
        int count = difficulty * 3;

        for (int i = 0; i < count; i++) {
            enemies.add(i % 2 == 0 ? "Alien" : "Drone");
        }
        return enemies;
    }

    public List<String> placePowerUps() {
        return Arrays.asList("Jetpack", "Laser Blaster");
    }
}


// CONCRETE FACTORIES

class ForestLevelFactory extends LevelFactory {
    LevelData createLevelData() {
        return new ForestLevelData();
    }
}

class DesertLevelFactory extends LevelFactory {
    LevelData createLevelData() {
        return new DesertLevelData();
    }
}

class OceanLevelFactory extends LevelFactory {
    LevelData createLevelData() {
        return new OceanLevelData();
    }
}

// NEW FACTORY (NO CHANGE IN EXISTING CODE)
class SpaceLevelFactory extends LevelFactory {
    LevelData createLevelData() {
        return new SpaceLevelData();
    }
}