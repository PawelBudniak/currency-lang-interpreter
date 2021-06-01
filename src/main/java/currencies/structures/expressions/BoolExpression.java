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
    }

    public RValue getFirstOperand(){
        return operands.get(0);
    }
}
