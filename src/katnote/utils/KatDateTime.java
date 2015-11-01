package katnote.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class KatDateTime {
    
    public static final LocalTime END_OF_DAY_TIME = LocalTime.of(23, 59);
    
    private LocalDate _date;
    private LocalTime _time;
    
    /**
     * Obtains an instance of {@code KatDateTime} from a date and time.
     *
     * @param date  the local date
     * @param time  the local time
     * @return the date-time, not null
     */
    public KatDateTime(LocalDate date, LocalTime time){
        _date = date;
        _time = time;
    }
    
    /**
     * Obtains an instance of {@code KatDateTime} from LocalDateTime
     */
    public KatDateTime(LocalDateTime datetime){
        _date = datetime.toLocalDate();
        _time = datetime.toLocalTime();
    }
    
    /*
     * Converts this KatDateTime object to LocalDateTime
     */
    public LocalDateTime toLocalDateTime(){
        return LocalDateTime.of(_date, _time);
    }
    
    /*
     * Gets the LocalDate part of this date-time.
     */
    public LocalDate getDate(){
        return _date;
    }
    
    /*
     * Gets the LocalTime part of this date-time.
     */
    public LocalTime getTime(){
        return _time;
    }
    
    /*
     * Returns whether this date time has date or not
     */
    public boolean hasDate(){
        return _date != null && !_date.isEqual(LocalDate.MIN);
    }
    
    /*
     * Returns whether this date time has time or not
     */
    public boolean hasTime(){
        return _time != null;
    }
    
    /*
     * Updates date part of current object to today
     */
    public void changeDate(){
        _date = LocalDate.now();
    }
    
    /*
     * Updates date part of current object to the new date
     * If new date is LocalDate.MIN or null, considered it as today
     */
    public void changeDate(LocalDate newDate){
        if (newDate == null || newDate.equals(LocalDate.MIN)){
            _date = LocalDate.now();
        }
        else{
            _date = newDate;
        }
    }
    
    /*
     * Updates time part of current object to the new time
     */
    public void changeTime(LocalTime newTime){
        _time = newTime;
    }
    
    /*
     * Updates time part of current object to the new date time
     */
    public void changeDateTime(KatDateTime newDateTime){
        if (newDateTime.hasDate()){
            _date = newDateTime.getDate();
        }
        if (newDateTime.hasTime()){
            _time = newDateTime.getTime();
        }
    }
    
    public String toString(){
        if (hasDate()){
            if (hasTime()){ // has both date and time
                return toLocalDateTime().toString();
            }
            else{ // has only date
                return _date.toString();
            }
        }
        else{ // has only time
            return _time.toString();
        }
    }
}
