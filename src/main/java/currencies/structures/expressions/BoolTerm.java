package currencies.structures.expressions;

import currencies.executor.Scope;
import currencies.types.CBoolean;
import currencies.types.CType;

import java.util.ArrayList;
import java.util.List;

public class BoolTerm extends RValue{
    private List<RValue> operands;

    public BoolTerm(List<RValue> operands) {
        assert operands.size() > 0;
        this.operands = operands;
    }

    public List<RValue> getOperands() {
        return operands;
    }

    @Override
    public CType getValue(Scope scope) {

        return operands.stream()
                .reduce(new CBoolean(true), (currentVal, rValue) -> currentVal.and(rValue.truthValue(scope)), CBoolean::and);
    }

    @Override
    public String toString (){
        return RValue.exprToStr(operands, "&&");
    }
}
