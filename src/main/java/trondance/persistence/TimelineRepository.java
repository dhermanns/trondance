package trondance.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import trondance.domain.Timeline;

import java.io.File;
import java.io.IOException;

@Repository
public class TimelineRepository {

    public void save(Timeline timeline) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("timeline.json"), TimelineTO.from(timeline));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Timeline load() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            TimelineTO timelineTO = mapper.readValue(new File("timeline.json"), TimelineTO.class);
            return timelineTO.toTimeline();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Timeline();
    }
}
