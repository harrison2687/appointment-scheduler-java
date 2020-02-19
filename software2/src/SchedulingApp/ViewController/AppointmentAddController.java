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
import static SchedulingApp.Model.DB.addNewAppointment;

public class AppointmentAddController {

    @FXML private TextField txtAppointmentAddTitle;
    @FXML private TextArea txtAppointmentAddDescription;
    @FXML private TextField txtAppointmentAddLocation;
    @FXML private TextField txtAppointmentAddContact;
    @FXML private ComboBox<String> cbAppointmentAddType;
    @FXML private DatePicker dpAppointmentAddDate;
    @FXML private TextField txtAppointmentAddStartH;
    @FXML private TextField txtAppointmentAddStartM;
    @FXML private ChoiceBox<String> choiceAppointmentAddStartAMPM;
    @FXML private TextField txtAppointmentAddEndH;
    @FXML private TextField txtAppointmentAddEndM;
    @FXML private ChoiceBox<String> choiceAppointmentAddEndAMPM;
    @FXML private TableView<Customer> tvAppointmentAddAdd;
    @FXML private TableColumn<Customer, String> tvAppointmentAddNameColumn;
    @FXML private TableColumn<Customer, String> tvAppointmentAddPhoneColumn;
    @FXML private Button btnAppointmentAddAdd;
    @FXML private TableView<Customer> tvAppointmentAddDelete;
    @FXML private TableColumn<Customer, String> tvAppointmentAddDeleteNameColumn;
    @FXML private TableColumn<Customer, String> tvAppointmentAddDeletePhoneColumn;
    @FXML private Button btnAppointmentAddDelete;
    @FXML private Button btnAppointmentAddSave;
    @FXML private Button btnAppointmentAddCancel;

    private ObservableList<Customer> currentCustomers = FXCollections.observableArrayList();

    private final ObservableList<String> types = FXCollections.observableArrayList(
            "Accounts-Onboarding", "Accounts-Offboarding", "Consultation-Portfolio", "Consultation-Audit", "Consultation-General", "Miscellaneous", "Presentation");

    @FXML
    private void addCustomerToDeleteTableView(ActionEvent event) {
        Customer customer = tvAppointmentAddAdd.getSelectionModel().getSelectedItem();
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
        updateAppointmentAddDeleteTableView();
    }

    @FXML
    private void deleteCustomerFromDeleteTableView(ActionEvent event) {
        Customer customer = tvAppointmentAddDelete.getSelectionModel().getSelectedItem();
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
            updateAppointmentAddDeleteTableView();
        }
    }

    @FXML
    private void saveAppointment(ActionEvent event) {
        Customer customer = null;
        if (currentCustomers.size() == 1) {
            customer = currentCustomers.get(0);
        }
        String title = txtAppointmentAddTitle.getText();
        String description = txtAppointmentAddDescription.getText();
        String location = txtAppointmentAddLocation.getText();
        String contact = txtAppointmentAddContact.getText();
        if (contact.length() == 0 && customer != null) {
            contact = customer.getCustomerName() + ", " + customer.getPhone();
        }
        String type = cbAppointmentAddType.getValue();
        LocalDate appointmentDate = dpAppointmentAddDate.getValue();
        String startH = txtAppointmentAddStartH.getText();
        String startM = txtAppointmentAddStartM.getText();
        String startAMPM = choiceAppointmentAddStartAMPM.getSelectionModel().getSelectedItem();
        String endH = txtAppointmentAddEndH.getText();
        String endM = txtAppointmentAddEndM.getText();
        String endAMPM = choiceAppointmentAddEndAMPM.getSelectionModel().getSelectedItem();

        String errorMessage = Appointment.appointmentValid(customer, title, description, location, type, appointmentDate,
                startH, startM, startAMPM, endH, endM, endAMPM);
        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error while adding Appointment");
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
        if (addNewAppointment(customer, title, description, location, contact, type, startUTC, endUTC)) {
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
    private void cancelAppointment(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Cancel adding Appointment");
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

    public void updateAppointmentAddTableView() {
        tvAppointmentAddAdd.setItems(Customer.getCustomerList());
    }

    public void updateAppointmentAddDeleteTableView() {
        tvAppointmentAddDelete.setItems(currentCustomers);
    }

    @FXML
    public void initialize() {
        //populating Types combobox with appointment types
        cbAppointmentAddType.setItems(types);

        //using lambdas to assign actions to buttons
        btnAppointmentAddAdd.setOnAction(event -> addCustomerToDeleteTableView(event));
        btnAppointmentAddDelete.setOnAction(event -> deleteCustomerFromDeleteTableView(event));
        btnAppointmentAddSave.setOnAction(event -> saveAppointment(event));
        btnAppointmentAddCancel.setOnAction(event -> cancelAppointment(event));

        //using lambdas to populate tables
        tvAppointmentAddNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        tvAppointmentAddPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        tvAppointmentAddDeleteNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        tvAppointmentAddDeletePhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());

        updateAppointmentAddTableView();
        updateAppointmentAddDeleteTableView();

    }


}
