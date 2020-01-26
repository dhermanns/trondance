package trondance.persistence;

import trondance.domain.Timeline;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TimelineTO {

    List<LightCommandTO> lightCommands;

    public TimelineTO() {}

    public TimelineTO(List<LightCommandTO> lightCommandTOs) {
        this.lightCommands = lightCommandTOs;
    }

    public static TimelineTO from(Timeline timeline) {

        if (timeline == null || timeline.getLightCommandList() == null || timeline.getLightCommandList().size() == 0) {
            return new TimelineTO(new ArrayList<>());
        }

        return new TimelineTO(
                timeline.getLightCommandList().stream()
                    .map(LightCommandTO::from)
                    .collect(Collectors.toList()));
    }

    public Timeline toTimeline() {
        Timeline timeline = new Timeline();
        lightCommands
                .forEach(command -> timeline.add(command.toLightCommand()));
        return timeline;
    }

    public List<LightCommandTO> getLightCommands() {
        return lightCommands;
    }

    public void setLightCommands(List<LightCommandTO> lightCommands) {
        this.lightCommands = lightCommands;
    }
}
