package src;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class loginController implements Initializable {
    private static final Client client = Client.sendSelf();
    public TextField userText;
    public TextField passText;
    public Text errorOutput;
    public Button loginBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public void login(ActionEvent actionEvent) {
        if (userText.getText().equals("")){
            errorOutput.setText("Please enter a username.");
            return;
        }
        if (userText.getText().equals("guest")){
            client.sendToServer("login," + userText.getText());
            return;
        }
        if (passText.getText().equals("")) {
            printError();
            return;
        }
        client.sendToServer("login," + userText.getText() + "," + passText.getText());
    }

    public void printError() {
        errorOutput.setText("Username and/or password incorrect");
    }

    public void close(){
        Stage stage = (Stage) loginBtn.getScene().getWindow();
        Platform.runLater(() -> {
            stage.close();
            Parent root;
            FXMLLoader loader = new FXMLLoader();
            try {
                root = loader.load(Objects.requireNonNull(getClass().getResource("storefront.fxml").openStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            client.controller = loader.getController();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 1020, 900));
            newStage.setTitle("Bevo's Bazar");
            newStage.show();
        });

    }
}
