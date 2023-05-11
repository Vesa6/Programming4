package com.gymgate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.*;

import org.json.JSONException;
import org.json.JSONObject;

public class DbgLogger {
    /*
     * This class handles the logging of this program.
     * In the configuration file you can also change the dummy RFID UI
     * to be visible on the startup of the program
     * (this is used to test the functions of the customer access control)
     */
    private static FileHandler logHandler;
    private static String logFile = "dbg_log.txt";
    private static String configFile = "config.json";
    private static boolean displayDummyRFID;
    private static String logLevel;

    public static Logger getLogger() {
        /*
         * Initiates the logger for the program.
         * The level of the logger will be determined by
         * configuration file (will be created if doesn't exist)
         * Old log file will be replaced with new one on every run of the program,
         * except the crash logs will be saved as "crashlog_dd-MM-yyyy_HH-mm-ss.txt"
         */
        createDefaultConfig();
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        try {
            logger.setUseParentHandlers(false);
            if (logHandler == null) {
                logHandler = new FileHandler(logFile);
                logHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(logHandler);
                String loggingLevel = getLogLevel().toLowerCase();
                if (loggingLevel.equals("fine")) {
                    logger.setLevel(Level.FINE);
                } else if (loggingLevel.equals("info")) {
                    logger.setLevel(Level.INFO);
                } else if (loggingLevel.equals("warning")) {
                    logger.setLevel(Level.WARNING);
                } else {
                    logger.setLevel(Level.SEVERE);
                }
                logger.info("logging level set to " + loggingLevel);
            }

            return logger;
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "Failed to start logging");
            return null;
        }
    }

    public void info(String message) {
        // Adds INFO level message to log
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        logger.log(Level.INFO, message);
    }

    public void warning(String message) {
        // Adds WARNING level message to log
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        logger.log(Level.WARNING, message);
    }

    public void severe(String message) {
        // Adds SEVERE level message to log
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        logger.log(Level.SEVERE, message);
    }

    public void fine(String message) {
        // Adds FINE level message to log (Used on finest debug traces
        // and will fill out log very fast)
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        logger.log(Level.FINE, message);
    }

    private static void createDefaultConfig() {
        /*
         * If default configuration file doesn't exist,
         * creates new one and sets logging level to severe and hides
         * dummy RFID UI
         */
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        if (checkIfExists()) {
            if (handleExistingFile()) {
                return;
            }
        }
        JSONObject json = new JSONObject();
        String jsonPath = configFile;
        json.put("logging_level", "severe");
        json.put("display_dummy_rfid", false);
        setLogLevel("severe");
        setDisplayDummyRFID(false);
        try {
            FileWriter fw = new FileWriter(jsonPath);
            fw.write(json.toString());
            fw.close();
        } catch (IOException e) {
            logger.warning("Failed to create default config file");
        }

    }

    private static boolean checkIfExists() {
        // Checks whether the configuration file exists or not
        File file = new File(configFile);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private static boolean handleExistingFile() {
        /*
         * Reads the configurations from existing config file
         */
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(configFile));
            String filecontent = null;

            while ((filecontent = br.readLine()) != null) {
                sb.append(filecontent);
            }
            br.close();
            try {
                JSONObject json = new JSONObject(sb.toString());
                if (json.has("logging_level") && json.has("display_dummy_rfid")) {
                    String[] levels = { "fine", "info", "warning", "severe" };
                    if ((Arrays.asList(levels).contains(json.getString("logging_level").toLowerCase()))
                            && (json.optBoolean("display_dummy_rfid"))) {
                        setLogLevel(json.getString("logging_level"));
                        setDisplayDummyRFID(json.getBoolean("display_dummy_rfid"));
                        return true;
                    }
                } else {
                    return false;
                }
            } catch (JSONException je) {
                logger.warning("Problems with JSON file. Generating new one");
            }
        } catch (IOException e) {
            logger.warning("Problems reading your configuration file");

        }
        return false;
    }

    public static void generateCrashLog() {
        /*
         * In case of SEVERE logging events (we expect the program to not to work
         * if these occur) this saves the log messages
         */
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        LocalDateTime dt = LocalDateTime.now();
        String date = dt.format(format);
        String filename = "crash_log_" + date + ".txt";
        Path source = Paths.get("dbg_log.txt");
        Path copy = Paths.get(filename);
        try {
            new File(copy.toString());
            Files.copy(source, copy);
            logger.info("Crashed? Please contact customer support.");
        } catch (IOException ioe) {
            System.out.println(ioe);
            logger.info("Failed to copy crash log: " + ioe);
        }

    }

    public static boolean isDisplayDummyRFID() {
        return displayDummyRFID;
    }

    public static void setDisplayDummyRFID(boolean paramDisplayDummyRFID) {
        displayDummyRFID = paramDisplayDummyRFID;
    }

    public static String getLogLevel() {
        return logLevel;
    }

    public static void setLogLevel(String paramLogLevel) {
        logLevel = paramLogLevel;
    }

}
