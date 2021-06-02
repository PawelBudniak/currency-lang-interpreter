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

        if (this instanceof CString || other instanceof CString)
            return new CString(this.toString() + other.toString());

        throw new ExecutionException("Cannot apply addition operator to types: " + this.getClass() + " and " + other.getClass(), position);
    }

    public String debugStr(){
        return this.toString();
    }
}
