package SchedulingApp.ViewController;

import SchedulingApp.Model.Customer;
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
import java.util.Optional;

import static SchedulingApp.Model.DB.addNewCustomer;

public class CustomerAddController {

    @FXML private TextField txtCustomerAddName;
    @FXML private TextField txtCustomerAddAddress;
    @FXML private TextField txtCustomerAddAddress2;
    @FXML private TextField txtCustomerAddCity;
    @FXML private TextField txtCustomerAddCountry;
    @FXML private TextField txtCustomerAddPostalCode;
    @FXML private TextField txtCustomerAddPhone;
    @FXML private Button btnCustomerAddSave;
    @FXML private Button btnCustomerAddCancel;

    @FXML
    private void saveCustomerAdd(ActionEvent event) {
        String customerName = txtCustomerAddName.getText();
        String customerAddress = txtCustomerAddAddress.getText();
        String customerAddress2 = txtCustomerAddAddress2.getText();
        String customerCity = txtCustomerAddCity.getText();
        String customerCountry = txtCustomerAddCountry.getText();
        String customerPostalCode = txtCustomerAddPostalCode.getText();
        String customerPhone = txtCustomerAddPhone.getText();

        String errorMessage = Customer.customerValid(customerName, customerAddress, customerCity, customerCountry, customerPostalCode, customerPhone);
        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error while adding Customer");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return;
        }
        try {
            addNewCustomer(customerName, customerAddress, customerAddress2, customerCity, customerCountry, customerPostalCode, customerPhone);
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

    @FXML
    private void cancelCustomerAdd(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Cancel adding Customer");
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

    @FXML
    public void initialize() {
        //using lambdas to assign events to buttons
        btnCustomerAddSave.setOnAction(event -> saveCustomerAdd(event));
        btnCustomerAddCancel.setOnAction(event -> cancelCustomerAdd(event));
    }
}
