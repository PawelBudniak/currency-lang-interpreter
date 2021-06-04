package currencies.structures.expressions;

import currencies.InterpreterException;
import currencies.executor.Scope;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
import currencies.reader.CharPosition;
import currencies.types.CType;

import java.util.List;

//List<CType> operands;
//'a', 3, true, 'a', true,
//'a' + 3 +  true
//

public class ArithmeticExpression extends RValue {
    private List<RValue> operands;
    private List<Token> operators;
    private Object value;

    public ArithmeticExpression(List<RValue> operands, List<Token> operators) {
        assert operands.size() > 0 && operators.size() == operands.size() - 1;

        this.operands = operands;
        this.operators = operators;
    }

    @Override
    public CType getValue(Scope scope) {
        return applyOperators(operands, operators, scope);
    }

    protected CType applyOperator (CType first, Token operator, CType second){

        try {
            if (operator.getType() == TokenType.T_PLUS)
                return add(first, second);
            else
                return subtract(first, second);
        } catch (InterpreterException e){
            e.setPosition(operator.getPosition());
            throw e;
        }

    }


    private static CType subtract(CType first, CType second){
        return first.acceptSubtract(second);
    }


    private static CType add(CType first, CType second){
        return first.acceptAdd(second);
    }


    @Override
    public String toString () {
        return exprToStr(operands, operators);
    }

}
