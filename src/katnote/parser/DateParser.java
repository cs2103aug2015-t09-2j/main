package katnote.parser;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParser {

    private static final String STR_EMPTY = "";
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

    private static final String ABSOLUTE_DATE_PATTERN = "^(\\d+)\\/(\\d+)(?:\\/(\\d+))?";
    private static final int ABSOLUTE_DATE_PATTERN_POS_DAY = 1;
    private static final int ABSOLUTE_DATE_PATTERN_POS_MONTH = 2;
    private static final int ABSOLUTE_DATE_PATTERN_POS_YEAR = 3;
    
    private static final String ABSOLUTE_DATE_TIME_PATTERN = "\\b(\\d{1,2})(?::(\\d+))?(am|pm|)$";
    private static final int ABSOLUTE_DATE_TIME_PATTERN_POS_HOUR = 1;
    private static final int ABSOLUTE_DATE_TIME_PATTERN_POS_MINUTE = 2;
    private static final int ABSOLUTE_DATE_TIME_PATTERN_POS_DAY_PM = 3;
    
    
    private static final String DAY_PERIOD_PM = "pm";
    private static final int DAY_HALF_NUMBER_OF_HOURS = 12;
    private static final int YEAR_TWO_DIGIT_LIMIT = 100;
    private static final int YEAR_TWO_DIGIT_OFFSET = 2000;

    // default hours of day when no hour given
    public static final int MIDDLE_OF_DAY = 0;
    public static final int BEGIN_OF_DAY = 1;
    public static final int END_OF_DAY = 2;
    
    private static final LocalTime END_OF_DAY_TIME = LocalTime.of(23, 59);

    /*
     * Gets today Date time
     */
    public static LocalDate getToday() {
        return LocalDate.now();
    }

    /*
     * Gets tomorrow Date time
     */
    public static LocalDate getTomorrow() {
        LocalDate date = LocalDate.now();
        date = date.plusDays(1);
        return date;
    }

    /*
     * Gets next week Date time
     */
    public static LocalDate getNextWeek() {
        LocalDate date = LocalDate.now();
        date = date.plusWeeks(1);
        return date;
    }

    /*
     * Finds the next desired weekday
     * 
     * @param desiredWeekday the next desired weekday
     * 
     */
    public static LocalDate getNextWeekDay(DayOfWeek desiredWeekday) {
        LocalDate date = LocalDate.now();
        while (true) {
            date = date.plusDays(1);
            if (date.getDayOfWeek() == desiredWeekday) {
                return date;
            }
        }
    }

    /*
     * Gets next month Date time
     */
    public static LocalDate getNextMonth() {
        LocalDate date = LocalDate.now();
        date = date.plusMonths(1);
        return date;
    }

    /*
     * Gets next year Date time
     */
    public static LocalDate getNextYear() {
        LocalDate date = LocalDate.now();
        date = date.plusYears(1);
        return date;
    }

    private static LocalDate parseRelativeDate(String time) {
        switch (time.toLowerCase()) {
            case RELATIVE_TIME_TODAY :
            case STR_EMPTY:
                return getToday();
            case RELATIVE_TIME_TOMORROW :
                return getTomorrow();
            case RELATIVE_TIME_NEXT_WEEK :
                return getNextWeek();
            case RELATIVE_TIME_NEXT_MONTH :
                return getNextMonth();
            case RELATIVE_TIME_NEXT_YEAR :
                return getNextYear();
            case RELATIVE_TIME_MONDAY :
                return getNextWeekDay(DayOfWeek.MONDAY);
            case RELATIVE_TIME_TUESDAY :
                return getNextWeekDay(DayOfWeek.TUESDAY);
            case RELATIVE_TIME_WEDNESDAY :
                return getNextWeekDay(DayOfWeek.WEDNESDAY);
            case RELATIVE_TIME_THURSDAY :
                return getNextWeekDay(DayOfWeek.THURSDAY);
            case RELATIVE_TIME_FRIDAY :
                return getNextWeekDay(DayOfWeek.FRIDAY);
            case RELATIVE_TIME_SATURDAY :
                return getNextWeekDay(DayOfWeek.SATURDAY);
            case RELATIVE_TIME_SUNDAY :
                return getNextWeekDay(DayOfWeek.SUNDAY);
        }
        return null;
    }

    /*
     * Converts string (absolute time format) to LocalDate
     */
    private static LocalDate parseAbsoluteDate(String time, int defaultTimeOption) {
        int currentYear = LocalDate.now().getYear();
        // matcher
        Matcher m = Pattern.compile(ABSOLUTE_DATE_PATTERN).matcher(time);
        if (m.find()) {
            int dayOfMonth = Integer.parseInt(m.group(ABSOLUTE_DATE_PATTERN_POS_DAY));
            int month = Integer.parseInt(m.group(ABSOLUTE_DATE_PATTERN_POS_MONTH));
            int year = m.group(ABSOLUTE_DATE_PATTERN_POS_YEAR) != null
                    ? Integer.parseInt(m.group(ABSOLUTE_DATE_PATTERN_POS_YEAR)) : currentYear;
                    
            if (year < YEAR_TWO_DIGIT_LIMIT){
                year += YEAR_TWO_DIGIT_OFFSET;
            }

            return LocalDate.of(year, month, dayOfMonth);
        }

        return null;
    }
    
    /*
     * Extracts time of day from time string. If no time of day found, return default
     * time of day instead
     */
    private static LocalTime extractTimeOfDay(String time, int defaultTimeOption){
        Matcher m = Pattern.compile(ABSOLUTE_DATE_TIME_PATTERN).matcher(time);
        if (m.find()) {
            int hour = Integer.parseInt(m.group(ABSOLUTE_DATE_TIME_PATTERN_POS_HOUR));
            int minute = m.group(ABSOLUTE_DATE_TIME_PATTERN_POS_MINUTE) != null
                    ? Integer.parseInt(m.group(ABSOLUTE_DATE_TIME_PATTERN_POS_MINUTE)) : 0;
            boolean isPM = DAY_PERIOD_PM.equals(m.group(ABSOLUTE_DATE_TIME_PATTERN_POS_DAY_PM));
            if (isPM) {
                hour += DAY_HALF_NUMBER_OF_HOURS;
            }
            return LocalTime.of(hour, minute);
        }
        else{ // when time of day not found, use default time option
            switch (defaultTimeOption) {
                case BEGIN_OF_DAY :
                    return LocalTime.MIN;
                case MIDDLE_OF_DAY :
                    return LocalTime.NOON;
                case END_OF_DAY :
                    return END_OF_DAY_TIME;
            }
            return LocalTime.now();
        }
    }
    
    /*
     * Trims time of day from the string
     */
    private static String trimTimeOfDay(String time){
        return time.replaceFirst(ABSOLUTE_DATE_TIME_PATTERN, "").trim();
    }

    /*
     * Converts string to LocalDateTime object
     */
    public static LocalDateTime parseDateTime(String time) {
        return parseDateTime(time, MIDDLE_OF_DAY);
    }

    /*
     * Converts string to LocalDateTime object
     */
    public static LocalDateTime parseDateTime(String time, int defaultTimeOption) {
        // get time of day and trim
        LocalTime timeOfDay = extractTimeOfDay(time, defaultTimeOption);
        time = trimTimeOfDay(time);
        // check if time is relative time
        LocalDate date = parseRelativeDate(time);
        if (date != null) { // date == null means it is not relative time
            return LocalDateTime.of(date,  timeOfDay);
        }
        // check some absolute time format
        date = parseAbsoluteDate(time, defaultTimeOption);
        return LocalDateTime.of(date,  timeOfDay);

    }

}
