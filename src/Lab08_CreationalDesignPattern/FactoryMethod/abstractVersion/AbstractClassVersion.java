package Lab08_CreationalDesignPattern.FactoryMethod.abstractVersion;

import java.util.ArrayList;
import java.util.List;


//  ABSTRACT CREATOR  (owns the workflow)
abstract class LevelFactory {

    // Factory Methods (subclasses MUST override)
    abstract String       createTerrain();
    abstract List<String> spawnEnemies(int difficulty);
    abstract List<String> placePowerUps();

    //Template Method (shared workflow subclasses INHERIT this)
    final void loadLevel(int difficulty) {

        // Step 1: Validate same for ALL
        if (difficulty < 1 || difficulty > 10)
            throw new IllegalArgumentException("Difficulty must be 1-10");
        System.out.println("=== Loading level (difficulty: "
                + difficulty + ") ===");

        // Step 2: Build terrain delegates to subclass
        String terrain = createTerrain();
        System.out.println("[TERRAIN] Generating: " + terrain);

        // Step 3: Spawn enemies delegates to subclass
        List<String> enemies = spawnEnemies(difficulty);
        System.out.println("[ENEMIES] Spawned "
                + enemies.size() + ": " + enemies);

        // Step 4: Place power-ups delegates to subclass
        List<String> powerUps = placePowerUps();
        System.out.println("[POWER-UPS] Placed: " + powerUps);

        // Step 5: Start music same for ALL
        System.out.println("[MUSIC] Playing ambient track");

        // Step 6: Countdown same for ALL
        System.out.println("[START] 3... 2... 1... GO!");
        System.out.println("Level loaded with " + enemies.size()
                + " enemies and " + powerUps.size() + " power-ups\n");
    }
}


//  CONCRETE CREATORS

class ForestLevelFactory extends LevelFactory {
    @Override String createTerrain() {
        return "Trees, rivers, muddy paths";
    }
    @Override List<String> spawnEnemies(int difficulty) {
        List<String> e = new ArrayList<>();
        for (int i = 0; i < difficulty * 3; i++)
            e.add(i % 2 == 0 ? "Wolf" : "Bear");
        return e;
    }
    @Override List<String> placePowerUps() {
        return List.of("Healing Herb", "Camouflage Cloak");
    }
}

class DesertLevelFactory extends LevelFactory {
    @Override String createTerrain() {
        return "Sand dunes, oasis, quicksand";
    }
    @Override List<String> spawnEnemies(int difficulty) {
        List<String> e = new ArrayList<>();
        for (int i = 0; i < difficulty * 3; i++)
            e.add(i % 3 == 0 ? "Scorpion" : "Bandit");
        return e;
    }
    @Override List<String> placePowerUps() {
        return List.of("Water Flask", "Sand Shield");
    }
}

class OceanLevelFactory extends LevelFactory {
    @Override String createTerrain() {
        return "Coral reefs, underwater caves";
    }
    @Override List<String> spawnEnemies(int difficulty) {
        List<String> e = new ArrayList<>();
        for (int i = 0; i < difficulty * 3; i++)
            e.add(i % 2 == 0 ? "Shark" : "Jellyfish");
        return e;
    }
    @Override List<String> placePowerUps() {
        return List.of("Oxygen Tank", "Trident");
    }
}


//  NEW LEVEL added WITHOUT touching anything above

class SpaceLevelFactory extends LevelFactory {
    @Override String createTerrain() {
        return "Asteroids, space station, zero gravity";
    }
    @Override List<String> spawnEnemies(int difficulty) {
        List<String> e = new ArrayList<>();
        for (int i = 0; i < difficulty * 3; i++)
            e.add(i % 2 == 0 ? "Alien" : "Drone");
        return e;
    }
    @Override List<String> placePowerUps() {
        return List.of("Jetpack", "Laser Blaster");
    }
}


// MAIN
public class AbstractClassVersion {
    public static void main(String[] args) {

        // Client works with the abstract type only
        LevelFactory[] factories = {
                new ForestLevelFactory(),
                new DesertLevelFactory(),
                new OceanLevelFactory(),
                new SpaceLevelFactory()   // NEW zero changes above
        };

        int[] difficulties = {3, 7, 5, 4};

        for (int i = 0; i < factories.length; i++)
            factories[i].loadLevel(difficulties[i]);
    }
}