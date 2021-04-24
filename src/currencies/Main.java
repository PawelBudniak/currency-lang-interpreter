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


//        FileReader fp = null;
//        try {
//            fp = new FileReader("data/input", "r");
//        } catch (FileNotFoundException e) {
//            System.err.println("This file does not exist");
//            System.exit(1);
//        }
//        Lexer lexer = new Lexer(fp);
//        Token token = lexer.getNextToken();
//        while (token.getType() != TokenType.T_EOT){
//            System.out.println(token);
//        }

        try (RandomAccessFile fp = new RandomAccessFile("data/input", "rw")){
        //try (InputStream in = new FileInputStream("data/input")){
            //Reader reader = new InputStreamReader(in, "utf-8");
            //fp.writeChar('C');
            //fp.seek(0);
            //System.out.println(""+ (char)reader.read());
            //System.out.println((char)fp.readByte());
            //System.exit(1);

            //RandomAccessFile fp = null;

            Lexer lexer = new Lexer(new FileReader(fp));
            Token token = lexer.getNextToken();
            while (token.getType() != TokenType.T_EOT) {
                System.out.println(token);
                token = lexer.getNextToken();
            }

        } catch (FileNotFoundException e) {
            System.err.println("This file does not exist");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
