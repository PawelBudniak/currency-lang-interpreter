package currencies;

public class Currency {
    public static final int CODE_LEN = 3;

    private Number value;
    private Type type;

    public Currency(String strvalue, Type type) {
        this.value = valueOf(strvalue);
        this.type = type;
    }

    public static Number valueOf(String strvalue){
        return NumberFactory.create(strvalue);
    }


    public enum Type{
        GBP,
        EUR,
        PLN,
        CHP,
        USD
    }
}
