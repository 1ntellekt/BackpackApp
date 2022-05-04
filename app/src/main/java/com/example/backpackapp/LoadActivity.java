package com.example.backpackapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.backpackapp.enteties.User;
import com.example.backpackapp.serverutils.Util;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("curr_user", MODE_PRIVATE);
                int id = sharedPreferences.getInt("id", 0);
                if (id==0){
                    startActivity(new Intent(LoadActivity.this, SignActivity.class));
                } else {
                    User user = new User(
                            id,
                            sharedPreferences.getString("name",""),
                            sharedPreferences.getString("surname",""),
                            sharedPreferences.getString("telephone",""),
                            sharedPreferences.getString("login",""),
                            sharedPreferences.getString("password",""),
                            sharedPreferences.getString("role","")
                    );
                    Util.setCurrentUser(user);
                    startActivity(new Intent(LoadActivity.this, MainActivity.class));
                }
            }
        },3000);


    }
}