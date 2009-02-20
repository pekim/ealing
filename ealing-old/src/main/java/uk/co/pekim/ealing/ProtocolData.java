package uk.co.pekim.ealing;

public class ProtocolData {
    private final char tag;
    private final int data;
    
    public ProtocolData(char tag, int data) {
        this.tag = tag;
        this.data = data;
    }

    public char getTag() {
        return tag;
    }

    public int getData() {
        return data;
    }

    @Override
    public String toString() {
        return Character.toString(tag) + data;
    }
}
