# csv-stream

csv-stream is a CSV parsing and formatting library with the following design goals:
  - Supports Java 8 Streams 
  - High performance
  - Configurable
  - Correct and complete CSV parsing as specified by RFC 4180
  - Flexible enough to cope with improperly formatted CSV files 

If you find any areas in which the above design goals were not met, please create an issue.  Pull-requests for any fixes or improvements are welcome and will receive consideration.

Note: Requires Java 8 or later.

Usage examples
--------------
Parse a string into tokens
```java
    String s = "one,two,three";
    String[] fields = CsvParser.split(s);
    Assert.assertArrayEquals(new String[]{"one","two","three"}, fields);
```
Parse a file (or any other reader) into a stream of records.
Here we are assuming that /tmp/foo.csv contains a header as the first row and that the data records follow.
```java
    FileReader reader = new FileReader("/tmp/foo.csv");
    Stream<String[]> records = CsvParser.defaultParser(reader).splitLines();
    int sum = records.mapToInt(record -> record.length).sum();
```

To customize the parser, use a CsvConfig object.  You can take the default config, and then customize from there.
```java
    CsvParser parser = new CsvParser(CsvConfig.DEFAULTS.withDelimiter('|'), reader);
    Stream<String[]> records = parser.splitLines();
    int sum = records.mapToInt(record -> record.length).sum();
```

If you would rather have a stream containing Map<String, String> instead of String[], you can use mappify.  Using mappify requires a header to be present as the first row in the input, or a header can also be provided in a customized CsvConfig object. 
```java
    String input = "First,Last,Age\nBob,Smith,44\nJane,Doe,40";
    StringReader reader = new StringReader(input);
    Stream<Map<String, String>> stream = CsvParser.defaultParser(reader).mappify();
    stream.forEach(System.out::println);
    // Output: {First=Bob, Last=Smith, Age=44}
    //         {First=Jane, Last=Doe, Age=40}
```
For further usage examples, see the test suite.


License
-------
MIT
