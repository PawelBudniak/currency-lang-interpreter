import currencies.structures.simple_values.Literal;
import currencies.types.CBoolean;
import currencies.types.CCurrency;
import currencies.ExecutionException;
import currencies.lexer.Lexer;
import currencies.parser.Parser;
import currencies.reader.CodeInputStream;
import currencies.structures.expressions.RValue;
import currencies.types.CNumber;
import currencies.types.CType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ExecutorTest {

    @BeforeAll
    static void loadCurrencies(){
        Util.loadCurrencies();
    }

    @Test
    void ComparisonNumberTrue(){
        Parser p = Util.parserFromStringStream("5 > 3");
        CType result = p.tryParseRValue().getValue();

        assertAll(
                () -> assertTrue(result instanceof CBoolean),
                () -> assertTrue(result.truthValue())
        );
    }

    @Test
    void ComparisonNumberFalse(){
        Parser p = Util.parserFromStringStream("5 <= 3");
        CType result = p.tryParseRValue().getValue();

        assertAll(
                () -> assertTrue(result instanceof CBoolean),
                () -> assertFalse(result.truthValue())
        );
    }

    @Test
    void ComparisonStringTrue(){
        Parser p = Util.parserFromStringStream("'abc' <= 'bbc'");
        CType result = p.tryParseRValue().getValue();

        assertAll(
                () -> assertTrue( result instanceof CBoolean),
                () -> assertTrue(result.truthValue())
        );
    }

    @Test
    void ComparisonStringFalse(){
        Parser p = Util.parserFromStringStream("'abc' > 'bbc'");
        CType result = p.tryParseRValue().getValue();

        assertAll(
                () -> assertTrue( result instanceof CBoolean),
                () -> assertFalse(result.truthValue())
        );
    }

    @Test
    void ComparisonCurrencyTrue(){
        Parser p = Util.parserFromStringStream("10 pln > 7 pln");
        CType result = p.tryParseRValue().getValue();

        assertAll(
                () -> assertTrue(result instanceof CBoolean),
                () -> assertTrue(result.truthValue())
        );
    }
    @Test
    void ComparisonCurrencyDifferentCodes(){
        Parser p = Util.parserFromStringStream("10 pln > 7 gbp");
        RValue rValue = p.tryParseRValue();

        assertThrows(ExecutionException.class, rValue::getValue, "Trying to compare different currency types without casting throws an error");
    }



    @Test
    void simpleFalseBoolTerm(){
        Parser p = Util.parserFromStringStream("false && true");
        RValue rValue = p.tryParseRValue();

        assertFalse(rValue.truthValue());
    }

    @Test
    void simpleTrueBoolTerm(){
        Parser p = Util.parserFromStringStream("true && 1");
        RValue rValue = p.tryParseRValue();

        assertTrue(rValue.truthValue());
    }

    @Test
    void longBoolTerm(){
        Parser p = Util.parserFromStringStream("true && true && true && true && false && true");
        RValue rValue = p.tryParseRValue();

        assertFalse(rValue.truthValue());
    }

    @Test
    void simpleFalseBoolExpr(){
        Parser p = Util.parserFromStringStream("false || 0");
        RValue rValue = p.tryParseRValue();

        assertFalse(rValue.truthValue());
    }

    @Test
    void simpleTrueBoolExpr(){
        Parser p = Util.parserFromStringStream("1 || false");
        RValue rValue = p.tryParseRValue();

        assertTrue(rValue.truthValue());
    }

    @Test
    void longBoolExpr(){
        Parser p = Util.parserFromStringStream("false || false || false || true || false || false");
        RValue rValue = p.tryParseRValue();

        assertTrue(rValue.truthValue());
    }

    @Test
    void simpleArithmeticTerm(){
        Parser p = Util.parserFromStringStream("3 * 6");
        RValue rValue = p.tryParseRValue();
        CNumber expected = CNumber.fromStr("18");

        assertEquals(expected, rValue.getValue());
    }





}
