package currencies.parser;

import currencies.NumberFactory;
import currencies.lexer.Lexer;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
import currencies.reader.CodeInputStream;
import currencies.structures.Function;
import currencies.structures.Program;
import currencies.structures.TypeAndId;
import currencies.structures.expressions.BoolExpression;
import currencies.structures.expressions.Comparison;
import currencies.structures.expressions.RValue;
import currencies.structures.simple_values.FunctionCall;
import currencies.structures.simple_values.Literal;
import currencies.structures.statements.*;
import org.junit.jupiter.api.Test;
import static currencies.lexer.TokenType.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import currencies.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    Parser parserFromStringStream(String s){
        InputStream stream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        return new Parser(new Lexer(new CodeInputStream(stream)));
    }

    @Test
    void noArgsFunction() {
        Parser p = parserFromStringStream("void fun() {}");
        p.nextToken();
        Function f = p.tryParseFunction();
        assertAll(
                () -> assertEquals(T_KW_VOID, f.getReturnType()),
                () -> assertEquals("fun", f.getName()),
                () -> assertTrue(f.getArgDefList().isEmpty()),
                () -> assertTrue(f.getBlock().getStatements().isEmpty())
        );

    }

    @Test
    void functionWithArgs() {
        Parser p = parserFromStringStream("string func(number a, currency b) {}");
        p.nextToken();
        Function f = p.tryParseFunction();
        List<TypeAndId> args = List.of(new TypeAndId(new Token(T_KW_NUMBER, null), "a"), new TypeAndId(new Token(T_KW_CURRENCY, null), "b"));

        assertAll(
                () -> assertNotNull(f),
                () -> assertEquals(T_KW_STRING, f.getReturnType()),
                () -> assertEquals("func", f.getName()),
                () -> assertEquals(f.getArgDefList(), args),
                () -> assertTrue(f.getBlock().getStatements().isEmpty())
        );

    }

    @Test
    void ifStatement(){
        Parser p = parserFromStringStream("if (a > 3) {}");
        p.nextToken();
        IfStatement statement = p.tryParseIfStatement();
        assertNotNull(statement);
        BoolExpression condition = statement.getCondition();
        RValue comparison = condition.getFirstOperand();

        assertTrue(comparison instanceof Comparison);

        String strRepr = comparison.toString();
        assertEquals("a > 3", strRepr);

    }

    @Test
    void loopStatement(){
        String strStatement = "while (a > 3) {\n" +
                "}";
        Parser p = parserFromStringStream(strStatement);
        p.nextToken();

        assertEquals(strStatement, p.tryParseLoop().toString());


    }



    @Test
    void boolExpression(){
        String expr = "a > 3 || 5 <= 4 && !(var > foo)";
        Parser p = parserFromStringStream(expr);
        p.nextToken();

        assertEquals(expr, p.tryParseBoolExpression().toString());
    }

    @Test
    void currencyLiteral(){
        Parser p = parserFromStringStream("10.42 pln");
        p.nextToken();

        Literal literal = p.tryParseLiteral();
        assertEquals(new Currency("10.42", Currency.Type.PLN), literal.getValue());

    }

    @Test
    void simpleArithmeticExpression(){
        String expr = "a + 10.42";
        Parser p = parserFromStringStream(expr);
        p.nextToken();

        assertEquals(expr, p.tryParseArithmeticExpression().toString());


    }

    @Test
    void complexArithmeticExpression(){
        String expr = "a + 10.42 * foo - (5 + 3)";
        Parser p = parserFromStringStream(expr);
        p.nextToken();

        assertEquals(expr, p.tryParseArithmeticExpression().toString());

    }

    @Test
    void declareAndInitialize(){
        String expr = "number num = 10.3;";
        Parser p = parserFromStringStream(expr);
        p.nextToken();

        assertEquals(expr, p.tryParseAssignOrFunCall().toString());
    }

    @Test
    void assignWithoutDeclaration(){
        String expr = "foo = 'test';";
        Parser p = parserFromStringStream(expr);
        p.nextToken();

        assertEquals(expr, p.tryParseAssignOrFunCall().toString());

    }

    @Test
    void assignBoolLiteral(){
        String expr = "foo = true;";
        Parser p = parserFromStringStream(expr);
        p.nextToken();

        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();



        assertEquals(expr, assignment.toString());
    }

    @Test
    void requireSemicolonAfterAssignment(){
        String expr = "foo = true";
        Parser p = parserFromStringStream(expr);
        p.nextToken();
        assertThrows(SyntaxException.class, () -> p.tryParseAssignOrFunCall());
    }

    @Test
    void exchangeDeclaration(){
        String statement = "exchange from pln to gbp 4.32;";
        Parser p = parserFromStringStream(statement);
        p.nextToken();

        ExchangeDeclaration decl = p.tryParseExchangeDeclaration();

        assertAll(
                () -> assertNotNull(decl),
                () -> assertEquals(Currency.Type.PLN, decl.getFrom()),
                () -> assertEquals(Currency.Type.GBP, decl.getTo()),
                () -> assertEquals("4.32", decl.getValue().toString()),
                () -> assertEquals(statement, decl.toString())
        );

    }
    @Test
    void parseSimpleProgram(){
        String programStr = "number main() {\n" +
                "a = 3;\n" +
                "return a;\n" +
                "}";
        Parser p = parserFromStringStream(programStr);

        Program program = p.parseProgram();

        assertAll(
                () -> assertNotNull(program),
                () -> assertEquals(2, program.getFunctions().get(0).getBlock().getStatements().size()),
                () -> assertEquals(programStr, program.toString())
        );

    }

    @Test
    void standaloneFunCall(){
        String statement = "print('test');";
        Parser p = parserFromStringStream(statement);
        p.nextToken();

        FunctionCall funcall = (FunctionCall) p.tryParseAssignOrFunCall();

        assertEquals(statement, funcall.toString());

    }

    @Test
    void funCallAsRValue(){
        String statement = "number b = fun(3, 5);";
        Parser p = parserFromStringStream(statement);
        p.nextToken();

        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();

        assertEquals(statement, assignment.toString());

    }

    @Test
    void currencyCast(){
        String str = "[pln] money";
        Parser p = parserFromStringStream(str);
        p.nextToken();

        RValue value = p.tryParseRValue();

        assertEquals(str, value.toString());
    }

    @Test
    void returnStatement(){
        String str = "return 3;";
        Parser p = parserFromStringStream(str);
        p.nextToken();

        ReturnStatement value = p.tryParseReturnStatement();

        assertEquals(str, value.toString());

    }

    @Test
    void RValuesAsFunctionCallParameters(){
        String funCall = "fun(fun2(x), 3 / 5, 12.34gbp);";
        Parser p = parserFromStringStream(funCall);
        p.nextToken();

        FunctionCall value = (FunctionCall)p.tryParseAssignOrFunCall();

        assertEquals(funCall, value.toString());

    }




}