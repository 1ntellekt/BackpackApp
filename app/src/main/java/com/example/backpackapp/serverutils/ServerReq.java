package com.example.backpackapp.serverutils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.media.MediaCodecInfo;
import android.net.Uri;
import android.util.Xml;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.backpackapp.backgroundtask.BackgroundTask;
import com.example.backpackapp.interfaces.OnResponse;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.spec.EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

public class ServerReq {

    private static final String BASE_URL = "http://192.168.43.163/";

    public static void getRequest(String url, Activity activity, OnResponse onResponse){
         new BackgroundTask(activity){
             HttpURLConnection connection;
             BufferedReader bufferedReader;
             StringBuffer stringBuffer;
             String textResponse;
             @Override
             public void doInBackground() {
                 try {
                     connection = (HttpURLConnection) new URL(BASE_URL+url).openConnection();
                     connection.setRequestMethod("GET");
                     int stCode = connection.getResponseCode();
                     if (stCode>299) bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                     else bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                     stringBuffer = new StringBuffer();
                     String line;
                     while ((line=bufferedReader.readLine())!=null){
                         stringBuffer.append(line);
                     }
                     bufferedReader.close();
                     textResponse = stringBuffer.toString();
                 } catch (IOException e){
                     e.printStackTrace();
                 } finally {
                     connection.disconnect();
                 }
             }
             @Override
             public void postExecute() {
               onResponse.onGetResponse(textResponse);
             }
         }.execute();
    }

    public static void deleteRequest(String url, Activity activity, OnResponse onResponse){
        new BackgroundTask(activity){
            HttpURLConnection connection;
            BufferedReader bufferedReader;
            StringBuffer stringBuffer;
            String textResponse;
            @Override
            public void doInBackground() {
                try {
                    connection = (HttpURLConnection) new URL(BASE_URL+url).openConnection();
                    connection.setRequestMethod("DELETE");
                    int stCode = connection.getResponseCode();
                    if (stCode>299) bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    else bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    stringBuffer = new StringBuffer();
                    String line;
                    while ((line=bufferedReader.readLine())!=null){
                        stringBuffer.append(line);
                    }
                    bufferedReader.close();
                    textResponse = stringBuffer.toString();
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            }
            @Override
            public void postExecute() {
                onResponse.onGetResponse(textResponse);
            }
        }.execute();
    }

    public static void patchRequest(String url, Map<String,Object>params,Activity activity, OnResponse onResponse){
        new BackgroundTask(activity){
            HttpURLConnection connection;
            BufferedReader bufferedReader;
            StringBuffer stringBuffer;
            String textResponse;
            @Override
            public void doInBackground() {
                try {
                    byte [] data = getBytesFromParams(params,true);
                    connection = (HttpURLConnection) new URL(BASE_URL+url).openConnection();
                    connection.setRequestMethod("PATCH");
                    connection.setRequestProperty("Content-Type","application/json");
                    connection.setRequestProperty("Content-Length", String.valueOf(data.length));
                    connection.setDoOutput(true);
                    connection.getOutputStream().write(data);
                    connection.getOutputStream().flush();
                    connection.getOutputStream().close();

                    int stCode = connection.getResponseCode();
                    if (stCode>299) bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    else bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    stringBuffer = new StringBuffer();
                    String line;
                    while ((line=bufferedReader.readLine())!=null){
                        stringBuffer.append(line);
                    }
                    bufferedReader.close();
                    textResponse = stringBuffer.toString();
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            }
            @Override
            public void postExecute() {
                onResponse.onGetResponse(textResponse);
            }
        }.execute();
    }

    public static void postRequest(String url, Map<String,Object>params, Activity activity, OnResponse onResponse){
        new BackgroundTask(activity){
            HttpURLConnection connection;
            BufferedReader bufferedReader;
            StringBuffer stringBuffer;
            String textResponse;
            @Override
            public void doInBackground() {
                try {
                    byte [] data = getBytesFromParams(params,false);
                    connection = (HttpURLConnection) new URL(BASE_URL+url).openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", String.valueOf(data.length));
                    connection.setDoOutput(true);
                    connection.getOutputStream().write(data);
                    connection.getOutputStream().flush();
                    connection.getOutputStream().close();

                    int stCode = connection.getResponseCode();
                    if (stCode>299) bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    else bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    stringBuffer = new StringBuffer();
                    String line;
                    while ((line=bufferedReader.readLine())!=null){
                        stringBuffer.append(line);
                    }
                    bufferedReader.close();
                    textResponse = stringBuffer.toString();
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            }
            @Override
            public void postExecute() {
                onResponse.onGetResponse(textResponse);
            }
        }.execute();
    }

    public static void uploadRequest(String url,File file,Activity activity,OnResponse onResponse){
        new BackgroundTask(activity){
            HttpURLConnection connection;
            BufferedReader bufferedReader;
            StringBuffer stringBuffer;
            String textResponse;
            String boundary = "----FormBoundaryFsDGhVGfHVHvChG",
            twoHyphens="--", endLine = "\r\n", filename = "myfile";
            int maxBufferSize = 1*1024*1024, bufferAvailable, bufferRead, bufferSize;
            byte [] buffer;
            @Override
            public void doInBackground() {
                try {
                    connection = (HttpURLConnection) new URL(BASE_URL+url).openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection","Keep-Alive");
                    connection.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);

                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                    dataOutputStream.writeBytes(twoHyphens+boundary+endLine);
                    dataOutputStream.writeBytes("Content-Disposition: form-data;name="+filename+";filename="+file.getPath()+endLine);
                    dataOutputStream.writeBytes("Content-Type: */*"+endLine);
                    dataOutputStream.writeBytes(endLine);

                    FileInputStream fileInputStream = new FileInputStream(file);
                    bufferAvailable = fileInputStream.available();
                    bufferSize = Math.min(maxBufferSize,bufferAvailable);
                    buffer = new byte[bufferSize];
                    bufferRead = fileInputStream.read(buffer,0,bufferSize);

                    while (bufferRead>0){
                        dataOutputStream.write(buffer,0,bufferSize);
                        bufferAvailable = fileInputStream.available();
                        bufferSize = Math.min(maxBufferSize,bufferAvailable);
                        bufferRead = fileInputStream.read(buffer,0,bufferSize);
                    }

                    dataOutputStream.writeBytes(endLine);
                    dataOutputStream.writeBytes(twoHyphens+boundary+endLine);

                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();

                    int stCode = connection.getResponseCode();
                    if (stCode>299) bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    else bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    stringBuffer = new StringBuffer();
                    String line;
                    while ((line=bufferedReader.readLine())!=null){
                        stringBuffer.append(line);
                    }
                    bufferedReader.close();
                    textResponse = stringBuffer.toString();
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            }
            @Override
            public void postExecute() {
                onResponse.onGetResponse(textResponse);
            }
        }.execute();
    }

    public static void downloadRequest(String url, File saveFile, @NonNull Activity activity, String filename) {
                DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(BASE_URL+url+filename);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationUri(Uri.fromFile(saveFile));
                request.setTitle("Downloading a "+filename+" file");
                request.setDescription("Downloading.....");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                downloadManager.enqueue(request);
    }

    public static byte[] getBytesFromParams(Map<String,Object>params,boolean isJson) throws UnsupportedEncodingException {
        StringBuilder data = new StringBuilder();

        if (!isJson){
          for (Map.Entry<String,Object>param:params.entrySet()) {
              if (data.length()!=0) data.append('&');
              data.append(URLEncoder.encode(param.getKey(),"UTF-8"));
              data.append("=");
              data.append(URLEncoder.encode(param.getValue().toString(),"UTF-8"));
          }
        } else {
            JSONObject jsonObject = new JSONObject(params);
            data.append(jsonObject);
        }
        return data.toString().getBytes(StandardCharsets.UTF_8);
    }

}
