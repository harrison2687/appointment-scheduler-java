package SchedulingApp.ViewController;

import SchedulingApp.Model.Reports;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class ReportsController {

    @FXML
    private Button btnGenerateAppointmentTypeByMonth;
    @FXML
    private Button btnGenerateConsultantSchedule;
    @FXML
    private Button btnGenerateCustomerSchedule;
    @FXML
    private Button btnReportsExit;


    private void exit(ActionEvent event) {
        try {
            Parent mainScreenParent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
            Scene mainScreenScene = new Scene(mainScreenParent);
            Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainScreenStage.setScene(mainScreenScene);
            mainScreenStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        //use lambda to assign actions to the buttons
        btnGenerateAppointmentTypeByMonth.setOnAction(event -> Reports.generateAppointmentTypesByMonth());
        btnGenerateConsultantSchedule.setOnAction(event -> Reports.generateScheduleForConsultants());
        btnGenerateCustomerSchedule.setOnAction(event -> Reports.generateUpcomingMeetingsByCustomer());
        btnReportsExit.setOnAction(event -> exit(event));
    }
}