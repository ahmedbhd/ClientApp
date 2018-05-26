package com.alphaford.pimapplication;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.alphaford.pimapplication.Models.History;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class NotificationService extends Service {
    Timer mTimer;
    int seuil;
    String nomChaine,token;
    int nombreVues;
    ConnexionManager cnx = new ConnexionManager();
    RequestQueue queue ;

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 2000, 2* 1000);
        queue = Volley.newRequestQueue(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            Bundle extras = intent.getExtras();
            seuil = extras.getInt("seuil");
            nomChaine = extras.getString("nomChaine");
            token = extras.getString("token");
            System.out.println("aaaaaaaaa"+seuil);
            Log.d("Nommmmm",nomChaine);
            fetchVues();

        }catch (Exception e){
            e.printStackTrace();
        }


        return super.onStartCommand(intent,flags,startId);
    }


    TimerTask timerTask =new TimerTask() {
        @Override
        public void run() {
            Log.e("Log","Running");
            fetchVues();
            if (seuil == nombreVues){
                notifiy();
                mTimer.cancel();
                timerTask.cancel();
            }

        }
    };

    @Override
    public void onDestroy() {
        try {
            mTimer.cancel();
            timerTask.cancel();
        }catch (Exception e ){
            e.printStackTrace();
        }
        Intent intent = new Intent("com.alphaford.pimapplication");
        intent.putExtra("WatchValue","torestor");

        sendBroadcast(intent);
    }
    public void notifiy(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("RSSPullService");
        Intent myIntent= new Intent(Intent.ACTION_VIEW, Uri.parse(""));
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0 ,myIntent ,0);
        Context context = getApplicationContext();
        Notification.Builder builder;
        // int seuil = prefs.getInt("seuilWatcher", 0);
        System.out.println("seuuuuuuuuil****"+seuil);
        System.out.println("Nombre"+nombreVues);
        builder = new Notification.Builder(context)
                .setContentTitle("Favorite Channel").setContentText("Your channel "+nomChaine+" achieves Now "+nombreVues+" Viewers :)").setContentIntent(pendingIntent).setDefaults(Notification.DEFAULT_SOUND).
                        setAutoCancel(true).setSmallIcon(R.drawable.favorite_icon);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);

    }
    private void fetchVues() {

        StringRequest request = new StringRequest(com.android.volley.Request.Method.GET, cnx.getPath(":8080/api/watcherchannel/"+nomChaine), onPostsLoaded, onPostsError)
        {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-access-token", token);
                headers.put("Content-Type", "application/json");

                return headers;
            }

        };
        queue.add(request);

    }
    private final com.android.volley.Response.Listener<String> onPostsLoaded = new com.android.volley.Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            nombreVues = Integer.valueOf(response);

        }
    };

    private final com.android.volley.Response.ErrorListener onPostsError = new com.android.volley.Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }


    };
}

