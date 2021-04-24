package currencies.reader;

public class CharPosition {

    private long filePosition;
    private int line;
    private int charNumber;

    public CharPosition(long filePosition, int line, int charNumber) {
        this.filePosition = filePosition;
        this.line = line;
        this.charNumber = charNumber;
    }
    public long getFilePosition() {
        return filePosition;
    }

    public int getLine() {
        return line;
    }

    public int getCharNumber() {
        return charNumber;
    }

    @Override
    public String toString() {
        return "CharPosition{" +
                "filePosition=" + filePosition +
                ", line=" + line +
                ", charNumber=" + charNumber +
                '}';
    }
}
