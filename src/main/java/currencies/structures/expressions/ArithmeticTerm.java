package currencies.structures.expressions;

import currencies.ExecutionException;
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
        this.operands = operands;
        this.operators = operators;
    }

    public CType getValue(Scope scope){
        return applyOperators(operands, operators, scope);
    }

//    public CType getValue(){
//        if (operands.size() == 1) {
//            assert false: "one operand term shouldn't be possible";
//            return operands.get(0).getValue();
//        }
//
//        CType value = calculate(operands.get(0).getValue(), operators.get(0), operands.get(1).getValue());
//        // break early if a true value was found
//        for (int i = 2; i < operands.size(); i++) {
//            value = calculate(value, operators.get(i-1), operands.get(i).getValue());
//        }
//
//        return value;
//    }



//    private static CType calculate(CType first, Token operator, CType second){
//        if (first instanceof CBoolean || first instanceof CString || second instanceof CBoolean || second instanceof CString)
//            throw new ExecutionException("Cannot apply multiplicative operator to types: " + first.getClass() + " and " + second.getClass(), operator.getPosition());
//
//        // both currencies
//        if (first instanceof CCurrency && second instanceof CCurrency){
//            return applyOperator((CCurrency)first, operator, (CCurrency)second);
//        }
//
//        if (first instanceof CCurrency && second instanceof CNumber){
//            return applyOperator((CCurrency) first, operator, (CNumber) second);
//        }
//
//        if (first instanceof CNumber && second instanceof CCurrency){
//            return applyOperator((CNumber) first, operator, (CCurrency) second);
//        }
//
//        if (first instanceof CNumber && second instanceof CNumber){
//            return applyOperator((CNumber) first, operator, (CNumber) second);
//        }
//
//        throw new ExecutionException("Unkown types", operator.getPosition());
//   }
//
//    private static CType applyOperator(CNumber first, Token operator, CNumber second){
//        if (operator.getType() == TokenType.T_MULT)
//            return first.multiply(second);
//        else
//            return first.divide(second);
//    }
//
//
//    private static CType applyOperator(CCurrency first, Token operator, CNumber second){
//        if (operator.getType() == TokenType.T_MULT)
//            return first.multiply(second);
//        else
//            return first.divide(second);
//    }
//
//    private static CType applyOperator(CNumber first, Token operator, CCurrency second){
//        if (operator.getType() == TokenType.T_MULT)
//            return second.multiply(first);
//        else
//            return first.divide(second);
//    }
//
//    private static CType applyOperator(CCurrency first, Token operator, CCurrency second){
//        if (!first.codesEqual(second))
//            throw new ExecutionException("Cannot apply multiplicative operator to currencies of different types", operator.getPosition());
//
//        if (operator.getType() == TokenType.T_MULT)
//            throw new ExecutionException("Cannot multiply two currency values", operator.getPosition());
//
//        return first.divide(second);
//    }

    protected CType applyOperator (CType first, Token operator, CType second){

        if (operator.getType() == TokenType.T_MULT)
            return multiply(first, second, operator.getPosition());
        else
            return divide(first, second, operator.getPosition());

    }


    private static CType divide(CType first, CType second, CharPosition position){

        return first.acceptDivide(second);
//        if (first instanceof CCurrency && second instanceof CCurrency){
//            if (!((CCurrency)first).codesEqual((CCurrency)second))
//                throw new ExecutionException("Cannot apply multiplicative operator to currencies of different types", position);
//
//            return ((CCurrency)first).divide((CCurrency)second);
//        }
//
//        if (first instanceof CCurrency && second instanceof CNumber){
//            return ((CCurrency)first).divide((CNumber)second);
//        }
//
//        if (first instanceof CNumber && second instanceof CNumber){
//            return ((CNumber)first).divide((CNumber)second);
//        }
//        throw new ExecutionException("Cannot apply division operator to types: " + first.getClass() + " and " + second.getClass(), position);
    }


    private static CType multiply(CType first, CType second, CharPosition position){

        if (first instanceof CCurrency && second instanceof CCurrency){
            throw new ExecutionException("Cannot apply multiplicative operator to currencies of different types", position);
        }

        if (first instanceof CCurrency && second instanceof CNumber){
            return ((CCurrency)first).multiply((CNumber)second);
        }

        if (first instanceof CNumber && second instanceof CCurrency){
            return ((CCurrency)second).multiply((CNumber) first);
        }

        if (first instanceof CNumber && second instanceof CNumber){
            return ((CNumber)first).multiply((CNumber)second);
        }
        throw new ExecutionException("Cannot apply multiplication operator to types: " + first.getClass() + " and " + second.getClass(), position);
    }

//    private static CType applyOperator(CType first, Token operator, CType second){
//        throw new ExecutionException("Cannot apply multiplicative operator to types: " + first.getClass() + " and " + second.getClass(), operator.getPosition());
//    }




    @Override
    public String toString (){
        return RValue.exprToStr(operands, operators);
    }
}
