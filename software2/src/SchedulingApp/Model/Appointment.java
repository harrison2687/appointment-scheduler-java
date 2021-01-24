package SchedulingApp.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Appointment {

    private IntegerProperty appointmentId;
    private IntegerProperty customerId;
    private StringProperty title;
    private StringProperty description;
    private StringProperty location;
    private StringProperty contact;
    private StringProperty type;
    private Timestamp startTime;
    private Timestamp endTime;
    private Date startDate;
    private Date endDate;
    private StringProperty dateString;
    private StringProperty startString;
    private StringProperty endString;
    private StringProperty createdBy;

    private static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();


    /**
     * primary constructor for Appointment
     */
    public Appointment(int appointmentId, int customerId, String title, String description, String location,
                       String contact, String type, Timestamp startTime, Timestamp endTime, Date startDate,
                       Date endDate, String createdBy) {
        this.appointmentId = new SimpleIntegerProperty(appointmentId);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
        this.contact = new SimpleStringProperty(contact);
        this.type = new SimpleStringProperty(type);
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        //day formatting
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        this.dateString = new SimpleStringProperty(format.format(startDate));
        //time formatting
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a z");
        this.startString = new SimpleStringProperty(timeFormat.format(startDate));
        this.endString = new SimpleStringProperty(timeFormat.format(endDate));
        this.createdBy = new SimpleStringProperty(createdBy);
    }

    /**
     * Observable List used to populate a list of appointments
     */
    public static ObservableList<Appointment> getAppointmentList() {
        return appointmentList;
    }

    /**
     * Setters and getters
     */
    public int getAppointmentId() {
        return appointmentId.get();
    }

    public IntegerProperty appointmentIdProperty() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId.set(appointmentId);
    }

    public int getCustomerId() {
        return customerId.get();
    }

    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getLocation() {
        return location.get();
    }

    public StringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public String getContact() {
        return contact.get();
    }

    public StringProperty contactProperty() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact.set(contact);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDateString() {
        return dateString.get();
    }

    public StringProperty dateStringProperty() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString.set(dateString);
    }

    public String getStartString() {
        return startString.get();
    }

    public StringProperty startStringProperty() {
        return startString;
    }

    public void setStartString(String startString) {
        this.startString.set(startString);
    }

    public String getEndString() {
        return endString.get();
    }

    public StringProperty endStringProperty() {
        return endString;
    }

    public void setEndString(String endString) {
        this.endString.set(endString);
    }

    public String getCreatedBy() {
        return createdBy.get();
    }

    public StringProperty createdByProperty() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy.set(createdBy);
    }

    /**
     * Validation messages for appointment items
     */
    public static String appointmentValid(Customer customer, String title, String description, String location, String type,
                                          LocalDate appointmentDate, String startHour, String startMinute, String startAMPM,
                                          String endHour, String endMinute, String endAMPM) throws NumberFormatException {
        String errorMessage = "";
        //different appointment error conditions + messages
        try {
            if (customer == null) {
                errorMessage = "Please select a customer associated with this appointment.";
            }
            if (title.length() == 0) {
                errorMessage = "Please type in a title for this appointment.";
            }
            if (description.length() == 0) {
                errorMessage = "Please type in a description for this appointment.";
            }
            if (location.length() == 0) {
                errorMessage = "Please provide a location for this appointment.";
            }
            if (type.length() == 0) {
                errorMessage = "Please select a valid appointment type.";
            }
            if (appointmentDate == null || startHour.equals("") || startMinute.equals("") || startAMPM.equals("") ||
            endHour.equals("") || endMinute.equals("") || endAMPM.equals("")) {
                errorMessage = "Please provide a start and end time.";
            }
            if (Integer.parseInt(startHour) < 1 || Integer.parseInt(startHour) > 12 || Integer.parseInt(endHour) < 1 ||
            Integer.parseInt(endHour) > 12 || Integer.parseInt(startMinute) < 0 || Integer.parseInt(startMinute) > 59 ||
            Integer.parseInt(endMinute) < 0 || Integer.parseInt(endMinute) > 59) {
                errorMessage = "Please check your times--the start and end times must be valid.";
            }
            if ((startAMPM.equals("PM") && endAMPM.equals("AM")) || (startAMPM.equals(endAMPM) && Integer.parseInt(startHour) != 12 && Integer.parseInt(startHour) > Integer.parseInt(endHour)) ||
                    (startAMPM.equals(endAMPM) && startHour.equals(endHour) && Integer.parseInt(startMinute) > Integer.parseInt(endMinute))) {
                errorMessage = "Appointment end times cannot equal or be before start times.";
            }
            if ((Integer.parseInt(startHour) < 9 && startAMPM.equals("AM")) || (Integer.parseInt(endHour) < 9 && endAMPM.equals("AM")) ||
                    (Integer.parseInt(startHour) >= 5 && Integer.parseInt(startHour) < 12 && startAMPM.equals("PM")) ||
                    (Integer.parseInt(endHour) >= 5 && Integer.parseInt(endHour) < 12 && endAMPM.equals("PM")) ||
                    (Integer.parseInt(startHour) == 12 && startAMPM.equals("AM")) || (Integer.parseInt(endHour)) == 12 && endAMPM.equals("AM")) {
                errorMessage = "Business hours are 9 AM to 5 PM--this appointment is not within business hours.";
            }
            if (appointmentDate.getDayOfWeek().toString().toUpperCase().equals("SATURDAY") || appointmentDate.getDayOfWeek().toString().toUpperCase().equals("SUNDAY")) {
                errorMessage = "Business days are Monday-Friday.  Please reschedule this appointment.";
            }
            /*else {
                errorMessage = "";
            }*/
        }
        //number catch for time textfields
        catch (NumberFormatException ex) {
            errorMessage = "Start and end times must be integers.";
        }
        finally {
            return errorMessage;
        }
    }
}
