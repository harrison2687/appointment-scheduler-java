package SchedulingApp.ViewController;

import SchedulingApp.Calendar.AnchorPaneNode;
import SchedulingApp.Calendar.MonthlyCalendarView;
import SchedulingApp.Calendar.WeeklyCalendarView;
import SchedulingApp.Model.Appointment;
import SchedulingApp.Model.Customer;
import static SchedulingApp.Model.Customer.getCustomerList;
import static SchedulingApp.Model.DB.setCustomerInactive;
import static SchedulingApp.Model.DB.updateAppointmentList;
import static SchedulingApp.Model.DB.updateCustomerList;
import static SchedulingApp.Model.DB.ApptIn15;

import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class MainScreenController {

    @FXML private GridPane mainScreenGrid;
    @FXML private Button btnMainScreenToggleView;
    @FXML private Button btnMainScreenCurrentDate;
    @FXML private Button btnAddAppointment;
    @FXML private Button btnAppointmentSummary;
    @FXML private Button btnReports;
    @FXML private Button btnAddCustomer;
    @FXML private Button btnModifyCustomer;
    @FXML private Button btnRemoveCustomer;
    @FXML private Button btnExit;
    /*@FXML private TableView<Appointment> tvAppointmentSummary;
    @FXML private TableColumn<Appointment, String> tvAppointmentSummaryDateColumn;
    @FXML private TableColumn<Appointment, String> tvAppointmentSummaryStartColumn;
    @FXML private TableColumn<Appointment, String> tvAppointmentSummaryEndColumn;
    @FXML private TableColumn<Appointment, String> tvAppointmentSummaryCustomerColumn;
    @FXML private TableColumn<Appointment, String> tvAppointmentSummaryTitleColumn;
    @FXML private TableColumn<Appointment, String> tvAppointmentSummaryDescriptionColumn;
    @FXML private TableColumn<Appointment, String> tvAppointmentSummaryLocationColumn;
    @FXML private TableColumn<Appointment, String> tvAppointmentSummaryContactColumn;*/
    @FXML private TableView<Customer> tvCustomers;
    @FXML private TableColumn<Customer, String> tvCustomerNameColumn;
    @FXML private TableColumn<Customer, String> tvCustomersAddressColumn;
    @FXML private TableColumn<Customer, String> tvCustomersAddress2Column;
    @FXML private TableColumn<Customer, String> tvCustomersCityColumn;
    @FXML private TableColumn<Customer, String> tvCustomersCountryColumn;
    @FXML private TableColumn<Customer, String> tvCustomersPostalCodeColumn;

    private static int customerIndexToModify;

    private boolean monthlyView = true;

    private final DateTimeFormatter df = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    private MonthlyCalendarView monthlyCalendar;
    private WeeklyCalendarView weeklyCalendar;
    private VBox monthlyCalendarView;
    private VBox weeklyCalendarView;

    @FXML
    private void openAddAppointment(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("AppointmentAdd.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void openAppointmentSummary(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("AppointmentSummary.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void openReports(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("Reports.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void openAddCustomer(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("CustomerAdd.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void openModifyCustomer(ActionEvent event) {
        Customer customerToModify = tvCustomers.getSelectionModel().getSelectedItem();
        if (customerToModify == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Modifying Customer");
            alert.setContentText("Please select a customer before modifying them.");
            alert.showAndWait();
            return;
        }
        customerIndexToModify = getCustomerList().indexOf(customerToModify);
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("CustomerModify.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void removeCustomer(ActionEvent event) {
        Customer customerToRemove = tvCustomers.getSelectionModel().getSelectedItem();
        if (customerToRemove == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Modifying Customer");
            alert.setContentText("Please select a customer before modifying them.");
            alert.showAndWait();
            return;
        }
        setCustomerInactive(customerToRemove);
    }

    @FXML
    private void exit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Exit Program");
        alert.setHeaderText("Confirm exiting program?");
        alert.setContentText("Press OK to leave the program.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    @FXML
    private void goToCurrentDate(ActionEvent event) {
        if (monthlyView) {
            mainScreenGrid.getChildren().remove(monthlyCalendarView);
            YearMonth currentYearMonth = YearMonth.now();
            monthlyCalendar = new MonthlyCalendarView(currentYearMonth);
            monthlyCalendarView = monthlyCalendar.getView();
            mainScreenGrid.add(monthlyCalendarView, 0, 0);
        }
        else {
            mainScreenGrid.getChildren().remove(weeklyCalendarView);
            LocalDate currentLocalDate = LocalDate.now();
            weeklyCalendar = new WeeklyCalendarView(currentLocalDate);
            weeklyCalendarView = weeklyCalendar.getView();
            mainScreenGrid.add(weeklyCalendarView, 0, 0);
        }
    }

    @FXML
    private void toggleCalendarView(ActionEvent event) {
        if (monthlyView) {
            mainScreenGrid.getChildren().remove(monthlyCalendarView);
            YearMonth currentYearMonth = monthlyCalendar.getCurrentYearMonth();
            LocalDate currentLocalDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonth(), 1);
            weeklyCalendar = new WeeklyCalendarView(currentLocalDate);
            weeklyCalendarView = weeklyCalendar.getView();
            mainScreenGrid.add(weeklyCalendarView, 0, 0);
            btnMainScreenToggleView.setText("Monthly View");
            monthlyView = false;
        }
        else {
            mainScreenGrid.getChildren().remove(weeklyCalendarView);
            LocalDate currentLocalDate = weeklyCalendar.getCurrentLocalDate();
            YearMonth currentYearMonth = YearMonth.from(currentLocalDate);
            monthlyCalendar = new MonthlyCalendarView(currentYearMonth);
            monthlyCalendarView = monthlyCalendar.getView();
            mainScreenGrid.add(monthlyCalendarView, 0, 0);
            btnMainScreenToggleView.setText("Weekly View");
            monthlyView = true;
        }
    }

    public static int getCustomerIndexToModify() {
        return customerIndexToModify;
    }

    public void updateCustomerTableView() {
        updateCustomerList();
        tvCustomers.setItems(getCustomerList());
    }

    @FXML
    public void initialize() {
        //using lambdas to assign actions to buttons more efficiently
        btnAddAppointment.setOnAction(event -> openAddAppointment(event));
        btnAppointmentSummary.setOnAction(event -> openAppointmentSummary(event));
        btnReports.setOnAction(event -> openReports(event));
        btnMainScreenCurrentDate.setOnAction(event -> goToCurrentDate(event));
        btnMainScreenToggleView.setOnAction(event -> toggleCalendarView(event));
        btnAddCustomer.setOnAction(event -> openAddCustomer(event));
        btnModifyCustomer.setOnAction(event -> openModifyCustomer(event));
        btnRemoveCustomer.setOnAction(event -> removeCustomer(event));
        btnExit.setOnAction(event -> exit(event));

        updateAppointmentList();

        monthlyCalendar = new MonthlyCalendarView(YearMonth.now());
        monthlyCalendarView = monthlyCalendar.getView();
        mainScreenGrid.add(monthlyCalendarView, 0, 0);

        /*tvAppointmentSummaryDateColumn.setCellValueFactory(cellData -> cellData.getValue().dateStringProperty());
        tvAppointmentSummaryStartColumn.setCellValueFactory(cellData -> cellData.getValue().startStringProperty());
        tvAppointmentSummaryEndColumn.setCellValueFactory(cellData -> cellData.getValue().endStringProperty());
        //tvAppointmentSummaryCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        tvAppointmentSummaryTitleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        tvAppointmentSummaryDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        tvAppointmentSummaryLocationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        tvAppointmentSummaryContactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());*/

        //using lambdas here to populate the customer table columns
        tvCustomerNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        tvCustomersAddressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        tvCustomersAddress2Column.setCellValueFactory(cellData -> cellData.getValue().address2Property());
        tvCustomersCityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        tvCustomersCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        tvCustomersPostalCodeColumn.setCellValueFactory(cellData -> cellData.getValue().postalCodeProperty());

        updateCustomerTableView();

        ApptIn15();

    }

}
