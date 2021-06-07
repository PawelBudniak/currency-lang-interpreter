package currencies.error;

import currencies.error.InterpreterException;
import currencies.reader.CharPosition;
import currencies.reader.FileReader;

public class ErrorHandler {

    private FileReader reader;

    public void handle(InterpreterException e){
        CharPosition position = e.getPosition();
        System.out.println("=".repeat(70));

        if (e.getPosition() != null) {
            System.out.println("Error at line: " + position.getLine() + ", char: " + position.getCharNumber());
            System.out.println(e.getMessage());
            System.out.println("<code>");
            reader.printSurrounding(position.getFilePosition());
            System.out.println("</code>");
        }
        else{
            System.out.println(e);
        }

        System.out.println("=".repeat(70));

    }

    public FileReader getReader() {
        return reader;
    }

    public void setReader(FileReader reader) {
        this.reader = reader;
    }
}
