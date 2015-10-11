package katnote.parser;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParser {
    
    private static final String RELATIVE_TIME_TODAY = "today";
    private static final String RELATIVE_TIME_TOMORROW = "tomorrow";
    private static final String RELATIVE_TIME_NEXT_WEEK = "next week";
    private static final String RELATIVE_TIME_NEXT_MONTH = "next month";
    private static final String RELATIVE_TIME_NEXT_YEAR = "next year";
    private static final String RELATIVE_TIME_MONDAY = "monday";
    private static final String RELATIVE_TIME_TUESDAY = "tuesday";
    private static final String RELATIVE_TIME_WEDNESDAY = "wednesday";
    private static final String RELATIVE_TIME_THURSDAY = "thursday";
    private static final String RELATIVE_TIME_FRIDAY = "friday";
    private static final String RELATIVE_TIME_SATURDAY = "saturday";
    private static final String RELATIVE_TIME_SUNDAY = "sunday";
    
    private static final String ABSOLUTE_TIME_PATTERN = "^(\\d+)\\/(\\d+)(?:\\/(\\d+))?(?: (\\d+):(\\d+)(am|pm|))?$";
    private static final int ABSOLUTE_TIME_PATTERN_POS_DAY = 1;
    private static final int ABSOLUTE_TIME_PATTERN_POS_MONTH = 2;
    private static final int ABSOLUTE_TIME_PATTERN_POS_YEAR = 3;
    private static final int ABSOLUTE_TIME_PATTERN_POS_HOUR = 4;
    private static final int ABSOLUTE_TIME_PATTERN_POS_MINUTE = 5;
    private static final int ABSOLUTE_TIME_PATTERN_POS_DAY_PM = 6;
    private static final String DAY_PERIOD_PM = "pm";
    private static final int DAY_NUMBER_OF_HOURS = 24;
    private static final int DAY_HALF_NUMBER_OF_HOURS = 12;
    
    // default hours of day when no hour given
    public static final int MIDDLE_OF_DAY = 0;
    public static final int BEGIN_OF_DAY = 1;
    public static final int END_OF_DAY = 2;
    
    /*
     * get today Date time
     */
    public static Date getToday(){
        Date today = Calendar.getInstance().getTime();
        return today;
    }
    
    /*
     * get tomorrow Date time
     */
    public static Date getTomorrow(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }
    
    /*
     * get next week Date time
     */
    public static Date getNextWeek(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, Calendar.DAY_OF_WEEK);
        return calendar.getTime();
    }
    
    /*
     * Find the next desired weekday
     * @param desiredWeekday the next desired weekday, 1->7 is Sunday, Monday,... Saturday, respectively
     */
    public static Date getNextWeekDay(int desiredWeekday){
        Calendar calendar = Calendar.getInstance();
        while (true){
            calendar.add(Calendar.DATE, 1);
            int weekday = calendar.get(Calendar.DAY_OF_WEEK);
            if (weekday == desiredWeekday){
                return calendar.getTime();
            }
        }        
    }
    
    /*
     * get next month Date time
     */
    public static Date getNextMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }
    
    /*
     * get next year Date time
     */
    public static Date getNextYear(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        return calendar.getTime();
    }
    
    private static Date parseRelativeTime(String time){
        switch (time.toLowerCase()){
            case RELATIVE_TIME_TODAY:
                return getToday();
            case RELATIVE_TIME_TOMORROW:
                return getTomorrow();
            case RELATIVE_TIME_NEXT_WEEK:
                return getNextWeek();
            case RELATIVE_TIME_NEXT_MONTH:
                return getNextMonth();
            case RELATIVE_TIME_NEXT_YEAR:
                return getNextYear();
            case RELATIVE_TIME_MONDAY:
                return getNextWeekDay(Calendar.MONDAY);
            case RELATIVE_TIME_TUESDAY:
                return getNextWeekDay(Calendar.TUESDAY);
            case RELATIVE_TIME_WEDNESDAY:
                return getNextWeekDay(Calendar.WEDNESDAY);
            case RELATIVE_TIME_THURSDAY:
                return getNextWeekDay(Calendar.THURSDAY);
            case RELATIVE_TIME_FRIDAY:
                return getNextWeekDay(Calendar.FRIDAY);
            case RELATIVE_TIME_SATURDAY:
                return getNextWeekDay(Calendar.SATURDAY);
            case RELATIVE_TIME_SUNDAY:
                return getNextWeekDay(Calendar.SUNDAY);
        }
        return null;
    }    
    
    
    /*
     * convert string (absolute time format) to string
     */
    private static Date parseAbsoluteTime(String time, int defaultHourOption) {
        // today
        Calendar calendar = Calendar.getInstance();
        int defaultHourOfDay = getDefaultHourOfDay(defaultHourOption);
        int defaultMinute = getDefaultMinute(defaultHourOption);
        // matcher
        Matcher m = Pattern.compile(ABSOLUTE_TIME_PATTERN).matcher(time);
        if (m.find()){
            int day = Integer.parseInt(m.group(ABSOLUTE_TIME_PATTERN_POS_DAY));
            int month = Integer.parseInt(m.group(ABSOLUTE_TIME_PATTERN_POS_MONTH));
            int year = m.group(ABSOLUTE_TIME_PATTERN_POS_YEAR) != null ? Integer.parseInt(m.group(ABSOLUTE_TIME_PATTERN_POS_YEAR)) : calendar.get(Calendar.YEAR);
            
            int hourOfDay = m.group(ABSOLUTE_TIME_PATTERN_POS_HOUR) != null
                    ? Integer.parseInt(m.group(ABSOLUTE_TIME_PATTERN_POS_HOUR)) : defaultHourOfDay;
            int minute = m.group(ABSOLUTE_TIME_PATTERN_POS_MINUTE) != null
                    ? Integer.parseInt(m.group(ABSOLUTE_TIME_PATTERN_POS_MINUTE)) : defaultMinute;
            boolean isPM = DAY_PERIOD_PM.equals(m.group(ABSOLUTE_TIME_PATTERN_POS_DAY_PM));
            
            if (isPM){
                hourOfDay += DAY_HALF_NUMBER_OF_HOURS;
            }           
            
            if (hourOfDay >= DAY_NUMBER_OF_HOURS){ // invalid date
                return null;
            }
            
            calendar.set(year, month - 1, day, hourOfDay, minute);
            return calendar.getTime();
        }
        
        return null;
    }
    
    private static int getDefaultMinute(int defaultHourOption) {
        if (defaultHourOption == END_OF_DAY){
            return 59;
        }
        return 0;
    }

    private static int getDefaultHourOfDay(int defaultHourOption) {
        switch (defaultHourOption){
            case BEGIN_OF_DAY:
                return 0;
            case MIDDLE_OF_DAY:
                return 12;
            case END_OF_DAY:
                return 23;
        }
        return 0;
    }

    /*
     * change the hour of date based on hour option
     */
    private static Date addHourOption(Date date, int defaultHourOption){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, getDefaultHourOfDay(defaultHourOption));
        calendar.set(Calendar.MINUTE, getDefaultMinute(defaultHourOption));
        return calendar.getTime();
    }

    /*
     * convert string to date
     */
    public static Date parseDate(String time){ 
        return parseDate(time, MIDDLE_OF_DAY);
    }
    
    /*
     * convert string to date
     */
    public static Date parseDate(String time, int defaultHourOption){ 
        // check if time is relative time
        Date date = parseRelativeTime(time);
        if (date != null){ // date == null means it is not relative time
            date = addHourOption(date, defaultHourOption);
            return date;
        }
        // check some absolute time format
        date = parseAbsoluteTime(time, defaultHourOption);        
        return date;
        
    }

    /*
    // use for testing, will be removed soon 
    public static void main(String args[]){        
        System.out.println("hehe");
        String s = "monday";
        Date d = parseDate(s);
        System.out.println(d);
    }
    //*/

}
