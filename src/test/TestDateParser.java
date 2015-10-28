package test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

import katnote.parser.DateParser;

public class TestDateParser {

    @Test
    public void testRelativeParser1() {
        LocalDateTime date;
        date = DateParser.parseDateTime("12am");
        assertNotNull(date);
        System.out.println(date.toString());
    }
    
    @Test
    public void testAbsoluteDateParser(){
        LocalDateTime date;
        // Test "25/10"
        date = DateParser.parseDateTime("25/10");
        assertNotNull(date);
        assertEquals("2015-10-25T12:00", date.toString());
        // Test "25/10 9pm"
        date = DateParser.parseDateTime("25/10 9pm");
        assertNotNull(date);
        assertEquals("2015-10-25T21:00", date.toString());
        // Test "25/10"
        date = DateParser.parseDateTime("25/10/15 4:26am");
        assertNotNull(date);
        assertEquals("2015-10-25T04:26", date.toString());
    }

}
