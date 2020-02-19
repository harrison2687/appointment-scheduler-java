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

import static SchedulingApp.Model.Customer.getCustomerList;
import static SchedulingApp.Model.DB.*;

public class CustomerModifyController {

    @FXML private TextField txtCustomerModName;
    @FXML private TextField txtCustomerModAddress;
    @FXML private TextField txtCustomerModAddress2;
    @FXML private TextField txtCustomerModCity;
    @FXML private TextField txtCustomerModCountry;
    @FXML private TextField txtCustomerModPostalCode;
    @FXML private TextField txtCustomerModPhone;
    @FXML private Button btnCustomerModSave;
    @FXML private Button btnCustomerModCancel;

    private Customer customer;
    int customerIndexToModify = MainScreenController.getCustomerIndexToModify();

    @FXML
    private void saveCustomerMod(ActionEvent event) {
        int customerId = customer.getCustomerId();
        String customerName = txtCustomerModName.getText();
        String customerAddress = txtCustomerModAddress.getText();
        String customerAddress2 = txtCustomerModAddress2.getText();
        String customerCity = txtCustomerModCity.getText();
        String customerCountry = txtCustomerModCountry.getText();
        String customerPostalCode = txtCustomerModPostalCode.getText();
        String customerPhone = txtCustomerModPhone.getText();

        String errorMessage = Customer.customerValid(customerName, customerAddress, customerCity, customerCountry, customerPostalCode, customerPhone);

        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error while modifying Customer");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return;
        }
        int modifyCustomerCheck = modifyCustomer(customerId, customerName, customerAddress, customerAddress2, customerCity, customerCountry, customerPostalCode, customerPhone);
        if (modifyCustomerCheck == 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error while modifying Customer");
            alert.setContentText("This customer already exists in the database.");
            alert.showAndWait();

        }
        else if (modifyCustomerCheck == 0) {
            int countryId = retrieveCountryId(customerCountry);
            int cityId = retrieveCityId(customerCity, countryId);
            int addressId = retrieveAddressId(customerAddress, customerAddress2, customerPostalCode, customerPhone, cityId);
            setCustomertoActive(customerName, addressId);
        }
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

    @FXML
    private void cancelCustomerMod(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Cancel modifying Customer");
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
        //get customer from table
        customer = getCustomerList().get(customerIndexToModify);

        //grab customer variables
        String customerName = customer.getCustomerName();
        String address = customer.getAddress();
        String address2 = customer.getAddress2();
        String city = customer.getCity();
        String country = customer.getCountry();
        String postalCode = customer.getPostalCode();
        String phone = customer.getPhone();

        //populate textfields with customer variables
        txtCustomerModName.setText(customerName);
        txtCustomerModAddress.setText(address);
        txtCustomerModAddress2.setText(address2);
        txtCustomerModCity.setText(city);
        txtCustomerModCountry.setText(country);
        txtCustomerModPostalCode.setText(postalCode);
        txtCustomerModPhone.setText(phone);

        //using lambdas to assign events to buttons
        btnCustomerModSave.setOnAction(event -> saveCustomerMod(event));
        btnCustomerModCancel.setOnAction(event -> cancelCustomerMod(event));
    }
}
