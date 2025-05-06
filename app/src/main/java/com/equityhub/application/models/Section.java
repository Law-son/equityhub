package com.equityhub.application.models;

import java.util.List;

public class Section {
    private String id;
    private String title;
    private String heading;
    private String image;
    private List<String> paragraphs;

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getHeading() { return heading; }
    public String getImage() { return image; }
    public List<String> getParagraphs() { return paragraphs; }
}
