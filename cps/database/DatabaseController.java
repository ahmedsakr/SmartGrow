package cps.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import cps.database.DatabaseInfo;
import cps.database.exceptions.SmartgrowDatabaseException;
import logging.SmartLog;

/**
 * DatabaseController provides an abstracted interface to the SmartGrow
 * Postgres database, enabling callers to swiftly communicate with the
 * database without a necessity to know its intricacies, such as setup.
 * 
 * @author Ahmed Sakr
 * @since October 30, 2019
 */
public class DatabaseController {

    // Initialize the logger instance for this instance.
    private static SmartLog logger = new SmartLog(DatabaseController.class.getName());

    // The database connection for this controller instance.
    private Connection dbConnection;

    /**
     * Initialize the database controller by establishing a connection to the
     * SmartGrow database.
     *
     * @throws SmartgrowDatabaseException
     */
    public DatabaseController() throws SmartgrowDatabaseException {

        // Establish connection with the Postgres database.
        this.connect();

        logger.debug("Established database connection");
    }

    /**
     * Query the database with the provided SQL prepared statement.
     *
     * @param sql The prepared statement
     * @return The result of the query
     */
    public ResultSet query(String sql) throws SmartgrowDatabaseException {
        try {

            // Execute the result-returning query.
            Statement statement = this.dbConnection.createStatement();
            return statement.executeQuery(sql);

        } catch (SQLException ex) {
            throw new SmartgrowDatabaseException(ex.getMessage());
        }
    }

    /**
     * Query the database with an INSERT, UPDATE, or DELETE (i.e., statements
     * that do not return a result).
     *
     * @param sql The prepared statement
     */
    public void update(String sql) throws SmartgrowDatabaseException {
        try {

            // Execute the no-result update query.
            Statement statement = this.dbConnection.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException ex) {
            throw new SmartgrowDatabaseException(ex.getMessage());
        }
    }

    /**
     * Attempt to establish a connection with the Smartgrow Postgres database
     * using the Database information found in DatabaseInfo.java
     *
     * @throws SmartgrowDatabaseException
     * @see {@link cps.database.DatabaseInfo}
     */
    private void connect() throws SmartgrowDatabaseException {

        // Attempt to retrieve the dynamic database user password from the environment.
        String user_password = System.getenv(DatabaseInfo.DATABASE_PASSWORD);
        if (user_password == null) {

            // The Smartgrow database user password variable is not set in the system.
            throw new SmartgrowDatabaseException("Database user environment variable not set");
        }

        // Specify the database user, password, and SSL requirement for the connection.
        Properties dbCredentials = new Properties();
        dbCredentials.setProperty("user", DatabaseInfo.DATABASE_USER);
        dbCredentials.setProperty("password", user_password);
        dbCredentials.setProperty("ssl", "false");

        try {
            this.dbConnection = DriverManager.getConnection(DatabaseInfo.DATABASE_URL, dbCredentials);
        } catch (SQLException ex) {

            // Issue with establishing the connection: perhaps the database settings are not permissive
            // to allow us to establish a connection? check pg_hba.conf.
            throw new SmartgrowDatabaseException(ex.getMessage());
        }
    }
}