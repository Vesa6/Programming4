package com.gymgate;

import java.sql.Statement;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.security.SecureRandom;
import java.sql.ResultSet;
import org.apache.commons.codec.digest.Crypt;

public class CustomerDatabase {

    private Connection connection = null;
    private static CustomerDatabase dbinstance = null;
    private SecureRandom secureRandom = new SecureRandom();

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
            String createUserDB = "CREATE TABLE Users(user_id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR(40) NOT NULL, password VARCHAR(40) NOT NULL)";
            String createEventDB = "CREATE TABLE Events(event_id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, customer_id INTEGER)";

            Statement createStatement = connection.createStatement();
            createStatement.executeUpdate(createDB);
            createStatement.close();

            Statement createUsers = connection.createStatement();
            createUsers.executeUpdate(createUserDB);
            createUsers.close();

            Statement createEvents = connection.createStatement();
            createEvents.executeUpdate(createEventDB);
            createEvents.close();

            // by default creating admin user while initializing the database
            registerUser("admin", "admin");

            // Below class is to create a test database with 1000 random customers to test
            // the functionalities of it
            new DatabaseFiller(1000);

            return true;
        }
        System.out.println("Database creation failed.");
        return false;
    }

    /*
     * This method adds a customer to the database, if the monthly radiobutton is
     * selected.
     * End date is calculated with the formula: Current date + amount of months. Ex.
     * 1.4.2023 + 2 months = 1.6.2023.
     */
    public void addCustomerMonthly(String firstName, String lastName, String phoneNumber, String email,
            String membershipType, String membership_start, String membership_end, String homeAddress, int visits,
            String notes) {
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
     * This method adds the customer to the database if the Kertakäynti radiobutton
     * is selected.
     */

    public void addCustomerVisits(String firstName, String lastName, String phoneNumber, String email,
            String membershipType, String homeAddress, int visits, String notes) {

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

    public void registerUser(String username, String password) {
        /*
         * Registers the username and password (crypted) given as parameters to database
         * Before this it should be checked (with userExists) that username is unique
         * 
         */
        try {
            byte bytes[] = new byte[13];
            secureRandom.nextBytes(bytes);
            String saltBytes = new String(Base64.getEncoder().encode(bytes));
            String salt = "$6$" + saltBytes;
            String hashedPassword = Crypt.crypt(password, salt);
            String userSet = "INSERT INTO Users(username, password) VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(userSet);
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }

    public boolean userExists(String username) throws SQLException {
        /*
         * Used to check if the username is available before attempting to register it
         * Returns true if user is already registered
         */

        String checkQuery = "SELECT username FROM Users WHERE username = ?";
        PreparedStatement ps = connection.prepareStatement(checkQuery);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        String toCompare = "";
        if (rs.next()) {
            toCompare = rs.getString("username");
        }
        ps.close();
        if (username.equals(toCompare)) {
            return true;
        }
        return false;
    }

    public boolean checkCredentials(String username, String password) {
        /*
         * Checks if the credentials user provided are valid. Returns false if not.
         */

        String userCredentials = "SELECT username, password FROM Users WHERE username = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(userCredentials);
            ps.setString(1, username);

            String usernameCompare = null;
            String passwordCompare = null;
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usernameCompare = rs.getString("username");
                passwordCompare = rs.getString("password");
            }
            if (usernameCompare == null) {
                System.out.println("Nonexisting user");
                return false;

            }

            if (username.equals(usernameCompare) && passwordCompare.equals(Crypt.crypt(password, passwordCompare))) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get user info from database");

        }
        return false;

    }

    public ResultSet getCustomers() {

        // Get all customers from DB
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM Customers");
            return results;
        } catch (SQLException e) {
            System.out.println("Error getting customers: " + e.getMessage());
            return null;
        }
    }

    public ResultSet getCustomerById(int id) {
        // Get customer by id
        try {
            String getCustomer = "SELECT * FROM Customers WHERE customer_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(getCustomer);
            preparedStatement.setInt(1, id);
            ResultSet results = preparedStatement.executeQuery();
            return results;
        } catch (SQLException e) {
            System.out.println("Error getting customer: " + e.getMessage());
            return null;
        }
    }

    public void updateCustomer(int customer_id, String first_name, String last_name, String address,
            String phone_number, String email, String additional_information, String membership_end, int visits) {

        String updateCustomer = "UPDATE Customers SET first_name = ?, last_name = ?, address = ?, phone_number = ?, email = ?, additional_information = ?, membership_end = ?, visits = ? WHERE customer_id = ?";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(updateCustomer);
            preparedStatement.setString(1, first_name);
            preparedStatement.setString(2, last_name);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, phone_number);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, additional_information);
            preparedStatement.setString(7, membership_end);
            preparedStatement.setInt(8, visits);
            preparedStatement.setInt(9, customer_id);
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
        }

    }

    public ResultSet getEvents(int limit) {

        try {
            Statement statement = connection.createStatement();
            String resultQuery = "SELECT e.date, c.first_name || ' ' || c.last_name AS name, e.customer_id FROM Events e INNER JOIN Customers c ON e.customer_id = c.customer_id ORDER BY e.event_id DESC LIMIT "
                    + limit;
            ResultSet results = statement.executeQuery(resultQuery);
            return results;

        } catch (SQLException e) {
            System.out.println("Error getting events: " + e.getMessage());
            return null;
        }
    }

    public void saveEvent(int customerid, String date) {
        // This function is to save the every customer visit to DB

        // Gonna implement the decrypt stuff somewhere else so this can be used to fill
        // database with test data
        String eventSet = "INSERT INTO Events(date, customer_id) VALUES (?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(eventSet);
            ps.setString(1, date);
            ps.setInt(2, customerid);

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error storing an event: " + e.getMessage());
        }

    }

}
