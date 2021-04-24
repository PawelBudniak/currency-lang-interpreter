package currencies.reader;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileReader implements CodeInput {

    public static final int EOF_CODE = -1;

    private RandomAccessFile file;
    private boolean isEOF;
    private int lineNumber = 0;
    private int charNumber = -1;


    public FileReader(RandomAccessFile f){
        file = f;
    }


    @Override
    public CharPosition getPosition() {
        try {
            return new CharPosition(file.getFilePointer(), lineNumber, charNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public char nextChar() {
        try {
            char c = (char)file.readByte();
            if (c == '\n'){
                ++lineNumber;
                charNumber = -1;
            }
            if (c != '\r' && c!= '\n'){
                ++charNumber;
            }
            return c;
        }
        catch (EOFException e){
            isEOF = true;
            return (char)EOF_CODE;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
       return !isEOF;
    }
}
