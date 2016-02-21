package org.sjj.csvstream;

import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvParserTest {

    private void checkResultWithoutHeader(String input, List<String[]> expected) {
        CsvConfig config = CsvConfig.DEFAULTS_WITHOUT_HEADER;
        CsvParser parser = new CsvParser(config, new StringReader(input));
        List<String[]> actual = parser.splitLines().collect(Collectors.toList());
        Assert.assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            String[] a = actual.get(i);
            String[] e = expected.get(i);
            Assert.assertArrayEquals(e, a);
        }
    }

    @Test
    public void testSplit() {
        String s = "one,two,three";
        String[] fields = CsvParser.split(s);
        Assert.assertArrayEquals(new String[]{"one","two","three"}, fields);
    }

    @Test
    public void testSplitLines() {
        String input = "First,Last,Age\nBob,Smith,44\nJane,Doe,40";
        StringReader reader = new StringReader(input);
        Stream<String[]> records = CsvParser.defaultParser(reader).splitLines();
        int sum = records.mapToInt(record -> record.length).sum();
        Assert.assertEquals(6, sum);
    }

    @Test
    public void testCustomizedParser() {
        String input = "First|Last|Age\nBob|Smith|44\nJane|Doe|40";
        StringReader reader = new StringReader(input);
        CsvParser parser = new CsvParser(CsvConfig.DEFAULTS.withDelimiter('|'), reader);
        Stream<String[]> records = parser.splitLines();
        int sum = records.mapToInt(record -> record.length).sum();
        Assert.assertEquals(6, sum);
    }

    @Test
    public void testMappify() {
        String input = "First,Last,Age\nBob,Smith,44\nJane,Doe,40";
        StringReader reader = new StringReader(input);
        Stream<Map<String, String>> stream = CsvParser.defaultParser(reader).mappify();
        stream.forEach(System.out::println);

    }

    @Test
    public void testGetDefault() {
        String s = "one,two,three";
        CsvParser p = CsvParser.defaultParser(new StringReader(s));
        Stream<String[]> records = p.splitLines();

    }

    @Test
    public void testWithEmptyFieldAtEnd() {
        String input = "one,two,three\nfour,five,";
        List<String[]> expected = Arrays.asList(
                new String[]{"one", "two", "three"},
                new String[]{"four", "five", ""});

        checkResultWithoutHeader(input, expected);

    }

    @Test
    public void testWithEmptyFieldAtBeginning() {
        String input = ",two,three\nfour,five,";
        List<String[]> expected = Arrays.asList(
                new String[]{"", "two", "three"},
                new String[]{"four", "five", ""});

        checkResultWithoutHeader(input, expected);
    }

    @Test
    public void testQuotedStrings() {
        String input = "\"one\",\"two\",\"three\"\n\"four\",five,";
        List<String[]> expected = Arrays.asList(
                new String[]{"one", "two", "three"},
                new String[]{"four", "five", ""});

        checkResultWithoutHeader(input, expected);
    }

    @Test
    public void testQuotedStrings2() {
        String input = "\"one\",\"two\",\"three,extra comma\"\n\"four\",five,";
        List<String[]> expected = Arrays.asList(
                new String[]{"one", "two", "three,extra comma"},
                new String[]{"four", "five", ""});

        checkResultWithoutHeader(input, expected);

    }

    @Test
    public void testWithAllEmptyFields() {
        String input = ",,\n";
        List<String[]> expected = new ArrayList<>();
        expected.add(new String[]{"", "", ""});

        checkResultWithoutHeader(input, expected);

    }

    @Test
    public void testWithAEmptyFieldInMiddle() {
        String input = "one,,two\n";
        List<String[]> expected = new ArrayList<>();
        expected.add(new String[]{"one", "", "two"});

        checkResultWithoutHeader(input, expected);

    }

    @Test
    public void testWithQuotedNewline() {

        String input = "one,two,three\nfour,five,\"six\nseven\"\neight,nine,ten";
        List<String[]> expected = Arrays.asList(
                new String[]{"one", "two", "three"},
                new String[]{"four", "five", "six\nseven"},
                new String[]{"eight", "nine", "ten"});

        checkResultWithoutHeader(input, expected);
    }

    @Test
    public void testWithEscapedQuote() {
        String input = "one,two,three\nfour,five,\"six\"\"seven\"\neight,nine,ten";
        List<String[]> expected = Arrays.asList(
                new String[]{"one", "two", "three"},
                new String[]{"four", "five", "six\"seven"},
                new String[]{"eight", "nine", "ten"});

        checkResultWithoutHeader(input, expected);
    }

    @Test
    public void testWithIllegalQuote() {
        String input = "one,two,three\nfour,five,\"six\"seven\"\neight,nine,ten";
        List<String[]> expected = Arrays.asList(
                new String[]{"one", "two", "three"},
                new String[]{"four", "five", "six\"seven"},
                new String[]{"eight", "nine", "ten"});

        checkResultWithoutHeader(input, expected);
    }

    private void checkHeaderResult(String input, String[] expectedHeader,
                                   String[][] expectedData) {
        CsvParser parser = new CsvParser(CsvConfig.DEFAULTS,
                new StringReader(input));

        Stream<String[]> lines = parser.splitLines();
        String[] actual = parser.getHeaderFields();
        Assert.assertArrayEquals(expectedHeader, actual);

        String[][] actual2 = lines.collect(Collectors.toList()).toArray(new String[0][0]);
        Assert.assertArrayEquals(expectedData, actual2);
    }

    @Test
    public void getHeaderTest() {
        String[] expectedHeader = {"name", "age", "phone"};
        String[][] expectedData = {{"Steve", "40", "555"}, {"Jim", "42", "995"}};
        String input = "name,age,phone\nSteve,40,555\nJim,42,995";
        checkHeaderResult(input, expectedHeader, expectedData);
    }

    @Test
    public void getHeaderWithCommentsTest() {
        String[] expectedHeader = {"name", "age", "phone"};
        String[][] expectedData = {{"Steve", "40", "555"}, {"Jim", "42", "995"}};
        String input = "# this is a comment\n# so is this\nname,age,phone\nSteve,40,555\nJim,42,995";
        checkHeaderResult(input, expectedHeader,
                expectedData);
    }
}