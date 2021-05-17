package currencies.structures.expressions;

import currencies.lexer.Token;

import java.util.List;

public class ArithmeticExpression extends RValue {
    private List<ArithmeticTerm> operands;
    private List<Token> operators;
    private Object value;

    public ArithmeticExpression(List<ArithmeticTerm> operands, List<Token> operators) {
        this.operands = operands;
        this.operators = operators;
    }

    static String exprToStr(List<? extends RValue> operands, List<Token> operators){
        StringBuilder str = new StringBuilder(operands.get(0).toString());

        for (int i = 1; i < operands.size(); ++i){
            str.append(" ").append(operators.get(i - 1).valueStr()).append(" ");
            str.append(operands.get(i));
        }
        return str.toString();
    }


    @Override
    public String toString () {
        return exprToStr(operands, operators);
    }

}
