package trondance;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import javax.inject.Named;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@Named
public class Controller {

    private Media media = new Media(new File("trondance.mp3").toURI().toString());
    private MediaPlayer mediaPlayer = new MediaPlayer(media);
    private SimpleDateFormat playTimeFormatter = new SimpleDateFormat("mm:ss:SSS");

    @FXML
    TextField nodeMcu1;
    @FXML
    Label mediaPlayerCurrentTimeLabel;

    @Autowired
    RestTemplate restTemplate;

    public Controller() {
        mediaPlayer.currentTimeProperty().addListener((observable, oldTime, newTime) -> {

            String formattedTime = playTimeFormatter.format(mediaPlayer.getCurrentTime().toMillis());
            mediaPlayerCurrentTimeLabel.setText(formattedTime);
        });
    }

    @FXML
    private void handlePlay(ActionEvent event) {
        mediaPlayer.play();
    }

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
