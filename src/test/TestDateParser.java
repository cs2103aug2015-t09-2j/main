package test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

import katnote.parser.DateParser;

public class TestDateParser {

    @Test
    public void testRelativeParser1() {
        LocalDateTime date;
        date = DateParser.parseDateTime("7pm tomorrow");
        assertNotNull(date);
        System.out.println(date.toString());
    }
    
    //*
    @Test
    public void testAbsoluteDateParser(){
        LocalDateTime date;
        
        // Test "25/10"
        date = DateParser.parseDateTime("25/10");
        assertNotNull(date);
        assertEquals("2015-10-25T12:00", date.toString());
        
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
    //*/

}
