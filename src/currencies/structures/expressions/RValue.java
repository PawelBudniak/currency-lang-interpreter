package currencies.structures.expressions;

import currencies.lexer.Token;
import currencies.lexer.TokenType;

import java.io.ObjectInputStream;
import java.util.List;
import java.util.Optional;

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

    static String exprToStr(List<? extends RValue> operands, List<Token> operators){

        assert operands.size() - 1 == operators.size();

        if (operands.size() > 1) {
            StringBuilder str = new StringBuilder("(");
            str.append(operands.get(0).toString());

            for (int i = 1; i < operands.size(); ++i) {
                str.append(" ").append(operators.get(i - 1).valueStr()).append(" ");
                str.append(operands.get(i));
            }

            str.append(")");
            return str.toString();
        }
        else{
            return operands.get(0).toString();
        }
    }

    public static String exprToStr(List<? extends RValue> operands, String operator){


        // if expr has more than one operand, display it inside parentheses
        if (operands.size() > 1) {
            StringBuilder str = new StringBuilder("(");
            str.append(operands.get(0).toString());

            for (int i = 1; i < operands.size(); ++i) {
                str.append(" ").append(operator).append(" ");
                str.append(operands.get(i));
            }

            str.append(")");
            return str.toString();
        }
        else{
            return operands.get(0).toString();
        }
    }


    public Object getValue(){
        return new Object();
    }

    //public abstract Object getValue();


}
