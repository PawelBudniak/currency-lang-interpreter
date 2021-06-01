package currencies.structures.expressions;

import currencies.lexer.Token;

import java.util.List;

public class ArithmeticTerm extends RValue {

    private List<RValue> operands;
    private List<Token> operators;
    private Object value;

    public ArithmeticTerm(List<RValue> operands, List<Token> operators) {
        this.operands = operands;
        this.operators = operators;
    }

    @Override
    public String toString (){
        return RValue.exprToStr(operands, operators);
    }
}