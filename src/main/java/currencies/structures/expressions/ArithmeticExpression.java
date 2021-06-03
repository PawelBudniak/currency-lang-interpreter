package currencies.structures.expressions;

import currencies.ExecutionException;
import currencies.executor.Scope;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
import currencies.reader.CharPosition;
import currencies.types.CCurrency;
import currencies.types.CNumber;
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
        this.operands = operands;
        this.operators = operators;
    }

    @Override
    public CType getValue(Scope scope) {
        return applyOperators(operands, operators, scope);
    }

    protected CType applyOperator (CType first, Token operator, CType second){

        if (operator.getType() == TokenType.T_PLUS)
            return add(first, second, operator.getPosition());
        else
            return subtract(first, second, operator.getPosition());

    }


    private static CType subtract(CType first, CType second, CharPosition position){

        if (first instanceof CCurrency && second instanceof CCurrency){
            if (!((CCurrency)first).codesEqual((CCurrency)second))
                throw new ExecutionException("Cannot apply subtraction operator to currencies of different types", position);

            return ((CCurrency)first).subtract((CCurrency)second);
        }

        if (first instanceof CNumber && second instanceof CNumber){
            return ((CNumber)first).subtract((CNumber)second);
        }
        throw new ExecutionException("Cannot apply subtraction operator to types: " + first.getClass() + " and " + second.getClass(), position);
    }


    private static CType add(CType first, CType second, CharPosition position){
        return first.add(second, position);
    }


    @Override
    public String toString () {
        return exprToStr(operands, operators);
    }

}
