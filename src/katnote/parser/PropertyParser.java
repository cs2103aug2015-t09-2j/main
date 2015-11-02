package katnote.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import katnote.command.CommandDetail;
import katnote.command.CommandProperties;
import katnote.command.CommandType;
import katnote.utils.DateTimeUtils;
import katnote.utils.KatDateTime;

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
        KatDateTime date;
        switch (optionName) {
            case CommandProperties.TIME_FROM :
                // Date time value
                date = DateParser.parseDateTime(optionValue);
                return date;
            case CommandProperties.TIME_BY :
            case CommandProperties.TIME_TO :
            case CommandProperties.TIME_UNTIL :
                // Date time value
                date = DateParser.parseDateTime(optionValue);
                return date;
            default :
                // String value
                return optionValue;
        }
    }

    /*
     * Automatically fills in the missing parts DateTime value of CommandDetail object
     */
    public static void synchronizeDateTimeValues(CommandDetail command) {
        KatDateTime startDate = command.getStartDate();
        KatDateTime endDate = command.getEndDate();
        KatDateTime dueDate = command.getDueDate();        
        // If command has only due date
        if (dueDate != null){
            // if no date specified, considered it as today
            if (!dueDate.hasDate()){
                dueDate.changeDate();
            }            
            // if there is no time field, consider the time as end of day
            if (!dueDate.hasTime()){
                dueDate.changeTime(KatDateTime.END_OF_DAY_TIME);
            }
        }
        // for command view tasks, if there is no end date, considered it as a very far time,
        if (command.getCommandType() == CommandType.VIEW_TASK){
            if (endDate == null){
                endDate = new KatDateTime(LocalDateTime.MAX);
                command.setProperty(CommandProperties.TIME_TO, endDate);
            }
            // also considered the due date as the end date
            command.setProperty(CommandProperties.TIME_BY, endDate);
            dueDate = endDate;
        }
        // If command has startDate and endDate
        if (startDate != null && endDate != null){                       
            if (!startDate.hasDate() || !endDate.hasDate()){ // at least one of them does not have date
                LocalDate laterDate = DateTimeUtils.getLater(startDate.getDate(), endDate.getDate());
                startDate.changeDate(laterDate);
                endDate.changeDate(laterDate);
            }
            // if there is no time field, consider start date as begin of day and 
            // end date as end of day
            if (!startDate.hasTime()){
                startDate.changeTime(LocalTime.MIDNIGHT);
            }            
            if (!endDate.hasTime()){
                endDate.changeTime(KatDateTime.END_OF_DAY_TIME);
            }                     
        }
    }
}
