package currencies;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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


    private static Map<String, Map<String, ? extends  Number>> exchangeRates;

    public static void loadExchangeRates(String file){

        exchangeRates = new HashMap<>();
        Gson gson = new Gson();

        try (Reader reader = new FileReader(file)) {

            Map <String, Map<String, String>> input = new HashMap<>();

            // Convert JSON File to Java Object
            input = gson.fromJson(reader, input.getClass());

            System.out.println(input.keySet());
            System.out.println(input.get("PLN").get("GBP").getClass());

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

    public static Set<String> getTypes (){
        return exchangeRates.keySet();
    }

    public enum Type{
        GBP,
        EUR,
        PLN,
        CHP,
        USD
    }
}
