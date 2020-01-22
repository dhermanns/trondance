package trondance.provider;

import javafx.stage.StageStyle;

import javax.inject.Named;

@Named
public class MainDialogProvider extends FXMLDialogProvider {

    public MainDialogProvider() {
        super(MainDialogProvider.class.getResource("/trondance/trondance-ui.fxml"), StageStyle.DECORATED);
    }
}
