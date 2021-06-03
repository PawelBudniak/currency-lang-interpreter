import currencies.lexer.Lexer;
import currencies.parser.Parser;
import currencies.reader.CodeInputStream;
import currencies.types.CCurrency;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

class Util {

    static void loadCurrencies(){
        CCurrency.loadExchangeRates("data/exchangeRates.json");
    }

    static Parser parserFromStringStream(String s){
        return new Parser(lexerFromStringStream(s));
    }

    static Lexer lexerFromStringStream(String s){
        InputStream stream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        return new Lexer(new CodeInputStream(stream));
    }

    static ByteArrayOutputStream beginCaptureStdout(){
        final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));
        return myOut;
    }

    static String getCaptured(ByteArrayOutputStream out){
        return out.toString();
    }
}
