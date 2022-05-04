package com.example.backpackapp.serverutils;

import androidx.annotation.NonNull;

import com.example.backpackapp.enteties.Author;
import com.example.backpackapp.enteties.Backpack;
import com.example.backpackapp.enteties.Book;
import com.example.backpackapp.enteties.Status;
import com.example.backpackapp.enteties.Subject;
import com.example.backpackapp.enteties.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonConv {

    @NonNull
    public static List<Author> getAuthors(String body){
        List<Author> authors = new ArrayList<>();

        try {
            JSONObject jsonBody = new JSONObject(body);
            JSONArray jsonArray = jsonBody.getJSONArray("authors");
            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Author author = new Author(
                        jsonObject.getInt("id"),
                        jsonObject.getString("full_name")
                );
                authors.add(author);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return authors;
    }

    @NonNull
    public static List<Subject> getSubjects(String body){
        List<Subject> subjects = new ArrayList<>();

        try {
            JSONObject jsonBody = new JSONObject(body);
            JSONArray jsonArray = jsonBody.getJSONArray("subjects");
            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Subject subject = new Subject(
                        jsonObject.getInt("id"),
                        jsonObject.getString("sub_full_name")
                );
                subjects.add(subject);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return subjects;
    }

    public static User getUser(String body){
        User user = null;
        try {
            JSONObject jsonBody = new JSONObject(body);
            JSONObject jsonObject = jsonBody.getJSONObject("user");
            user = new User(
                    jsonObject.getInt("id"),
                    jsonObject.getString("name"),
                    jsonObject.getString("surname"),
                    jsonObject.getString("telephone"),
                    jsonObject.getString("login"),
                    jsonObject.getString("password"),
                    jsonObject.getString("role")
            );
        }catch (JSONException e){
            e.printStackTrace();
        }
        return user;
    }

    public static Status getStatus(String body){
        Status status = null;
        try {
            JSONObject jsonBody = new JSONObject(body);
            status = new Status(
                    jsonBody.getBoolean("status"),
                    jsonBody.getString("message")
            );
        }catch (JSONException e){
            e.printStackTrace();
        }
        return status;
    }

    @NonNull
    public static List<Backpack> getMyBackpack(String body){
        List<Backpack> backpackList = new ArrayList<>();
        try {
            JSONObject jsonBody = new JSONObject(body);
            JSONArray jsonArray = jsonBody.getJSONArray("books_back");
            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Backpack backpack = new Backpack(
                        jsonObject.getInt("id"),
                        jsonObject.getInt("user_id"),
                        jsonObject.getInt("book_id")
                );
                backpackList.add(backpack);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return backpackList;
    }

    @NonNull
    public static List<Book> getBooks(String body){
        List<Book> books = new ArrayList<>();
        try {
            JSONObject jsonBody = new JSONObject(body);
            JSONArray jsonArray = jsonBody.getJSONArray("books");
            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Book book = new Book(
                        jsonObject.getInt("id"),
                        jsonObject.getString("name"),
                        jsonObject.getString("file"),
                        jsonObject.getString("full_name"),
                        jsonObject.getString("sub_full_name"),
                        jsonObject.getInt("class")
                );
                books.add(book);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return books;
    }

    public static int getCountBooks(String body){
        int count = 0;
        try {
            JSONObject jsonObject = new JSONObject(body);
            count = jsonObject.getInt("count");
        } catch (JSONException e){
            e.printStackTrace();
        }
        return count;
    }

}
