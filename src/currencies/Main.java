package currencies;

import currencies.lexer.Lexer;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
import currencies.reader.CharPosition;
import currencies.reader.FileReader;

import java.io.*;
import java.nio.charset.Charset;

public class Main {

    public static void main(String[] args) {


        try (RandomAccessFile fp = new RandomAccessFile("data/input", "rw")){

            Lexer lexer = new Lexer(new FileReader(fp));
            Token token = lexer.getNextToken();
            while (token.getType() != TokenType.T_EOT) {
                System.out.println(token);
                token = lexer.getNextToken();
            }
            String s = new StringBuilder().append('\\').append('t').toString();
            System.out.println(new Token(TokenType.T_STR_LITERAL, s,  new CharPosition(10,10,10)));

        } catch (FileNotFoundException e) {
            System.err.println("This file does not exist");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
