import currencies.reader.CharPosition;
import currencies.reader.CodeInputStream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReaderTest {

    @Test
    void singleLine(){
        CodeInputStream reader = Util.codeInputStream("abcdefg");
        reader.nextChar();
        char last = reader.nextChar();
        CharPosition position = reader.getPosition();

        assertAll(
                () -> assertEquals('b', last),
                () -> assertEquals(1, position.getLine()),
                () -> assertEquals(2, position.getCharNumber())
        );
    }

    @Test
    void multipleLinesLF(){
        CodeInputStream reader = Util.codeInputStream("a\nbc\ndef");

        reader.nextChar();
        reader.nextChar();
        char last = reader.nextChar();

        CharPosition position = reader.getPosition();

        assertAll(
                () -> assertEquals('b', last),
                () -> assertEquals(2, position.getLine()),
                () -> assertEquals(1, position.getCharNumber())
        );
    }

    @Test
    void multipleLinesCRLF(){
        CodeInputStream reader = Util.codeInputStream("a\n\rbc\ndef");

        reader.nextChar();
        reader.nextChar();
        reader.nextChar();
        char last = reader.nextChar();

        CharPosition position = reader.getPosition();

        assertAll(
                () -> assertEquals('b', last),
                () -> assertEquals(2, position.getLine()),
                () -> assertEquals(1, position.getCharNumber())
        );
    }

}
