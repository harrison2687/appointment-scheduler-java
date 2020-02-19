package SchedulingApp.ViewController;

import SchedulingApp.Model.Appointment;
import SchedulingApp.Model.DB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import static SchedulingApp.Model.DB.updateAppointmentList;
import static SchedulingApp.Model.Appointment.getAppointmentList;

public class AppointmentSummaryController {

    @FXML private TableView<Appointment> tvAppointmentSummary;
    @FXML private TableColumn<Appointment, String> tvAppointmentSummaryTitleColumn;
    @FXML private TableColumn<Appointment, String> tvAppointmentSummaryDateColumn;
    @FXML private TableColumn<Appointment, String> tvAppointmentSummaryContactColumn;
    @FXML private Button btnAppointmentSummaryGetInfo;
    @FXML private Button btnAppointmentSummaryModifyAppointment;
    @FXML private Button btnAppointmentSummaryDeleteAppointment;
    @FXML private Button btnAppointmentSummaryExit;
    @FXML private Label lblAppointmentSummaryTitle;
    @FXML private Label lblAppointmentSummaryDescription;
    @FXML private Label lblAppointmentSummaryLocation;
    @FXML private Label lblAppointmentSummaryContact;
    @FXML private Label lblAppointmentSummaryType;
    @FXML private Label lblAppointmentSummaryStartTime;
    @FXML private Label lblAppointmentSummaryEndTime;
    @FXML private Label lblAppointmentSummaryCreatedBy;

    private static int appointmentIndexToModify;

    @FXML
    private void getMoreInfo(ActionEvent event) {
        Appointment appointment = tvAppointmentSummary.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error getting Appointment Info");
            alert.setContentText("Please select an appointment to view more details.");
            alert.showAndWait();
        }
        else {
            lblAppointmentSummaryTitle.setText("Title: " + appointment.getTitle());
            lblAppointmentSummaryDescription.setText("Description: " + appointment.getDescription());
            lblAppointmentSummaryLocation.setText("Location: " + appointment.getLocation());
            lblAppointmentSummaryContact.setText("Contact: " + appointment.getContact());
            lblAppointmentSummaryType.setText("Type: " + appointment.getType());
            lblAppointmentSummaryStartTime.setText("Start Time: " + appointment.getStartString());
            lblAppointmentSummaryEndTime.setText("End Time: " + appointment.getEndString());
            lblAppointmentSummaryCreatedBy.setText("Created By: " + appointment.getCreatedBy());
        }
    }

    @FXML
    private void openModifyAppointment(ActionEvent event) {
        Appointment appointment = tvAppointmentSummary.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error while going to modify Appointment");
            alert.setContentText("Please select an appointment to modify it.");
            alert.showAndWait();
            return;
        }
        appointmentIndexToModify = getAppointmentList().indexOf(appointment);
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("AppointmentModify.fxml"));
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
    private void deleteAppointment(ActionEvent event) {
        Appointment appointmentToDelete = tvAppointmentSummary.getSelectionModel().getSelectedItem();
        if (appointmentToDelete == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error while going to delete Appointment");
            alert.setContentText("Please select an appointment to delete it.");
            alert.showAndWait();
            return;
        }
        DB.deleteAppointment(appointmentToDelete);
    }

    @FXML
    private void exit(ActionEvent event) {
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

    public static int getAppointmentIndexToModify() {
        return appointmentIndexToModify;
    }

    @FXML
    public void updateAddAppointmentTableView() {
        updateAppointmentList();
        tvAppointmentSummary.setItems(Appointment.getAppointmentList());
    }

    @FXML
    public void initialize() {
        //Using lambdas here to assign actions to buttons
        btnAppointmentSummaryGetInfo.setOnAction(event -> getMoreInfo(event));
        btnAppointmentSummaryModifyAppointment.setOnAction(event -> openModifyAppointment(event));
        btnAppointmentSummaryDeleteAppointment.setOnAction(event -> deleteAppointment(event));
        btnAppointmentSummaryExit.setOnAction(event -> exit(event));
        //Using lambdas here to populate table with information
        tvAppointmentSummaryTitleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        tvAppointmentSummaryDateColumn.setCellValueFactory(cellData -> cellData.getValue().dateStringProperty());
        tvAppointmentSummaryContactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());

        updateAddAppointmentTableView();
    }
}
