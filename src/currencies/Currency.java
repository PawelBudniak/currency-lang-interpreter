package currencies;

import java.util.Objects;

public class Currency {
    public static final int CODE_LEN = 3;

    private Number value;
    private Type type;

    public Currency(String strvalue, Type type) {
        this.value = valueOf(strvalue);
        this.type = type;
    }

    public Currency(Number value, Type type) {
        this.value = value;
        this.type = type;
    }

    public static Number valueOf(String strvalue){
        return NumberFactory.get(strvalue);
    }


    public Number getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    public String getTypeStr(){
        return type.toString().toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(value, currency.value) &&
                type == currency.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "value=" + value +
                ", type=" + type +
                '}';
    }

    public enum Type{
        GBP,
        EUR,
        PLN,
        CHP,
        USD
    }
}
