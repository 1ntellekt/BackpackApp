package com.example.backpackapp.enteties;

public class Book {
    private int id;
    private String name;
    private String filename;
    private String authorName;
    private String subName;
    private int classNum;

    public Book(int id, String name, String filename, String authorName, String subName, int classNum) {
        this.id = id;
        this.name = name;
        this.filename = filename;
        this.authorName = authorName;
        this.subName = subName;
        this.classNum = classNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public int getClassNum() {
        return classNum;
    }

    public void setClassNum(int classNum) {
        this.classNum = classNum;
    }
}
