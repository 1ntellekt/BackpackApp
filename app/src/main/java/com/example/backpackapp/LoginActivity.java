package com.example.backpackapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.backpackapp.enteties.Status;
import com.example.backpackapp.enteties.User;
import com.example.backpackapp.serverutils.JsonConv;
import com.example.backpackapp.serverutils.ServerReq;
import com.example.backpackapp.serverutils.Util;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Spinner roleSpinner;
    private ArrayAdapter arrayAdapter;
    private EditText ed_login, ed_password, ed_name, ed_surname, ed_number;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        roleSpinner = findViewById(R.id.roleSpinner);
        ed_password = findViewById(R.id.ed_password);
        ed_login = findViewById(R.id.ed_login);
        ed_name = findViewById(R.id.ed_name);
        ed_surname = findViewById(R.id.ed_surname);
        ed_number = findViewById(R.id.ed_number);

        arrayAdapter = ArrayAdapter.createFromResource(this,R.array.spinner_role, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(arrayAdapter);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                role = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void onSignUp(View view) {
        startActivity(new Intent(LoginActivity.this,SignActivity.class));
    }

    public void onLoginUser(View view) {
        if (ed_login.getText().toString().equals("")){
            Toast.makeText(LoginActivity.this, "Login is null!", Toast.LENGTH_SHORT).show();
        } else if (ed_password.getText().toString().equals("")){
            Toast.makeText(LoginActivity.this, "Password is null!", Toast.LENGTH_SHORT).show();
        } else if (ed_name.getText().toString().equals("")){
            Toast.makeText(LoginActivity.this, "Name is null!", Toast.LENGTH_SHORT).show();
        }else if (ed_surname.getText().toString().equals("")){
            Toast.makeText(LoginActivity.this, "Surname is null!", Toast.LENGTH_SHORT).show();
        }else if (ed_number.getText().toString().equals("")){
            Toast.makeText(LoginActivity.this, "Telephone is null!", Toast.LENGTH_SHORT).show();
        }
        else {
            Map<String,Object> params = new HashMap<>();
            params.put("login", ed_login.getText().toString());
            params.put("password",ed_password.getText().toString());
            params.put("name", ed_name.getText().toString());
            params.put("surname", ed_surname.getText().toString());
            params.put("telephone", ed_number.getText().toString());
            params.put("role", role);

            ServerReq.postRequest("login",params,LoginActivity.this, textResponse -> {
                Status status = JsonConv.getStatus(textResponse);
                if (status.isStatus()){
                    User user = JsonConv.getUser(textResponse);
                    saveToShared(user);
                    Util.setCurrentUser(user);
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }
                Toast.makeText(LoginActivity.this, status.getMessage(), Toast.LENGTH_SHORT).show();
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