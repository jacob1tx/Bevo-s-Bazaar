package src;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AppController implements Initializable {

    static final Object bid = new Object();
    public MenuItem history;
    public MenuItem logout;
    public MenuItem close;
    public Text welcome;
    public Text errorOut;
    public MenuItem pauseUnpause;
    private MediaPlayer player;
    public String username = client.username;
    @FXML
    private GridPane gridPane;

    private static ArrayList<SquareController> controllerArray = new ArrayList<>();

    private static final Client client = Client.sendSelf();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client.sendToServer("setup");
        while (client.updating) {}
        for (int i = 0; i < client.items.size(); i++) {
            Item item = client.items.get(i);
            try {
                FXMLLoader loader = new FXMLLoader();
                AnchorPane pane = loader.load(Objects.requireNonNull(getClass().getResource("itemSquare.fxml").openStream()));
                SquareController controller = loader.getController();
                controllerArray.add(controller);
                controller.updateTitle(item.getTitle());
                controller.updateDescription(item.getDescription());
                controller.updatePrice(item.getPrice());
                controller.setNumber(i);
                controller.setInitTime(item.getCompletionTime());
                controller.setBINvalue(item.getBINPrice());
                if (!item.getHighestBidder().equals("none")) // set highest bidder
                    controller.updateMaxBidder(item.getHighestBidder());
                if (item.isSold()) // mark as sold
                    controller.setSold();
                else if (Client.seconds > item.getCompletionTime()) { // timer ran out, mark as sold or not sold
                    if (item.getHighestBidder().equals("none")) // no bidders
                        controller.setNotSold();
                    else
                        controller.setSold();
                }
                gridPane.add(pane, i % 5, i / 5);
                updateWelcome(client.username);
            } catch (IOException e) {
                System.out.println("something went wrong: IOException");
                throw new RuntimeException(e);
            }
            String file = "src/src/sounds/ElevatorMusic.mp3";
            Media sound = new Media(new File(file).toURI().toString());
            player = new MediaPlayer(sound);
            player.play();
        }
    }

    /** Sends the new price and item number to the server for verification
     *
     * @param val update price, prechecked for format correctness
     * @param i item number in the gridpane
     */
    public void updateBid(String val, int i) {
        synchronized (bid) {
            client.sendToServer("bid," + val + "," + i);
        }
    }

    public void setError(String text) {
        errorOut.setText(text);
    }

    public void updatePrice(String price, int i, String maxBidder, boolean sold) {
        SquareController controller = controllerArray.get(i);
        controller.updatePrice(price);
        controller.updateMaxBidder(maxBidder);
        if (sold) { // block out the item because it has been sold
            controller.setSold();
        }
    }

    public void updateTime() {
        for (int i = 0; i < client.items.size(); i++) {
            controllerArray.get(i).updateTimer(Client.seconds);
        }
    }

    public void updateWelcome(String text) {
        welcome.setText("Welcome, " + text + "!");
    }

    public void getHistory(ActionEvent actionEvent) {
        client.sendToServer("history," + username);
    }

    public void showHistory()  {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("historyMenu.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Platform.runLater(() -> {
            Stage historyStage = new Stage();
            historyStage.setScene(new Scene(root));
            historyStage.show();
        });
    }

    public void pauseUnpause(ActionEvent actionEvent) {
        if (pauseUnpause.getText().equals("Pause Music")) {
            player.pause();
            pauseUnpause.setText("Play Music");
        } else {
            player.play();
            pauseUnpause.setText("Pause Music");
        }
    }

    public void showBids() {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("bidsMenu.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Platform.runLater(() -> {
            Stage historyStage = new Stage();
            historyStage.setScene(new Scene(root));
            historyStage.show();
        });
    }

    public void logout(ActionEvent actionEvent) {
        Stage stage = (Stage) welcome.getScene().getWindow();
        player.stop();
        controllerArray.clear();
        client.items.clear();
        Platform.runLater(() -> {
            stage.close();
            FXMLLoader loader = new FXMLLoader();
            Parent root;
            try {
                root = loader.load(Objects.requireNonNull(getClass().getResource("login.fxml").openStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SceneGenerator.controller = loader.getController();
            Stage primaryStage = new Stage();
            primaryStage.setScene(new Scene(root, 464, 325));
            primaryStage.setTitle("Login");
            primaryStage.show();
        });
    }

    public void closeWindow(ActionEvent actionEvent) {
        System.exit(1);
    }
}
