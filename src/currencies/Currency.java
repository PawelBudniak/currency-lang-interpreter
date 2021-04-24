package currencies;

import java.math.BigDecimal;

public class Currency {
    public static final int CODE_LEN = 3;

    private BigDecimal value;
    private Type type;

    public Currency(BigDecimal value, Type type) {
        this.value = value;
        this.type = type;
    }


    public enum Type{
        GBP,
        EUR,
        PLN,
        CHP,
        USD
    }
}
