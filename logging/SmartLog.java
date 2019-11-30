package logging;

import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * SmartLog is a lightweight logging library implemented for the purposes of
 * the SmartGrow System. An existing logging library was initially adopted (log4j2)
 * but it had issues with running successfully on android.
 * 
 * @author Ahmed Sakr
 * @since November 14, 2019
 */
public class SmartLog {

    // The class that we are logging for as their agent
    private String className;

    // The log format for all messages
    private static String LOG_FORMAT = "%s [%s, %s] %s: %s\n";

    // Supported logging levels by the SmartLog library
    private static enum LoggingLevel {
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        FATAL;
    };

    // The minimum logging level that logs must have to appear.
    private LoggingLevel CURRENT_LOGGING_LEVEL = SmartLog.LoggingLevel.INFO;

    /**
     * Initialize the SmartLog object with the class that we are logging for.
     * 
     * @param class The class we are logging for
     */
    public SmartLog(String className) {
        this.className = className;
    }

    /*
     * Retrieve a string representation for the logging level.
     */
    private static String getLoggingLevelString(LoggingLevel level) {
        switch (level) {
            case FATAL:
                return "FATAL";
            case WARNING:
                return "WARNING";
            case ERROR:
                return "ERROR";
            case INFO:
                return "INFO";
            case DEBUG:
                return "DEBUG";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Log a fatal message to the console.
     *
     * @param message The fatal message
     */
    public void fatal(String message) {
        this.log(LoggingLevel.FATAL, message);
    }

    /**
     * Log an error message to the console.
     *
     * @param message The error message
     */
    public void error(String message) {
        this.log(LoggingLevel.ERROR, message);
    }

    /**
     * Log a warning message to the console.
     *
     * @param message The warning message
     */
    public void warn(String message) {
        this.log(LoggingLevel.WARNING, message);
    }

    /**
     * Log an informational message to the system.
     *
     * @param message The information messgae
     */
    public void info(String message) {
        this.log(LoggingLevel.INFO, message);
    }

    /**
     * Log a debug message to the system.
     *
     * @param message The debug message
     */
    public void debug(String message) {
        this.log(LoggingLevel.DEBUG, message);
    }

    /*
     * Log a message to the console.
     */
    private void log(LoggingLevel level, String message) {

        // Do not log if the logging level is insufficient.
        if (CURRENT_LOGGING_LEVEL.ordinal() > level.ordinal()) {
            return;
        }

        System.out.printf(LOG_FORMAT,
            new SimpleDateFormat("HH:mm:ss.SSS").format(Calendar.getInstance().getTime()),
            Thread.currentThread().getName(),
            getLoggingLevelString(level),
            this.className, message);
    }
}