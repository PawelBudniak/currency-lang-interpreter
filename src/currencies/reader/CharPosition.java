package currencies.reader;

public class CharPosition {

    private long filePosition;
    private int line;
    private int charAtLine;

    public CharPosition(long filePosition, int line, int charAtLine) {
        this.filePosition = filePosition;
        this.line = line;
        this.charAtLine = charAtLine;
    }
    public long getFilePosition() {
        return filePosition;
    }

    public int getLine() {
        return line;
    }

    public int getCharAtLine() {
        return charAtLine;
    }
}
