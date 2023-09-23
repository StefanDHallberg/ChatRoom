package Entities;

public class TextMessage implements Message {
    private String content;
    private int senderClientNr;
    public TextMessage(String content, int senderClientNr) {
        this.content = content;
        this.senderClientNr = senderClientNr;
    }
    public TextMessage(String content) {
        this(content, -1);  // random nr, setting -1 as default value to indicate that the client number is not properly set
    }

    public String getContent() {
        return content;
    }

    public int getSenderClientNr() {
        return senderClientNr;
    }

}