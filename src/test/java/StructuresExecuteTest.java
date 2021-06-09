import currencies.execution.Scope;
import currencies.lexer.TokenType;
import currencies.structures.Function;
import currencies.structures.Program;
import currencies.structures.simple_values.FunctionCall;
import currencies.structures.simple_values.Identifier;
import currencies.structures.simple_values.Variable;
import currencies.structures.statements.Assignment;
import currencies.structures.statements.IfStatement;
import currencies.structures.statements.Loop;
import currencies.types.*;
import currencies.execution.ExecutionException;
import currencies.parser.Parser;
import currencies.structures.expressions.RValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StructuresExecuteTest {

    @BeforeAll
    static void loadCurrencies(){
        Util.loadCurrencies();
    }

    @Test
    void ComparisonNumberTrue(){
        Parser p = Util.parserFromStringStream("5 > 3");
        CType result = p.tryParseRValue().getValue(Scope.empty());

        assertAll(
                () -> assertTrue(result instanceof CBoolean),
                () -> assertTrue(result.truthValue())
        );
    }

    @Test
    void ComparisonNumberFalse(){
        Parser p = Util.parserFromStringStream("5 <= 3");
        CType result = p.tryParseRValue().getValue(Scope.empty());

        assertAll(
                () -> assertTrue(result instanceof CBoolean),
                () -> assertFalse(result.truthValue())
        );
    }

    @Test
    void ComparisonStringTrue(){
        Parser p = Util.parserFromStringStream("'abc' <= 'bbc'");
        CType result = p.tryParseRValue().getValue(Scope.empty());

        assertAll(
                () -> assertTrue( result instanceof CBoolean),
                () -> assertTrue(result.truthValue())
        );
    }

    @Test
    void ComparisonStringFalse(){
        Parser p = Util.parserFromStringStream("'abc' > 'bbc'");
        CType result = p.tryParseRValue().getValue(Scope.empty());

        assertAll(
                () -> assertTrue( result instanceof CBoolean),
                () -> assertFalse(result.truthValue())
        );
    }

    @Test
    void ComparisonCurrencyTrue(){
        Parser p = Util.parserFromStringStream("10 pln > 7 pln");
        CType result = p.tryParseRValue().getValue(Scope.empty());

        assertAll(
                () -> assertTrue(result instanceof CBoolean),
                () -> assertTrue(result.truthValue())
        );
    }
    @Test
    void ComparisonCurrencyDifferentCodesIsPossible(){
        Parser p = Util.parserFromStringStream("10 pln > 7 gbp");
        RValue rValue = p.tryParseRValue();

        assertThrows(ExecutionException.class, () -> rValue.getValue(Scope.empty()), "Trying to compare different currency types without casting throws an error");
    }



    @Test
    void simpleFalseBoolTerm(){
        Parser p = Util.parserFromStringStream("false && true");
        RValue rValue = p.tryParseRValue();

        assertFalse(rValue.truthValue(Scope.empty()));
    }

    @Test
    void simpleTrueBoolTerm(){
        Parser p = Util.parserFromStringStream("true && 1");
        RValue rValue = p.tryParseRValue();

        assertTrue(rValue.truthValue(Scope.empty()));
    }

    @Test
    void longBoolTerm(){
        Parser p = Util.parserFromStringStream("true && true && true && true && false && true");
        RValue rValue = p.tryParseRValue();

        assertFalse(rValue.truthValue(Scope.empty()));
    }

    @Test
    void simpleFalseBoolExpr(){
        Parser p = Util.parserFromStringStream("false || 0");
        RValue rValue = p.tryParseRValue();

        assertFalse(rValue.truthValue(Scope.empty()));
    }

    @Test
    void simpleTrueBoolExpr(){
        Parser p = Util.parserFromStringStream("1 || false");
        RValue rValue = p.tryParseRValue();

        assertTrue(rValue.truthValue(Scope.empty()));
    }

    @Test
    void longBoolExpr(){
        Parser p = Util.parserFromStringStream("false || false || false || true || false || false");
        RValue rValue = p.tryParseRValue();

        assertTrue(rValue.truthValue(Scope.empty()));
    }

    @Test
    void simpleArithmeticTermMul(){
        Parser p = Util.parserFromStringStream("3 * 6");
        RValue rValue = p.tryParseRValue();
        CNumber expected = CNumber.fromStr("18");

        assertEquals(expected, rValue.getValue(Scope.empty()));
    }

    @Test
    void simpleArithmeticTermDiv(){
        Parser p = Util.parserFromStringStream("8 / 4");
        RValue rValue = p.tryParseRValue();
        CNumber expected = CNumber.fromStr("2");

        assertEquals(expected, rValue.getValue(Scope.empty()));
    }

    @Test
    void longArithmeticTerm(){
        Parser p = Util.parserFromStringStream("3 * 6 / 2 * 5 / 15 * 7 * 2");
        RValue rValue = p.tryParseRValue();
        CNumber expected = CNumber.fromStr("42");

        assertEquals(expected, rValue.getValue(Scope.empty()));
    }

    @Test
    void currencyDivision(){
        Parser p = Util.parserFromStringStream("8 gbp / 4 gbp");
        RValue rValue = p.tryParseRValue();
        CNumber expected = CNumber.fromStr("2");

        assertEquals(expected, rValue.getValue(Scope.empty()));
    }

    @Test
    void currencyDivisionDifferentCodesThrows(){
        Parser p = Util.parserFromStringStream("8 gbp / 4 pln");
        RValue rValue = p.tryParseRValue();

        assertThrows(ExecutionException.class, () -> rValue.getValue(Scope.empty()));
    }

    @Test
    void currencyMultThrows(){
        Parser p = Util.parserFromStringStream("8 gbp * 4 gbp");
        RValue rValue = p.tryParseRValue();

        assertThrows(ExecutionException.class, () ->  rValue.getValue(Scope.empty()));
    }

    @Test
    void currencyMultByScalar(){
        Parser p = Util.parserFromStringStream("8 gbp * 4 ");
        RValue rValue = p.tryParseRValue();
        CCurrency expected = new CCurrency("32", "gbp");

        assertEquals(expected, rValue.getValue(Scope.empty()));
    }

    @Test
    void MultiplicationIsCommutative(){
        Parser p = Util.parserFromStringStream("8 gbp * 4 == 4 * 8 gbp");
        RValue rValue = p.tryParseRValue();

        assertTrue(rValue.truthValue(Scope.empty()));
    }

    @Test
    void currencyDivByScalar(){
        Parser p = Util.parserFromStringStream("8 gbp / 4 ");
        RValue rValue = p.tryParseRValue();
        CCurrency expected = new CCurrency("2", "gbp");

        assertEquals(expected, rValue.getValue(Scope.empty()));
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
                () -> assertEquals(CNumber.fromStr("3.0"), variable.getSavedValue()),
                () -> assertEquals(CNumber.fromStr("3.0"), scope.getVariable("a").getSavedValue(), "assignment should modify scope")
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
                () -> assertEquals(new CString("test"), variable.getSavedValue()),
                () -> assertEquals(new CString("test"), scope.getVariable("a").getSavedValue(), "assignment should modify scope")
        );
    }


    @Test
    void assignExistingVar(){
        Parser p = Util.parserFromStringStream("a = 3.0;");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();
        Variable initialVar = new Variable(new Identifier("a", null), CType.typeOf(TokenType.T_KW_NUMBER));
        initialVar.setValue(CNumber.fromStr("2"));
        scope.newVariable(initialVar);

        assignment.execute(scope);
        Variable variable = scope.getVariable("a");
        assertAll(
                () -> assertNotNull(variable),
                () -> assertEquals(CNumber.fromStr("3.0"), variable.getSavedValue()),
                () -> assertEquals(CNumber.fromStr("3.0"), scope.getVariable("a").getSavedValue(), "assignment should modify scope")
        );
    }

    @Test
    void assignToNotDeclaredVarFails(){
        Parser p = Util.parserFromStringStream("a = 3.0;");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();

        assertAll(
                () -> assertThrows(ExecutionException.class, () -> assignment.execute(scope)),
                () -> assertNull(scope.getVariable("a"))
        );
    }

    @Test
    void assignMismatchTypesFails(){
        Parser p = Util.parserFromStringStream("string a = 3.0;");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();

        assertAll(
                () -> assertThrows(ExecutionException.class, () -> assignment.execute(scope)),
                () -> assertNull(scope.getVariable("a"))
        );
    }

    @Test
    void assignMismatchTypesFailsWithExistingVariable(){
        Parser p = Util.parserFromStringStream("a = 3.0;");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();
        Variable existingVar = new Variable(new Identifier("a", null), CType.typeOf(TokenType.T_KW_STRING));
        existingVar.setValue(new CString("strVal"));
        scope.newVariable(existingVar);

        assertAll(
                () -> assertThrows(ExecutionException.class, () -> assignment.execute(scope)),
                () -> assertEquals(new CString("strVal"), scope.getVariable("a").getSavedValue())
        );
    }

    @Test
    void minusOperator(){
        Parser p = Util.parserFromStringStream("-10.3;");
        RValue rValue = p.tryParseRValue();

        assertEquals(CNumber.fromStr("-10.3"), rValue.getValue(Scope.empty()));
    }

    @Test
    void notOperator(){
        Parser p = Util.parserFromStringStream("!true;");
        RValue rValue = p.tryParseRValue();

        assertEquals(new CBoolean(false), rValue.getValue(Scope.empty()));
    }

    @Test
    void castOperatorSameCurrency(){
        Parser p = Util.parserFromStringStream("[pln] 10 pln;");
        RValue rValue = p.tryParseRValue();

        assertEquals(new CCurrency("10", "pln"), rValue.getValue(Scope.empty()));
    }

    @Test
    void castOperator(){
        Parser p = Util.parserFromStringStream("[gbp] 10 pln;");
        RValue rValue = p.tryParseRValue();
        CCurrency.setExchangeRate("pln", "gbp", CNumber.fromStr("0.2"));

        assertEquals(new CCurrency("2.0", "gbp"), rValue.getValue(Scope.empty()));
    }

    @Test
    void numberAddition(){
        Parser p = Util.parserFromStringStream("3.5 + 4.3");
        RValue rValue = p.tryParseRValue();

        assertEquals(CNumber.fromStr("7.8"), rValue.getValue(Scope.empty()));
    }

    @Test
    void numberSubtraction(){
        Parser p = Util.parserFromStringStream("3.5 - 4.3");
        RValue rValue = p.tryParseRValue();

        assertEquals(CNumber.fromStr("-0.8"), rValue.getValue(Scope.empty()));
    }

    @Test
    void currencyNumberSubtractionFails(){
        Parser p = Util.parserFromStringStream("3.5 gbp - 4.3");
        RValue rValue = p.tryParseRValue();

        assertThrows(ExecutionException.class, () -> rValue.getValue(Scope.empty()));
    }

    @Test
    void stringNumberSubtractionFails(){
        Parser p = Util.parserFromStringStream("'str' - 4.3");
        RValue rValue = p.tryParseRValue();

        assertThrows(ExecutionException.class, () -> rValue.getValue(Scope.empty()));
    }

    @Test
    void currencyAdditionSameCodes(){
        Parser p = Util.parserFromStringStream("3.5gbp + 4.3gbp");
        RValue rValue = p.tryParseRValue();

        assertEquals(new CCurrency("7.8", "gbp"), rValue.getValue(Scope.empty()));
    }

    @Test
    void currencyAdditionDifferentCodesFails(){
        Parser p = Util.parserFromStringStream("3.5gbp + 4.3pln");
        RValue rValue = p.tryParseRValue();

        assertThrows(ExecutionException.class, ()-> rValue.getValue(Scope.empty()));
    }

    @Test
    void stringAddition(){
        Parser p = Util.parserFromStringStream("'a' + 35");
        Parser p2 = Util.parserFromStringStream("35 + 'a'");
        RValue rValue = p.tryParseRValue();
        RValue rValue2 = p2.tryParseRValue();

        assertAll(
                () -> assertEquals(new CString("a35"), rValue.getValue(Scope.empty())),
                () -> assertEquals(new CString("35a"), rValue2.getValue(Scope.empty()))
        );

    }

    @Test
    void usePreviouslyDefinedVariable(){
        Parser p = Util.parserFromStringStream("a * 3");
        RValue rValue = p.tryParseRValue();
        Scope scope = Scope.empty();
        scope.newVariable(new Variable(new Identifier("a", null), CType.typeOf(TokenType.T_KW_NUMBER), CNumber.fromStr("5")));

        assertEquals(CNumber.fromStr("15"), rValue.getValue(scope));
    }

    @Test
    void useAssignedVariable(){
        Parser p = Util.parserFromStringStream("number a = 5; a * 3");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();
        assignment.execute(scope);
        RValue rValue = p.tryParseRValue();

        assertEquals(CNumber.fromStr("15"), rValue.getValue(scope));
    }

    @Test
    void printFunction(){
        Parser p = Util.parserFromStringStream("print(23);");
        FunctionCall funcall = (FunctionCall) p.tryParseAssignOrFunCall();

        final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));

        funcall.execute(Scope.empty());

        final String stdOutResult = myOut.toString();

        assertEquals("23\n", stdOutResult);
    }

    @Test
    void printFunctionMultipleArgs(){
        Parser p = Util.parserFromStringStream("print(23, 'a', 'b', 10 gbp);");
        FunctionCall funcall = (FunctionCall) p.tryParseAssignOrFunCall();

        final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));

        funcall.execute(Scope.empty());

        final String stdOutResult = myOut.toString();

        assertEquals("23ab10gbp\n", stdOutResult);
    }

    @Test
    void whileLoop(){
        Parser p = Util.parserFromStringStream("number x = 0; while (x < 3) { x = x + 1;}");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();
        assignment.execute(scope);

        Loop loop = p.tryParseLoop();
        loop.execute(scope);

        assertAll(
                () -> assertTimeoutPreemptively(Duration.ofMillis(2000), () -> loop.execute(scope),
                        "executing a 3-iteration while loop should definitely take less than 2 seconds"),
                () -> assertEquals(CNumber.fromStr("3"), scope.getVariable("x").getSavedValue())
        );

    }

    @Test
    void ifStatement() {
        Parser p = Util.parserFromStringStream("number x = 0; if (1 gbp) { x = x + 1;}");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();
        assignment.execute(scope);

        IfStatement ifStatement = p.tryParseIfStatement();
        ifStatement.execute(scope);

        assertEquals(CNumber.fromStr("1"), scope.getVariable("x").getSavedValue());
    }

    @Test
    void ifStatementFalseTruthValue() {
        Parser p = Util.parserFromStringStream("number x = 0; if (0 gbp) { x = x + 1;}");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();
        assignment.execute(scope);

        IfStatement ifStatement = p.tryParseIfStatement();
        ifStatement.execute(scope);

        assertEquals(CNumber.fromStr("0"), scope.getVariable("x").getSavedValue());
    }

    @Test
    void returnIsTheLastStatementExecutedInFunction(){
        Parser p = Util.parserFromStringStream(
                "number deep(){\n" +
                "    if (true){\n" +
                "        if (true){\n" +
                "            return 3;\n" +
                "        }\n" +
                "        print('cienko');\n" +
                "    }\n" +
                "    print('cienko2');\n" +
                "}" +
                "deep();"
                );


        Function function = p.tryParseFunction();
        FunctionCall funcall = (FunctionCall) p.tryParseAssignOrFunCall();

        final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));

        Scope scope = Scope.empty();
        scope.newFunction(function);

        CType returned = funcall.getValue(scope);

        final String stdOutResult = myOut.toString();
        assertAll(
                () -> assertEquals("", stdOutResult),
                () -> assertEquals(CNumber.fromStr("3"), returned)
        );
    }

    @Test
    void currencyCastFromNumberIsPossible(){
        Parser p = Util.parserFromStringStream("currency x = [gbp] 10;");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();

        assignment.execute(scope);

        assertEquals(new CCurrency("10", "gbp"), scope.getVariable("x").getSavedValue());
    }

    @Test
    void blocksAllowForLocalVariables(){
        Parser p = Util.parserFromStringStream(
                        "void main(){" +
                                "    number x = 0;" +
                                "    if (!x){" +
                                "        number x = 10;" +
                                "        print(x);" +
                                "    }" +
                                "    print(x);" +
                                "}"
        );
        Program program = p.parseProgram();

        ByteArrayOutputStream stream = Util.beginCaptureStdout();
        program.execute();
        String stdout = Util.getCaptured(stream);

        assertEquals("10\n0\n", stdout);
    }



    @Test
    void outerVariablesCanBeChangedFromInnerBlocks(){
        Parser p = Util.parserFromStringStream(
                "void main(){" +
                        "    number x = 0;" +
                        "    if (!x){" +
                        "        x = 10;" +
                        "        print(x);" +
                        "    }" +
                        "    print(x);" +
                        "}"
        );
        Program program = p.parseProgram();

        ByteArrayOutputStream stream = Util.beginCaptureStdout();
        program.execute();
        String stdout = Util.getCaptured(stream);

        assertEquals("10\n10\n", stdout);
    }

    @Test
    void recursiveFunctionsWork(){
        Parser p = Util.parserFromStringStream(
                "number factorial(number n){" +
                        "    if (n == 0){" +
                        "    return 1;" +
                        "     }" +
                        "     return n * factorial(n-1);" +
                        "}" +
                        "factorial(6);"
        );
        Function function = p.tryParseFunction();
        FunctionCall functionCall = (FunctionCall) p.tryParseAssignOrFunCall();

        Scope scope = Scope.empty();
        scope.newFunction(function);
        CType result = functionCall.getValue(scope);

        assertEquals(CNumber.fromStr("720"), result);
    }

    @Test
    void disallowRedefiningFunctionParameters(){
        Parser p = Util.parserFromStringStream(
                "void fun(bool param){\n" +
                        "    bool param = false;\n" +
                        "}\n" +
                        "fun(false);"
        );
        Function function = p.tryParseFunction();
        FunctionCall functionCall = (FunctionCall) p.tryParseAssignOrFunCall();

        Scope scope = Scope.empty();
        scope.newFunction(function);


        assertThrows(ExecutionException.class, () -> functionCall.execute(scope));

    }

    @Test
    void functionParametersArePassedByCopy(){
        Parser p = Util.parserFromStringStream(
                "void tryReassign(number n){" +
                        "    n = 29;" +
                        "}" +
                        "number n = 5;" +
                        "tryReassign(n);"
        );
        Function function = p.tryParseFunction();
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        FunctionCall functionCall = (FunctionCall) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();

        scope.newFunction(function);
        assignment.execute(scope);
        functionCall.execute(scope);

        assertEquals(CNumber.fromStr("5"), scope.getVariable("n").getSavedValue());
    }

    @Test
    void assignAnythingToTypeAny(){
        Parser p = Util.parserFromStringStream(
                "any a = 3.0;" +
                    "any b = 'str';");
        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();
        Assignment assignment2 = (Assignment) p.tryParseAssignOrFunCall();
        Scope scope = Scope.empty();

        assignment.execute(scope);
        assignment2.execute(scope);

        assertAll(
                () -> assertEquals(CNumber.fromStr("3.0"), scope.getVariable("a").getSavedValue()),
                () -> assertEquals(new CString("str"), scope.getVariable("b").getSavedValue())
        );
    }


    @Test
    void functionCanSpecifyReturnTypeAny(){
        Parser p = Util.parserFromStringStream(
                    "any fun1(){" +
                        "   return 3.0 gbp;" +
                        "}" +
                        "any fun2(){" +
                            "return 'str';" +
                        "}" +
                        "fun1();" +
                        "fun2();"
        );
        Function fun1 = p.tryParseFunction();
        Function fun2 = p.tryParseFunction();
        FunctionCall funcall1 = (FunctionCall) p.tryParseAssignOrFunCall();
        FunctionCall funcall2 = (FunctionCall) p.tryParseAssignOrFunCall();


        Scope scope = Scope.empty();

        scope.newFunction(fun1);
        scope.newFunction(fun2);

        assertEquals(new CCurrency("3.0", "gbp"), funcall1.getValue(scope));
        assertEquals(new CString("str"), funcall2.getValue(scope));
    }









}
