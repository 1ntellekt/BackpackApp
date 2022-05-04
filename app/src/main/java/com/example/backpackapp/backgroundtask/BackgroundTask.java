package com.example.backpackapp.backgroundtask;

import android.app.Activity;

abstract public class BackgroundTask {
   private Activity activity;

    public BackgroundTask(Activity activity) {
        this.activity = activity;
    }
    private void startDoInTask(){
       new Thread(new Runnable() {
           @Override
           public void run() {
               doInBackground();
               activity.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       postExecute();
                   }
               });
           }
       }).start();
    }
    public void execute(){startDoInTask();}
    abstract public void doInBackground();
    abstract public void postExecute();
}
