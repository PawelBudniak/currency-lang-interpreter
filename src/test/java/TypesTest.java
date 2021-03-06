import currencies.execution.Scope;
import currencies.parser.Parser;
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

        assertFalse(p.tryParseLiteral().truthValue(Scope.empty()));
    }

    @Test
    void nonEmptyStringTruthValue(){
        Parser p = Util.parserFromStringStream("'aa'");

        assertTrue(p.tryParseLiteral().truthValue(Scope.empty()));
    }

    @Test
    void zeroNumberTruthValue(){
        Parser p = Util.parserFromStringStream("0.00");

        assertFalse(p.tryParseLiteral().truthValue(Scope.empty()));
    }

    @Test
    void nonZeroNumberTruthValue(){
        Parser p = Util.parserFromStringStream("0.02");

        assertTrue(p.tryParseLiteral().truthValue(Scope.empty()));
    }

    @Test
    void zeroCurrencyTruthValue(){
        Parser p = Util.parserFromStringStream("0.000 gbp");

        assertFalse(p.tryParseLiteral().truthValue(Scope.empty()));
    }

    @Test
    void nonZeroCurrencyTruthValue(){
        Parser p = Util.parserFromStringStream("0.003 gbp");

        assertTrue(p.tryParseLiteral().truthValue(Scope.empty()));
    }

    @Test
    void booleanTruthValue(){
        assertAll(
                () -> assertTrue(new CBoolean(true).truthValue()),
                () -> assertFalse(new CBoolean(false).truthValue())
        );
    }



}
