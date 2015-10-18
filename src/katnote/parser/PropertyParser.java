package katnote.parser;

import java.util.Date;

import katnote.command.CommandDetail;
import katnote.command.CommandProperties;

public class PropertyParser {

    /*
     * Convert the property string value into appropriate Object based on
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
            case CommandKeywords.KW_UNTIL :
                command.setProperty(CommandProperties.TIME_UNTIL,
                        parseOptionValue(CommandProperties.TIME_UNTIL, propertyValue));
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
            case CommandKeywords.KW_MARK :
                command.setProperty(CommandProperties.EDIT_MARK, propertyValue);
                break;
            default :
                // unknown property, do nothing
                break;
        }
    }

    /*
     * Convert the option string value into appropriate Object based on
     * optionName.
     * 
     */
    public static Object parseOptionValue(String optionName, String optionValue) {
        Date date;
        switch (optionName) {
            case CommandProperties.TIME_FROM :
                // Date time value
                date = DateParser.parseDate(optionValue, DateParser.BEGIN_OF_DAY);
                return date;
            case CommandProperties.TIME_BY :
            case CommandProperties.TIME_TO :
            case CommandProperties.TIME_UNTIL :
                // Date time value
                date = DateParser.parseDate(optionValue, DateParser.END_OF_DAY);
                return date;
            default :
                // String value
                return optionValue;
        }
    }
}
