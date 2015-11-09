package katnote.parser;

public class DateParseException extends Exception {

    /**
     * Generated version ID
     */
    private static final long serialVersionUID = 3591822869512871536L;

    public DateParseException() {
        super("Invalid date format");
    }

}
