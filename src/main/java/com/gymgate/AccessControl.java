package com.gymgate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class AccessControl {
    /*
     * This gets called to check if the customers membership
     * is valid for entering
     */
    private int customerId;
    private LocalDate endDate;
    private String memb_type;
    private int visits;
    private String memb_end;
    Logger logger = Logger.getLogger(DbgLogger.class.getName());

    public AccessControl() {
        logger.info("Created an instance of AccessControl");
    }

    public boolean checkAccess(int customerid) {
        // first_name = ?, last_name = ?, address = ?, phone_number = ?, email = ?,
        // additional_information = ?, membership_end = ?, visits = ? WHERE customer_id
        // = ?";
        // membership_type, visits, membership_end FROM Customers WHERE customer_id =
        // ?";
        if(!CustomerDatabase.getInstance().customerExists(customerid)){
            logger.warning("User " + customerid + " does not exist");
            return false;
        }
        this.customerId = customerid;
        boolean isValid = true;
        try {
            ResultSet rs = CustomerDatabase.getInstance().getTypeAndRelatedInfo(customerId);
            while (rs.next()) {
                this.memb_type = rs.getString("membership_type");
                this.visits = rs.getInt("visits");
                this.memb_end = rs.getString("membership_end");
            }
        } catch (SQLException e) {
            logger.warning("Problems getting customer info for access control: " + e);
        }
        if(getMembershipType().toLowerCase().equals("kuukausijäsenyys")){
            if(!accessMonthly()){
                logger.info("User with id " + customerid + " attempted to entry (membership out of date)");
                isValid = false;
            }
        }else{
            if(accessOther()){
                CustomerDatabase.getInstance().minusOneVisit(this.customerId, this.visits);
            }else{
                logger.info("User with id " + customerid + " attempted to entry (out of visits)");
                isValid = false;
            }
        }
        return isValid;
    }

    private boolean accessMonthly() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate mEnd = LocalDate.parse(memb_end, formatter);

        return now.isBefore(mEnd);
    }

    private boolean accessOther() {
        visits = visits - 1;
        if (visits < 0) {
            return false;
        }
        return true;

    }

    public int getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getMembershipType() {
        return this.memb_type;
    }

    public void setMembershipType(String memb_type) {
        this.memb_type = memb_type;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getVisitsLeft() {
        return this.visits;
    }

    public void setVisitsLeft(int visits) {
        this.visits = visits;
    }

    public String getMembershipEnd() {
        return this.memb_type;
    }

    public void setMembershipEnd(String memb_end) {
        this.memb_end = memb_end;
    }

}
