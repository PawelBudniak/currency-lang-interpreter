package currencies.structures.expressions;

import currencies.execution.Scope;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
import currencies.types.CBoolean;
import currencies.types.CType;

public class Factor extends RValue {
    RValue expression;
    Token unaryOp;

    public Factor(Token unaryOp, RValue expression) {
        this.expression = expression;
        this.unaryOp = unaryOp;
    }

    public Token getUnaryOperator() {
        return unaryOp;
    }

    public RValue getExpression() {
        return expression;
    }


    public static RValue factorOrRValue(Token unaryOperator, RValue expression){
        if (unaryOperator == null)
            return expression;
        return new Factor(unaryOperator, expression);
    }

    @Override
    public CType getValue(Scope scope){
        if (unaryOp == null)
            return expression.getValue(scope);

        if (unaryOp.getType() == TokenType.T_MINUS){
            return expression.getValue(scope).negate();
        }
        if (unaryOp.getType() == TokenType.T_EXCLAMATION){
            return new CBoolean(!expression.truthValue(scope));
        }
        if (unaryOp.getType() == TokenType.T_CURRENCY_CODE){
            return expression.getValue(scope).currencyCast((String)unaryOp.getValue());
        }
        throw new RuntimeException("Unknown unary op");
    }

    @Override
    public String toString() {
        String str = "";

        str += RValue.unaryOpToStr(unaryOp);
        str += expression.toString();

        return str;
    }
}
