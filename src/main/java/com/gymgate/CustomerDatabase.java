package com.gymgate;

import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.security.SecureRandom;
import java.sql.ResultSet;
import org.apache.commons.codec.digest.Crypt;
import java.util.logging.Logger;

public class CustomerDatabase {
    private static final Logger logger = DbgLogger.getLogger();
    private AccessControl ac = new AccessControl();
    private Connection connection = null;
    private static CustomerDatabase dbinstance = null;
    private SecureRandom secureRandom = new SecureRandom();

    public static synchronized CustomerDatabase getInstance() {
        if (dbinstance == null) {
            dbinstance = new CustomerDatabase();
            logger.info("Created a new instance of CustomerDatabase");
        }
        return dbinstance;
    }

    public CustomerDatabase() {

        try {
            open("CustomerDB.db");
        } catch (SQLException e) {
            logger.severe("Error opening database: " + e.getMessage());
            DbgLogger.generateCrashLog();
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
            String createUserDB = "CREATE TABLE Users(user_id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR(40) NOT NULL UNIQUE, password VARCHAR(40) NOT NULL)";
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
            //new DatabaseFiller();

            return true;
        }
        logger.severe("Database creation failed");
        DbgLogger.generateCrashLog();
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
            preparedStatement.setInt(9, visits);
            preparedStatement.setString(10, notes);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            logger.info("Added customer: " + firstName + " " + lastName);
        } catch (SQLException e) {
            logger.warning("Error adding customer: " + e.getMessage());
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
            logger.info("Added customer: " + firstName + " " + lastName);
        } catch (SQLException e) {
            logger.warning("Error adding customer: " + e.getMessage());
        }
    }

    public void registerUser(String username, String password) {
        /*
         * Registers the username and password (crypted) given as parameters to database
         * Before this it should be checked (with userExists) that username is unique
         * 
         */
        try {
            String hashedPassword = cryptPass(password);
            String userSet = "INSERT INTO Users(username, password) VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(userSet);
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            logger.warning("Error registering user: " + e.getMessage());

        }
    }

    public String cryptPass(String password) {
        /*
         * Returns crypted password from a string that is given as parameter
         */
        byte bytes[] = new byte[13];
        secureRandom.nextBytes(bytes);
        String saltBytes = new String(Base64.getEncoder().encode(bytes));
        String salt = "$6$" + saltBytes;
        String hashedPassword = Crypt.crypt(password, salt);

        return hashedPassword;
    }

    public String getCryptedPassword(String username) {
        /*
         * Gets password of user in crypted form
         */
        try {
            String getPass = "SELECT (password) FROM Users WHERE username = ?";
            PreparedStatement ps = connection.prepareStatement(getPass);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            String hashedPass = "";
            if (rs.next()) {
                hashedPass = rs.getString("password");
                ps.close();
                return hashedPass;
            }

        } catch (SQLException e) {
            logger.warning("Failed to get hashed password from database");
        }
        return null;

    }

    public void changePassword(String username, String password) {
        String hashedPass = cryptPass(password);
        String updateQuery = "UPDATE Users SET password = ? WHERE username = ?";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, hashedPass);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            logger.warning("Failed changing password: " + e.getMessage());
        }

    }

    public boolean userExists(String username) throws SQLException {
        /*
         * Used to check if the username is available before attempting to register it
         * Returns true if user is already registered
         * Will be used if we get to make "add user" function to admin
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

    public boolean customerExists(int id) {
        /*
         * Checks from database if requested userid even exists
         */
        int toCompare = 0;
        try{
        String checkQuery = "SELECT customer_id FROM Customers WHERE customer_id = ?";
        PreparedStatement ps = connection.prepareStatement(checkQuery);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            toCompare = rs.getInt("customer_id");
        }
        ps.close();
        }catch(SQLException e){
            logger.warning("Problems getting to check if customer " + id + "exists: " + e);
            return false;
        }
        return (id == toCompare && toCompare != 0);
    }

    public void addRFIDEvent(int id) {
        /*
         * Adds event from RFID scan to events
         */
        if(!ac.checkAccess(id)){
            return;
        }
        String eventSet = "INSERT INTO Events(date, customer_id) VALUES (?,?)";
        try {

            DateTimeFormatter format = DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss");
            LocalDateTime dt = LocalDateTime.now();
            String date = dt.format(format);

            PreparedStatement ps = connection.prepareStatement(eventSet);
            ps.setString(1, date);
            ps.setInt(2, id);

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            logger.warning("Error storing an event: " + e.getMessage());
        }

    }

    public void minusOneVisit(int id, int newvisits){
        /* 
         * Deducts one visit from user after entering
         */
        String updateCustomer = "UPDATE Customers SET visits = ? WHERE customer_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateCustomer);
            preparedStatement.setInt(1, newvisits);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            logger.info("Deducted one visit from customer " + id);
        } catch (SQLException e) {
            logger.warning("Error updating customer: " + e.getMessage());
        }
        
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
                logger.info("Attempted to login as user '" + username + "' (this username does not exist)");
                return false;

            }

            if (username.equals(usernameCompare) && passwordCompare.equals(Crypt.crypt(password, passwordCompare))) {
                return true;
            }
        } catch (SQLException e) {
            logger.warning("Failed to get user info from database");

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
            logger.warning("Error getting customers from DB: " + e.getMessage());
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
            logger.warning("Error getting customer with id " + id + ": " + e.getMessage());
            return null;
        }
    }

    public ResultSet getTypeAndRelatedInfo(int id){
        try {
            String getCustomer = "SELECT membership_type, visits, membership_end FROM Customers WHERE customer_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(getCustomer);
            preparedStatement.setInt(1, id);
            ResultSet results = preparedStatement.executeQuery();
            return results;
        } catch (SQLException e) {
            logger.warning("Error getting customer with id " + id + ": " + e.getMessage());
            return null;
        }
    }

    public void updateCustomer(int customer_id, String first_name, String last_name, String address,
            String phone_number, String email, String additional_information, String membership_end, int visits) {
        /* 
         * Will be called if the customer is edited
         */
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
            logger.info("Updated info of customer: " + first_name + " " + last_name);
        } catch (SQLException e) {
            logger.warning("Error updating customer: " + e.getMessage());
        }

    }

    public ResultSet getEvents() {
        /* 
         * Gets all the events from the database to display
         */
        try {
            Statement statement = connection.createStatement();
            String resultQuery = "SELECT e.date, c.first_name || ' ' || c.last_name AS name, e.customer_id FROM Events e INNER JOIN Customers c ON e.customer_id = c.customer_id ORDER BY e.event_id DESC";
            ResultSet results = statement.executeQuery(resultQuery);
            //TODO add close():s?
            return results;

        } catch (SQLException e) {
            logger.warning("Error getting events: " + e.getMessage());

            return null;
        }
    }

    public void saveEvent(int customerid, String date) {
        // This function is to save the every customer visit to DB
        // The testuser parameter is to get events from databasefiller function
        //(this ignores the access control)
        String eventSet = "INSERT INTO Events(date, customer_id) VALUES (?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(eventSet);
            ps.setString(1, date);
            ps.setInt(2, customerid);

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            logger.warning("Error storing an event: " + e.getMessage());
        }

    }

    public ResultSet selectEventDate(String startDate, String endDate) {
        /* 
         * Select events from event view between the dates given as parameter
         */
        try {
            String resultQuery = "SELECT e.date, c.first_name || ' ' || c.last_name AS name, e.customer_id FROM Events e INNER JOIN Customers c ON e.customer_id = c.customer_id WHERE e.date BETWEEN ? AND ? ORDER BY e.event_id DESC";
            PreparedStatement ps = connection.prepareStatement(resultQuery);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ResultSet results = ps.executeQuery();
            return results;
        } catch (SQLException e) {
            logger.warning("Error getting events between given dates: " + e.getMessage());
            return null;
        }

    }

    public boolean deleteCustomer(int customerId) {
        /* 
         * Deletes customer from DB
         */

        String deleteCustomer = "DELETE FROM Customers WHERE customer_id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteCustomer);
            preparedStatement.setInt(1, customerId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            logger.info("Deleted customer: " + customerId);
            return true;
        } catch (SQLException e) {
            logger.warning("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    public ResultSet searchByName(String firstName, String lastName) {
        /* 
         * Search users by name from browse customers
         */
        try {
            String resultQuery = "SELECT * FROM Customers WHERE first_name = ? AND last_name = ?";
            PreparedStatement ps = connection.prepareStatement(resultQuery);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ResultSet results = ps.executeQuery();
            return results;
        } catch (SQLException e) {
            logger.warning("Error getting events between given dates: " + e.getMessage());
            return null;
        }
    }

}
