package currencies.executor;

import currencies.InterpreterException;
import currencies.types.CCurrency;
import currencies.ExecutionException;
import currencies.reader.CharPosition;
import currencies.structures.expressions.RValue;

public class Utils {
//    public static void requireSameTypes(RValue first, RValue second, String action, CharPosition position){
//        if (first.getValue() != second.getType())
//            throw new ExecutionException("Cannot " + action + "values of different types", position);
//    }


    public static void requireSameTypes(Object first, Object second, String action, CharPosition position){
        if (first.getClass() != second.getClass())
            throw new ExecutionException("Cannot " + action + " values of different types", position);
    }

    public static void requireSameCurrencyTypes(Object first, Object second, String action, CharPosition position){
        if (first instanceof CCurrency && second instanceof CCurrency){
            String firstCode = ((CCurrency)first).getCode();
            String secondCode = ((CCurrency)second).getCode();
            if (!firstCode.equals(secondCode)){
                throw new ExecutionException("Cannot " + action + " currencies of different types, a cast is required: e.g: [pln] curr_variable. \n" +
                        "Provided types: " + firstCode + ", " + secondCode, position);
            }
        }
    }
}
