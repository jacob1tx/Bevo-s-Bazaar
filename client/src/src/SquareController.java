package src;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class SquareController implements Initializable {
    public AnchorPane pane;
    public Text maxBidder;
    public Text BINvalue;
    public Button buy;
    public Rectangle block;
    public Text soldUser;
    public Text soldPrice;
    private int number;
    public Text title;
    public Text description;
    public Text timeRemaining;
    public Text value;
    public TextField bidAmount;
    public Button bid;
    private int initTime;
    private static final Client client = Client.sendSelf();

    public void updateTitle(String text) {
        title.setText(text);
    }
    public void updateDescription(String text) {
        description.setText(text);
    }
    public void updateTime(String text){
        timeRemaining.setText("Time remaining: " + text);
    }
    public void updatePrice(String text) {
        value.setText("$" + text);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pane.setStyle("-fx-background-color: SANDYBROWN; -fx-border-color: SADDLEBROWN; -fx-border-width: 2");
        description.setStyle("-fx-background-color: SANDYBROWN;");
    }

    /**
     * First asserts valid bid format, then sends it to the server
     * @param actionEvent on Bid click
     */
    public void sendBid(ActionEvent actionEvent) {
        String val = bidAmount.getText().trim();
        if (val.lastIndexOf(".") != val.indexOf(".")) { // > 1 \.
            System.out.println("Invalid bid");
            return;
        }
        bid(val);
    }

    public void setNumber(int i) {
        number = i;
    }

    public void setInitTime(int initTime) {
        this.initTime = initTime;
    }

    public void updateMaxBidder(String user) {
        maxBidder.setText("Highest bid: " + user);
    }

    public void setMaxBidder(String text) {
        maxBidder.setText(text);
    }
    public void setBINvalue(String val) {
        BINvalue.setText("$" + val);
    }

    /**
     * cover up the item because it has been sold
     */
    public void setSold() {
        String soldTo = maxBidder.getText().substring(13);
        client.sendToServer("addHistory," + soldTo +  "," + title.getText());
        block.setVisible(true);
        soldUser.setVisible(true);
        soldUser.setText("Sold to " + soldTo);
        soldPrice.setVisible(true);
        soldPrice.setText("for " + value.getText() + "!");
    }
    public void setNotSold() {
        block.setVisible(true);
        soldPrice.setVisible(true);
        soldPrice.setText("Time elapsed. No bidders!");
    }

    public void buyPress(ActionEvent actionEvent) {
        bid(BINvalue.getText().substring(1));
    }
    public void updateTimer(int sec) {
        if (block.isVisible()) // item sold or timer has run out
            return;
        int timeCount = initTime - sec;
        if (timeCount >= 0) {
            int minutes = timeCount / 60;
            StringBuilder seconds = new StringBuilder(String.valueOf(timeCount % 60));
            while (seconds.length() < 2) {
                seconds.insert(0, "0");
            }
            updateTime(minutes + ":" + seconds);
        }
        else  { // timer has just completed
            if (maxBidder.getText().equals("No bids")) // no bids, mark as not sold
                setNotSold();
            else
                setSold(); // mark as sold
        }
    }

    private void bid (String val) {
        client.controller.setError("");
        String [] comp = val.split("\\.");
        // assert correct format: XX or XX.XX
        if (comp.length == 0) { // no bid or "."
            client.controller.setError("Invalid bid");
            return;
        }
        try {
            Integer.valueOf(comp[0]); // verify Integers
            if (comp.length == 2)
                Integer.valueOf(comp[1]);
        } catch (NumberFormatException e) {
            client.controller.setError("Invalid bid");
            return;
        }
        if (comp.length == 2) {
            if (comp[1].length() != 2) { // verify 2 decimal points given
                client.controller.setError("Invalid bid");
                return;
            }
        }
        client.controller.updateBid(val, number);
    }

    public void showHistory(ActionEvent actionEvent) {
        client.sendToServer("getBids," + title.getText() + "," + client.controller.username);
    }
}
