package org.sjj.csvstream;

/**
 * This class holds the configuration for a CsvParser or CsvFormatter.
 * Use the CsvConfig.DEFAULTS to take all the defaults, or use
 * CsvConfig.Builder to override the defaults.
 *
 * E.g.
 *    CsvConfig config = CsvConfig.Builder().withDelimiter('|')
 *                                          .withHeaderFlag(false)
 *                                          .build();
 *
 * Defaults:
 *   a comma , as the delimiter,
 *   a hash # as the comment character
 *   a double-quote " as the quote character
 *   and expects the first non-comment line to be the header
 *
 *
 */
public class CsvConfig {
    // Constants
    static final char               DEFAULT_DELIMITER = ',';
    static final char               DEFAULT_COMMENT = '#';
    static final char               DEFAULT_QUOTE = '"';
    static final boolean            DEFAULT_HEADER_FLAG = true;

    public static final CsvConfig DEFAULTS =
            new CsvConfig(
                    DEFAULT_HEADER_FLAG,
                    DEFAULT_DELIMITER,
                    DEFAULT_COMMENT,
                    DEFAULT_QUOTE);

    public static final CsvConfig DEFAULTS_WITHOUT_HEADER =
            new CsvConfig(
                    !DEFAULT_HEADER_FLAG,
                    DEFAULT_DELIMITER,
                    DEFAULT_COMMENT,
                    DEFAULT_QUOTE);


    // Private bits
    private final boolean            headerFlag;
    private final char               delimiter;
    private final char               comment;
    private final char               quote;

    public CsvConfig(boolean headerFlag, char delimiter, char comment, char quote) {
        this.headerFlag = headerFlag;
        this.delimiter = delimiter;
        this.comment = comment;
        this.quote = quote;
    }

    public final boolean isHeaderFlag() {
        return headerFlag;
    }

    public final char getDelimiter() {
        return delimiter;
    }

    public final char getComment() {
        return comment;
    }

    public final char getQuote() {
        return quote;
    }

    public final CsvConfig withDelimiter(char delimiter) {
        return new CsvConfig(this.headerFlag, delimiter, this.comment, this.quote);
    }

    public final CsvConfig withComment(char comment) {
        return new CsvConfig(this.headerFlag, this.delimiter, comment, this.quote);
    }

    public final CsvConfig withQuote(char quote) {
        return new CsvConfig(this.headerFlag, this.delimiter, this.comment, quote);
    }

    public final CsvConfig withHeaderFlag(boolean headerFlag) {
        return new CsvConfig(headerFlag, this.delimiter, this.comment, this.quote);
    }
}
