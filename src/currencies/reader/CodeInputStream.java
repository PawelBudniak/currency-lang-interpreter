package currencies.reader;

import java.io.*;

public class CodeInputStream implements CodeInput {

    public static final int EOF_CODE = -1;

    private Reader reader;
    private int lineNumber = 0;
    private int charNumber = -1;
    private boolean isEOF = false;


    public CodeInputStream(InputStream in) {
        reader = new InputStreamReader(in);
    }


    @Override
    public CharPosition getPosition() {
        return new CharPosition(-1, lineNumber, charNumber);

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
                charNumber = -1;
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
