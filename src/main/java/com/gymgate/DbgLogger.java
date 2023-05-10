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
import java.util.logging.*;
import org.json.JSONObject;

public class DbgLogger {
    private static FileHandler logHandler;
    private static String logFile = "dbg_log.txt";
    private static String configFile = "config.json";
    private static boolean displayDummyRFID;
    private static String logLevel;

    public static Logger getLogger() {
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
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        logger.log(Level.INFO, message);
    }

    public void warning(String message) {
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        logger.log(Level.WARNING, message);
    }

    public void severe(String message) {
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        logger.log(Level.SEVERE, message);
    }

    public void fine(String message) {
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        logger.log(Level.FINE, message);
    }

    private static void createDefaultConfig() {
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        if (!checkIfExists()) {
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
        } else {
            handleExistingFile();
        }

    }

    private static boolean checkIfExists() {
        File file = new File(configFile);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private static void handleExistingFile() {
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(configFile));
            String filecontent = null;

            while ((filecontent = br.readLine()) != null) {
                sb.append(filecontent);
            }
            br.close();
            JSONObject json = new JSONObject(sb.toString());
            setLogLevel(json.getString("logging_level"));
            setDisplayDummyRFID(json.getBoolean("display_dummy_rfid"));
        } catch (IOException e) {
            logger.warning("Problems reading your configuration file");
        }
    }

    public static void generateCrashLog() {
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        LocalDateTime dt = LocalDateTime.now();
        String date = dt.format(format);
        String filename = "crash_log_" + date + ".txt";
        Path source = Paths.get("dbg_log.txt");
        Path copy = Paths.get(filename);
        try{
            new File(copy.toString());
            Files.copy(source,copy);
            logger.info("Crashed? Please contact customer support.");
        }catch(IOException ioe){
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
