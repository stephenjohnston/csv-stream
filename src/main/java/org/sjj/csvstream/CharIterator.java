package org.sjj.csvstream;

import java.io.IOException;
import java.io.Reader;
import java.util.PrimitiveIterator;

public class CharIterator implements PrimitiveIterator.OfInt {
    // Constants
    private static final int DEFAULT_CHUNK_SIZE = 8192;

    // Private bits that keep the state of the iterator
    private Reader     reader;
    private char[]     chunk;
    private int        position = 0;
    private int        chunkLength;
    private int        prevInt = -1;

    public CharIterator(Reader reader) {
        this.reader = reader;
        this.chunk = new char[DEFAULT_CHUNK_SIZE];
        readNextChunk(); // initialize the first chunk.
    }

    private void readNextChunk() {
        position = 0;
        try {
            chunkLength = reader.read(chunk);
        } catch (IOException e) {
            // TODO cleanup this exception
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasNext() {
        if (chunkLength == -1) {
            return false;
        }
        return true;
    }

    @Override
    public int nextInt() {
        prevInt = chunk[position];

        if (position == 0 && chunkLength == -1) {
            return -1;
        }
        int ret_val = chunk[position++];

        if (position >= chunkLength) {
            readNextChunk();
        }
        return ret_val;
    }

    public int getPrevInt() {
        return prevInt;
    }
}
