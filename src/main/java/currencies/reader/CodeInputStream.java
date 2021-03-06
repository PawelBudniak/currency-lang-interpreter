package currencies.reader;

import java.io.*;
import java.util.Objects;

public class CodeInputStream implements CodeInput {

    private Reader reader;
    private int lineNumber = firstLineNumber;
    private int charNumber = firstColumnNumber - 1;
    private boolean isEOF = false;


    public CodeInputStream(InputStream in) {
        reader = new InputStreamReader(in);
    }


    @Override
    public CharPosition getPosition() {
        return new CharPosition(NOT_SUPPORTED, lineNumber, charNumber);

    }

    @Override
    public char nextChar() {

        try {
            int r = reader.read();

            if (r == -1) {
                isEOF = true;
                return (char) EOF_CODE;
            }

            char c = (char) r;

            if (c == '\n') {
                ++lineNumber;
                charNumber = firstColumnNumber - 1;
            }
            if (c != '\r' && c != '\n') {
                ++charNumber;
            }
            return c;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return !isEOF;
    }
}
