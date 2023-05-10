package com.gymgate;

import java.io.IOException;
import java.util.logging.*;

public class DbgLogger {
    private static FileHandler handler;

    public static Logger getLogger() {
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        try {
            logger.setUseParentHandlers(false);
            if (handler == null) {
                handler = new FileHandler("dbg_log.txt");
                handler.setFormatter(new SimpleFormatter());
                logger.setLevel(Level.INFO);
                logger.addHandler(handler);
            }
            
            return logger;
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "Failed to start logging");
            return null;
        }
    }

    public void info(String message){
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        logger.log(Level.INFO, message);
    }

    public void warning(String message){
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        logger.log(Level.WARNING, message);
    }

    public void severe(String message){
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        logger.log(Level.SEVERE, message);
    }

    public void fine(String message){
        Logger logger = Logger.getLogger(DbgLogger.class.getName());
        logger.log(Level.FINE, message);
    }
}
