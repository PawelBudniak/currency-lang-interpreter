package currencies.structures.expressions;

import java.util.List;

public class BoolExpression extends RValue{
    private List<BoolTerm> operands;
    private boolean value;

    public BoolExpression(List<BoolTerm> operands) {
        this.operands = operands;
    }

    public List<BoolTerm> getOperands() {
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
        return operands.get(0).getOperands().get(0).getExpression();
    }
}
