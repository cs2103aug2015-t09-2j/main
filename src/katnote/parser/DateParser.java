package katnote.parser;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public static LocalDateTime getToday() {
        return LocalDateTime.now();
    }

    /*
     * get tomorrow Date time
     */
    public static LocalDateTime getTomorrow() {
        LocalDateTime date = LocalDateTime.now();
        date = date.plusDays(1);
        return date;
    }

    /*
     * get next week Date time
     */
    public static LocalDateTime getNextWeek() {
        LocalDateTime date = LocalDateTime.now();
        date = date.plusWeeks(1);
        return date;
    }

    /*
     * Find the next desired weekday
     * 
     * @param desiredWeekday the next desired weekday
     * 
     */
    public static LocalDateTime getNextWeekDay(DayOfWeek desiredWeekday) {
        LocalDateTime date = LocalDateTime.now();
        while (true) {
            date = date.plusDays(1);
            if (date.getDayOfWeek() == desiredWeekday) {
                return date;
            }
        }
    }

    /*
     * get next month Date time
     */
    public static LocalDateTime getNextMonth() {
        LocalDateTime date = LocalDateTime.now();
        date = date.plusMonths(1);
        return date;
    }

    /*
     * get next year Date time
     */
    public static LocalDateTime getNextYear() {
        LocalDateTime date = LocalDateTime.now();
        date = date.plusYears(1);
        return date;
    }

    private static LocalDateTime parseRelativeTime(String time) {
        switch (time.toLowerCase()) {
            case RELATIVE_TIME_TODAY :
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
     * convert string (absolute time format) to string
     */
    private static LocalDateTime parseAbsoluteTime(String time, int defaultTimeOption) {
        // default values
        LocalTime defaultTimeOfDay = getDefaultTimeOfDay(defaultTimeOption);
        int currentYear = LocalDateTime.now().getYear();
        // matcher
        Matcher m = Pattern.compile(ABSOLUTE_TIME_PATTERN).matcher(time);
        if (m.find()) {
            int dayOfMonth = Integer.parseInt(m.group(ABSOLUTE_TIME_PATTERN_POS_DAY));
            int month = Integer.parseInt(m.group(ABSOLUTE_TIME_PATTERN_POS_MONTH));
            int year = m.group(ABSOLUTE_TIME_PATTERN_POS_YEAR) != null
                    ? Integer.parseInt(m.group(ABSOLUTE_TIME_PATTERN_POS_YEAR)) : currentYear;

            int hourOfDay = m.group(ABSOLUTE_TIME_PATTERN_POS_HOUR) != null
                    ? Integer.parseInt(m.group(ABSOLUTE_TIME_PATTERN_POS_HOUR)) : defaultTimeOfDay.getHour();
            int minute = m.group(ABSOLUTE_TIME_PATTERN_POS_MINUTE) != null
                    ? Integer.parseInt(m.group(ABSOLUTE_TIME_PATTERN_POS_MINUTE)) : defaultTimeOfDay.getMinute();
            boolean isPM = DAY_PERIOD_PM.equals(m.group(ABSOLUTE_TIME_PATTERN_POS_DAY_PM));

            if (isPM) {
                hourOfDay += DAY_HALF_NUMBER_OF_HOURS;
            }

            if (hourOfDay >= DAY_NUMBER_OF_HOURS) { // invalid date
                return null;
            }

            LocalDateTime date = LocalDateTime.of(year, month, dayOfMonth, hourOfDay, minute);
            return date;
        }

        return null;
    }

    private static LocalTime getDefaultTimeOfDay(int defaultTimeOption) {
        switch (defaultTimeOption) {
            case BEGIN_OF_DAY :
                return LocalTime.MIN;
            case MIDDLE_OF_DAY :
                return LocalTime.NOON;
            case END_OF_DAY :
                return LocalTime.MAX;
        }
        return LocalTime.now();
    }

    /*
     * change the hour of date based on hour option
     */
    private static LocalDateTime adjustByDefaultTime(LocalDateTime date, int defaultTimeOption) {
        date = LocalDateTime.of(date.toLocalDate(), getDefaultTimeOfDay(defaultTimeOption));
        return date;
    }

    /*
     * convert string to date
     */
    public static LocalDateTime parseDate(String time) {
        return parseDate(time, MIDDLE_OF_DAY);
    }

    /*
     * convert string to date
     */
    public static LocalDateTime parseDate(String time, int defaultTimeOption) {
        // check if time is relative time
        LocalDateTime date = parseRelativeTime(time);
        if (date != null) { // date == null means it is not relative time
            date = adjustByDefaultTime(date, defaultTimeOption);
            return date;
        }
        // check some absolute time format
        date = parseAbsoluteTime(time, defaultTimeOption);
        return date;

    }

}
