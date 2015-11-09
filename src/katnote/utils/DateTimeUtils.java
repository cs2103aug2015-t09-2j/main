//@@author A0126517H
package katnote.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeUtils {

    /**
     * Returns the later date
     * 
     * @param date1
     * @param date2
     * @return the later date among date1 and date2
     * 
     */
    public static LocalDate getLater(LocalDate date1, LocalDate date2) {
        if (date1.isAfter(date2)) {
            return date1;
        } else {
            return date2;
        }
    }

    /**
     * Returns the new date-time object with new date time value
     * 
     * @return LocalDateTime object with updated value from newDateTime
     */
    public static LocalDateTime updateDateTime(LocalDateTime datetime, KatDateTime newDateTime) {
        LocalDate date = datetime.toLocalDate();
        LocalTime time = datetime.toLocalTime();
        if (newDateTime.hasDate()) {
            date = newDateTime.getDate();
        }
        if (newDateTime.hasTime()) {
            time = newDateTime.getTime();
        }
        return LocalDateTime.of(date, time);
    }
}
