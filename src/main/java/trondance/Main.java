package trondance;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main extends Application {

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) throws Exception{

        primaryStage = stage;
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
        Stage mainStage = context.getBean(MainDialogProvider.class).get();

        mainStage.setTitle("Tron Dance Player");
        mainStage.show();

    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("Hello World!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
