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

        CCurrency.loadExchangeRates("data/exchangeRates.json");

        ErrorHandler errorHandler = new ErrorHandler();


        try (RandomAccessFile fp = new RandomAccessFile("data/program", "rw")){

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
