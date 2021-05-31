package currencies.structures.expressions;

import java.util.List;

public class BoolExpression extends RValue{
    private List<RValue> operands;
    private boolean value;

    public BoolExpression(List<RValue> operands) {
        this.operands = operands;
    }

    public List<RValue> getOperands() {
        return operands;
    }

    @Override
    public String toString (){

        return exprToStr(operands, "||");

//        StringBuilder str = new StringBuilder(operands.get(0).toString());
//
//        for (int i = 1; i < operands.size(); ++i){
//            str.append(" || ");
//            str.append(operands.get(i));
//        }
//        return str.toString();
    }

    public RValue getFirstOperand(){
        return operands.get(0);
    }
}
