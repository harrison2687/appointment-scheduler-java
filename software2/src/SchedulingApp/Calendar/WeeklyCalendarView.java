package SchedulingApp.Calendar;

import SchedulingApp.Model.Appointment;

import java.util.*;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.text.DateFormatSymbols;
import java.time.LocalDate;

public class WeeklyCalendarView {

    private Text weekTitle;
    private LocalDate currentLocalDate;
    private ArrayList<AnchorPaneNode> calendarDayPanes = new ArrayList<>(7);
    private VBox weeklyCalendarView;

    public WeeklyCalendarView(LocalDate localDate) {

        currentLocalDate = localDate;

        GridPane calendar = new GridPane();
        calendar.setPrefSize(600, 400);
        calendar.setGridLinesVisible(true);

        for (int i=0; i<7; i++) {
            AnchorPaneNode ap = new AnchorPaneNode();
            ap.setPrefSize(200, 200);
            calendar.add(ap, i, 0);
            calendarDayPanes.add(ap);
        }

        Text[] daysOfWeek;
        daysOfWeek = new Text[]{
                new Text("Sunday"), new Text("Monday"), new Text("Tuesday"), new Text("Wednesday"), new Text("Thursday"),
                new Text("Friday"), new Text("Saturday")};
        GridPane dayLabels = new GridPane();
        dayLabels.setPrefWidth(600);
        int col = 0;
        for (Text day : daysOfWeek) {
            AnchorPane ap = new AnchorPane();
            ap.setPrefSize(200, 10);
            ap.setBottomAnchor(day, 5.0);
            day.setWrappingWidth(100);
            day.setTextAlignment(TextAlignment.CENTER);
            day.setWrappingWidth(100);
            day.setTextAlignment(TextAlignment.CENTER);
            ap.getChildren().add(day);
            dayLabels.add(ap, col++, 0);
        }

        weekTitle = new Text();
        Button btnBackOneWeek = new Button("<");
        btnBackOneWeek.setOnAction(event -> backOneWeek());
        Button btnForwardOneWeek = new Button(">");
        btnForwardOneWeek.setOnAction(event -> forwardOneWeek());
        HBox titleBar = new HBox(btnBackOneWeek, weekTitle, btnForwardOneWeek);
        titleBar.setAlignment(Pos.BASELINE_CENTER);

        populateCalendar(localDate);

        weeklyCalendarView = new VBox(titleBar, dayLabels, calendar);
    }

    //setting the first day of the week to Sunday
    public void populateCalendar(LocalDate localDate) {
        LocalDate calendarDate = localDate;
        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY")) {
            calendarDate = calendarDate.minusDays(1);
        }

        LocalDate startDate = calendarDate;
        LocalDate endDate = calendarDate.plusDays(6);
        String localizedStartDateMonth = new DateFormatSymbols().getMonths()[startDate.getMonthValue() - 1];
        String startDateMonthProper = localizedStartDateMonth.substring(0, 1).toUpperCase() + localizedStartDateMonth.substring(1);
        String startDateTitle = startDateMonthProper + " " + startDate.getDayOfMonth();
        String localizedEndDateMonth = new DateFormatSymbols().getMonths()[endDate.getMonthValue() - 1];
        String endDateMonthProper = localizedEndDateMonth.substring(0, 1).toUpperCase() + localizedEndDateMonth.substring(1);
        String endDateTitle = endDateMonthProper + " " + endDate.getDayOfMonth();
        weekTitle.setText("  " + startDateTitle + " - " + endDateTitle + ", " + endDate.getYear() + "  ");

        for (AnchorPaneNode ap : calendarDayPanes) {
            if (ap.getChildren().size() != 0) {
                ap.getChildren().remove(0, ap.getChildren().size());
            }
            Text date = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            ap.setDate(calendarDate);
            ap.setTopAnchor(date, 5.0);
            ap.setLeftAnchor(date, 5.0);
            ap.getChildren().add(date);

            ObservableList<Appointment> appointmentList = Appointment.getAppointmentList();
            int calendarDateYear = calendarDate.getYear();
            int calendarDateMonth = calendarDate.getMonthValue();
            int calendarDateDay = calendarDate.getDayOfMonth();
            int appointmentCount = 0;
            for (Appointment appointment : appointmentList) {
                Date appointmentDate = appointment.getStartDate();
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.setTime(appointmentDate);
                int appointmentYear = calendar.get(Calendar.YEAR);
                int appointmentMonth = calendar.get(Calendar.MONTH) + 1;
                int appointmentDay = calendar.get(Calendar.DAY_OF_MONTH);
                if (calendarDateYear == appointmentYear && calendarDateMonth == appointmentMonth && calendarDateDay == appointmentDay) {
                    appointmentCount++;
                }
            }
            if (appointmentCount != 0) {
                Text appointmentsForDay = new Text(String.valueOf(appointmentCount));
                appointmentsForDay.setFont(Font.font(30));
                appointmentsForDay.setFill(Color.GREEN);
                ap.getChildren().add(appointmentsForDay);
                ap.setTopAnchor(appointmentsForDay, 20.0);
                ap.setLeftAnchor(appointmentsForDay, 40.0);
            }
            calendarDate = calendarDate.plusDays(1);
        }
    }

    private void backOneWeek() {
        currentLocalDate = currentLocalDate.minusWeeks(1);
        populateCalendar(currentLocalDate);
    }

    private void forwardOneWeek() {
        currentLocalDate = currentLocalDate.plusWeeks(1);
        populateCalendar(currentLocalDate);
    }

    public VBox getView() {
        return weeklyCalendarView;
    }

    public LocalDate getCurrentLocalDate() {
        return currentLocalDate;
    }
}
