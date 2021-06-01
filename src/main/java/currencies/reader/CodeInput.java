package currencies.reader;

import java.io.IOException;

public interface CodeInput {
    int EOF_CODE = -1;
    int NOT_SUPPORTED = -2;

    CharPosition getPosition();
    char nextChar();
    boolean hasNext();

}
