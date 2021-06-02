import currencies.parser.Parser;
import currencies.structures.simple_values.Literal;
import currencies.types.CBoolean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TypesTest {

    @BeforeAll
    static void loadCurrencies(){
        Util.loadCurrencies();
    }

    @Test
    void emptyStringTruthValue(){
        Parser p = Util.parserFromStringStream("''");

        assertFalse(p.tryParseLiteral().truthValue());
    }

    @Test
    void nonEmptyStringTruthValue(){
        Parser p = Util.parserFromStringStream("'aa'");

        assertTrue(p.tryParseLiteral().truthValue());
    }

    @Test
    void zeroNumberTruthValue(){
        Parser p = Util.parserFromStringStream("0.00");

        assertFalse(p.tryParseLiteral().truthValue());
    }

    @Test
    void nonZeroNumberTruthValue(){
        Parser p = Util.parserFromStringStream("0.02");

        assertTrue(p.tryParseLiteral().truthValue());
    }

    @Test
    void zeroCurrencyTruthValue(){
        Parser p = Util.parserFromStringStream("0.000 gbp");

        assertFalse(p.tryParseLiteral().truthValue());
    }

    @Test
    void nonZeroCurrencyTruthValue(){
        Parser p = Util.parserFromStringStream("0.003 gbp");

        assertTrue(p.tryParseLiteral().truthValue());
    }

    @Test
    void booleanTruthValue(){
        assertAll(
                () -> assertTrue(new CBoolean(true).truthValue()),
                () -> assertFalse(new CBoolean(false).truthValue())
        );
    }



}
