package org.sjj.csvstream;

import org.junit.Assert;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * Created by stephen on 2/9/16.
 */
public class CsvFormatterTest {
    @Test
    public void testFormmatterBasic() {
        String[] fields = { "hello", "this", "is a", "test"};
        String expected = "hello,this,is a,test";
        CsvFormatter formatter = new CsvFormatter();
        String actual = formatter.format(fields);
        Assert.assertEquals(expected, actual);
        System.out.println(actual);
    }

    @Test
    public void testFormmatterWithCommaInField() {
        String[] fields = { "hello", "this", "is a", "test, okay?"};
        String expected = "hello,this,is a,\"test, okay?\"";
        CsvFormatter formatter = new CsvFormatter();
        String actual = formatter.format(fields);
        Assert.assertEquals(expected, actual);
        System.out.println(actual);
    }

    @Test
    public void testFormmatterWithQuoteInField() {
        String[] fields = { "and", "he said \"let there be light\"", "done" };
        String expected = "and,\"he said \"\"let there be light\"\"\",done";
        CsvFormatter formatter = new CsvFormatter();
        String actual = formatter.format(fields);
        Assert.assertEquals(expected, actual);
        System.out.println(actual);
    }

    @Test
    public void formatFile() {
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        CsvFormatter formatter = new CsvFormatter();
        String[] fields1 = { "and", "he said", "done" };
        String[] fields2 = { "and", "another", "one done" };
        Arrays.asList(fields1, fields2).stream()
                .map(formatter::format)
                .forEach(pw::println);
        System.out.println(writer.toString());
    }
}
