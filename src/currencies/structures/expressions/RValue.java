package currencies.structures.expressions;

import currencies.lexer.Token;
import currencies.lexer.TokenType;

import java.io.ObjectInputStream;
import java.util.Optional;

public abstract class RValue {

    protected static String unaryOpToStr(Token unaryOperator){
        String str = "";
        if (unaryOperator != null){
            if (unaryOperator.getType() == TokenType.T_MINUS ||
                    unaryOperator.getType() == TokenType.T_EXCLAMATION) {
                str += unaryOperator.getValue();
            }
            // currency cast
            else{
                str += "[" + unaryOperator.getType().toString().toLowerCase() + "]";
            }
        }
        return str;
    }

    public Object getValue(){
        return new Object();
    }

    //public abstract Object getValue();


}
