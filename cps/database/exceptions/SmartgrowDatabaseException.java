package cps.database.exceptions;

/**
 * SmartgrowDatabaseException is an exception classification encapsulating
 * all SQLExceptions and database connection errors.
 * 
 * @author Ahmed Sakr
 * @since October 30, 2019
 */
public class SmartgrowDatabaseException extends Exception {
    
    /**
     * Initialize the exception.
     * 
     * @param message The error message.
     */
    public SmartgrowDatabaseException(String message) {
        super(message);
    }
}