package engine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Completed {


    private int id;
    @Id
    private String completedAt;
    @JsonIgnore
    private String author;

    public Completed() {}

    public Completed(int id, String completedAt, String author) {
        this.id = id;
        this.completedAt = completedAt;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public String getAuthor() {
        return author;
    }

}
