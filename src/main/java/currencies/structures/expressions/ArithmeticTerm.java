package currencies.structures.expressions;

import currencies.ExecutionException;
import currencies.InterpreterException;
import currencies.executor.Scope;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
import currencies.parser.Parser;
import currencies.reader.CharPosition;
import currencies.types.*;

import java.util.List;

public class ArithmeticTerm extends RValue {

    private List<RValue> operands;
    private List<Token> operators;
    private Object value;

    public ArithmeticTerm(List<RValue> operands, List<Token> operators) {
        assert operands.size() > 0 && operators.size() == operands.size() - 1;

        this.operands = operands;
        this.operators = operators;
    }

    public CType getValue(Scope scope){
        return applyOperators(operands, operators, scope);
    }

    protected CType applyOperator (CType first, Token operator, CType second){

        try {
            if (operator.getType() == TokenType.T_MULT)
                return multiply(first, second);
            else
                return divide(first, second);
        } catch (ExecutionException e){
            e.setPosition(operator.getPosition());
            throw e;
        }

    }

    private static CType divide(CType first, CType second){
        return first.acceptDivide(second);
    }


    private static CType multiply(CType first, CType second) {
        return first.acceptMultiply(second);
    }


    @Override
    public String toString (){
        return RValue.exprToStr(operands, operators);
    }
}
