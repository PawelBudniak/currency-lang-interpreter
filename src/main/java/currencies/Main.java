package currencies;

import currencies.types.CCurrency;

public class Main {

    public static void main(String[] args) {

        CCurrency.loadExchangeRates("data/exchangeRates.json");


//        try (RandomAccessFile fp = new RandomAccessFile("data/input", "rw")){
//
//            Lexer lexer = new Lexer(new FileReader(fp));
//            Token token = lexer.getNextToken();
//            while (token.getType() != TokenType.T_EOT) {
//                System.out.println(token);
//                token = lexer.getNextToken();
//            }
//            String s = new StringBuilder().append('\\').append('t').toString();
//
//        } catch (FileNotFoundException e) {
//            System.err.println("This file does not exist");
//            System.exit(1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
   }
}
