package currencies.structures.expressions;

import currencies.executor.Scope;
import currencies.types.CBoolean;
import currencies.types.CType;

import java.util.ArrayList;
import java.util.List;

public class BoolTerm extends RValue{
    private List<RValue> operands;

    public BoolTerm(List<RValue> operands) {
        this.operands = operands;
    }

    public List<RValue> getOperands() {
        return operands;
    }

//    public static BoolTerm fromRValue(RValue rvalue){
//        List<BoolFactor> operand = new ArrayList<>();
//        operand.add(new BoolFactor(null, rvalue));
//        return new BoolTerm(operand);
//    }


    @Override
    public CType getValue(Scope scope) {

        assert operands.size() > 1;
//
//        boolean value = operands.get(0).truthValue() && operands.get(1).truthValue();
//        // break early if a false value was found
//        for (int i = 2; i < operands.size() && value; i++) {
//            value = operands.get(i).truthValue();
//        }
//
//        return new CBoolean(value);

        return operands.stream()
                .reduce(new CBoolean(true), (currentVal, rValue) -> currentVal.and(rValue.truthValue(scope)), CBoolean::and);
    }

    @Override
    public String toString (){
        return RValue.exprToStr(operands, "&&");
    }
}
