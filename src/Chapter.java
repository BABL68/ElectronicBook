import java.io.Serializable;

public class Chapter implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String content;

    public Chapter(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @Override
    public String toString() {
        return "Глава: " + title + "\n" + content;
    }
}
