package currencies.structures.expressions;

import java.util.ArrayList;
import java.util.List;

public class BoolTerm extends RValue{
    private List<BoolFactor> operands;

    public BoolTerm(List<BoolFactor> operands) {
        this.operands = operands;
    }

    public List<BoolFactor> getOperands() {
        return operands;
    }

    public static BoolTerm fromRValue(RValue rvalue){
        List<BoolFactor> operand = new ArrayList<>();
        operand.add(new BoolFactor(null, rvalue));
        return new BoolTerm(operand);
    }


    @Override
    public String toString (){

        return RValue.exprToStr(operands, "&&");
//        StringBuilder str = new StringBuilder(operands.get(0).toString());
//
//        for (int i = 1; i < operands.size(); ++i){
//            str.append(" && ");
//            str.append(operands.get(i));
//        }
//        return str.toString();
    }
}
