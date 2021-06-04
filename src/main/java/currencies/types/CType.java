package currencies.types;


import currencies.ExecutionException;

import java.util.Map;
import java.util.Objects;
import static java.util.Map.entry;

public abstract class CType <T extends CType<T>> implements Comparable<T>{

    private static final Map<String, Class<?>> typeMap = Map.ofEntries(
            entry("string", CString.class),
            entry("number", CNumber.class),
            entry("bool", CBoolean.class),
            entry("currency", CCurrency.class)
    );


    public abstract boolean truthValue();
    public abstract Object getValue();

    public String debugStr(){
        return this.toString();
    }

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


    public static CType assign (CType value, Class<?> requiredType){
        if (value.getClass() != requiredType)
            throw new ExecutionException("Can't assign " + value.getClass() + " to " + requiredType, null);
        return value;
    }

    public CType negate(){
        throw new ExecutionException("Cannot negate type: " + this.getClass(), null);
    }


    private static CType defaultAdd(CType first, CType second){
        if (first instanceof CString || second instanceof CString)
            return new CString(first.toString() + second.toString());

        throw new ExecutionException("Cannot apply addition operator to types: " + first.getClass() + " and " + second.getClass(), null);
    }

    private static CType defaultDivide(CType first, CType second){
        throw new ExecutionException("Cannot apply division operator to types: " + first.getClass() + " and " + second.getClass(), null);
    }

    public static CType defaultSubtract(CType first, CType second){
        throw new ExecutionException("Cannot apply subtraction operator to types: " + first.getClass() + " and " + second.getClass(), null);
    }

    public static CType defaultMultiply(CType first, CType second){
        throw new ExecutionException("Cannot apply multiplication operator to types: " + first.getClass() + " and " + second.getClass(), null);
    }

    public CCurrency currencyCast(String targetCode){
        throw new ExecutionException("Cannot cast to currency type: " + this.getClass(), null);
    }



    public CType acceptMultiply(CType other){
        return defaultMultiply(this, other);
    }

    protected CType visitMultiply(CNumber other){
        return defaultMultiply(other, this);
    }

    protected CType visitMultiply(CCurrency other){
        return defaultMultiply(other, this);
    }

    public CType acceptSubtract(CType other){
        return defaultSubtract(this, other);
    }

    protected CType visitSubtract(CNumber other){ return defaultSubtract(other, this); }

    protected CType visitSubtract(CCurrency other){ return defaultSubtract(other, this); }

    public CType acceptAdd(CType other){
        return defaultAdd(this, other);
    }

    protected CType visitAdd(CCurrency other){ return defaultAdd(other, this); }

    protected CType visitAdd(CNumber other){
        return defaultAdd(other, this);
    }

    public CType acceptDivide(CType other){
        return defaultDivide(this, other);
    }

    protected CType visitDivide(CNumber other){
        return defaultDivide(other, this);
    }

    protected CType visitDivide(CCurrency other){
        return defaultDivide(other, this);
    }


}
