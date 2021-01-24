package SchedulingApp.Model;

import SchedulingApp.ViewController.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.Date;

public class DB {

    /**
     * Database connection information
     */
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB = "U05Ns3";
    private static final String URL = "jdbc:mysql://3.227.166.251/" + DB;
    private static final String USER = "U05Ns3";
    private static final String PASS = "53688552387";

    private static String currentUser;
    private static int openCount = 0;

    /**
     * Login event log writing
     */
    public static boolean checkLogInCreds(String userName, String password) {
        int userId = getUserId(userName);
        boolean goodPass = checkPassword(userId, password);
        if (goodPass) {
            setCurrentUser(userName);
            //sets up the login event log
            try {
                Path path = Paths.get("Login_event_log.txt");
                Files.write(path, Arrays.asList("User " + currentUser + " successfully logged in at " + Date.from(Instant.now()).toString() + "."),
                        StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Part 1 of the checkLogInCreds
     * This will check to see if there is a user ID associated with a username
     */
    private static int getUserId(String userName) {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        //set userId to -1 prior to running the query
        int userId = -1;
        //SQL query to select userId from username provided
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT userId FROM user WHERE userName = '" + userName + "'");
            if (rs.next()) {
                userId = rs.getInt("userId");
            }
            rs.close();
        }
        catch (SQLException ex) {
            //LoginController.incrementDatabaseError();
            System.out.println("SQLException: " + ex.getMessage());
        }
        return userId;
    }

    /**
     * Part 2 of the checkLogInCreds
     * This will check to see if the password provided matches what's in the db
     */
    private static boolean checkPassword(int userId, String password) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT password FROM user WHERE userId = " + userId);
            //set password initially to null
            String dbPass = null;
            if (rs.next()) {
                dbPass = rs.getString("password");
            }
            else {
                return false;
            }
            rs.close();
            if (dbPass.equals(password)) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static void setCurrentUser(String userName) {
        currentUser = userName;
    }

    /**
     * Creates a notification for appointments that will occur within 15 minutes
     */
    public static void ApptIn15() {
        if (openCount == 0) {
            ObservableList<Appointment> userAppointments = FXCollections.observableArrayList();
            for (Appointment appointment : Appointment.getAppointmentList()) {
                if (appointment.getCreatedBy().equals(currentUser)) {
                    userAppointments.add(appointment);
                }
            }
            //check to see if any appointments start in 15 minutes
            for (Appointment appointment : userAppointments) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Date.from(Instant.now()));
                calendar.add(Calendar.MINUTE, 15);
                Date notificationCutoff = calendar.getTime();
                //create alert if there are appointments in 15 minutes
                if (appointment.getStartDate().before(notificationCutoff)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Upcoming Appointment");
                    alert.setHeaderText("Appointment in the next 15 minutes:");
                    alert.setContentText("Title: " + appointment.getTitle() + "\n" + "Location: " + appointment.getDescription() + "\n"
                        + "Contact: " + appointment.getContact() + "\n" + "Start Time: " + appointment.getStartString() + "\n"
                        + "End Time: " + appointment.getEndString());
                    alert.showAndWait();
                }
            }
            openCount++;
        }
    }

    /**
     * method used to update Customer list after a db change
     */
    public static void updateCustomerList() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
        Statement statement = conn.createStatement()) {
            ObservableList<Customer> customerList = Customer.getCustomerList();
            customerList.clear();
            //resultset creates a list of customerId's
            ResultSet rs = statement.executeQuery("SELECT customerId FROM customer WHERE active = 1");
            ArrayList<Integer> customerIdList = new ArrayList<>();
            while (rs.next()) {
                customerIdList.add(rs.getInt(1));
            }
            for (int customerId : customerIdList) {
                Customer customer = new Customer();
                ResultSet custrs = statement.executeQuery("SELECT customerName, active, addressId FROM customer WHERE customerId = " + customerId);
                custrs.next();
                String customerName = custrs.getString(1);
                int active = custrs.getInt(2);
                int addressId = custrs.getInt(3);
                customer.setCustomerId(customerId);
                customer.setCustomerName(customerName);
                customer.setActive(active);
                customer.setAddressId(addressId);
                //query for address retrieval
                ResultSet addrs = statement.executeQuery("SELECT address, address2, postalCode, phone, cityId FROM address where addressId = " + addressId);
                addrs.next();
                String address = addrs.getString(1);
                String address2 = addrs.getString(2);
                String postalCode = addrs.getString(3);
                String phone = addrs.getString(4);
                int cityId = addrs.getInt(5);
                customer.setAddress(address);
                customer.setAddress2(address2);
                customer.setPostalCode(postalCode);
                customer.setPhone(phone);
                customer.setCityId(cityId);
                //query for city retrieval
                ResultSet cityrs = statement.executeQuery("SELECT city, countryId FROM city WHERE cityId = " + cityId);
                cityrs.next();
                String city = cityrs.getString(1);
                int countryId = cityrs.getInt(2);
                customer.setCity(city);
                customer.setCountryId(countryId);
                //query for country retrieval
                ResultSet counrs = statement.executeQuery("SELECT country FROM country WHERE countryId = " + countryId);
                counrs.next();
                String country = counrs.getString(1);
                customer.setCountry(country);
                //grabbing all the results and adding the new customer object into customerList
                customerList.add(customer);
            }
        }
        catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error connecting to Database.");
            alert.setContentText("Please check your database connection and try again.");
            alert.show();
        }
    }


    /**
     * Adding a new customer
     * @param customerName
     * @param address
     * @param address2
     * @param city
     * @param country
     * @param postalCode
     * @param phone
     */
    public static void addNewCustomer(String customerName, String address, String address2,
                                   String city, String country, String postalCode, String phone) {
        try {
            int countryId = retrieveCountryId(country);
            int cityId = retrieveCityId(city, countryId);
            int addressId = retrieveAddressId(address, address2, postalCode, phone, cityId);
            if (customerAlreadyExists(customerName, addressId)) {
                try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                     Statement statement = conn.createStatement()) {
                    ResultSet rs = statement.executeQuery("SELECT active FROM customer WHERE "
                            + "customerName = '" + customerName + "' AND addressId = " + addressId);
                    rs.next();
                    int active = rs.getInt(1);
                    if (active == 1) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Customer Error");
                        alert.setHeaderText("Error adding customer");
                        alert.setContentText("This customer already exists, please check your entry and try again.");
                        alert.showAndWait();
                    } else if (active == 0) {
                        setCustomertoActive(customerName, addressId);
                    }
                }
            } else {
                addCustomer(customerName, addressId);
            }
        }
        catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Error");
            alert.setHeaderText("Error connecting to Database");
            alert.setContentText("Please check your connection and try again.");
            alert.showAndWait();
        }
    }

    public static int retrieveCountryId(String country) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
        Statement statement = conn.createStatement()) {
            ResultSet countryCheck = statement.executeQuery("SELECT countryId FROM country WHERE country = '" + country + "'");
            if (countryCheck.next()) {
                int countryId = countryCheck.getInt(1);
                countryCheck.close();
                return countryId;
            }
            else {
                countryCheck.close();
                int countryId;
                ResultSet allCountryId = statement.executeQuery("SELECT countryId FROM country ORDER BY countryId");
                if (allCountryId.last()) {
                    countryId = allCountryId.getInt(1) + 1;
                    allCountryId.close();
                }
                else {
                    allCountryId.close();
                    countryId = 1;
                }
                PreparedStatement countryAdd = null;
                String insert = "INSERT INTO country (countryId, country, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES (?, ?, CURRENT_DATE, ?, CURRENT_TIMESTAMP, ?";
                try {
                    countryAdd = conn.prepareStatement(insert);
                    countryAdd.setInt(1, countryId);
                    countryAdd.setString(2, country);
                    countryAdd.setString(3, currentUser);
                    countryAdd.setString(4, currentUser);
                    int result = countryAdd.executeUpdate();
                    if (result == 0) {
                        System.out.println("Unable to add country, please check your SQL statement.");
                    }
                } catch (SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                }
                return countryId;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public static int retrieveCityId(String city, int countryId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement statement = conn.createStatement()) {
            ResultSet cityCheck = statement.executeQuery("SELECT cityId FROM city WHERE city = '" + city + "' AND countryId = " + countryId);
            if (cityCheck.next()) {
                int cityId = cityCheck.getInt(1);
                cityCheck.close();
                return cityId;
            }
            else {
                cityCheck.close();
                int cityId;
                ResultSet allCityId = statement.executeQuery("SELECT cityId FROM city ORDER BY cityId");
                if (allCityId.last()) {
                    cityId = allCityId.getInt(1) + 1;
                    allCityId.close();
                }
                else {
                    allCityId.close();
                    cityId = 1;
                }
                PreparedStatement cityAdd = null;
                String insert = "INSERT INTO city (cityId, city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)";
                try {
                    cityAdd = conn.prepareStatement(insert);
                    cityAdd.setInt(1, cityId);
                    cityAdd.setString(2, city);
                    cityAdd.setInt(3, countryId);
                    cityAdd.setString(4, currentUser);
                    cityAdd.setString(5, currentUser);
                    int result = cityAdd.executeUpdate();
                    if (result == 0) {
                        System.out.println("City unable to be added, please check your SQL code.");
                    }
                } catch (SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                } finally {
                    if (cityAdd != null) {
                        cityAdd.close();
                    }
                }
                return cityId;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public static int retrieveAddressId(String address, String address2, String postalCode, String phone, int cityId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement statement = conn.createStatement()) {
            ResultSet addressCheck = statement.executeQuery("SELECT addressId FROM address WHERE address = '" + address + "' AND address2 = '" + address2 + "' AND " +
                    "postalCode = '" + postalCode + "' AND phone = '" + phone + "' AND cityId = " + cityId);
            if (addressCheck.next()) {
                int addressId = addressCheck.getInt(1);
                addressCheck.close();
                return addressId;
            }
            else {
                addressCheck.close();
                int addressId;
                ResultSet allAddressId = statement.executeQuery("SELECT addressId FROM address ORDER BY addressId");
                if (allAddressId.last()) {
                    addressId = allAddressId.getInt(1) + 1;
                    allAddressId.close();
                }
                else {
                    allAddressId.close();
                    addressId = 1;
                }
                PreparedStatement addressAdd = null;
                String insert = "INSERT INTO address (addressId, address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, ?, CURRENT_TIMESTAMP, ?)";
                try {
                    addressAdd = conn.prepareStatement(insert);
                    addressAdd.setInt(1, addressId);
                    addressAdd.setString(2, address);
                    addressAdd.setString(3, address2);
                    addressAdd.setInt(4, cityId);
                    addressAdd.setString(5, postalCode);
                    addressAdd.setString(6, phone);
                    addressAdd.setString(7, currentUser);
                    addressAdd.setString(8, currentUser);
                    int result = addressAdd.executeUpdate();
                    if (result == 0) {
                        System.out.println("Address not added, please check your SQL statement!");
                    }
                } catch (SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                } finally {
                    if (addressAdd != null) {
                        addressAdd.close();
                    }
                }
                return addressId;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    private static boolean customerAlreadyExists(String customerName, int addressId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
        Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT customerId FROM customer WHERE customerName = '" + customerName + "' " +
                    "AND addressId = " + addressId);
            if (rs.next()) {
                rs.close();
                return true;
            }
            else {
                rs.close();
                return false;
            }
        }
    }

    public static void setCustomertoActive(String customerName, int addressId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement statement = conn.createStatement()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Set customer to active?");
            alert.setContentText("This customer already exists, but is set as inactive.  Press OK to make this customer active.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                statement.executeUpdate("UPDATE customer SET active = 1, lastUpdate = CURRENT_TIMESTAMP, " +
                        "lastUpdateBy = '" + currentUser + "' WHERE customerName = '" + customerName + "' AND addressId = " + addressId);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void addCustomer(String customerName, int addressId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT customerId FROM customer ORDER BY customerId");
            int customerId;
            if (rs.last()) {
                customerId = rs.getInt(1) + 1;
                rs.close();
            }
            else {
                rs.close();
                customerId = 1;
            }
            statement.executeUpdate("INSERT INTO customer VALUES (" + customerId + ", '" + customerName + "', " + addressId + ", 1, " +
                    "CURRENT_DATE, '" + currentUser + "', CURRENT_TIMESTAMP, '" + currentUser + "')");
        }
    }

    public static int modifyCustomer(int customerId, String customerName, String address, String address2,
                                     String city, String country, String postalCode, String phone) {
        try {
            int countryId = retrieveCountryId(country);
            int cityId = retrieveCityId(city, countryId);
            int addressId = retrieveAddressId(address, address2, postalCode, phone, cityId);
            if (customerAlreadyExists(customerName, addressId)) {
                int existingCustomerId = getCustomerId(customerName, addressId);
                int activeStatus = getActiveStatus(existingCustomerId);
                return activeStatus;
            } else {
                updateCustomer(customerId, customerName, addressId);
                cleanDatabase();
                return -1;
            }
        }
        catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Customer Error");
            alert.setHeaderText("Error modifying customer");
            alert.setContentText("Please check your database connection and try again.");
            alert.showAndWait();
            return -1;
        }
    }

    private static int getCustomerId(String customerName, int addressId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT customerId FROM customer WHERE customerName = '" + customerName + "' AND addressId = " + addressId);
            rs.next();
            int customerId = rs.getInt(1);
            return customerId;
        }
    }

    private static int getActiveStatus(int customerId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT active FROM customer WHERE customerId = " + customerId);
            rs.next();
            int active = rs.getInt(1);
            return active;
        }
    }

    private static void updateCustomer(int customerId, String customerName, int addressId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement statement = conn.createStatement()) {
            statement.executeUpdate("UPDATE customer SET customerName = '" + customerName + "', addressId = " + addressId + ", " +
                    "lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" + currentUser + "' WHERE customerId = " + customerId);
        }
    }

    public static void setCustomerInactive(Customer customerToRemove) {
        int customerId = customerToRemove.getCustomerId();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm removing customer");
        alert.setContentText("Click OK to remove the customer from the customer list and set them as inactive.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 Statement statement = conn.createStatement()) {
                statement.executeUpdate("UPDATE customer SET active = 0 WHERE customerId = " + customerId);
            }
            catch (SQLException ex) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Customer Error");
                alert1.setHeaderText("Error modifying customer");
                alert1.setContentText("Please check your database connection and try again.");
                alert1.showAndWait();
            }
            updateCustomerList();
        }
    }

    /**
     * SQL statement to update appointment list with appointments that will occur
     */
    public static void updateAppointmentList() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
            Statement statement = conn.createStatement()) {
            ObservableList<Appointment> appointmentList = Appointment.getAppointmentList();
            appointmentList.clear();
            ResultSet apptrs = statement.executeQuery("SELECT appointmentId FROM appointment WHERE start >= CURRENT_TIMESTAMP");
            ArrayList<Integer> appointmentIdList = new ArrayList<>();
            while(apptrs.next()) {
                appointmentIdList.add(apptrs.getInt(1));
            }
            //create appointment object for the appointmentIds in list
            for (int appointmentId : appointmentIdList) {
                apptrs = statement.executeQuery("SELECT customerId, title, description, location, contact, type, start, end, createdBy "
                    + "FROM appointment WHERE appointmentId = " + appointmentId);
                apptrs.next();
                int customerId = apptrs.getInt(1);
                String title = apptrs.getString(2);
                String description = apptrs.getString(3);
                String location = apptrs.getString(4);
                String contact = apptrs.getString(5);
                String type = apptrs.getString(6);
                Timestamp startTimestamp = apptrs.getTimestamp(7);
                Timestamp endTimestamp = apptrs.getTimestamp(8);
                String createdBy = apptrs.getString(9);
                //timestamp manipulations
                DateFormat dfUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                dfUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
                java.util.Date start = dfUTC.parse(startTimestamp.toString());
                java.util.Date end = dfUTC.parse(endTimestamp.toString());
                //construct new appointment object
                Appointment appointment = new Appointment(appointmentId, customerId, title, description, location, contact, type, startTimestamp, endTimestamp, start, end, createdBy);
                appointmentList.add(appointment);
            }
        }
        catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Error");
            alert.setHeaderText("Error adding appointment");
            alert.setContentText("Please check your database connection and try again.");
            alert.showAndWait();
        }
    }

    /**
     * adds a new appointment if it passes an appointment overlap check
     */
    public static boolean addNewAppointment(Customer customer, String title, String description, String location,
                                         String contact, String type, ZonedDateTime startUTC, ZonedDateTime endUTC) {
        String startUTCString = startUTC.toString();
        startUTCString = startUTCString.substring(0,10) + " " + startUTCString.substring(11,16) + ":00";
        Timestamp startTimestamp = Timestamp.valueOf(startUTCString);
        String endUTCString = endUTC.toString();
        endUTCString = endUTCString.substring(0,10) + " " + endUTCString.substring(11,16) + ":00";
        Timestamp endTimestamp = Timestamp.valueOf(endUTCString);
        //appointment overlap warning
        if (AppointmentOverlap(startTimestamp, endTimestamp)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Alert");
            alert.setHeaderText("Appointment Overlap Alert");
            alert.setContentText("The appointment time overlaps with an already-scheduled appointment.");
            alert.showAndWait();
            return false;
        }
        //proceeds to add appointment
        else {
            int customerId = customer.getCustomerId();
            addAppointment(customerId, title, description, location, contact, type, startTimestamp, endTimestamp);
            return true;
        }
    }

    /**
     * Appointment overlap validation check
     */
    private static boolean AppointmentOverlap(Timestamp startTimestamp, Timestamp endTimestamp) {
        updateAppointmentList();
        ObservableList<Appointment> appointmentList = Appointment.getAppointmentList();
        for (Appointment appointment: appointmentList) {
            Timestamp existingStartTimestamp = appointment.getStartTime();
            Timestamp existingEndTimestamp = appointment.getEndTime();
            if (startTimestamp.after(existingStartTimestamp) && startTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (endTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.before(existingStartTimestamp) && endTimestamp.after(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.equals(existingStartTimestamp)) {
                return true;
            }
            if (endTimestamp.equals(existingEndTimestamp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a new appointment and save to database
     */
    private static void addAppointment(int customerId, String title, String description, String location, String contact,
                                       String type, Timestamp startTimestamp, Timestamp endTimestamp) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
            Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT appointmentId FROM appointment ORDER BY appointmentId");
            int appointmentID;
            if (rs.last()) {
                appointmentID = rs.getInt(1) + 1;
                rs.close();
            }
            else {
                rs.close();
                appointmentID = 1;
            }
            PreparedStatement appointmentAdd = null;
            String insert = "INSERT INTO appointment (customerId, title, description, type, location, contact, "
                    + "url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy, userId) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?, ?)";
            try {
                appointmentAdd = conn.prepareStatement(insert);
                //appointmentAdd.setInt(1, appointmentID);
                appointmentAdd.setInt(1, customerId);
                appointmentAdd.setString(2, title);
                appointmentAdd.setString(3, description);
                appointmentAdd.setString(4, type);
                appointmentAdd.setString(5, location);
                appointmentAdd.setString(6, contact);
                appointmentAdd.setString(7, "");
                appointmentAdd.setTimestamp(8, startTimestamp);
                appointmentAdd.setTimestamp(9, endTimestamp);
                appointmentAdd.setString(10, currentUser);
                appointmentAdd.setString(11, currentUser);
                appointmentAdd.setInt(12, getUserId(currentUser));
                int result = appointmentAdd.executeUpdate();
                if (result == 1) {
                    System.out.println("Appointment added!");
                } else {
                    System.out.println("Appointment unable to be added!");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            } finally {
                if (appointmentAdd != null) {
                    appointmentAdd.close();
                }
            }
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
            /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Error");
            alert.setHeaderText("Error adding appointment");
            alert.setContentText("Please check your database connection and try again.");
            alert.showAndWait();*/
        }
    }

    /**
     * SQL query for modifying an existing appointment
     */
    private static void updateAppointment(int appointmentId, int customerId, String title, String description, String location,
                                          String contact, String type, Timestamp startTimestamp, Timestamp endTimestamp) throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASS);
        PreparedStatement statement = null;
        String insert = "UPDATE appointment SET customerId = ?, title = ?, type = ?, description = ?, start = ?, end = ?, "
                + "lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ? "
                + "WHERE appointmentId = ?";
        try {
            statement = conn.prepareStatement(insert);
            statement.setInt(1, customerId);
            statement.setString(2, title);
            statement.setString(3, type);
            statement.setString(4, description);
            statement.setTimestamp(5, startTimestamp);
            statement.setTimestamp(6, endTimestamp);
            statement.setString(7, currentUser);
            statement.setInt(8, appointmentId);
            int result = statement.executeUpdate();
            if (result == 1) {
                System.out.println("Appointment updated successfully!");
            } else {
                System.out.println("Appointment was not updated.");
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Modify an existing appointment
     */
    public static boolean modifyAppointment(int appointmentId, Customer customer, String title, String description, String location,
                                            String contact, String type, ZonedDateTime startUTC, ZonedDateTime endUTC) {
        try {
            String startUTCString = startUTC.toString();
            startUTCString = startUTCString.substring(0, 10) + " " + startUTCString.substring(11, 16) + ":00";
            Timestamp startTimestamp = Timestamp.valueOf(startUTCString);
            String endUTCString = endUTC.toString();
            endUTCString = endUTCString.substring(0, 10) + " " + endUTCString.substring(11, 16) + ":00";
            Timestamp endTimestamp = Timestamp.valueOf(endUTCString);
            if (AppointmentOverlapOthers(startTimestamp, endTimestamp)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment Error");
                alert.setHeaderText("Error modifying appointment--overlapping times");
                alert.setContentText("Unable to modify appointment--the time overlaps with another appointment.");
                alert.showAndWait();
                return false;
            } else {
                int customerId = customer.getCustomerId();
                updateAppointment(appointmentId, customerId, title, description, location, contact, type, startTimestamp, endTimestamp);
                return true;
            }
        }
        catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Error");
            alert.setHeaderText("Error modifying appointment");
            alert.setContentText("Please check your database connection and try again.");
            alert.showAndWait();
            return false;
        }
    }

    private static boolean AppointmentOverlapOthers(Timestamp startTimestamp, Timestamp endTimestamp) throws SQLException, ParseException {
        int appointmentIndexToRemove = AppointmentSummaryController.getAppointmentIndexToModify();
        ObservableList<Appointment> appointmentList = Appointment.getAppointmentList();
        appointmentList.remove(appointmentIndexToRemove);
        for (Appointment appointment: appointmentList) {
            Timestamp existingStartTimestamp = appointment.getStartTime();
            Timestamp existingEndTimestamp = appointment.getEndTime();
            if (startTimestamp.after(existingStartTimestamp) && startTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (endTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.before(existingStartTimestamp) && endTimestamp.after(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.equals(existingStartTimestamp)) {
                return true;
            }
            if (endTimestamp.equals(existingEndTimestamp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes a selected Appointment
     * @param appointmentToDelete
     */
    public static void deleteAppointment(Appointment appointmentToDelete) {
        int appointmentId = appointmentToDelete.getAppointmentId();
        //show delete confirmation message
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete Appointment Confirmation");
        alert.setContentText("Please confirm you would like to delete the selected Appointment.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
            Statement statement = conn.createStatement()) {
                statement.executeUpdate("DELETE FROM appointment WHERE appointmentId =" + appointmentId);
            }
            catch (Exception ex) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Error deleting Appointment");
                alert1.setHeaderText("Error deleting Appointment");
                alert1.setContentText("Please check your database connection and try again");
                alert1.showAndWait();
            }
            updateAppointmentList();
        }
    }

    private static void cleanDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement statement = conn.createStatement()) {
            ResultSet addrRs = statement.executeQuery("SELECT DISTINCT addressId FROM customer ORDER BY addressId");
            ArrayList<Integer> addressIdListFromCustomer = new ArrayList<>();
            while (addrRs.next()) {
                addressIdListFromCustomer.add(addrRs.getInt(1));
            }
            addrRs = statement.executeQuery("SELECT DISTINCT addressId FROM address ORDER BY addressId");
            ArrayList<Integer> addressIdListFromAddress = new ArrayList<>();
            while (addrRs.next()) {
                addressIdListFromAddress.add(addrRs.getInt(1));
            }
            for (int i = 0; i < addressIdListFromCustomer.size(); i++) {
                for (int j = 0; j < addressIdListFromAddress.size(); j++) {
                    if (addressIdListFromCustomer.get(i) == addressIdListFromAddress.get(j)) {
                        addressIdListFromAddress.remove(j);
                        j--;
                    }
                }
            }
            if (addressIdListFromAddress.isEmpty()) {}
            else {
                for (int addressId : addressIdListFromAddress) {
                    statement.executeUpdate("DELETE FROM address WHERE addressId = " + addressId);
                }
            }

            ResultSet cityRs = statement.executeQuery("SELECT DISTINCT cityId FROM address ORDER BY cityId");
            ArrayList<Integer> cityIdListFromAddress = new ArrayList<>();
            while (cityRs.next()) {
                cityIdListFromAddress.add(cityRs.getInt(1));
            }

            cityRs = statement.executeQuery("SELECT DISTINCT cityId FROM city ORDER BY cityId");
            ArrayList<Integer> cityIdListFromCity = new ArrayList<>();
            while (cityRs.next()) {
                cityIdListFromCity.add(cityRs.getInt(1));
            }
            for (int i = 0; i < cityIdListFromAddress.size(); i++) {
                for (int j = 0; j < cityIdListFromCity.size(); j++) {
                    if (cityIdListFromAddress.get(i) == cityIdListFromCity.get(j)) {
                        cityIdListFromCity.remove(j);
                        j--;
                    }
                }
            }
            if (cityIdListFromCity.isEmpty()) {}
            else {
                for (int cityId : cityIdListFromCity) {
                    statement.executeUpdate("DELETE FROM city WHERE cityId = " + cityId);
                }
            }

            ResultSet counRs = statement.executeQuery("SELECT DISTINCT countryId FROM city ORDER BY countryId");
            ArrayList<Integer> countryIdListFromCity = new ArrayList<>();
            while (counRs.next()) {
                countryIdListFromCity.add(counRs.getInt(1));
            }

            counRs = statement.executeQuery("SELECT DISTINCT countryId FROM country ORDER BY countryId");
            ArrayList<Integer> countryIdListFromCountry = new ArrayList<>();
            while (counRs.next()) {
                countryIdListFromCountry.add(counRs.getInt(1));
            }
            for (int i = 0; i < countryIdListFromCity.size(); i++) {
                for (int j = 0; j < countryIdListFromCountry.size(); j++) {
                    if (countryIdListFromCity.get(i) == countryIdListFromCountry.get(j)) {
                        countryIdListFromCountry.remove(j);
                        j--;
                    }
                }
            }
            if (countryIdListFromCountry.isEmpty()) {}
            else {
                for (int countryId : countryIdListFromCountry) {
                    statement.executeUpdate("DELETE FROM country WHERE countryId = " + countryId);
                }
            }

        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }

}
