package currencies.structures.expressions;

import currencies.types.CCurrency;
import currencies.lexer.Token;
import currencies.lexer.TokenType;

import java.math.BigDecimal;
import java.util.List;

public abstract class RValue {

    protected static String unaryOpToStr(Token unaryOperator){
        String str = "";
        if (unaryOperator != null){
            if (unaryOperator.getType() == TokenType.T_MINUS ||
                unaryOperator.getType() == TokenType.T_EXCLAMATION) {
                str += unaryOperator.valueStr();
            }
            // currency cast
            else{
                str += "[" + unaryOperator.getValue().toString().toLowerCase() + "] ";
            }
        }
        return str;
    }

    protected static String exprToStr(List<? extends RValue> operands, List<Token> operators){
        assert operands.size() - 1 == operators.size();
        return exprToStr(operands, null, operators);
    }

    protected static String exprToStr(List<? extends RValue> operands, String operator) {
        return exprToStr(operands, operator, null);
    }

    private static String exprToStr(List<? extends RValue> operands, String operator, List<Token> operators){

        assert operator != null || operators != null;

        // if expr has more than one operand, display it inside parentheses
        if (operands.size() > 1) {
            StringBuilder str = new StringBuilder("(");
            str.append(operands.get(0).toString());

            for (int i = 1; i < operands.size(); ++i) {
                str.append(" ");
                if (operator != null)
                    str.append(operator);
                else
                    str.append(operators.get(i-1).valueStr());
                str.append(" ");
                str.append(operands.get(i));
            }

            str.append(")");
            return str.toString();
        }
        else{
            return operands.get(0).toString();
        }
    }

    public boolean truthValue(){
        Object value = getValue();

        if (value instanceof String)
            return ((String) value).isEmpty();
        if (value instanceof CCurrency)
            return ((CCurrency) value).getValue().compareTo(BigDecimal.ZERO) == 0;
        if (value instanceof BigDecimal)
            //TODO: change types
            return ((BigDecimal) value).compareTo(BigDecimal.ZERO) == 0;
        if (value instanceof Boolean)
            return (Boolean) value;

        throw new RuntimeException("Unkown type");

    }

    public Object getValue(){
        return new Object();
    }
    public TokenType getType() { return TokenType.T_UNKNOWN; }

    //public abstract Object getValue();


}
