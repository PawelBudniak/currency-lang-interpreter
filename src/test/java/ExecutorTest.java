import currencies.types.CCurrency;
import currencies.ExecutionException;
import currencies.lexer.Lexer;
import currencies.parser.Parser;
import currencies.reader.CodeInputStream;
import currencies.structures.expressions.RValue;
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
        CCurrency.loadExchangeRates("data/exchangeRates.json");
    }

    Parser parserFromStringStream(String s){
        InputStream stream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        return new Parser(new Lexer(new CodeInputStream(stream)));
    }

    @Test
    void ComparisonNumberTrue(){
        Parser p = parserFromStringStream("5 > 3");
        Object result = p.tryParseRValue().getValue();

        assertAll(
                () -> assertTrue( result instanceof Boolean),
                () -> assertTrue((Boolean) result)
        );
    }

    @Test
    void ComparisonNumberFalse(){
        Parser p = parserFromStringStream("5 <= 3");
        Object result = p.tryParseRValue().getValue();

        assertAll(
                () -> assertTrue( result instanceof Boolean),
                () -> assertFalse((Boolean) result)
        );
    }

    @Test
    void ComparisonStringTrue(){
        Parser p = parserFromStringStream("'abc' <= 'bbc'");
        Object result = p.tryParseRValue().getValue();

        assertAll(
                () -> assertTrue( result instanceof Boolean),
                () -> assertTrue((Boolean) result)
        );
    }

    @Test
    void ComparisonStringFalse(){
        Parser p = parserFromStringStream("'abc' > 'bbc'");
        Object result = p.tryParseRValue().getValue();

        assertAll(
                () -> assertTrue( result instanceof Boolean),
                () -> assertFalse((Boolean) result)
        );
    }

    @Test
    void ComparisonCurrencyTrue(){
        Parser p = parserFromStringStream("10 pln > 7 pln");
        Object result = p.tryParseRValue().getValue();

        assertAll(
                () -> assertTrue( result instanceof Boolean),
                () -> assertTrue((Boolean) result)
        );
    }
    @Test
    void ComparisonCurrencyDifferentCodes(){
        Parser p = parserFromStringStream("10 pln > 7 gbp");
        RValue rValue = p.tryParseRValue();

        assertThrows(ExecutionException.class, rValue::getValue, "Trying to compare different currency types without casting throws an error");
    }





}
