package id.ac.binus.taskmanagerapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {
    private String id;
    private String title;
    private String description;
    private String deadline;

    // Constructor
    public Task(String id, String title, String description, String deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
    }
    public Date getDeadlineDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            return sdf.parse(deadline);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}