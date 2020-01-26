package trondance.util;

import javafx.util.Duration;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;

public class DurationHelper {

    private static SimpleDateFormat durationFormatter = new SimpleDateFormat("mm:ss:SSS");

    public static String toString(Duration duration) {

        return durationFormatter.format(Date.from(Instant.ofEpochMilli(
                Double.valueOf(duration.toMillis()).longValue())));
    }
}
