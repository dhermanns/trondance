package trondance.persistence;

import javafx.util.Duration;
import trondance.domain.LightCommand;

public class LightCommandTO {

    private Integer duration;
    private Integer personNumber;
    private String effect;

    public LightCommandTO() {}

    public LightCommandTO(Integer duration, Integer personNumber, String effect) {
        this.duration = duration;
        this.personNumber = personNumber;
        this.effect = effect;
    }

    public static LightCommandTO from(LightCommand lightCommand) {

        return new LightCommandTO(
                Double.valueOf(lightCommand.getDuration().toMillis()).intValue(),
                lightCommand.getPersonNumber().intValue(),
                lightCommand.getEffect());
    }

    public LightCommand toLightCommand() {
        return new LightCommand(Duration.millis(duration), personNumber, effect);
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(Integer personNumber) {
        this.personNumber = personNumber;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }
}
