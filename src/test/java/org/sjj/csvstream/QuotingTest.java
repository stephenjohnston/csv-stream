package org.sjj.csvstream;

import static org.junit.Assert.*;
import org.junit.Test;

public class QuotingTest {
  @Test
  public void testEncodeDecodeNestedQuotes() {
    String[] inner_data = { "3.14", "test test", "abc", "123,456.00" };
    CsvFormatter formatter = new CsvFormatter();
    String cell = formatter.format(inner_data);
    String[] outer_data = { cell, "another test", "Pipe | Test"};
    formatter = new CsvFormatter(CsvConfig.DEFAULTS.withDelimiter('|'));

    String encoded_string = formatter.format(outer_data);
//    System.out.println("Encoded_string: " + encoded_string);

    // Now break the encoded-string back apart and make sure we get the data back out.
    String[] new_outer_data = CsvParser.split(encoded_string, '|');
    for (int i = 0; i < outer_data.length; i++) {
      assertEquals(outer_data[i], new_outer_data[i]);
    }
    String[] new_inner_data = CsvParser.split(outer_data[0]);
    for (int i = 0; i < inner_data.length; i++) {
      assertEquals(inner_data[i], new_inner_data[i]);
    }
  }

}
