package test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

import katnote.parser.DateParser;

public class TestDateParser {

    @Test
    public void testRelativeParser1() {
        LocalDateTime date;
        date = DateParser.parseDate("20/10 12:54");
        assertNotNull(date);
        System.out.println(date.toString());
    }

}
