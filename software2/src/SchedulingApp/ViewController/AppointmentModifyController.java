package SchedulingApp.ViewController;

import SchedulingApp.Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static SchedulingApp.Model.Appointment.getAppointmentList;
import static SchedulingApp.Model.Customer.getCustomerList;
import static SchedulingApp.Model.DB.modifyAppointment;

public class AppointmentModifyController {

    @FXML private TextField txtAppointmentModTitle;
    @FXML private TextArea txtAppointmentModDescription;
    @FXML private TextField txtAppointmentModLocation;
    @FXML private TextField txtAppointmentModContact;
    @FXML private ComboBox<String> cbAppointmentModType;
    @FXML private DatePicker dpAppointmentModDate;
    @FXML private TextField txtAppointmentModStartH;
    @FXML private TextField txtAppointmentModStartM;
    @FXML private ChoiceBox<String> choiceAppointmentModStartAMPM;
    @FXML private TextField txtAppointmentModEndH;
    @FXML private TextField txtAppointmentModEndM;
    @FXML private ChoiceBox<String> choiceAppointmentModEndAMPM;
    @FXML private TableView<Customer> tvAppointmentAddMod;
    @FXML private TableColumn<Customer, String> tvAppointmentModNameColumn;
    //@FXML private TableColumn<Customer, String> tvAppointmentModCityColumn;
    //@FXML private TableColumn<Customer, String> tvAppointmentModCountryColumn;
    @FXML private TableColumn<Customer, String> tvAppointmentModPhoneColumn;
    @FXML private Button btnAppointmentAddMod;
    @FXML private TableView<Customer> tvAppointmentModDelete;
    @FXML private TableColumn<Customer, String> tvAppointmentModDeleteNameColumn;
    //@FXML private TableColumn<Customer, String> tvAppointmentModDeleteCityColumn;
    //@FXML private TableColumn<Customer, String> tvAppointmentModDeleteCountryColumn;
    @FXML private TableColumn<Customer, String> tvAppointmentModDeletePhoneColumn;
    @FXML private Button btnAppointmentModDelete;
    @FXML private Button btnAppointmentModSave;
    @FXML private Button btnAppointmentModCancel;

    private Appointment appointment;

    int appointmentIndexToModify = AppointmentSummaryController.getAppointmentIndexToModify();

    private ObservableList<Customer> currentCustomers = FXCollections.observableArrayList();

    private final ObservableList<String> types = FXCollections.observableArrayList(
            "Accounts-Onboarding", "Accounts-Offboarding", "Consultation-Portfolio", "Consultation-Audit", "Consultation-General", "Miscellaneous", "Presentation");

    @FXML
    private void addCustomerToDeleteTableView(ActionEvent event) {
        Customer customer = tvAppointmentAddMod.getSelectionModel().getSelectedItem();
        if (customer == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error while adding Customer");
            alert.setContentText("Please select a customer entry to associate it with the appointment.");
            alert.showAndWait();
            return;
        }
        if (currentCustomers.size() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error while adding Customer");
            alert.setContentText("Please select a single customer to associate with the appointment.");
            alert.showAndWait();
            return;
        }
        currentCustomers.add(customer);
        updateAppointmentModDeleteTableView();
    }

    @FXML
    private void deleteCustomerFromDeleteTableView(ActionEvent event) {
        Customer customer = tvAppointmentModDelete.getSelectionModel().getSelectedItem();
        if (customer == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error while removing Customer");
            alert.setContentText("Please select a customer entry to remove it.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to remove this customer?");
        alert.setContentText("Press OK to confirm removing the selected customer.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            currentCustomers.remove(customer);
            updateAppointmentModDeleteTableView();
        }
    }

    @FXML
    private void saveModifyAppointment(ActionEvent event) {
        Customer customer = null;
        if (currentCustomers.size() == 1) {
            customer = currentCustomers.get(0);
        }
        int appointmentId = appointment.getAppointmentId();
        String title = txtAppointmentModTitle.getText();
        String description = txtAppointmentModDescription.getText();
        String location = txtAppointmentModLocation.getText();
        String contact = txtAppointmentModContact.getText();
        if (contact.length() == 0 && customer != null) {
            contact = customer.getCustomerName() + ", " + customer.getPhone();
        }
        String type = cbAppointmentModType.getSelectionModel().getSelectedItem();
        LocalDate appointmentDate = dpAppointmentModDate.getValue();
        String startH = txtAppointmentModStartH.getText();
        String startM = txtAppointmentModStartM.getText();
        String startAMPM = choiceAppointmentModStartAMPM.getSelectionModel().getSelectedItem();
        String endH = txtAppointmentModEndH.getText();
        String endM = txtAppointmentModEndM.getText();
        String endAMPM = choiceAppointmentModEndAMPM.getSelectionModel().getSelectedItem();

        String errorMessage = Appointment.appointmentValid(customer, title, description, location, type, appointmentDate,
                startH, startM, startAMPM, endH, endM, endAMPM);
        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error while modifying Appointment");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return;
        }
        SimpleDateFormat ldf = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        ldf.setTimeZone(TimeZone.getDefault());
        Date startLocal = null;
        Date endLocal = null;
        try {
            startLocal = ldf.parse(appointmentDate.toString() + " " + startH + ":" + startM + " " + startAMPM);
            endLocal = ldf.parse(appointmentDate.toString() + " " + endH + ":" + endM + " " + endAMPM);
        }
        catch (ParseException ex) {
            ex.printStackTrace();
        }
        ZonedDateTime startUTC = ZonedDateTime.ofInstant(startLocal.toInstant(), ZoneId.of("UTC"));
        ZonedDateTime endUTC = ZonedDateTime.ofInstant(endLocal.toInstant(), ZoneId.of("UTC"));
        if (modifyAppointment(appointmentId, customer, title, description, location, contact, type, startUTC, endUTC)) {
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void cancelModifyAppointment(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Cancel modifying Appointment");
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setContentText("Press OK to cancel out and return to the Main Screen.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void updateAppointmentModTableView() {
        tvAppointmentAddMod.setItems(Customer.getCustomerList());
    }

    public void updateAppointmentModDeleteTableView() {
        tvAppointmentModDelete.setItems(currentCustomers);
    }

    @FXML
    public void initialize() {
        appointment = getAppointmentList().get(appointmentIndexToModify);

        cbAppointmentModType.setItems(types);

        String title = appointment.getTitle();
        String description = appointment.getDescription();
        String location = appointment.getLocation();
        String contact = appointment.getContact();
        String type = appointment.getType();
        Date appointmentDate = appointment.getStartDate();

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(appointmentDate);
        int appointmentYear = calendar.get(Calendar.YEAR);
        int appointmentMonth = calendar.get(Calendar.MONTH) + 1;
        int appointmentDay = calendar.get(Calendar.DAY_OF_MONTH);
        LocalDate appointmentLocalDate = LocalDate.of(appointmentYear, appointmentMonth, appointmentDay);
        String startString = appointment.getStartString();
        String startHour = startString.substring(0, 2);
        if (Integer.parseInt(startHour) < 10) {
            startHour = startHour.substring(1, 2);
        }
        String startMinute = startString.substring(3, 5);
        String startAMPM = startString.substring(6, 8);
        String endString = appointment.getEndString();
        String endHour = endString.substring(0, 2);
        if (Integer.parseInt(endHour) < 10) {
            endHour = endHour.substring(1, 2);
        }
        String endMinute = endString.substring(3, 5);
        String endAMPM = endString.substring(6, 8);

        int customerId = appointment.getCustomerId();
        ObservableList<Customer> customerList = getCustomerList();
        for (Customer customer : customerList) {
            if (customer.getCustomerId() == customerId) {
                currentCustomers.add(customer);
            }
        }

        //populate information tables for appointment modifications
        txtAppointmentModTitle.setText(title);
        txtAppointmentModDescription.setText(description);
        txtAppointmentModLocation.setText(location);
        txtAppointmentModContact.setText(contact);
        cbAppointmentModType.setValue(type);
        dpAppointmentModDate.setValue(appointmentLocalDate);
        txtAppointmentModStartH.setText(startHour);
        txtAppointmentModStartM.setText(startMinute);
        choiceAppointmentModStartAMPM.setValue(startAMPM);
        txtAppointmentModEndH.setText(endHour);
        txtAppointmentModEndM.setText(endMinute);
        choiceAppointmentModEndAMPM.setValue(endAMPM);

        //lambdas to assign actions to buttons
        btnAppointmentAddMod.setOnAction(event -> addCustomerToDeleteTableView(event));
        btnAppointmentModDelete.setOnAction(event -> deleteCustomerFromDeleteTableView(event));
        btnAppointmentModSave.setOnAction(event -> saveModifyAppointment(event));
        btnAppointmentModCancel.setOnAction(event -> cancelModifyAppointment(event));

        //lambdas to populate customer tables
        tvAppointmentModNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        //tvAppointmentModCityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        //tvAppointmentModCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        tvAppointmentModPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        tvAppointmentModDeleteNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        //tvAppointmentModDeleteCityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        //tvAppointmentModDeleteCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        tvAppointmentModDeletePhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());

        //update customer tables to reflect customer selection activities
        updateAppointmentModTableView();
        updateAppointmentModDeleteTableView();

    }




}
