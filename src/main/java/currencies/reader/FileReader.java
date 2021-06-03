package currencies.reader;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileReader implements CodeInput {


    private RandomAccessFile file;
    private boolean isEOF;
    private int lineNumber = firstLineNumber;
    private int charNumber = firstColumnNumber;


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
                charNumber = firstColumnNumber - 1;
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

    public void printSurrounding(long filePos){
        try {
            long lineStart = findLineStart(filePos);
            file.seek(lineStart);


            long diff = filePos - lineStart;

            String line = file.readLine();
            String stripped = line.stripLeading();
            long lenStripped = line.length() - stripped.length();
            diff -= lenStripped;

            System.out.println(stripped);
            System.out.println(" ".repeat((int) diff-1) + "^");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long findLineStart(long filePos) throws IOException {
        char c;
         do {
            file.seek(--filePos);
            c = (char) file.readByte();
        } while (c != '\n' && filePos > 0);
        return filePos + 1;
    }

    @Override
    public boolean hasNext() {
       return !isEOF;
    }
}
