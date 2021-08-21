package currencies;

import currencies.error.ErrorHandler;
import currencies.error.InterpreterException;
import currencies.lexer.Lexer;
import currencies.parser.Parser;
import currencies.reader.FileReader;
import currencies.structures.Program;
import currencies.types.CCurrency;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Main {

    public static void main(String[] args) {

        if (args.length < 1){
            System.err.println("Error: Please specify the source file as the first argument");
            System.err.println("Usage: mvn exec:java <source-code-path> [exchange-rates-file-path]");
        }

        String exchangeRates = "data/exchangeRates.json";
        if (args.length == 2)
            exchangeRates = args[1];

        CCurrency.loadExchangeRates(exchangeRates);

        ErrorHandler errorHandler = new ErrorHandler();


        try (RandomAccessFile fp = new RandomAccessFile(args[0], "rw")){

             FileReader reader = new FileReader(fp);
             Parser p = new Parser(new Lexer(reader));
             errorHandler.setReader(reader);

             try {
                 Program program = p.parseProgram();
                 program.execute();
             }catch (InterpreterException e){
                 errorHandler.handle(e);
             }

        } catch (FileNotFoundException e) {
            System.err.println("This file does not exist");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
