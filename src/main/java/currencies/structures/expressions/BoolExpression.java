package currencies.structures.expressions;

import currencies.executor.Scope;
import currencies.types.CBoolean;
import currencies.types.CType;

import java.util.List;

public class BoolExpression extends RValue{
    private List<RValue> operands;
    private boolean value;

    public BoolExpression(List<RValue> operands) {
        assert operands.size() > 0;
        this.operands = operands;
    }

    public List<RValue> getOperands() {
        return operands;
    }

    @Override
    public String toString (){
        return exprToStr(operands, "||");
    }

    @Override
    public CType getValue(Scope scope) {
        return operands.stream()
                .reduce(new CBoolean(false), (currentVal, rValue) -> currentVal.or(rValue.truthValue(scope)), CBoolean::or);

    }

}
