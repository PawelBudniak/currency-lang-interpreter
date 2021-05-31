package currencies.structures.expressions;

import currencies.lexer.Token;
import currencies.lexer.TokenType;


public class BoolFactor extends RValue{

    Token unaryOperator;
    RValue expression;

    public BoolFactor(Token unaryOperator, RValue expression) {
        this.unaryOperator = unaryOperator;
        this.expression = expression;
    }

    public Token getUnaryOperator() {
        return unaryOperator;
    }

    public RValue getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        String str = "";

        str += RValue.unaryOpToStr(unaryOperator);

        if (expression instanceof BoolExpression){
            str += "(";
            str += expression.toString();
            str += ")";
        }
        else{
            str += expression.toString();
        }

        return str;
    }
}
