package currencies.structures.expressions;

import currencies.types.CCurrency;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
import currencies.types.CType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

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
        return getValue().truthValue();
    }

    public CType getValue(){
        return new CType() {
            @Override
            public int compareTo(Object o) {
                return 0;
            }

            @Override
            public boolean truthValue() {
                return false;
            }

            @Override
            public Object getValue() {
                return null;
            }
        };
    }

    //public abstract Object getValue();


}
