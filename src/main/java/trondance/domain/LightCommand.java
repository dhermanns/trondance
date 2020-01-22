package trondance.domain;

import javafx.beans.property.*;

import java.time.Instant;

public class LightCommand {

    private ObjectProperty<Instant> timestamp;
    private IntegerProperty personNumber;
    private StringProperty effect;

    public LightCommand(Instant timestamp, Integer personNumber, String effect) {
        this.timestamp = new SimpleObjectProperty<>(timestamp);
        this.personNumber = new SimpleIntegerProperty(personNumber);
        this.effect = new SimpleStringProperty(effect);
    }

    public Instant getTimestamp() {
        return timestamp.get();
    }

    public ObjectProperty<Instant> timestampProperty() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = new SimpleObjectProperty<>(timestamp);
    }

    public Integer getPersonNumber() {
        return personNumber.get();
    }

    public IntegerProperty personNumberProperty() {
        return personNumber;
    }

    public void setPersonNumber(IntegerProperty personNumber) {
        this.personNumber = personNumber;
    }

    public String getEffect() {
        return effect.get();
    }

    public StringProperty effectProperty() {
        return effect;
    }

    public void setEffect(StringProperty effect) {
        this.effect = effect;
    }
}
