package currencies.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import currencies.NumberFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class CCurrency implements Comparable<CCurrency>, CType{
    public static final int CODE_LEN = 3;

    private BigDecimal value;
    private String code;

    public CCurrency(String strvalue, String code) {
        this.value = NumberFactory.get(strvalue);
        this.code = code;
    }

    public CCurrency(BigDecimal value, String code) {
        this.value = value;
        this.code = code;
    }


    @Override
    public BigDecimal getValue() {
        return value;
    }

    /** False if 0 (any precision) */
    @Override
    public boolean truthValue(){
        return value.compareTo(BigDecimal.ZERO) != 0;
    }

    public String getCode() {
        return code;
    }

    public String getTypeStr(){
        return code.toString().toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CCurrency currency = (CCurrency) o;
        return Objects.equals(value, currency.value) &&
                Objects.equals(code, currency.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, code);
    }

    @Override
    public String toString() {
        return value.toString() + code;
    }


    private static Map<String, Map<String, ? extends  Number>> exchangeRates;

    public static void loadExchangeRates(String file){

        exchangeRates = new HashMap<>();
        Gson gson = new Gson();

        try (Reader reader = new FileReader(file)) {

            Map <String, Map<String, String>> input = new HashMap<>();

            // Convert JSON File to Java Object
            input = gson.fromJson(reader, input.getClass());

            // transform exchange rate values into objects provided by NumberFactory
            for (String currencyFrom: input.keySet()){

                Map<String, ? extends  Number> nestedRates = input.get(currencyFrom).entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> e.getKey(),
                                e -> NumberFactory.get(e.getValue())
                        ));

                exchangeRates.put(currencyFrom, nestedRates);

            }
            // Stream only version
//            exchangeRates = input.entrySet()
//                    .stream()
//                    .collect(Collectors.toMap(
//                            e -> e.getKey(),
//                            e -> e.getValue().entrySet().stream()
//                                .collect(Collectors.toMap(
//                                        nestedEntry -> nestedEntry.getKey(),
//                                        nestedEntry -> NumberFactory.get(nestedEntry.getValue())
//                                ))
//                    ));

            gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(exchangeRates);
            System.out.println(json);



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Set<String> allCodes (){
        return exchangeRates.keySet();
    }

    @Override
    public int compareTo(CCurrency other) {

        if (!this.getCode().equals(other.getCode()))
            throw new RuntimeException("Cannot compare currencies of different types, a cast is required: e.g: [pln] curr_variable");

        return getValue().compareTo(other.getValue());
    }
}
