package trondance;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import trondance.domain.LightCommand;
import trondance.domain.Timeline;
import trondance.persistence.TimelineRepository;
import trondance.util.DurationHelper;

import javax.inject.Named;
import java.io.File;
import java.util.List;

@Named
public class Controller {

    private Media media = new Media(new File("trondance.wav").toURI().toString());
    private MediaPlayer mediaPlayer = new MediaPlayer(media);

    private Timeline timeline = new Timeline();

    private Integer lastScrollPosition = 0;

    @FXML
    TextField nodeMcu1;
    @FXML
    TextField nodeMcu2;
    @FXML
    TextField nodeMcu3;
    @FXML
    Label mediaPlayerCurrentTimeLabel;
    @FXML
    Slider playbackSlider;

    @FXML
    TableView<LightCommand> lightCommandsTable;
    @FXML
    private TableColumn<LightCommand, Duration> timestampColumn;
    @FXML
    private TableColumn<LightCommand, Integer> personNumberColumn;
    @FXML
    private TableColumn<LightCommand, String> effectColumn;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TimelineRepository timelineRepository;

    @FXML
    private void initialize() {
        timestampColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        timestampColumn.setCellFactory(col -> new TableCell<LightCommand, Duration>() {
            @Override
            protected void updateItem(Duration item, boolean empty) {

                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(DurationHelper.toString(item));
            }
        });
        personNumberColumn.setCellValueFactory(
                cellData -> cellData.getValue().personNumberProperty().asObject());
        effectColumn.setCellValueFactory(
                cellData -> cellData.getValue().effectProperty());

        playbackSlider.setMin(0);
        playbackSlider.setMax(668);
        playbackSlider.setSnapToTicks(false);
        mediaPlayer.setCycleCount(1);

        mediaPlayer.setOnReady(() -> {

            mediaPlayerCurrentTimeLabel.textProperty().bind(
                    Bindings.createStringBinding(() -> {
                                Duration time = mediaPlayer.getCurrentTime();
                                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
                                    List<LightCommand> commandsToExecute =
                                            timeline.determineCommandsToExecute(time);
                                    execute(commandsToExecute);
                                    timeline.advanceTimelineTo(time.add(Duration.ONE));
                                }
                                /*Integer position = timeline.getNextCommandPosition(time);
                                if (position != -1 && position != lastScrollPosition) {
                                    lightCommandsTable.scrollTo(position);
                                    //lightCommandsTable.requestFocus();
                                    lightCommandsTable.getSelectionModel().select(position);
                                    //lightCommandsTable.getFocusModel().focus(position);
                                    lastScrollPosition = position;
                                }*/
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
                if (mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                    timeline.advanceTimelineTo(
                            Duration.seconds(newValue.doubleValue()).add(Duration.ONE));
                }
            });

            mediaPlayer.play();
            mediaPlayer.pause();
            mediaPlayer.seek(Duration.ZERO);
        });

        timeline = timelineRepository.load();
        lightCommandsTable.setItems(timeline.getLightCommandList());
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
        recordCommand(1, "flash");
        timeline.advanceTimelineTo(mediaPlayer.getCurrentTime().add(Duration.ONE));
        System.out.println("Flashed!");
    }

    @FXML
    private void handleSecondFlash(ActionEvent event) {

        execute(nodeMcu2.getText(), "flash");
        recordCommand(2, "flash");
        timeline.advanceTimelineTo(mediaPlayer.getCurrentTime().add(Duration.ONE));
        System.out.println("Flashed!");
    }

    private void recordCommand(int person, String flash) {
        timeline.add(new LightCommand(
                mediaPlayer.getCurrentTime(), person, flash));
    }

    @FXML
    private void handleFirstMove(ActionEvent event) {
        execute(nodeMcu1.getText(), "move");
        recordCommand(1, "move");
        timeline.advanceTimelineTo(mediaPlayer.getCurrentTime().add(Duration.ONE));
        System.out.println("Moved!");
    }

    @FXML
    private void handleSecondMove(ActionEvent event) {
        execute(nodeMcu2.getText(), "move");
        recordCommand(2, "move");
        timeline.advanceTimelineTo(mediaPlayer.getCurrentTime().add(Duration.ONE));
        System.out.println("Moved!");
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Integer focusedIndex = lightCommandsTable.getSelectionModel().getFocusedIndex();
        if (focusedIndex >= 0) {
            timeline.removeAtIndex(focusedIndex);
        }
    }

    @FXML
    private void handleExitButton(ActionEvent event) {
        timelineRepository.save(timeline);
        System.exit(0);
    }

    private void execute(String ip, String command) {

        try {
            restTemplate.exchange(
                    String.format("http://%s/%s", ip, command), HttpMethod.GET, HttpEntity.EMPTY, String.class);
        } catch (Exception ignore) {
        }
    }

    private void execute(List<LightCommand> commands) {

        try {
            commands.parallelStream()
                    .forEach(command -> {
                        System.out.println(String.format("Executed command '%s' for person '%s'.",
                                command.getEffect(), command.getPersonNumber()));
                        restTemplate.exchange(
                                String.format("http://%s/%s", getIp(command.getPersonNumber()), command.getEffect()),
                                HttpMethod.GET, HttpEntity.EMPTY, String.class);
                    });
        } catch (Exception ignore) {
        }
    }

    private String getIp(Integer personNumber) {
        switch(personNumber) {
            case 1: return nodeMcu1.getText();
            case 2: return nodeMcu2.getText();
            case 3: return nodeMcu3.getText();
        }
        throw new IllegalStateException();
    }
}
