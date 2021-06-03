package currencies.reader;

import java.io.IOException;

public interface CodeInput {
    int EOF_CODE = -1;
    int NOT_SUPPORTED = -2;

    int firstLineNumber = 1;
    int firstColumnNumber = 1;

    //TODO: fix this
    CharPosition getPosition();
    char nextChar();
    boolean hasNext();

}
