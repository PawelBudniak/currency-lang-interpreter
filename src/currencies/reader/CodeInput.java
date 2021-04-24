package currencies.reader;

import java.io.IOException;

public interface CodeInput {
    CharPosition getPosition();
    char nextChar();
    boolean hasNext();

}
