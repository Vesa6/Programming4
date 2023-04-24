package com.gymgate;

import java.sql.Statement;
import java.time.LocalDate;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerDatabase {
    
    private Connection connection = null;
    private static CustomerDatabase dbinstance = null;


    public static synchronized CustomerDatabase getInstance() { 
        if (dbinstance == null) {
            dbinstance = new CustomerDatabase();
        }
        return dbinstance;
    }

    public CustomerDatabase() {

        try {
            open("CustomerDB.db");
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

            String createDB = "CREATE TABLE Customers(customer_id INTEGER PRIMARY KEY AUTOINCREMENT, first_name VARCHAR(40) NOT NULL, last_name VARCHAR(40) NOT NULL, phone_number VARCHAR(20) NOT NULL, email VARCHAR(50), membership_type VARCHAR(30), membership_start VARCHAR(100), membership_end VARCHAR(100), address VARCHAR(50), visits INTEGER, additional_information VARCHAR(300))";
            Statement createStatement = connection.createStatement();
            createStatement.executeUpdate(createDB);
            createStatement.close();

            return true;
        }
        System.out.println("Database creation failed.");
        return false;
    }

    /*
     * This method adds a customer to the database, if the monthly radiobutton is selected.
     * End date is calculated with the formula: Current date + amount of months. Ex. 1.4.2023 + 2 months = 1.6.2023.
     */
    public void addCustomerMonthly(String firstName, String lastName, String phoneNumber, String email, String membershipType, String membership_start, String membership_end, String homeAddress, int visits, String notes) {
        try {
            String insertCustomer = "INSERT INTO Customers(first_name, last_name, phone_number, email, membership_type, membership_start, membership_end, address, visits, additional_information) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
            PreparedStatement preparedStatement = connection.prepareStatement(insertCustomer);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, membershipType);
            preparedStatement.setString(6, membership_start);
            preparedStatement.setString(7, membership_end);
            preparedStatement.setString(8, homeAddress);
            preparedStatement.setString(9, notes);
    
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Error adding customer: " + e.getMessage());
        }
    }

    /*
     * This method adds the customer to the database if the Kertakäynti radiobutton is selected.
     */

    public void addCustomerVisits(String firstName, String lastName, String phoneNumber, String email, String membershipType, String homeAddress, int visits, String notes) {

        try {
            String insertCustomer = "INSERT INTO Customers(first_name, last_name, phone_number, email, membership_type, address, visits, additional_information) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
            PreparedStatement preparedStatement = connection.prepareStatement(insertCustomer);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, membershipType);
            preparedStatement.setString(6, homeAddress);
            preparedStatement.setInt(7, visits);
            preparedStatement.setString(8, notes);
    
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Error adding customer: " + e.getMessage());
        }
    }
    
}
