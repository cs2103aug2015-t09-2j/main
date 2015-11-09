//@@author A0126517H
package katnote.parser;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import katnote.utils.KatDateTime;

public class DateParser {

    // relative time strings
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
    private static final String[] MONTHS_OF_YEAR = { "january", "february", "march", "april", "may", "june",
            "july", "august", "september", "october", "november", "december" };
    private static final int MONTH_MIN_LENGTH = 3;

    // Date pattern and properties positions
    private static final String ABSOLUTE_DATE_PATTERN = "(\\d+)\\/(\\w+)(?:\\/(\\d+))?";
    private static final int ABSOLUTE_DATE_PATTERN_POS_DAY = 1;
    private static final int ABSOLUTE_DATE_PATTERN_POS_MONTH = 2;
    private static final int ABSOLUTE_DATE_PATTERN_POS_YEAR = 3;

    // Time pattern and properties positions
    private static final String ABSOLUTE_DATE_TIME_PATTERN = "(\\d{1,2})(?::(\\d+)(am|pm|)|(am|pm))";
    private static final int ABSOLUTE_DATE_TIME_PATTERN_POS_HOUR = 1;
    private static final int ABSOLUTE_DATE_TIME_PATTERN_POS_MINUTE = 2;
    private static final int ABSOLUTE_DATE_TIME_PATTERN_POS_DAY_PM = 3;
    private static final int ABSOLUTE_DATE_TIME_PATTERN_POS_DAY_PM_L = 4;

    // Special constants related to time
    private static final String DAY_PERIOD_PM = "pm";
    private static final int DAY_HALF_NUMBER_OF_HOURS = 12;
    private static final int NUMBER_OF_MONTHS = 12;
    private static final int YEAR_TWO_DIGIT_LIMIT = 100;
    private static final int YEAR_TWO_DIGIT_OFFSET = 2000;

    // default hours of day when no hour given
    public static final int MIDDLE_OF_DAY = 0;
    public static final int BEGIN_OF_DAY = 1;
    public static final int END_OF_DAY = 2;

    // exception message
    private static final String STR_INVALID_DATE_FORMAT = "\"%1$s\" is not recognized as a date time value";

    /**
     * Gets today Date time
     * 
     * @return the current date using the system clock and default time-zone,
     *         not null
     */
    public static LocalDate getToday() {
        return LocalDate.now();
    }

    /**
     * Gets tomorrow Date time
     * 
     * @return the date after the current date using the system clock and
     *         default time-zone, not null
     */
    public static LocalDate getTomorrow() {
        LocalDate date = LocalDate.now();
        date = date.plusDays(1);
        return date;
    }

    /**
     * Gets next week Date time
     * 
     * @return the date which is 7 days after the current date using the system
     *         clock and default time-zone, not null
     */
    public static LocalDate getNextWeek() {
        LocalDate date = LocalDate.now();
        date = date.plusWeeks(1);
        return date;
    }

    /**
     * Finds the next desired weekday
     * 
     * @param desiredWeekday the next desired weekday
     * @return the next desired weekday date after the current date using the
     *         system clock and default time-zone, not null
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

    /**
     * Gets next month Date time
     * 
     * @return the date which is 1 month after the current date using the system
     *         clock and default time-zone, not null
     */
    public static LocalDate getNextMonth() {
        LocalDate date = LocalDate.now();
        date = date.plusMonths(1);
        return date;
    }

    /**
     * Gets next year Date time
     * 
     * @return the date which is 1 year after the current date using the system
     *         clock and default time-zone, not null
     */
    public static LocalDate getNextYear() {
        LocalDate date = LocalDate.now();
        date = date.plusYears(1);
        return date;
    }

    /*
     * Converts the relative time string into LocalDate object
     */
    private static LocalDate parseRelativeDate(String time) {
        switch (time.toLowerCase()) {
            case STR_EMPTY :
                return LocalDate.MIN;
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

    private static String emptyIfNull(String s) {
        return (s == null) ? STR_EMPTY : s;
    }

    /**
     * Converts month string to number. Returns -1 if the value is invalid
     * 
     * @param monthStr
     * 
     * @return
     */
    private static int parseMonth(String monthStr) {
        monthStr = monthStr.toLowerCase();
        try {
            return Integer.parseInt(monthStr);
        } catch (NumberFormatException e) {
            if (monthStr.length() < MONTH_MIN_LENGTH) {
                return -1; // invalid months
            }
            for (int i = 0; i < NUMBER_OF_MONTHS; i++) {
                if (MONTHS_OF_YEAR[i].startsWith(monthStr)) {
                    return i + 1;
                }
            }
            return -1;
        }
    }

    /*
     * Converts string (absolute time format) to LocalDate
     */
    private static LocalDate parseAbsoluteDate(String time) {
        int currentYear = LocalDate.now().getYear();
        // matcher
        Matcher m = Pattern.compile(ABSOLUTE_DATE_PATTERN).matcher(time);
        if (m.find()) {
            int dayOfMonth = Integer.parseInt(m.group(ABSOLUTE_DATE_PATTERN_POS_DAY));
            int month = parseMonth(m.group(ABSOLUTE_DATE_PATTERN_POS_MONTH));
            int year = m.group(ABSOLUTE_DATE_PATTERN_POS_YEAR) != null
                    ? Integer.parseInt(m.group(ABSOLUTE_DATE_PATTERN_POS_YEAR)) : currentYear;

            if (year < YEAR_TWO_DIGIT_LIMIT) {
                year += YEAR_TWO_DIGIT_OFFSET;
            }

            return LocalDate.of(year, month, dayOfMonth);
        }

        return null;
    }

    /*
     * Extracts time of day from time string. If no time of day found, return
     * null instead
     */
    private static LocalTime extractTimeOfDay(String time) {
        Matcher m = Pattern.compile(ABSOLUTE_DATE_TIME_PATTERN).matcher(time);
        if (m.find()) {
            int hour = Integer.parseInt(m.group(ABSOLUTE_DATE_TIME_PATTERN_POS_HOUR));
            int minute = m.group(ABSOLUTE_DATE_TIME_PATTERN_POS_MINUTE) != null
                    ? Integer.parseInt(m.group(ABSOLUTE_DATE_TIME_PATTERN_POS_MINUTE)) : 0;
            String dayPM = emptyIfNull(m.group(ABSOLUTE_DATE_TIME_PATTERN_POS_DAY_PM))
                    + emptyIfNull(m.group(ABSOLUTE_DATE_TIME_PATTERN_POS_DAY_PM_L));
            boolean isPM = DAY_PERIOD_PM.equals(dayPM);
            if (dayPM != STR_EMPTY && hour == 12) { // special case 12am and
                                                    // 12pm
                hour = 0;
            }
            if (isPM) {
                hour = (hour + DAY_HALF_NUMBER_OF_HOURS) % 24;
            }
            return LocalTime.of(hour, minute);
        } else { // when time of day not found, return null
            return null;
        }
    }

    /*
     * Trims time of day from the string
     */
    private static String trimTimeOfDay(String time) {
        return time.replaceFirst(ABSOLUTE_DATE_TIME_PATTERN, "").trim();
    }

    /**
     * Converts string to KatDateTime object
     * 
     * @param time
     * 
     * @return New KatDateTime object representing the time string
     * @throws CommandParseException
     */
    public static KatDateTime parseDateTime(String time) throws CommandParseException {
        // get time of day and trim
        LocalTime timeOfDay = extractTimeOfDay(time);
        String remainingTime = trimTimeOfDay(time);
        // check if time is relative time
        LocalDate date = parseRelativeDate(remainingTime);
        if (date != null) { // date == null means it is not relative time
            return new KatDateTime(date, timeOfDay);
        }
        // check some absolute time format
        date = parseAbsoluteDate(remainingTime);
        if (date == null && timeOfDay == null) {
            throw new CommandParseException(String.format(STR_INVALID_DATE_FORMAT, time));
        }
        return new KatDateTime(date, timeOfDay);

    }

}