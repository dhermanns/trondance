package trondance;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import javax.inject.Named;

@Named
public class Controller {

    @FXML
    TextField nodeMcu1;

    @Autowired
    RestTemplate restTemplate;

    @FXML
    private void handleFirstFlash(ActionEvent event) {

        execute(nodeMcu1.getText(), "flash");
        System.out.println("Flashed!");
    }

    @FXML
    private void handleFirstMove(ActionEvent event) {

        execute(nodeMcu1.getText(), "move");
        System.out.println("Moved!");
    }

    @FXML
    private void handleExitButton(ActionEvent event) {
        System.exit(0);
    }

    private void execute(String ip, String command) {
        restTemplate.exchange(
                String.format("http://%s/%s", ip, command), HttpMethod.GET, HttpEntity.EMPTY, String.class);
    }
}
