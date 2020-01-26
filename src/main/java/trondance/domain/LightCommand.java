package trondance.domain;

import javafx.beans.property.*;
import javafx.util.Duration;

public class LightCommand {

    private ObjectProperty<Duration> durationProperty;
    private IntegerProperty personNumberProperty;
    private StringProperty effectProperty;

    public LightCommand(Duration duration, Integer personNumber, String effect) {
        this.durationProperty = new SimpleObjectProperty<>(duration);
        this.personNumberProperty = new SimpleIntegerProperty(personNumber);
        this.effectProperty = new SimpleStringProperty(effect);
    }

    public Duration getDuration() {
        return durationProperty.get();
    }

    public ObjectProperty<Duration> durationProperty() {
        return durationProperty;
    }

    public void setDuration(Duration duration) {
        this.durationProperty = new SimpleObjectProperty<>(duration);
    }

    public Integer getPersonNumber() {
        return personNumberProperty.get();
    }

    public IntegerProperty personNumberProperty() {
        return personNumberProperty;
    }

    public void setPersonNumber(Integer personNumber) {
        this.personNumberProperty = new SimpleIntegerProperty(personNumber);
    }

    public String getEffect() {
        return effectProperty.get();
    }

    public StringProperty effectProperty() {
        return effectProperty;
    }

    public void setEffect(StringProperty effect) {
        this.effectProperty = effect;
    }
}
