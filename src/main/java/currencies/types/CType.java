package currencies.types;


import currencies.ExecutionException;
import currencies.reader.CharPosition;

import java.util.Map;
import java.util.Objects;
import static java.util.Map.entry;

public abstract class CType <T extends CType<T>> implements Comparable<T>{

    public abstract boolean truthValue();
    public abstract Object getValue();

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    public static Class<?> typeOf(String s){
        return typeMap.get(s);
    }

    private static final Map<String, Class<?>> typeMap = Map.ofEntries(
            entry("string", CString.class),
            entry("number", CNumber.class),
            entry("bool", CBoolean.class),
            entry("currency", CCurrency.class)
    );

    public CType negate(){
        throw new ExecutionException("Cannot negate type: " + this.getClass(), null);
    }

    public CCurrency currencyCast(String targetCode){
        throw new ExecutionException("Cannot cast to currency type: " + this.getClass(), null);
    }

    public CType add(CType other, CharPosition position){
        return defaultAdd(this, other, position);
//
//        if (this instanceof CString || other instanceof CString)
//            return new CString(this.toString() + other.toString());
//
//        throw new ExecutionException("Cannot apply addition operator to types: " + this.getClass() + " and " + other.getClass(), position);
    }

    public String debugStr(){
        return this.toString();
    }


    public static CType assign (CType value, Class<?> requiredType){
        if (value.getClass() != requiredType)
            throw new ExecutionException("Can't assign " + value.getClass() + " to " + requiredType, null);
        return value;
    }

    public CType acceptSubtract(CType other){
        throw new ExecutionException("Cannot apply subtraction operator to types: " + this.getClass() + " and " + other.getClass(), null);
    }

    protected CType visitSubtract(CNumber other){
        throw new ExecutionException("Cannot apply subtraction operator to types: " + this.getClass() + " and " + other.getClass(), null);
    }

    protected CType visitSubtract(CCurrency other){
        throw new ExecutionException("Cannot apply subtraction operator to types: " + this.getClass() + " and " + other.getClass(), null);
    }

//    public CType subtract(CType other){
//        throw new ExecutionException("Cannot apply subtraction operator to types: " + this.getClass() + " and " + other.getClass(), null);
//    }
//
//    public CType subtract(CNumber other) {
//        throw new ExecutionException("Cannot apply subtraction operator to types: " + this.getClass() + " and " + other.getClass(), null);}
//
//    public CType subtract(CCurrency other) {
//        throw new ExecutionException("Cannot apply subtraction operator to types: " + this.getClass() + " and " + other.getClass(), null);
//    }

    public CType add(CCurrency other, CharPosition position){
        throw new ExecutionException("Cannot apply addition operator to types: " + this.getClass() + " and " + other.getClass(), position);
    }

    public CType add(CNumber other, CharPosition position){
        return defaultAdd(other, this, position);
        //throw new ExecutionException("Cannot apply addition operator to types: " + this.getClass() + " and " + other.getClass(), position);
    }

    private static CType defaultAdd(CType first, CType second, CharPosition position){
        if (first instanceof CString || second instanceof CString)
            return new CString(first.toString() + second.toString());

        throw new ExecutionException("Cannot apply addition operator to types: " + first.getClass() + " and " + second.getClass(), position);
    }

    private static CType defaultDivide(CType first, CType second){
        throw new ExecutionException("Cannot apply division operator to types: " + first.getClass() + " and " + second.getClass(), null);
    }

    public CType acceptDivide(CType other){
        return defaultDivide(other, this);
    }

    protected CType visitDivide(CNumber other){
        return defaultDivide(other, this);
    }

    protected CType visitDivide(CCurrency other){
        return defaultDivide(other, this);
    }


}
