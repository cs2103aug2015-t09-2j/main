package katnote.utils;

import java.time.LocalDate;

public class DateTimeUtils {   
    
    /*
     * Returns the later date
     */
    public static LocalDate getLater(LocalDate date1, LocalDate date2){
        if (date1.isAfter(date2)){
            return date1;
        }
        else{
            return date2;
        }
    }
}
