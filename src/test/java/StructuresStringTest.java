import currencies.lexer.Token;
import currencies.lexer.TokenType;
import currencies.structures.expressions.*;
import currencies.structures.simple_values.FunctionCall;
import currencies.structures.simple_values.Identifier;
import currencies.structures.simple_values.Literal;
import currencies.structures.simple_values.Variable;
import currencies.types.CBoolean;
import currencies.types.CCurrency;
import currencies.types.CNumber;
import currencies.types.CString;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.util.ArrayList;
import java.util.List;

public class StructuresStringTest {

    @BeforeAll
    static void loadCurrencies(){
        Util.loadCurrencies();
    }

    @Test
    void literal(){

        Literal strLiteral = new Literal(new CString("test"));
        Literal numLiteral = new Literal(CNumber.fromStr("10.2"));
        Literal curLiteral = new Literal(new CCurrency(CNumber.fromStr("5"), "pln"));
        Literal boolLiteral = new Literal(new CBoolean(true));

        assertAll(
                () -> assertEquals("'test'", strLiteral.toString()),
                () -> assertEquals("10.2", numLiteral.toString()),
                () -> assertEquals("5pln", curLiteral.toString()),
                () -> assertEquals("true",boolLiteral.toString())
        );

    }

    @Test
    void variable(){
        Variable var = new Variable(new Identifier("foo", null));

        assertEquals("foo", var.toString());
    }

    @Test
    void identifier(){
        Identifier id = new Identifier("foo", null);

        assertEquals("foo", id.toString());
    }

    @Test
    void funCall(){
        Variable var = new Variable(new Identifier("foo", null));
        Literal literal = new Literal(CNumber.fromStr("10"));
        Identifier funName = new Identifier("myFun", null);

        FunctionCall functionCall = new FunctionCall(funName, List.of(var, literal));

        assertEquals("myFun(foo, 10)", functionCall.toString());
    }

    @Test
    void funCallAsStatement(){
        Variable var = new Variable(new Identifier("foo", null));
        Literal literal = new Literal(CNumber.fromStr("10"));
        Identifier funName = new Identifier("myFun", null);

        FunctionCall functionCall = new FunctionCall(funName, List.of(var, literal));
        functionCall.setAsStatement(true);

        assertEquals("myFun(foo, 10);", functionCall.toString());
    }

    @Test
    void arithmeticExpression(){

        List<RValue> operands = new ArrayList<>();
        operands.add(new Literal(CNumber.fromStr("5")));
        operands.add(new Literal(CNumber.fromStr("7")));
        operands.add(new Literal(new CString("test")));
        List<Token> operators = new ArrayList<>();
        operators.add(new Token(TokenType.T_PLUS, "+", null));
        operators.add(new Token(TokenType.T_MINUS, "-", null));


        ArithmeticExpression ex = new ArithmeticExpression(operands, operators);
        assertEquals("(5 + 7 - 'test')", ex.toString());
    }

    @Test
    void arithmeticTerm(){

        List<RValue> operands = new ArrayList<>();
        operands.add(new Literal(CNumber.fromStr("5")));
        operands.add(new Literal(CNumber.fromStr("7")));
        operands.add(new Literal(new CString("test")));
        List<Token> operators = new ArrayList<>();
        operators.add(new Token(TokenType.T_MULT, "*", null));
        operators.add(new Token(TokenType.T_DIV, "/", null));


        ArithmeticTerm ex = new ArithmeticTerm(operands, operators);
        assertEquals("(5 * 7 / 'test')", ex.toString());
    }

    @Test
    void boolTerm(){

        List<RValue> operands = new ArrayList<>();
        operands.add(new Literal(CNumber.fromStr("5")));
        operands.add(new Literal(CNumber.fromStr("7")));
        operands.add(new Literal(new CString("test")));

        BoolTerm ex = new BoolTerm(operands);
        assertEquals("(5 && 7 && 'test')", ex.toString());
    }

    @Test
    void boolExpression(){

        List<RValue> operands = new ArrayList<>();
        operands.add(new Literal(CNumber.fromStr("5")));
        operands.add(new Literal(CNumber.fromStr("7")));
        operands.add(new Literal(new CString("test")));

        BoolExpression ex = new BoolExpression(operands);
        assertEquals("(5 || 7 || 'test')", ex.toString());
    }

    @Test
    void comparison(){

        RValue left = new Literal(CNumber.fromStr("5"));
        RValue right = new Literal(new CString("test"));
        Token operator = new Token(TokenType.T_GTE, ">=", null);

        Comparison comparison = new Comparison(left, operator, right);

        assertEquals("(5 >= 'test')", comparison.toString());
    }

    @Test
    void factorWithUnaryOp(){

        RValue val = new Literal(CNumber.fromStr("5"));
        Token operator = new Token(TokenType.T_MINUS, "-", null);

        Factor factor = new Factor(operator, val);

        assertEquals("-5", factor.toString());
    }

    @Test
    void factorWithExpr(){

        List<RValue> operands = new ArrayList<>();
        operands.add(new Literal(CNumber.fromStr("5")));
        operands.add(new Literal(CNumber.fromStr("7")));
        operands.add(new Literal(new CString("test")));
        List<Token> operators = new ArrayList<>();
        operators.add(new Token(TokenType.T_PLUS, "+", null));
        operators.add(new Token(TokenType.T_MINUS, "-", null));
        ArithmeticExpression ex = new ArithmeticExpression(operands, operators);

        Factor factor = new Factor(null, ex);

        assertEquals("(5 + 7 - 'test')", factor.toString());
    }

    @Test
    void factorWithExprAndUnaryOp(){

        List<RValue> operands = new ArrayList<>();
        operands.add(new Literal(CNumber.fromStr("5")));
        operands.add(new Literal(CNumber.fromStr("7")));
        operands.add(new Literal(new CString("test")));
        List<Token> operators = new ArrayList<>();
        operators.add(new Token(TokenType.T_PLUS, "+", null));
        operators.add(new Token(TokenType.T_MINUS, "-", null));
        ArithmeticExpression ex = new ArithmeticExpression(operands, operators);

        Token operator = new Token(TokenType.T_EXCLAMATION, "!", null);

        Factor factor = new Factor(operator, ex);

        assertEquals("!(5 + 7 - 'test')", factor.toString());
    }

    @Test
    void exprWithParenthesizedSubExpr(){

        List<RValue> subOperands = new ArrayList<>();
        subOperands.add(new Literal(CNumber.fromStr("5")));
        subOperands.add(new Literal(CNumber.fromStr("7")));
        Token subOperator = new Token(TokenType.T_PLUS, "+", null);

        ArithmeticExpression subExpr = new ArithmeticExpression(subOperands, List.of(subOperator));

        Literal outerLiteral = new Literal(CNumber.fromStr("10"));
        Token outerOperator = new Token(TokenType.T_MINUS, "-", null);

        ArithmeticExpression outerExpr = new ArithmeticExpression(List.of(outerLiteral, subExpr), List.of(outerOperator));

        assertEquals("(10 - (5 + 7))", outerExpr.toString());
    }



}
