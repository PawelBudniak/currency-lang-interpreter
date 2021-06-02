package currencies.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class CCurrency extends CType<CCurrency> implements Comparable<CCurrency>{
    public static final int CODE_LEN = 3;

    private CNumber value;
    private String code;

    public CCurrency(String strvalue, String code) {
        this.value = CNumber.fromStr(strvalue);
        this.code = code;
    }

    public CCurrency(CNumber value, String code) {
        this.value = value;
        this.code = code;
    }


    @Override
    public CNumber getValue() {
        return value;
    }

    /** False if 0 (any precision) */
    @Override
    public boolean truthValue(){
        return value.truthValue();
    }

    public String getCode() {
        return code;
    }

    public String getTypeStr(){
        return code.toString().toLowerCase();
    }

    public CNumber divide(CCurrency other){
        return value.divide(other.getValue());
    }

    public CCurrency add(CCurrency other){
        return new CCurrency(value.add(other.getValue()), code);
    }

    public CCurrency subtract(CCurrency other){
        return new CCurrency(value.subtract(other.getValue()), code);
    }

    public CCurrency multiply(CNumber other){
        return new CCurrency(value.multiply(other), code);
    }

    public CCurrency divide(CNumber other){
        return new CCurrency(value.divide(other), code);
    }

    public boolean codesEqual(CCurrency other){
        return code.equals(other.getCode());
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


    private static Map<String, Map<String, CNumber>> exchangeRates;

    public static void loadExchangeRates(String file){

        exchangeRates = new HashMap<>();
        Gson gson = new Gson();

        try (Reader reader = new FileReader(file)) {

            Map <String, Map<String, String>> input = new HashMap<>();

            // Convert JSON File to Java Object
            input = gson.fromJson(reader, input.getClass());

            // transform exchange rate values into objects provided by NumberFactory
            for (String currencyFrom: input.keySet()){

                Map<String, CNumber> nestedRates = input.get(currencyFrom).entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> e.getKey(),
                                e -> CNumber.fromStr(e.getValue())
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