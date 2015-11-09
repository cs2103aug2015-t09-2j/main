//@@author A0126517H
package katnote.parser;

public class CommandParseException extends Exception {

    private static final String STR_INVALID_DATE_FORMAT = "Invalid date format: %1$s";

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -8012762824288625187L;

    /**
     * Constructs a new CommandParseException with the specified detail message.
     *
     * @param message the detail message.
     */
    public CommandParseException(String message) {
        super(message);
    }

    /**
     * Constructs a new CommandParseException with the specified detail message
     * and cause.
     *
     * @param message the detail message.
     * @param cause the cause
     */
    public CommandParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new invalid date format exception
     * 
     * @param dateTimeStr the invalid format date string
     * @return CommandParseException object for this exception
     */
    public static CommandParseException createInvalidDateFormatException(String dateTimeStr) {
        return new CommandParseException(String.format(STR_INVALID_DATE_FORMAT, dateTimeStr));
    }
}
