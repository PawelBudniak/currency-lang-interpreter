package currencies;

import currencies.lexer.Lexer;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
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



        try (RandomAccessFile fp = new RandomAccessFile("data/program", "rw")){

            Parser p = new Parser(new Lexer(new FileReader(fp)));

            Program program = p.parseProgram();
            program.execute();
//            Token token = lexer.getNextToken();
//            while (token.getType() != TokenType.T_EOT) {
//                System.out.println(token);
//                token = lexer.getNextToken();
//            }
//            String s = new StringBuilder().append('\\').append('t').toString();

        } catch (FileNotFoundException e) {
            System.err.println("This file does not exist");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

   }
}
