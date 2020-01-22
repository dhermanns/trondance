package trondance;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import trondance.domain.LightCommand;

import javax.inject.Named;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Named
public class Controller {

    private Media media = new Media(new File("trondance.wav").toURI().toString());
    private MediaPlayer mediaPlayer = new MediaPlayer(media);
    private SimpleDateFormat playTimeFormatter = new SimpleDateFormat("mm:ss:SSS");

    private ObservableList<LightCommand> lightCommands = FXCollections.observableArrayList();

    @FXML
    TextField nodeMcu1;
    @FXML
    Label mediaPlayerCurrentTimeLabel;
    @FXML
    Slider playbackSlider;

    @FXML
    TableView<LightCommand> lightCommandsTable;
    @FXML
    private TableColumn<LightCommand, Instant> timestampColumn;
    @FXML
    private TableColumn<LightCommand, Integer> personNumberColumn;
    @FXML
    private TableColumn<LightCommand, String> effectColumn;

    @Autowired
    RestTemplate restTemplate;

    @FXML
    private void initialize() {
        timestampColumn.setCellValueFactory(cellData -> cellData.getValue().timestampProperty());
        timestampColumn.setCellFactory(col -> new TableCell<LightCommand, Instant>() {
            @Override
            protected void updateItem(Instant item, boolean empty) {

                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(playTimeFormatter.format(Date.from(item)));
            }
        });
        personNumberColumn.setCellValueFactory(
                cellData -> cellData.getValue().personNumberProperty().asObject());
        effectColumn.setCellValueFactory(
                cellData -> cellData.getValue().effectProperty());
        lightCommands.add(new LightCommand(Instant.ofEpochSecond(1), 1, "Flash"));
        lightCommands.add(new LightCommand(Instant.ofEpochSecond(2), 1, "Flash"));
        lightCommands.add(new LightCommand(Instant.ofEpochSecond(3), 1, "Flash"));
        lightCommands.add(new LightCommand(Instant.ofEpochSecond(4), 1, "Move"));
        lightCommandsTable.setItems(lightCommands);

        playbackSlider.setMin(0);
        playbackSlider.setMax(668);
        playbackSlider.setSnapToTicks(false);
        mediaPlayer.setCycleCount(1);

        mediaPlayer.setOnReady(() -> {
            mediaPlayerCurrentTimeLabel.textProperty().bind(
                    Bindings.createStringBinding(() -> {
                                Duration time = mediaPlayer.getCurrentTime();
                                return String.format("%02d:%04.1f",
                                        (int) time.toMinutes() % 60,
                                        time.toSeconds() % 60);
                            },
                            mediaPlayer.currentTimeProperty()));

            playbackSlider.maxProperty().bind(
                    Bindings.createDoubleBinding(
                            () -> mediaPlayer.getTotalDuration().toSeconds(),
                            mediaPlayer.totalDurationProperty()));

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                playbackSlider.setValue(newValue.toSeconds());
            });

            playbackSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
            });
        });
    }

    @FXML
    private void handlePlay(ActionEvent event) {
        mediaPlayer.play();
    }

    @FXML
    private void handlePause(ActionEvent event) {
        mediaPlayer.pause();
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
