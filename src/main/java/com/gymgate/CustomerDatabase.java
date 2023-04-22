package com.gymgate;

import java.sql.Statement;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CustomerDatabase {
    
    private Connection connection = null;
    private static CustomerDatabase dbinstance = null;


    public static synchronized CustomerDatabase getInstance() { 
        if (dbinstance == null) {
            dbinstance = new CustomerDatabase("CustomerDB");
        }
        return dbinstance;
    }

    public CustomerDatabase(String dbName) {

        try {
            open(dbName);
        } catch (SQLException e) {
            System.out.println("Error opening database: " + e.getMessage());
        }
    }

    public void open(String dbName) throws SQLException {

        File file = new File(dbName);
        boolean Exists = file.exists();

        String connectionString = "jdbc:sqlite:" + dbName;

        connection = DriverManager.getConnection(connectionString);

        if (!Exists) {
            initializeDatabase(connection);
        }
    }

    private boolean initializeDatabase(Connection connection) throws SQLException {
        
        if (connection != null) {

            String createDB = "CREATE TABLE IF NOT EXISTS Customers (customer_id INTEGER PRIMARY KEY AUTOINCREMENT, first_name VARCHAR(40) NOT NULL, last_name VARCHAR(40) NOT NULL, phone_number VARCHAR(20) NOT NULL, email VARCHAR(50), rfid INTEGER DEFAULT NULL, membership_type VARCHAR(30), membership_start DATE, membership_end DATE, address VARCHAR(50), additional_information VARCHAR(300)";
            
            Statement createStatement = connection.createStatement();
            createStatement.executeUpdate(createDB);
            createStatement.close();

            return true;
        }
        System.out.println("Database creation failed.");
        return false;
    }
}
