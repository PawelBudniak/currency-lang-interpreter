import currencies.lexer.Token;
import currencies.parser.Parser;
import currencies.parser.SyntaxException;
import currencies.structures.Function;
import currencies.structures.Program;
import currencies.structures.TypeAndId;
import currencies.structures.expressions.*;
import currencies.structures.simple_values.FunctionCall;
import currencies.structures.simple_values.Identifier;
import currencies.structures.simple_values.Literal;
import currencies.structures.statements.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static currencies.lexer.TokenType.*;

import currencies.types.CCurrency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {

    @BeforeAll
    static void loadCurrencies(){
        Util.loadCurrencies();
    }

    @Test
    void noArgsFunction() {
        Parser p = Util.parserFromStringStream("void fun() {}");
        Function f = p.tryParseFunction();
        assertAll(
                () -> assertEquals(T_KW_VOID, f.getReturnType()),
                () -> assertEquals("fun", f.getId()),
                () -> assertTrue(f.getArgDefList().isEmpty()),
                () -> assertTrue(f.getBlock().getStatements().isEmpty())
        );

    }

    @Test
    void functionWithArgs() {
        Parser p = Util.parserFromStringStream("string func(number a, currency b) {}");
        Function f = p.tryParseFunction();
        List<TypeAndId> args = List.of
                (new TypeAndId(new Token(T_KW_NUMBER, null), new Identifier("a", null)),
                 new TypeAndId(new Token(T_KW_CURRENCY, null),  new Identifier("b", null)));

        assertAll(
                () -> assertNotNull(f),
                () -> assertEquals(T_KW_STRING, f.getReturnType()),
                () -> assertEquals("func", f.getId()),
                () -> assertEquals(args, f.getArgDefList()),
                () -> assertTrue(f.getBlock().getStatements().isEmpty())
        );

    }


    @Test
    void ifStatement(){
        String strStatement = "if (a > 3) { }";
        String parenthesizedCondition = "(a > 3)";
        Parser p = Util.parserFromStringStream(strStatement);

        IfStatement statement = p.tryParseIfStatement();


        assertAll(
                () -> assertNotNull(statement),
                () -> assertEquals(parenthesizedCondition, statement.getCondition().toString()),
                () -> assertTrue(statement.getBlock().getStatements().isEmpty())
        );

    }

    @Test
    void loopStatement(){
        String strStatement = "while (a > 3) { }";
        String parenthesizedCondition = "(a > 3)";
        Parser p = Util.parserFromStringStream(strStatement);

        Loop loop = p.tryParseLoop();


        assertAll(
                () -> assertNotNull(loop),
                () -> assertEquals(parenthesizedCondition, loop.getCondition().toString()),
                () -> assertTrue(loop.getBlock().getStatements().isEmpty())
        );

    }

    @Test
    void boolExpression(){
        String expr = "a > 3 || 5 <= 4 && !(var > foo)";
        String parenthesized = "((a > 3) || ((5 <= 4) && !(var > foo)))";
        Parser p = Util.parserFromStringStream(expr);

        assertEquals(parenthesized, p.tryParseBoolExpression().toString());
    }

    @Test
    void boolExpressionWithArithmetic(){
        String expr = "a + b > 3 || 5 * 2 <= 4 && !(var > foo)";
        String parenthesized = "(((a + b) > 3) || (((5 * 2) <= 4) && !(var > foo)))";

        Parser p = Util.parserFromStringStream(expr);

        assertEquals(parenthesized, p.tryParseBoolExpression().toString());
    }

    @Test
    void boolExpressionWithParenthesesInArithmetic(){
        String expr = "(a + b) * 4 > 3 || 5 * 2 <= 4 && !(var > foo)";
        String parenthesized = "((((a + b) * 4) > 3) || (((5 * 2) <= 4) && !(var > foo)))";
        Parser p = Util.parserFromStringStream(expr);

        assertEquals(parenthesized, p.tryParseBoolExpression().toString());
    }

    @Test
    void currencyLiteral(){
        Parser p = Util.parserFromStringStream("10.42 pln");

        Literal literal = p.tryParseLiteral();
        assertEquals(new CCurrency("10.42", "pln"), literal.getValue());

    }

    @Test
    void simpleArithmeticExpression(){
        String expr = "a + 10.42";
        String parenthesized = "(a + 10.42)";
        Parser p = Util.parserFromStringStream(expr);

        assertEquals(parenthesized, p.tryParseArithmeticExpression().toString());


    }

    @Test
    void operatorPriorityNoParentheses(){
        String expr = "a + 10.42 * x";
        String parenthesized = "(a + (10.42 * x))";
        Parser p = Util.parserFromStringStream(expr);

        assertEquals(parenthesized, p.tryParseArithmeticExpression().toString());


    }

    @Test
    void operatorPriorityWithParentheses(){
        String expr = "(a + 10.42) * x";
        String parenthesized = "((a + 10.42) * x)";
        Parser p = Util.parserFromStringStream(expr);

        assertEquals(parenthesized, p.tryParseArithmeticExpression().toString());


    }


    @Test
    void complexArithmeticExpression(){
        String expr = "a + 10.42 * foo - (5 + 3)";
        String parenthesized = "(a + (10.42 * foo) - (5 + 3))";
        Parser p = Util.parserFromStringStream(expr);

        assertEquals(parenthesized, p.tryParseArithmeticExpression().toString());

    }

    @Test
    void declareAndInitialize(){
        String expr = "number num = 10.3;";
        Parser p = Util.parserFromStringStream(expr);

        assertEquals(expr, p.tryParseAssignOrFunCall().toString());
    }

    @Test
    void assignWithoutDeclaration(){
        String expr = "foo = 'test';";
        Parser p = Util.parserFromStringStream(expr);

        assertEquals(expr, p.tryParseAssignOrFunCall().toString());

    }

    @Test
    void assignBoolLiteral(){
        String expr = "foo = true;";
        Parser p = Util.parserFromStringStream(expr);

        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();



        assertEquals(expr, assignment.toString());
    }

    @Test
    void requireSemicolonAfterAssignment(){
        String expr = "foo = true";
        Parser p = Util.parserFromStringStream(expr);
        assertThrows(SyntaxException.class, () -> p.tryParseAssignOrFunCall());
    }

    @Test
    void exchangeDeclaration(){
        String statement = "exchange from pln to gbp 4.32;";
        Parser p = Util.parserFromStringStream(statement);

        ExchangeDeclaration decl = p.tryParseExchangeDeclaration();

        assertAll(
                () -> assertNotNull(decl),
                () -> assertEquals("pln", decl.getFrom()),
                () -> assertEquals("gbp", decl.getTo()),
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
        Parser p = Util.parserFromStringStream(programStr);

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
        Parser p = Util.parserFromStringStream(statement);

        FunctionCall funcall = (FunctionCall) p.tryParseAssignOrFunCall();

        assertEquals(statement, funcall.toString());

    }

    @Test
    void funCallAsRValue(){
        String statement = "number b = fun(3, 5);";
        Parser p = Util.parserFromStringStream(statement);

        Assignment assignment = (Assignment) p.tryParseAssignOrFunCall();

        assertEquals(statement, assignment.toString());

    }

    @Test
    void currencyCast(){
        String str = "[pln] money";
        Parser p = Util.parserFromStringStream(str);

        RValue value = p.tryParseRValue();

        assertEquals(str, value.toString());
    }

    @Test
    void returnStatement(){
        String str = "return 3;";
        Parser p = Util.parserFromStringStream(str);

        ReturnStatement value = p.tryParseReturnStatement();

        assertEquals(str, value.toString());

    }

    @Test
    void RValuesAsFunctionCallParameters(){
        String funCall = "fun(fun2(x), 3 / 5, 12.34gbp);";
        String parenthesized = "fun(fun2(x), (3 / 5), 12.34gbp);";
        Parser p = Util.parserFromStringStream(funCall);

        FunctionCall value = (FunctionCall)p.tryParseAssignOrFunCall();

        assertEquals(parenthesized, value.toString());

    }

    @Test
    void boolExprAsRValue(){
        String str = "bool a = (x == 3);";
        Parser p = Util.parserFromStringStream(str);

        Assignment value = (Assignment)p.tryParseAssignOrFunCall();

        assertEquals(str, value.toString());

    }

    @Test
    void ComplexBoolExprAsRValue(){
        String str = "bool a = x == 3 || b > 2 && a + fun(6) > 2;";
        String parenthesized = "bool a = ((x == 3) || ((b > 2) && ((a + fun(6)) > 2)));";
        Parser p = Util.parserFromStringStream(str);

        Assignment assignment = (Assignment)p.tryParseAssignOrFunCall();

        assertAll(
                () -> assertEquals(parenthesized, assignment.toString()),
                () -> assertTrue(assignment.getValue() instanceof BoolExpression),
                () -> assertTrue( ((BoolExpression)(assignment.getValue())).getOperands().size() == 2)
        );

    }

    @Test
    void BoolExprWithParentheses(){
        String str = "abc = (3 + 2) * 3 >= 10;";
        String parenthesized = "abc = (((3 + 2) * 3) >= 10);";
        Parser p = Util.parserFromStringStream(str);

        Assignment assignment = (Assignment)p.tryParseAssignOrFunCall();

        assertAll(
                () -> assertEquals(parenthesized, assignment.toString()),
                () -> assertTrue(assignment.getValue() instanceof Comparison)
        );

    }

    @Test
    void BoolExprWithParentheses2(){
        String str = "abc = (a || b) && c || d;";
        String parenthesized = "abc = (((a || b) && c) || d);";
        Parser p = Util.parserFromStringStream(str);

        Assignment assignment = (Assignment)p.tryParseAssignOrFunCall();


        assertEquals(parenthesized, assignment.toString());

    }

    @Test
    void avoidExpressionNestingWhenOnlyOneOperand(){
        String str = "5";
        Parser p = Util.parserFromStringStream(str);

        RValue val = p.tryParseRValue();

        assertTrue(val instanceof Literal);
    }

    @Test
    void avoidExpressionNestingWhenOnlyOneOperand2(){
        String str = "5 * 3";
        Parser p = Util.parserFromStringStream(str);

        RValue val = p.tryParseRValue();

        assertTrue(val instanceof ArithmeticTerm);
    }

    @Test
    void boolUnaryOp(){
        String str = "!(a || b && c)";
        Parser p = Util.parserFromStringStream(str);

        RValue val = p.tryParseRValue();

        assertAll(
                () -> assertTrue(val instanceof Factor),
                () -> assertTrue(((Factor)val).getUnaryOperator().getType() == T_EXCLAMATION),
                () -> assertTrue(((Factor)val).getExpression() instanceof BoolExpression)
        );
    }






}