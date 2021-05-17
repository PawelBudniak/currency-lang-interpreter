package currencies;

import java.math.BigDecimal;

/** Produces precise non IEEE-754 numbers */
public class NumberFactory {
    public static Number get(String strvalue){
        return new BigDecimal(strvalue);
    }
}
