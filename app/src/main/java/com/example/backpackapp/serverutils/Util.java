package com.example.backpackapp.serverutils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.backpackapp.enteties.Subject;
import com.example.backpackapp.enteties.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Util {
    private static User currentUser;
    private static int countBooks;

    public static final int DATA_VERSION = 1;
    public static final String DATABASE_NAME = "booksDB";
    public static final String TABLE_NAME = "books";

    public static String KEY_ID = "id";
    public static String KEY_FILE = "file";
    public static  String KEY_NAME = "name";
    public static  String KEY_NAME_AUTHOR = "full_name";
    public static String KEY_SUB_NAME = "sub_full_name";
    public static String KEY_CLASS = "class";

    public static int getCountBooks() {
        return countBooks;
    }

    public static void setCountBooks(int countBooks) {
        Util.countBooks = countBooks;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        Util.currentUser = currentUser;
    }

    @Nullable
    public static File getFile(@NonNull InputStream inputStream, String filename){
          File file = new File(filename);
            try {
                OutputStream outputStream = new FileOutputStream(file);
                byte [] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer))>0){
                    outputStream.write(buffer,0,len);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                return file;
            } catch (IOException e)
            {
                e.printStackTrace();
            }
          return null;
    }


}
