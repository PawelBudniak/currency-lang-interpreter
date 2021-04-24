package currencies.reader;

import java.io.IOException;

public interface CodeInput {
    long getPosition();
    char nextChar();
    boolean hasNext();

}
