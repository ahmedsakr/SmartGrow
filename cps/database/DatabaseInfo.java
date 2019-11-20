package cps.database;

/**
 * DatabaseInfo stores all SmartGrow database credentials, including
 * database name, database tables, and database user.
 * 
 * @author Ahmed Sakr
 * @since October 30, 2019
 */
public class DatabaseInfo {

    // The name of the SmartGrow database that houses all data.
    public static final String DATABASE_NAME = "smartgrow";

    // The Postrges JDBC URL to connect to the database.
    public static final String DATABASE_URL = String.format("jdbc:postgresql:%s", DATABASE_NAME);

    // The database user qualfiied to access and store data in the database.
    public static final String DATABASE_USER = "smartgrow_client";

    // This is the environment variable name which will be used to dynamically retrieve the password.
    public static final String DATABASE_PASSWORD = "SMARTGROW_PASSWORD";

    // The table name for the plant sensors data
    public static final String DATABASE_SENSORS_TABLE = "plant_data";
}