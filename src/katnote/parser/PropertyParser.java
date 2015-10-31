package katnote.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;

import katnote.command.CommandDetail;
import katnote.command.CommandProperties;
import katnote.utils.DateTimeUtils;

public class PropertyParser {

    /*
     * Converts the property string value into appropriate Object based on
     * propertyName. For example, parseProperty(“from”, “24/10/2015”) will
     * return a Date object representing 24/10/2015.
     * 
     * @param propertyName Command property keyword (can be "from", "to",
     * "by",...)
     * 
     * @param propertyValue Value of command property inputed by user input
     * value
     * 
     * @param command the CommandDetail object which is currently working on.
     * The result object of this property will be added as an option to this
     * CommandDetail object
     * 
     */
    public static void parseProperty(String propertyName, String propertyValue, CommandDetail command) {
        switch (propertyName) {
            case CommandKeywords.KW_FROM :
                command.setProperty(CommandProperties.TIME_FROM,
                        parseOptionValue(CommandProperties.TIME_FROM, propertyValue));
                break;
            case CommandKeywords.KW_BY :
                command.setProperty(CommandProperties.TIME_BY,
                        parseOptionValue(CommandProperties.TIME_BY, propertyValue));
                break;
            case CommandKeywords.KW_TO :
                command.setProperty(CommandProperties.TIME_TO,
                        parseOptionValue(CommandProperties.TIME_TO, propertyValue));
                break;
            case CommandKeywords.KW_ON :
                // take the begin of day to TIME_FROM and end of day to TIME_TO
                command.setProperty(CommandProperties.TIME_FROM,
                        parseOptionValue(CommandProperties.TIME_FROM, propertyValue));
                command.setProperty(CommandProperties.TIME_TO,
                        parseOptionValue(CommandProperties.TIME_TO, propertyValue));
                break;
            case CommandKeywords.KW_SET :
                command.setProperty(CommandProperties.EDIT_SET_PROPERTY, new EditTaskOption(propertyValue));
                break;
            default :
                // unknown property, do nothing
                break;
        }
    }

    /*
     * Converts the option string value into appropriate Object based on
     * optionName.
     * 
     */
    public static Object parseOptionValue(String optionName, String optionValue) {
        LocalDateTime date;
        switch (optionName) {
            case CommandProperties.TIME_FROM :
                // Date time value
                date = DateParser.parseDateTime(optionValue, DateParser.BEGIN_OF_DAY);
                return date;
            case CommandProperties.TIME_BY :
            case CommandProperties.TIME_TO :
            case CommandProperties.TIME_UNTIL :
                // Date time value
                date = DateParser.parseDateTime(optionValue, DateParser.END_OF_DAY);
                return date;
            default :
                // String value
                return optionValue;
        }
    }

    /*
     * 
     */
    public static void synchronizeDateTimeValues(CommandDetail command) {
        LocalDateTime startDate = command.getStartDate();
        LocalDateTime endDate = command.getEndDate();
        LocalDateTime dueDate = command.getDueDate();
        if (dueDate != null && DateTimeUtils.hasMinDate(dueDate)){
            dueDate = DateTimeUtils.changeDate(dueDate);
            command.setProperty(CommandProperties.TIME_BY, dueDate);
        }
        if (startDate != null && endDate != null){
            if (DateTimeUtils.hasMinDate(startDate) || DateTimeUtils.hasMinDate(endDate)){
                LocalDate laterDate = DateTimeUtils.getLater(startDate, endDate).toLocalDate();
                startDate = DateTimeUtils.changeDate(startDate, laterDate);
                endDate = DateTimeUtils.changeDate(endDate, laterDate);
                command.setProperty(CommandProperties.TIME_FROM, startDate);
                command.setProperty(CommandProperties.TIME_TO, endDate);
            }
        }        
    }
}
