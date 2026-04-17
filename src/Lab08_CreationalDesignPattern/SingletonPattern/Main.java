package Lab08_CreationalDesignPattern.SingletonPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// LOGGER (DOUBLE-CHECKED LOCKING)

class AppLogger {

    private static volatile AppLogger instance;

    private String logFile;
    private List<String> buffer;
    private int logCount;

    // Private constructor
    private AppLogger(String logFile) {
        this.logFile = logFile;
        this.buffer = new ArrayList<>();
        this.logCount = 0;
        System.out.println("[LOGGER] Opened: " + logFile);
    }

    // Global access point
    public static AppLogger getInstance(String logFile) {
        if (instance == null) {
            synchronized (AppLogger.class) {
                if (instance == null) {
                    instance = new AppLogger(logFile);
                }
            }
        }
        return instance;
    }

    public void log(String level, String message) {
        logCount++;
        String entry = String.format("[%s #%d] %s",
                level, logCount, message);
        buffer.add(entry);
        System.out.println(entry);
    }

    public void info(String msg)  { log("INFO", msg); }
    public void warn(String msg)  { log("WARN", msg); }
    public void error(String msg) { log("ERROR", msg); }

    public int getLogCount() { return logCount; }

    public List<String> getBuffer() {
        return new ArrayList<>(buffer);
    }
}


// ======================= CONFIG (ENUM SINGLETON) =======================

enum AppConfig {

    INSTANCE;

    private Map<String, String> settings;

    // Constructor runs once
    AppConfig() {
        settings = new HashMap<>();
        settings.put("db.host", "localhost");
        settings.put("db.port", "5432");
        settings.put("app.name", "GradePortal");
        settings.put("app.debug", "false");

        System.out.println("[CONFIG] Loaded "
                + settings.size() + " settings");
    }

    public String get(String key) {
        return settings.getOrDefault(key, "");
    }

    public void set(String key, String value) {
        settings.put(key, value);
    }

    public int size() {
        return settings.size();
    }
}


// MAIN (TESTING SINGLETON)

public class Main {

    public static void main(String[] args) {

        // Module A
        AppLogger loggerA = AppLogger.getInstance("app.log");
        AppConfig configA = AppConfig.INSTANCE;

        loggerA.info("Module A started");
        loggerA.info("DB: " + configA.get("db.host"));
        configA.set("app.debug", "true");

        // Module B (same instances reused)
        AppLogger loggerB = AppLogger.getInstance("app.log");
        AppConfig configB = AppConfig.INSTANCE;

        loggerB.info("Module B started");
        loggerB.info("Debug: " + configB.get("app.debug"));

        // Verification
        System.out.println("\nTotal log count: "
                + loggerA.getLogCount()); // should be 4

        System.out.println("Debug mode: "
                + configB.get("app.debug")); // should be true
    }
}