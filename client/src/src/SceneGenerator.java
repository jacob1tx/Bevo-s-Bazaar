package src;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

/**
 * Runs the Javafx programs for login and the storefront
 */
public class SceneGenerator extends Application {
    public static loginController controller;
    public static void generate(String [] args) {launch(args);}
    @Override
    public void start(Stage primaryStage) throws Exception {
        // login screen
        Parent root;
        FXMLLoader loader = new FXMLLoader();
        root = loader.load(Objects.requireNonNull(getClass().getResource("login.fxml").openStream()));
        controller = loader.getController();
        primaryStage.setScene(new Scene(root, 464, 325));
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
}
