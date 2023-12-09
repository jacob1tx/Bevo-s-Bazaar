package src;

import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ResourceBundle;

public class bidsController implements Initializable {
    public VBox vbox;
    public Text title;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] comp = Client.allBids.split(",");
        title.setText("Bids on " + comp[0]);
        if (comp[1].equals("none"))
            vbox.getChildren().add(textCreator("No bids!"));
        else {
            String [] bids = comp[1].split(";");
            for (String h : bids) {
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
