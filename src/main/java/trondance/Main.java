package trondance;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import trondance.config.AppConfiguration;
import trondance.provider.MainDialogProvider;

public class Main extends Application {

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) {

        primaryStage = stage;
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
        Stage mainStage = context.getBean(MainDialogProvider.class).get();

        mainStage.setTitle("Tron Dance Player");
        mainStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
