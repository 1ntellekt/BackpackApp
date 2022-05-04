package com.example.backpackapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.backpackapp.enteties.Status;
import com.example.backpackapp.enteties.User;
import com.example.backpackapp.serverutils.JsonConv;
import com.example.backpackapp.serverutils.ServerReq;
import com.example.backpackapp.serverutils.Util;

import java.util.HashMap;
import java.util.Map;

public class SignActivity extends AppCompatActivity {

    private EditText ed_login, ed_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_activity);
        ed_password = findViewById(R.id.ed_password);
        ed_login = findViewById(R.id.ed_login);

    }

    public void onSignUp(View view) {
        startActivity(new Intent(SignActivity.this,LoginActivity.class));
    }

    public void onAuthUser(View view) {
        if (ed_login.getText().toString().equals("")){
            Toast.makeText(SignActivity.this, "Login is null!", Toast.LENGTH_SHORT).show();
        } else if (ed_password.getText().toString().equals("")){
            Toast.makeText(SignActivity.this, "Password is null!", Toast.LENGTH_SHORT).show();
        } else {
            Map<String,Object> params = new HashMap<>();
            params.put("login", ed_login.getText().toString());
            params.put("password",ed_password.getText().toString());
            ServerReq.postRequest("auth",params,SignActivity.this, textResponse -> {
                Status status = JsonConv.getStatus(textResponse);
                if (status.isStatus()){
                    User user = JsonConv.getUser(textResponse);
                    saveToShared(user);
                    Util.setCurrentUser(user);
                    startActivity(new Intent(SignActivity.this,MainActivity.class));
                }
                Toast.makeText(SignActivity.this, status.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveToShared(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("curr_user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id",user.getId());
        editor.putString("login",user.getLogin());
        editor.putString("password",user.getPassword());
        editor.putString("name",user.getName());
        editor.putString("surname",user.getSurname());
        editor.putString("telephone",user.getTelephone());
        editor.putString("role",user.getRole());
        editor.apply();
    }

}