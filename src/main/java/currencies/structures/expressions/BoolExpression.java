package currencies.structures.expressions;

import currencies.types.CBoolean;
import currencies.types.CType;

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

    @Override
    public CType getValue() {
        if (operands.size() == 1) {
            assert false: "one operand boolexpr shouldn't be possible";
            return operands.get(0).getValue();
        }

//        boolean value = operands.get(0).truthValue() || operands.get(1).truthValue();
//        // break early if a true value was found
//        for (int i = 2; i < operands.size() && !value; i++) {
//            value = operands.get(i).truthValue() || value;
//        }
//
//        return new CBoolean(value);

        return operands.stream()
                .reduce(new CBoolean(false), (currentVal, rValue) -> currentVal.or(rValue.truthValue()), CBoolean::or);

    }

}
