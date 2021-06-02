package currencies.types;

import currencies.structures.expressions.RValue;

import java.util.Objects;

public class CBoolean extends CType<CBoolean> implements Comparable<CBoolean> {

    private Boolean bool;

    public CBoolean(Boolean bool) {
        this.bool = bool;
    }

    public CBoolean or(CBoolean other){
        return new CBoolean(bool || other.getValue());
    }

    public CBoolean or(boolean other){
        return new CBoolean(bool || other);
    }

    public CBoolean and(CBoolean other){
        return new CBoolean(bool && other.getValue());
    }

    public CBoolean and(boolean other){
        return new CBoolean(bool && other);
    }

    @Override
    public Boolean getValue() {
        return bool;
    }

    @Override
    public boolean truthValue() {
        return bool;
    }

    @Override
    public int compareTo(CBoolean cBoolean) {
        return bool.compareTo(cBoolean.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CBoolean cBoolean = (CBoolean) o;
        return Objects.equals(bool, cBoolean.bool);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bool);
    }
}
