package currencies.structures.expressions;

import currencies.ExecutionException;
import currencies.executor.Scope;
import currencies.executor.Utils;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
import currencies.types.CBoolean;
import currencies.types.CType;

import java.util.Set;

public class Comparison extends RValue {
    private RValue rightOperand;
    private Token operator;
    private RValue leftOperand;

    public Comparison(RValue leftOperand, Token operator, RValue rightOperand) {
        this.rightOperand = rightOperand;
        this.operator = operator;
        this.leftOperand = leftOperand;
    }

    private TokenType operatorType() { return operator.getType(); }

    @Override
    public CBoolean getValue(Scope scope) {
        CType leftValue = leftOperand.getValue(scope);
        CType rightValue = rightOperand.getValue(scope);

        Utils.requireSameTypes(leftValue, rightValue,"compare", operator.getPosition());
        Utils.requireSameCurrencyTypes(leftValue,rightValue,"compare", operator.getPosition());

        int result = leftValue.compareTo(rightValue);


        switch (operatorType()){
            case T_GT:
                return new CBoolean(result > 0);
            case T_LT:
                return new CBoolean(result < 0);
            case T_GTE:
                return new CBoolean(result >=  0);
            case T_LTE:
                return new CBoolean(result <= 0);
            case T_EQUALS:
                return new CBoolean(result == 0);
            case T_NOTEQUALS:
                return new CBoolean(result != 0);

        }
        throw new ExecutionException("Incorrect comparison operator", operator.getPosition());
    }

    @Override
    public String toString() {
        return "(" + leftOperand + " " + operator.getValue() + " " + rightOperand + ")";
    }
}
