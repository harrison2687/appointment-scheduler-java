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
import java.time.YearMonth;

public class MonthlyCalendarView {

    private Text monthTitle;
    private YearMonth currentYearMonth;
    private ArrayList<AnchorPaneNode> calendarDayPanes = new ArrayList<>(35);
    private VBox monthlyCalendarView;

    public MonthlyCalendarView(YearMonth yearMonth) {
        currentYearMonth = yearMonth;
        GridPane calendar = new GridPane();
        calendar.setPrefSize(600, 400);
        calendar.setGridLinesVisible(true);

        for (int i=0; i<5; i++) {
            for (int j=0; j<7; j++) {
                AnchorPaneNode ap = new AnchorPaneNode();
                ap.setPrefSize(200, 200);
                calendar.add(ap, j, i);
                calendarDayPanes.add(ap);
            }
        }

        //setting days of the week on the calendar
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

        monthTitle = new Text();
        Button btnBackOneMonth = new Button("<");
        btnBackOneMonth.setOnAction(event -> backOneMonth());
        Button btnForwardOneMonth = new Button(">");
        btnForwardOneMonth.setOnAction(event -> forwardOneMonth());
        HBox titleBar = new HBox(btnBackOneMonth, monthTitle, btnForwardOneMonth);
        titleBar.setAlignment(Pos.BASELINE_CENTER);

        populateCalendar(yearMonth);

        monthlyCalendarView = new VBox(titleBar, dayLabels, calendar);
    }

    //populating calendar and setting the first day as Sunday
    public void populateCalendar(YearMonth yearMonth) {
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY")) {
            calendarDate = calendarDate.minusDays(1);
        }

        String localizedMonth = new DateFormatSymbols().getMonths()[yearMonth.getMonthValue()-1];
        String properMonth = localizedMonth.substring(0,1).toUpperCase() + localizedMonth.substring(1);
        monthTitle.setText("  " + properMonth + " " + String.valueOf(yearMonth.getYear()) + "  ");

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
                appointmentsForDay.setFont(Font.font(28));
                appointmentsForDay.setFill(Color.GREEN);
                ap.getChildren().add(appointmentsForDay);
                ap.setTopAnchor(appointmentsForDay, 20.0);
                ap.setLeftAnchor(appointmentsForDay, 40.0);
            }
            calendarDate = calendarDate.plusDays(1);
        }
    }

    private void backOneMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth);
    }

    private void forwardOneMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth);
    }

    public VBox getView() {
        return monthlyCalendarView;
    }

    public YearMonth getCurrentYearMonth() {
        return currentYearMonth;
    }
}
