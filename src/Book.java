import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private String author;
    private String releaseDate;
    private List<Page> pages;

    public Book(String title, String description, String author, String releaseDate) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.releaseDate = releaseDate;
        this.pages = new ArrayList<>();
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAuthor() { return author; }
    public String getReleaseDate() { return releaseDate; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setAuthor(String author) { this.author = author; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public List<Page> getPages() {
        if (pages == null) pages = new ArrayList<>();
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "Книга{" +
                "Назва='" + title + '\'' +
                ", опис='" + description + '\'' +
                ", автор='" + author + '\'' +
                ", дата='" + releaseDate + '\'' +
                '}';
    }
}