package trondance;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.net.URL;

public class FXMLDialogProvider implements Provider<Stage> {

    @Inject
    private ApplicationContext applicationContext;
    private final StageStyle style;
    private final URL fxml;

    public FXMLDialogProvider(URL fxml, StageStyle style) {
        this.style = style;
        this.fxml = fxml;
    }

    public Stage get() {
        Stage dialog = new Stage(style);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(Main.getPrimaryStage());
        try {
            FXMLLoader loader = new FXMLLoader(fxml);
            loader.setControllerFactory(new Callback<Class<?>, Object>() {

                public Object call(Class<?> aClass) {
                    return applicationContext.getBean(aClass);
                }
            });
            dialog.setScene(new Scene((Parent) loader.load(), 800, 600));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return dialog;
    }
}
