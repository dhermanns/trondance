package trondance.util;

import javafx.util.Duration;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;

public class DurationHelper {

    private static SimpleDateFormat durationFormatter = new SimpleDateFormat("mm:ss.SSS");
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static String toString(Duration duration) {

        return durationFormatter.format(Date.from(Instant.ofEpochMilli(
                Double.valueOf(duration.toMillis()).longValue())));
    }

    public static Duration valueOf(String stringDuration) {

        int millis = dateTimeFormatter.parse("00:" + stringDuration).get(ChronoField.MILLI_OF_DAY);
        return new Duration(millis);
    }
}
