package currencies.reader;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileReader implements CodeInput {

    public static final int EOF_CODE = 4;

    private RandomAccessFile file;
    private boolean isEOF;

    public FileReader(String path, String mode) throws FileNotFoundException {
        file = new RandomAccessFile(path, mode);
    }


    @Override
    public long getPosition() {
        try {
            return file.getFilePointer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public char nextChar() {
        try {
            return file.readChar();
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
