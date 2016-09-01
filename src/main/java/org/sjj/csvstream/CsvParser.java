package org.sjj.csvstream;

import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CsvParser {
    // Constants
    private static final char     LF_CHAR = '\n';
    private static final char     CR_CHAR = '\r';

    // Private bits
    private final CharIterator    iter;
    private final char            quote;
    private final char            delim;
    private final char            comment;
    private final List<String>    fields = new ArrayList<>();
    private final StringBuilder   sb = new StringBuilder();
    private final String[]        headerFields;

    public static CsvParser defaultParser(Reader reader) {
        return new CsvParser(CsvConfig.DEFAULTS, reader);
    }

    public CsvParser(CsvConfig config, Reader r) {
        this(config, r, null);
    }

    public CsvParser(CsvConfig config, Reader r, String[] headerFields) {
        this.iter = new CharIterator(r);
        this.quote = config.getQuote();
        this.delim = config.getDelimiter();
        this.comment = config.getComment();

        if (config.isHeaderFlag()) {
            // If the user configuration is setup to read in a header,
            // read it in.  Get the first non-comment row.
            String[] headersFromFile = split();

            if (headerFields == null) {
                this.headerFields = headersFromFile;
            } else {
                // If the user specified the headerFields directly, this takes precedence
                // the header that may have been in the file.
                // The subtle side effect here is that the header has been consumed from the file
                // by the split() above.
                this.headerFields = headerFields;
            }
        } else {
            this.headerFields = headerFields;
        }
    }

    public String[] getHeaderFields() {
        return this.headerFields;
    }

    private boolean isNewline(int ch) {
        NewLineType nlt = NewLineType.LF;
        if (nlt == NewLineType.LF)
            return ch == LF_CHAR;
        else if (nlt == NewLineType.CR)
            return ch == CR_CHAR;
        else if (nlt == NewLineType.CRLF)
            return (ch == CR_CHAR && iter.getPrevInt() == LF_CHAR);
        else
            return (ch == LF_CHAR && iter.getPrevInt() == CR_CHAR);
    }

    Stream<String[]> splitLines() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(new Iterator<String[]>() {
                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @Override
                    public String[] next() {
                        return split();
                    }
                }, Spliterator.IMMUTABLE),
                false);
    }


    private int parseQuotedField() {
        this.sb.setLength(0);
        int ch = iter.nextInt();
        while (ch != -1) {
            if (ch == quote) {
                ch = iter.nextInt();
                if (ch == delim || isNewline(ch) || !iter.hasNext()) {
                    // we found the end of the field.
                    break;
                }
                else if (ch == quote) {
                    // quote quote seen together, so this isn't the end of the field.
                    sb.append(quote);
                    ch = iter.nextInt();
                    continue;
                }
                // Technically an illegal quote, but we can just add it.
                // still not the end of the field.
                sb.append(quote);
            }
            sb.append((char)ch);
            ch = iter.nextInt();
        }
        fields.add(sb.toString().trim());
        return ch;
    }

    private int parseField(int ch) {
        this.sb.setLength(0);

        while (ch != delim && !isNewline(ch)  && ch != -1) {
            sb.append((char)ch);
            ch = iter.nextInt();
        }
        fields.add(sb.toString().trim());
        return ch;//(ch != -1 && !isNewline(ch));
    }

    private void consumeComment() {
        int ch = iter.nextInt();
        while (!isNewline(ch) && ch != -1) {
            ch = iter.nextInt();
        }
    }

    public final String[] split() {
        fields.clear();
        sb.setLength(0);

        if (!iter.hasNext()) {
            return null;
        }

        boolean moreFields = true;
        while (moreFields) {
            int ch = iter.nextInt();
            if (ch == comment) {
                consumeComment();
            } else if (ch == quote) {
                ch = parseQuotedField();
            } else {
                ch = parseField(ch);
            }

            if (ch == -1 || isNewline(ch)) {
                moreFields = false;
            }
        }

        return fields.toArray(new String[fields.size()]);
    }

    private Map<String, String> toMap(String[] header, String[] values) {
        Map<String, String> map = new LinkedHashMap<>();

        for (int i = 0; i < header.length; i++) {
            // If the data line doesn't contain enough fields or doesn't match the header length,
            // we are going to do our best to fill out a map that contains the entries that it does have.
            if (i > values.length) {
                break;
            }
            map.put(header[i], values[i]);
        }
        return map;
    }

    public Stream<Map<String, String>> mappify() {
        if (this.headerFields != null) {
            return this.splitLines()
                    .map(fields -> toMap(this.headerFields, fields));
        } else {
            return null;
        }
    }

    public static String[] split(String s) {
        CsvParser p = new CsvParser(CsvConfig.DEFAULTS.withHeaderFlag(false), new StringReader(s));
        return p.split();
    }

    public static String[] split(CsvConfig config, String s) {
        CsvParser p = new CsvParser(config, new StringReader(s));
        return p.split();
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        FileReader reader = new FileReader("/Users/stephen/Downloads/worldcitiespop.txt");
        CsvParser p2 = new CsvParser(CsvConfig.DEFAULTS, reader);

        int num = 0;
        String[] s;
        while ((s = p2.split()) != null) {
            num += s.length;
        }
        long end = System.currentTimeMillis();
        System.out.println(num);
        System.out.println(end - start + "ms");
    }

}
