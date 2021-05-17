package currencies.structures.expressions;

import currencies.lexer.Token;

public class ArithmeticFactor extends RValue {
    RValue expression;
    Token unaryOp;

    public ArithmeticFactor(Token unaryOp, RValue expression) {
        this.expression = expression;
        this.unaryOp = unaryOp;
    }

    @Override
    public String toString() {
        String str = "";

        str += RValue.unaryOpToStr(unaryOp);

        if (expression instanceof ArithmeticExpression){
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
