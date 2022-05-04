package com.example.backpackapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;


import com.example.backpackapp.enteties.User;
import com.example.backpackapp.fragments.BackpackFragment;
import com.example.backpackapp.fragments.InfoFragment;
import com.example.backpackapp.fragments.PersonFragment;
import com.example.backpackapp.fragments.SearchFragment;
import com.example.backpackapp.serverutils.JsonConv;
import com.example.backpackapp.serverutils.ServerReq;
import com.example.backpackapp.serverutils.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frameHolder;
    private BottomNavigationView navbar;
    private ImageButton btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameHolder = findViewById(R.id.frameHolder);
        navbar = findViewById(R.id.navbar);
        btnExit = findViewById(R.id.btnExit);

        navbar.setSelectedItemId(R.id.nav_search);
        replaceFrame(SearchFragment.newInstance(),true);

        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_search:
                        replaceFrame(SearchFragment.newInstance(),false);
                        return true;
                    case R.id.nav_backpack:
                        replaceFrame(BackpackFragment.newInstance(),false);
                        return true;
                    case R.id.nav_person:
                        replaceFrame(PersonFragment.newInstance(),false);
                        return true;
                    case R.id.nav_info:
                        replaceFrame(InfoFragment.newInstance(),false);
                        return true;
                }
                return false;
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.getCurrentUser().setId(0);
                saveToShared(Util.getCurrentUser());
                startActivity(new Intent(MainActivity.this,LoadActivity.class));
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkCountBooks() {
        ServerReq.getRequest("countbooks",MainActivity.this,body->{
            Util.setCountBooks(JsonConv.getCountBooks(body));
            if (Util.getCountBooks()>getCountInPref()){
                saveCountInPref(Util.getCountBooks());
                notifyShow();
            }
           // Toast.makeText(MainActivity.this, "size:"+getCountInPref(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveCountInPref(int count){
        SharedPreferences sharedPreferences = getSharedPreferences("count_books",MODE_PRIVATE);
        SharedPreferences.Editor editor  = sharedPreferences.edit();
        editor.putInt("count",count);
        editor.apply();
    }

    private int getCountInPref(){
        SharedPreferences sharedPreferences = getSharedPreferences("count_books",MODE_PRIVATE);
        return sharedPreferences.getInt("count",0);
    }

    private final int REQ_PENDING_INTENT = 111;
    private final int NOTIFY_ID = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notifyShow(){
        PendingIntent pendingIntent = PendingIntent.getActivity(this,REQ_PENDING_INTENT,new Intent(this,MainActivity.class),PendingIntent.FLAG_CANCEL_CURRENT);
        String CHANNEL_ID="MYCHANNEL";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name",NotificationManager.IMPORTANCE_LOW);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_menu_book)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_menu_book))
                .setTicker("Новая книга была добавлена!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Электронный портфель")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText("Была добавлена новая книга!");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(NOTIFY_ID, builder.build());
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            Fragment currFragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size()-1);
            getSupportFragmentManager().popBackStack();
            setNavBarByBackPressed(currFragment);
        /*FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(currFragment);
        fragmentTransaction.attach(currFragment);
        fragmentTransaction.commit();*/
        //Toast.makeText(MainActivity.this, "size:"+getSupportFragmentManager().getFragments().size(), Toast.LENGTH_SHORT).show();
    }

    private void setNavBarByBackPressed(Fragment currFragment) {
        if (currFragment instanceof InfoFragment) navbar.setSelectedItemId(R.id.nav_info);
        else if (currFragment instanceof BackpackFragment) navbar.setSelectedItemId(R.id.nav_backpack);
        else if (currFragment instanceof PersonFragment) navbar.setSelectedItemId(R.id.nav_person);
        else if (currFragment instanceof SearchFragment) navbar.setSelectedItemId(R.id.nav_search);
    }

    private void replaceFrame(Fragment fragment, boolean isBegin) {
       FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
       if (isBegin) fragmentTransaction.replace(R.id.frameHolder,fragment).commit();
       else fragmentTransaction.replace(R.id.frameHolder,fragment).addToBackStack(fragment.getClass().getName()).commit();
    }
}