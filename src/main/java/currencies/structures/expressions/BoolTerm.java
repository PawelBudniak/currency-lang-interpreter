package currencies.structures.expressions;

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


//    @Override
//    public Object getValue() {
//        if (operands.size() == 1) {
//            assert false: "one operand boolterm shouldn't be possible";
//            return operands.get(0).getValue();
//        }
//        return operands.stream()
//                .reduce(true, (first, second) -> Boolean.valueOf(first.getValue()) && second.getValue());
//
//
//    }

    @Override
    public String toString (){
        return RValue.exprToStr(operands, "&&");
    }
}
