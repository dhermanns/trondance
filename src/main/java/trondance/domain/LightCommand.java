package trondance.domain;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

@SuppressWarnings("unused")
public class LightCommand {

    private ObjectProperty<Duration> durationProperty;
    private IntegerProperty personNumberProperty;
    private StringProperty effectProperty;
    private StringProperty parameter;

    public LightCommand(Duration duration, Integer personNumber, String effect, String parameter) {
        this.durationProperty = new SimpleObjectProperty<>(duration);
        this.personNumberProperty = new SimpleIntegerProperty(personNumber);
        this.effectProperty = new SimpleStringProperty(effect);
        this.parameter = new SimpleStringProperty(parameter);
    }

    public LightCommand(Duration duration, Integer personNumber, String effect, Color color) {
        this.durationProperty = new SimpleObjectProperty<>(duration);
        this.personNumberProperty = new SimpleIntegerProperty(personNumber);
        this.effectProperty = new SimpleStringProperty(effect);
        this.parameter = new SimpleStringProperty(color2QueryParameter(color));
    }

    public static String color2QueryParameter(Color color) {
        return String.format("?R=%s&G=%s&B=%s",
                Double.valueOf(color.getRed() * 255).intValue(),
                Double.valueOf(color.getGreen() * 255).intValue(),
                Double.valueOf(color.getBlue() * 255).intValue());
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

    public StringProperty parameterProperty() {
        return parameter;
    }

    public String getParameter() {
        return parameterProperty().get();
    }
}
