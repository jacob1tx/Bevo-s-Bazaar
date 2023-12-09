package src;

import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ResourceBundle;

public class historyController implements Initializable {
    public VBox vbox;
    private final Client client = Client.sendSelf();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] history = Client.purchaseHistory.split(",");
        if (history[0].equals("none"))
            vbox.getChildren().add(textCreator("No purchases!"));
        else {
            Text text = new Text(client.username + "'s purchases: ");
            text.setFont(Font.font("Malgun Gothic Bold", 16));
            text.setWrappingWidth(172.0);
            text.setTextAlignment(TextAlignment.CENTER);
            vbox.getChildren().add(text);
            for (String h : history) {
                vbox.getChildren().add(textCreator(h));
            }
        }
    }

    private Text textCreator(String item) {
        Text ret = new Text(item);
        ret.setFont(Font.font("Malgun Gothic Semilight", 14));
        ret.setWrappingWidth(172.0);
        ret.setTextAlignment(TextAlignment.CENTER);
        return ret;
    }
}
