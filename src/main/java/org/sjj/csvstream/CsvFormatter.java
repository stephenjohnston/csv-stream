package org.sjj.csvstream;

/**
 * Implementation based on RFC 4180 (https://tools.ietf.org/html/rfc4180)
 *
 * This is a utility class to format CSV output.  It will place the delimiter
 * between fields, and only quote fields when necessary.  It is necessary to
 * wrap quotes around a field when the field contains the delimiter character
 * or when it contains the quote character.  If a quote character is embedded
 * in a field, the quote is repeated to escape it.
 *
 * By default, the formatter configuration uses:
 *   a comma , as the delimiter,
 *   a hash # as the comment character
 *   a double-quote " as the quote character
 *
 * The CsvConfig allows you to override default settings.  You can create a
 * custom CsvConfig using the CsvConfig.Builder class.  In the case of a
 * Formatter, the headerFlag in the CsvConfig is not used.  If you want a
 * header, you can format it as you would any other line and insert it as
 * the first non-comment line.
 *
 */
public class CsvFormatter {
    // Private bits - immutable
    private final char quote;
    private final char delimiter;
    private final char comment;

    // Private bits - Mutable state
    // (for performance: try to avoid creating lots of StringBuilder objects)
    private StringBuilder stringBuilder = new StringBuilder();

    public CsvFormatter() {
        this(CsvConfig.DEFAULTS);
    }

    public CsvFormatter(CsvConfig config) {
        this.delimiter = config.getDelimiter();
        this.quote = config.getQuote();
        this.comment = config.getComment();
    }

    private void formatField(String field) {
        int startPos = stringBuilder.length();

        char[] chars = field.toCharArray();
        boolean needQuotes = false;
        for (char aChar : chars) {
            if (aChar == delimiter) {
                needQuotes = true;
            } else if (aChar == quote) {
                needQuotes = true;
                // need to double the quote up.
                stringBuilder.append(aChar);
            }
            stringBuilder.append(aChar);
        }
        if (needQuotes && startPos >= 0 && startPos < stringBuilder.length() ) {
            // wrap with quotes
            stringBuilder.insert(startPos, '"');
            stringBuilder.append(quote);
        }
    }

    public String format(String[] fields) {
        this.stringBuilder.setLength(0);
        for (int i = 0; i < fields.length; i++) {
            formatField(fields[i]);
            if (i < fields.length - 1)
                this.stringBuilder.append(delimiter);
        }
        return this.stringBuilder.toString();
    }
}
