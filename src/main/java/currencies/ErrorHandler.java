package currencies;

import currencies.reader.CharPosition;
import currencies.reader.FileReader;

public class ErrorHandler {

    private FileReader reader;

    public void handle(InterpreterException e){
        CharPosition position = e.getPosition();
        System.out.println("=".repeat(70));
        System.out.println("Error at line: " + position.getLine() + ", char: " + position.getCharNumber());
        System.out.println(e.getMessage());
        System.out.println("<code>");
        reader.printSurrounding(position.getFilePosition());
        System.out.println("</code>");
        System.out.println("=".repeat(70));

    }

    public FileReader getReader() {
        return reader;
    }

    public void setReader(FileReader reader) {
        this.reader = reader;
    }
}
