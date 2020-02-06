package trondance;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
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

@SuppressWarnings("rawtypes")
@Named
public class Controller {

    private Media media = new Media(new File("trondance.wav").toURI().toString());
    private MediaPlayer mediaPlayer = new MediaPlayer(media);

    private Timeline timeline = new Timeline();

    @FXML
    TextField nodeMcu1;
    @FXML
    ChoiceBox nodeMcu1ChoiceBox;
    @FXML
    TextField nodeMcu2;
    @FXML
    ChoiceBox nodeMcu2ChoiceBox;
    @FXML
    TextField nodeMcu3;
    @FXML
    ChoiceBox nodeMcu3ChoiceBox;
    @FXML
    CheckBox recordCheckBox;
    @FXML
    Label mediaPlayerCurrentTimeLabel;
    @FXML
    ColorPicker colorPicker;
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
    @FXML
    private TableColumn<LightCommand, String> parameterColumn;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TimelineRepository timelineRepository;

    @SuppressWarnings("unchecked")
    @FXML
    private void initialize() {
        ObservableList<String> nodeMcuCommandList = FXCollections.observableArrayList("flash", "move", "movehands", "fill", "turnoff");

        nodeMcu1ChoiceBox.setItems(nodeMcuCommandList);
        nodeMcu1ChoiceBox.getSelectionModel().select(0);
        nodeMcu2ChoiceBox.setItems(nodeMcuCommandList);
        nodeMcu2ChoiceBox.getSelectionModel().select(0);
        nodeMcu3ChoiceBox.setItems(nodeMcuCommandList);
        nodeMcu3ChoiceBox.getSelectionModel().select(0);

        timestampColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        timestampColumn.setCellFactory(col -> new TableCell<>() {

            private TextField textField;

            @Override
            public void startEdit() {
                super.startEdit();

                if (textField == null) {
                    createTextField();
                }

                setGraphic(textField);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                textField.selectAll();
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();

                setText(DurationHelper.toString(getItem()));
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }

            @Override
            public void commitEdit(Duration newDuration) {
                super.commitEdit(newDuration);

                setText(DurationHelper.toString(getItem()));
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }

            private String getString() {
                return getItem() == null ? "" : DurationHelper.toString(getItem());
            }

            private void createTextField() {
                textField = new TextField(getString());
                textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
                textField.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        commitEdit(DurationHelper.valueOf(textField.getText()));
                    } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                });
            }

            @Override
            protected void updateItem(Duration item, boolean empty) {

                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(DurationHelper.toString(item));
            }
        });
        timestampColumn.setOnEditCommit(
                cellEditEvent -> cellEditEvent.getTableView().getItems().get(
                        cellEditEvent.getTablePosition().getRow()).setDuration(cellEditEvent.getNewValue()));
        personNumberColumn.setCellValueFactory(
                cellData -> cellData.getValue().personNumberProperty().asObject());
        effectColumn.setCellValueFactory(
                cellData -> cellData.getValue().effectProperty());
        parameterColumn.setCellValueFactory(
                cellData -> cellData.getValue().parameterProperty());

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
                                    timeline.advanceTimelineTo(time);
                                }
                                return String.format("%02d:%04.1f",
                                        (int) time.toMinutes() % 60,
                                        time.toSeconds() % 60);
                            },
                            mediaPlayer.currentTimeProperty()));

            playbackSlider.maxProperty().bind(
                    Bindings.createDoubleBinding(
                            () -> mediaPlayer.getTotalDuration().toSeconds(),
                            mediaPlayer.totalDurationProperty()));

            mediaPlayer.currentTimeProperty().addListener(
                    (observable, oldValue, newValue) -> playbackSlider.setValue(newValue.toSeconds()));

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
        lightCommandsTable.setEditable(true);
        lightCommandsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mediaPlayer.seek(newSelection.getDuration());
            }
        });
    }

    @FXML
    private void handlePlay() {
        mediaPlayer.play();
    }

    @FXML
    private void handlePause() {
        mediaPlayer.pause();
    }

    @FXML
    private void handleExecuteFirst() {

        execute(nodeMcu1.getText(), nodeMcu1ChoiceBox.getSelectionModel().getSelectedItem().toString(), colorPicker.getValue());
        if (recordCheckBox.isSelected()) {
            recordCommand(1, nodeMcu1ChoiceBox.getSelectionModel().getSelectedItem().toString(), colorPicker.getValue());
            timeline.advanceTimelineTo(mediaPlayer.getCurrentTime().add(Duration.ONE));
        }
        System.out.println(String.format("Executed %s!", nodeMcu1ChoiceBox.getSelectionModel().getSelectedItem().toString()));
    }

    @FXML
    private void handleExecuteSecond() {

        execute(nodeMcu2.getText(), nodeMcu2ChoiceBox.getSelectionModel().getSelectedItem().toString(), colorPicker.getValue());
        if (recordCheckBox.isSelected()) {
            recordCommand(2, nodeMcu2ChoiceBox.getSelectionModel().getSelectedItem().toString(), colorPicker.getValue());
            timeline.advanceTimelineTo(mediaPlayer.getCurrentTime().add(Duration.ONE));
        }
        System.out.println(String.format("Executed %s!", nodeMcu2ChoiceBox.getSelectionModel().getSelectedItem().toString()));
    }

    private void recordCommand(int person, String command, Color color) {
        timeline.add(new LightCommand(
                mediaPlayer.getCurrentTime(), person, command, color));
    }

    @FXML
    private void handleDeleteButton() {
        int focusedIndex = lightCommandsTable.getSelectionModel().getFocusedIndex();
        if (focusedIndex >= 0) {
            timeline.removeAtIndex(focusedIndex);
        }
    }

    @FXML
    private void handleExitButton() {
        timelineRepository.save(timeline);
        System.exit(0);
    }

    private void execute(String ip, String command, Color color) {

        try {
            restTemplate.exchange(
                    String.format("http://%s/%s%s", ip, command, LightCommand.color2QueryParameter(color)), HttpMethod.GET, HttpEntity.EMPTY, String.class);
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
                                String.format("http://%s/%s?%s",
                                        getIp(command.getPersonNumber()),
                                        command.getEffect(),
                                        command.getParameter()),
                                HttpMethod.GET, HttpEntity.EMPTY, String.class);
                    });
        } catch (Exception ignore) {
        }
        if (commands.size() > 0) {
            tableSelect(commands.iterator().next().getDuration());
        }
    }

    private void tableSelect(Duration duration) {
        Integer rowNr = timeline.getNextCommandPosition(duration);
        if (rowNr >= 0) {
            lightCommandsTable.getSelectionModel().select(rowNr);
            lightCommandsTable.scrollTo(rowNr);
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
