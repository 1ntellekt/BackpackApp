package com.example.backpackapp.enteties;

public class Backpack {
    private int id;
    private int user_id;
    private int book_id;

    public Backpack(int id, int user_id, int book_id) {
        this.id = id;
        this.user_id = user_id;
        this.book_id = book_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }
}
