package trondance.domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Die Timeline beinhaltet eine Liste von LightCommand's. Diese Liste ist nach den Zeitstempeln
 * sortiert.
 */
public class Timeline {

    private ObservableList<LightCommand> lightCommands = FXCollections.observableArrayList();
    private Duration executedUntilDuration = Duration.ZERO.subtract(Duration.ONE);

    public void add(LightCommand flash) {
        lightCommands.add(flash);
        lightCommands.sort(Comparator.comparing(LightCommand::getDuration));
    }

    public ObservableList<LightCommand> getLightCommandList() {
        return lightCommands;
    }

    public List<LightCommand> determineCommandsToExecute(Duration time) {

        return lightCommands.stream()
                .filter(command ->
                        command.getDuration().greaterThanOrEqualTo(executedUntilDuration)
                        && command.getDuration().lessThan(time))
                .collect(Collectors.toList());
    }

    public void advanceTimelineTo(Duration newExecutedUntilDuration) {
        this.executedUntilDuration = newExecutedUntilDuration;
    }

    public Integer getNextCommandPosition(Duration currentTime) {
        Integer index = 0;
        for (LightCommand command : lightCommands) {
            if (command.getDuration().greaterThanOrEqualTo(currentTime)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public void removeAtIndex(int focusedIndex) {
        lightCommands.remove(focusedIndex);
    }
}
