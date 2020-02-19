package SchedulingApp.ViewController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import static SchedulingApp.Model.DB.checkLogInCreds;

public class LoginController {

    @FXML private Label lblLoginTitle;
    @FXML private Label lblLoginUsername;
    @FXML private TextField txtLoginUsername;
    @FXML private Label lblLoginPassword;
    @FXML private TextField txtLoginPassword;
    @FXML private Label lblLoginErrorMessage;
    @FXML private Button btnLoginSubmit;

    public static int databaseError = 0;

    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("Login", Locale.getDefault());
        lblLoginTitle.setText(rb.getString("lblTitle"));
        lblLoginUsername.setText(rb.getString("lblUsername"));
        lblLoginPassword.setText(rb.getString("lblPassword"));
        btnLoginSubmit.setText(rb.getString("btnSubmit"));
    }

    @FXML
    private void submitLogin(ActionEvent event) {
        String userName = txtLoginUsername.getText();
        String password = txtLoginPassword.getText();
        txtLoginPassword.setText("");
        ResourceBundle rb = ResourceBundle.getBundle("Login", Locale.getDefault());
        if (userName.equals("") || password.equals("")) {
            lblLoginErrorMessage.setText(rb.getString("lblNoUserPass"));
            return;
        }
        boolean correctCredentials = checkLogInCreds(userName, password);
        if (correctCredentials) {
            try {
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage)((Node)event.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else if (databaseError > 0) {
            lblLoginErrorMessage.setText(rb.getString("lblConnectionError"));
        }
        else {
            lblLoginErrorMessage.setText(rb.getString("lblWrongUserPass"));
        }
    }

    @FXML
    public static void incrementDatabaseError() {
        databaseError++;
    }

    @FXML
    public void initialize() {
        setLanguage();
        btnLoginSubmit.setOnAction(event -> submitLogin(event));
    }

}
