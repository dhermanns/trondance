package trondance;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.StageStyle;

import javax.inject.Named;

@Named
public class MainDialogProvider extends FXMLDialogProvider {

    public MainDialogProvider() {
        super(MainDialogProvider.class.getResource("trondance-ui.fxml"), StageStyle.DECORATED);
    }
}
