package katnote.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateTimeUtils {
    
    /*
     * Checks whether the date part of LocalDateTime variable is LocalDate.MIN or not 
     */
    public static boolean hasMinDate(LocalDateTime dateTime){
        LocalDate date = dateTime.toLocalDate();
        return (date.isEqual(LocalDate.MIN));
    }
    
    /*
     * Changes date part of LocalDateTime variable to today
     */
    public static LocalDateTime changeDate(LocalDateTime dateTime){        
        return LocalDateTime.of(LocalDate.now(), dateTime.toLocalTime());
    }
    
    /*
     * Changes date part of LocalDateTime variable to the new date
     * If new date is LocalDate.MIN, considered it as today
     */
    public static LocalDateTime changeDate(LocalDateTime dateTime, LocalDate newDate){
        if (newDate.isEqual(LocalDate.MIN)){
            newDate = LocalDate.now();
        }
        return LocalDateTime.of(newDate, dateTime.toLocalTime());
    }
    
    /*
     * Returns the later date time
     */
    public static LocalDateTime getLater(LocalDateTime date1, LocalDateTime date2){
        if (date1.isAfter(date2)){
            return date1;
        }
        else{
            return date2;
        }
    }
}
