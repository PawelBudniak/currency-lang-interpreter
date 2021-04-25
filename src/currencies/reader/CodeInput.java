package currencies.reader;

import java.io.IOException;

public interface CodeInput {
    public static final int EOF_CODE = -1;

    CharPosition getPosition();
    char nextChar();
    boolean hasNext();

}
