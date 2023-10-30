package com.example.demo.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {

    private LoggerUtil() {
        // Private constructor to prevent instantiation
    }

    public static void debug(String className, String message) {
        Logger logger = LoggerFactory.getLogger(className);
        logger.debug(message);
    }

    public static void info(String className, String message) {
        Logger logger = LoggerFactory.getLogger(className);
        logger.info(message);
    }

    public static void warn(String className, String message) {
        Logger logger = LoggerFactory.getLogger(className);
        logger.warn(message);
    }

    public static void error(String className, String message) {
        Logger logger = LoggerFactory.getLogger(className);
        logger.error(message);
    }

    public static void error(String className, String message, Throwable throwable) {
        Logger logger = LoggerFactory.getLogger(className);
        logger.error(message, throwable);
    }
}
