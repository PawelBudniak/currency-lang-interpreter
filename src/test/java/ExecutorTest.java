import currencies.executor.Scope;
import currencies.structures.simple_values.Literal;
import currencies.structures.simple_values.Variable;
import currencies.structures.statements.Assignment;
import currencies.types.*;
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
    void simpleArithmeticTermMul(){
        Parser p = Util.parserFromStringStream("3 * 6");
        RValue rValue = p.tryParseRValue();
        CNumber expected = CNumber.fromStr("18");

        assertEquals(expected, rValue.getValue());
    }

    @Test
    void simpleArithmeticTermDiv(){
        Parser p = Util.parserFromStringStream("8 / 4");
        RValue rValue = p.tryParseRValue();
        CNumber expected = CNumber.fromStr("2");

        assertEquals(expected, rValue.getValue());
    }

    @Test
    void longArithmeticTerm(){
        Parser p = Util.parserFromStringStream("3 * 6 / 2 * 5 / 15 * 7 * 2");
        RValue rValue = p.tryParseRValue();
        CNumber expected = CNumber.fromStr("42");

        assertEquals(expected, rValue.getValue());
    }

    @Test
    void currencyDivision(){
        Parser p = Util.parserFromStringStream("8 gbp / 4 gbp");
        RValue rValue = p.tryParseRValue();
        CNumber expected = CNumber.fromStr("2");

        assertEquals(expected, rValue.getValue());
    }

    @Test
    void currencyDivisionDifferentCodesThrows(){
        Parser p = Util.parserFromStringStream("8 gbp / 4 pln");
        RValue rValue = p.tryParseRValue();

        assertThrows(ExecutionException.class, () -> rValue.getValue());
    }

    @Test
    void currencyMultThrows(){
        Parser p = Util.parserFromStringStream("8 gbp * 4 gbp");
        RValue rValue = p.tryParseRValue();

        assertThrows(ExecutionException.class, () ->  rValue.getValue());
    }

    @Test
    void currencyMultByScalar(){
        Parser p = Util.parserFromStringStream("8 gbp * 4 ");
        RValue rValue = p.tryParseRValue();
        CCurrency expected = new CCurrency("32", "gbp");

        assertEquals(expected, rValue.getValue());
    }

    @Test
    void currencyDivByScalar(){
        Parser p = Util.parserFromStringStream("8 gbp / 4 ");
        RValue rValue = p.tryParseRValue();
        CCurrency expected = new CCurrency("2", "gbp");

        assertEquals(expected, rValue.getValue());
    }

    @Test
    void declareAndAssign(){
        Parser p = Util.parserFromStringStream("number a = 3.0;");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();

        assignment.execute(scope);
        Variable variable = scope.getVariable("a");
        assertAll(
                () -> assertNotNull(variable),
                () -> assertEquals(CNumber.fromStr("3.0"), variable.getValue())
        );
    }
    @Test
    void declareAndAssignString(){
        Parser p = Util.parserFromStringStream("string a = 'test';");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();

        assignment.execute(scope);
        Variable variable = scope.getVariable("a");
        assertAll(
                () -> assertNotNull(variable),
                () -> assertEquals(new CString("test"), variable.getValue())
        );
    }


    @Test
    void assignExistingVar(){
        Parser p = Util.parserFromStringStream("a = 3.0;");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();
        scope.newVariable(new Variable("a", CType.typeOf("number")));

        assignment.execute(scope);
        Variable variable = scope.getVariable("a");
        assertAll(
                () -> assertNotNull(variable),
                () -> assertEquals(CNumber.fromStr("3.0"), variable.getValue())
        );
    }

    @Test
    void assignToNotDeclaredVarFails(){
        Parser p = Util.parserFromStringStream("a = 3.0;");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();

        assertThrows(ExecutionException.class, () -> assignment.execute(scope));
    }

    @Test
    void assignMismatchTypesFails(){
        Parser p = Util.parserFromStringStream("string a = 3.0;");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();

        assertThrows(ExecutionException.class, () -> assignment.execute(scope));
    }

    @Test
    void assignMismatchTypesFailsWithExistingVariable(){
        Parser p = Util.parserFromStringStream("a = 3.0;");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();
        scope.newVariable(new Variable("a", CType.typeOf("string")));

        assertThrows(ExecutionException.class, () -> assignment.execute(scope));
    }

    @Test
    void minusOperator(){
        Parser p = Util.parserFromStringStream("-10.3;");
        RValue rValue = p.tryParseRValue();

        assertEquals(CNumber.fromStr("-10.3"), rValue.getValue());
    }

    @Test
    void notOperator(){
        Parser p = Util.parserFromStringStream("!true;");
        RValue rValue = p.tryParseRValue();

        assertEquals(new CBoolean(false), rValue.getValue());
    }

    @Test
    void castOperatorSameCurrency(){
        Parser p = Util.parserFromStringStream("[pln] 10 pln;");
        RValue rValue = p.tryParseRValue();

        assertEquals(new CCurrency("10", "pln"), rValue.getValue());
    }

    @Test
    void castOperator(){
        Parser p = Util.parserFromStringStream("[gbp] 10 pln;");
        RValue rValue = p.tryParseRValue();
        CCurrency.setExchangeRate("pln", "gbp", CNumber.fromStr("0.2"));

        assertEquals(new CCurrency("2.0", "gbp"), rValue.getValue());
    }

    @Test
    void numberAddition(){
        Parser p = Util.parserFromStringStream("3.5 + 4.3");
        RValue rValue = p.tryParseRValue();

        assertEquals(CNumber.fromStr("7.8"), rValue.getValue());
    }

    @Test
    void currencyAdditionSameCodes(){
        Parser p = Util.parserFromStringStream("3.5gbp + 4.3gbp");
        RValue rValue = p.tryParseRValue();

        assertEquals(new CCurrency("7.8", "gbp"), rValue.getValue());
    }

    @Test
    void currencyAdditionDifferentCodesFails(){
        Parser p = Util.parserFromStringStream("3.5gbp + 4.3pln");
        RValue rValue = p.tryParseRValue();

        assertThrows(ExecutionException.class, ()-> rValue.getValue());
    }

    @Test
    void stringAddition(){
        Parser p = Util.parserFromStringStream("'a' + 35");
        Parser p2 = Util.parserFromStringStream("35 + 'a'");
        RValue rValue = p.tryParseRValue();
        RValue rValue2 = p2.tryParseRValue();

        assertAll(
                () -> assertEquals(new CString("a35"), rValue.getValue()),
                () -> assertEquals(new CString("35a"), rValue2.getValue())
        );

    }





}
