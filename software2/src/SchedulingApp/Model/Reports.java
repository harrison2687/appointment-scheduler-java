package SchedulingApp.Model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static SchedulingApp.Model.DB.updateAppointmentList;
import static SchedulingApp.Model.DB.updateCustomerList;

public class Reports {

    //generate a report for number of appointment types by month
    public static void generateAppointmentTypesByMonth() {
        updateAppointmentList();
        ResourceBundle rb = ResourceBundle.getBundle("Reports", Locale.getDefault());
        String report = rb.getString("lblAppointmentTypeByMonthTitle");
        ArrayList<String> monthsWithAppointments = new ArrayList<>();
        for (Appointment appointment : Appointment.getAppointmentList()) {
            Date startDate = appointment.getStartDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            String yearMonth = year + "-" + month;
            if (month < 10) {
                yearMonth = year + "-0" + month;
            }
            if (!monthsWithAppointments.contains(yearMonth)) {
                monthsWithAppointments.add(yearMonth);
            }
        }
        Collections.sort(monthsWithAppointments);
        for (String yearMonth : monthsWithAppointments) {
            int year = Integer.parseInt(yearMonth.substring(0,4));
            int month = Integer.parseInt(yearMonth.substring(5,7));
            int typeCount = 0;
            ArrayList<String> types = new ArrayList<>();
            for (Appointment appointment : Appointment.getAppointmentList()) {
                Date startDate = appointment.getStartDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);

                int appointmentYear = calendar.get(Calendar.YEAR);
                int appontmentMonth = calendar.get(Calendar.MONTH) + 1;
                if (year == appointmentYear && month == appontmentMonth) {
                    String type = appointment.getType();
                    if (!types.contains(type)) {
                        types.add(type);
                        typeCount++;
                    }
                }
            }
            //format year-month to report
            report = report + yearMonth + ":" + typeCount + "\n";
            report = report + rb.getString("lblTypes");
            for (String type : types) {
                report = report + " " + type + ",";
            }
            report = report.substring(0, report.length()-1);
            report = report + "\n \n";
        }
        try {
            Path path = Paths.get("Appointment_Type_By_Month.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //generate a consultant schedule report, showing type and title of appointment
    public static void generateScheduleForConsultants() {
        updateAppointmentList();
        ResourceBundle rb = ResourceBundle.getBundle("Reports", Locale.getDefault());
        String report = rb.getString("lblConsultantScheduleTitle");
        ArrayList<String> consultantsWithAppointments = new ArrayList<>();
        for (Appointment appointment : Appointment.getAppointmentList()) {
            String consultant = appointment.getCreatedBy();
            if (!consultantsWithAppointments.contains(consultant)) {
                consultantsWithAppointments.add(consultant);
            }
        }

        Collections.sort(consultantsWithAppointments);
        for (String consultant : consultantsWithAppointments) {
            report = report + consultant + ": \n";
            for (Appointment appointment : Appointment.getAppointmentList()) {
                String appointmentConsultant = appointment.getCreatedBy();
                if (consultant.equals(appointmentConsultant)) {
                    //get appointment date, type and title
                    String date = appointment.getDateString();
                    String type = appointment.getType();
                    String title = appointment.getTitle();
                    Date startDate = appointment.getStartDate();
                    //modify times to AM/PM format
                    String startTime = startDate.toString().substring(11,16);
                    if (Integer.parseInt(startTime.substring(0,2)) > 12) {
                        startTime = Integer.parseInt(startTime.substring(0,2)) - 12 + startTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(startTime.substring(0,2)) == 12) {
                        startTime = startTime + "PM";
                    }
                    else {
                        startTime = startTime + "AM";
                    }
                    Date endDate = appointment.getEndDate();
                    String endTime = endDate.toString().substring(11,16);
                    if (Integer.parseInt(endTime.substring(0,2)) > 12) {
                        endTime = Integer.parseInt(endTime.substring(0,2)) - 12 + endTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(endTime.substring(0,2)) == 12) {
                        endTime = endTime + "PM";
                    }
                    else {
                        endTime = endTime + "AM";
                    }
                    String timeZone = startDate.toString().substring(20,23);
                    //add appointment info to report
                    report = report + date + ": Type: " + type + ", Title: " + title + rb.getString("lblFrom") + startTime + rb.getString("lblTo") +
                            endTime + " " + timeZone + ". \n";
                }
            }
            //add paragraph break between consultants
            report = report + "\n \n";
        }
        //print report to Schedule_By_Consultant.txt, and overwrites file.
        try {
            Path path = Paths.get("Schedule_By_Consultant.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    //create report for each customer's upcoming meetings, with types
    public static void generateUpcomingMeetingsByCustomer() {
        updateAppointmentList();
        ResourceBundle rb = ResourceBundle.getBundle("Reports", Locale.getDefault());
        //initialize report string
        String report = rb.getString("lblCustomerScheduleTitle");
        ArrayList<Integer> customerIdsWithAppointments = new ArrayList<>();
        //check customerId of each appointment then adds new customer to arraylist
        for (Appointment appointment : Appointment.getAppointmentList()) {
            int customerId = appointment.getCustomerId();
            if (!customerIdsWithAppointments.contains(customerId)) {
                customerIdsWithAppointments.add(customerId);
            }
        }
        //sort customerId's
        Collections.sort(customerIdsWithAppointments);
        updateCustomerList();
        for (int customerId : customerIdsWithAppointments) {
            for (Customer customer : Customer.getCustomerList()) {
                int customerIdToCheck = customer.getCustomerId();
                if (customerId == customerIdToCheck) {
                    // Add customer name to report
                    report = report + customer.getCustomerName() + ": \n";
                }
            }
            for (Appointment appointment : Appointment.getAppointmentList()) {
                int appointmentCustomerId = appointment.getCustomerId();
                // Check if appointment's customerId matches customer
                if (customerId == appointmentCustomerId) {
                    // Get appointment date and type
                    String date = appointment.getDateString();
                    String type = appointment.getType();
                    Date startDate = appointment.getStartDate();
                    // Modify times to AM/PM format
                    String startTime = startDate.toString().substring(11,16);
                    if (Integer.parseInt(startTime.substring(0,2)) > 12) {
                        startTime = Integer.parseInt(startTime.substring(0,2)) - 12 + startTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(startTime.substring(0,2)) == 12) {
                        startTime = startTime + "PM";
                    }
                    else {
                        startTime = startTime + "AM";
                    }
                    Date endDate = appointment.getEndDate();
                    String endTime = endDate.toString().substring(11,16);
                    if (Integer.parseInt(endTime.substring(0,2)) > 12) {
                        endTime = Integer.parseInt(endTime.substring(0,2)) - 12 + endTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(endTime.substring(0,2)) == 12) {
                        endTime = endTime + "PM";
                    }
                    else {
                        endTime = endTime + "AM";
                    }
                    String timeZone = startDate.toString().substring(20,23);
                    //add appointment info to report
                    report = report + date + ": " + type + rb.getString("lblFrom") + startTime + rb.getString("lblTo") +
                            endTime + " " + timeZone + ". \n";
                }
            }
            //add paragraph break between customers
            report = report + "\n \n";
        }
        //print report to ScheduleByCustomer.txt and overwrites the file
        try {
            Path path = Paths.get("Customer_Upcoming_Schedule.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
