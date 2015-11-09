//@@author A0126517H
package test;

import static org.junit.Assert.*;
import org.junit.Test;

import katnote.parser.CommandParseException;
import katnote.parser.DateParser;
import katnote.utils.KatDateTime;

public class TestDateParser {

    @Test
    public void testRelativeParser1() throws CommandParseException {
        KatDateTime date;
        date = DateParser.parseDateTime("7pm tomorrow");
        assertNotNull(date);
        System.out.println(date.toString());
    }

    // *
    @Test
    public void testAbsoluteDateParser() throws CommandParseException {
        KatDateTime date;

        // Test "7pm" (only time)
        date = DateParser.parseDateTime("7pm");
        assertNotNull(date);
        assertEquals("19:00", date.toString());

        // Test "25/10" (only date)
        date = DateParser.parseDateTime("25/10");
        assertNotNull(date);
        assertEquals("2015-10-25", date.toString());

        // Test "19/11/2015 12:00pm"
        date = DateParser.parseDateTime("19/11/2015 12:00pm");
        assertNotNull(date);
        assertEquals("2015-11-19T12:00", date.toString());

        // Test "19/Aug 19:00"
        date = DateParser.parseDateTime("19/Aug 19:00");
        assertNotNull(date);
        assertEquals("2015-08-19T19:00", date.toString());

        // Test "19/Aug 19:00"
        date = DateParser.parseDateTime("19/Aug 19:00");
        assertNotNull(date);
        assertEquals("2015-08-19T19:00", date.toString());

        // Test "25/10 9pm"
        date = DateParser.parseDateTime("25/10 9pm");
        assertNotNull(date);
        assertEquals("2015-10-25T21:00", date.toString());

        // Test "20:00 20/November"
        date = DateParser.parseDateTime("20:00 20/November");
        assertNotNull(date);
        assertEquals("2015-11-20T20:00", date.toString());

        // Test "6pm 19/Nov"
        date = DateParser.parseDateTime("6pm 19/Nov");
        assertNotNull(date);
        assertEquals("2015-11-19T18:00", date.toString());
    }
    // */

}
