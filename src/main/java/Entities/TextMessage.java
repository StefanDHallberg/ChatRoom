package Entities;

public class TextMessage implements Message {
    private String content;

    public TextMessage(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }
}