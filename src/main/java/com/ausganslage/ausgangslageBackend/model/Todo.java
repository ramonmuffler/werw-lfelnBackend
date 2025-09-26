package com.ausganslage.ausgangslageBackend.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private boolean completed;

    // Many todos belong to one person
    @ManyToOne
    @JoinColumn(name = "person_id")
    @JsonBackReference
    private Person person;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }
}

