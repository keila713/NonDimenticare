package com.example.nondimenticare;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_ID_1 = "channel1";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel nt = new NotificationChannel(
                    CHANNEL_ID_1,
                    "Cumpleañeros",
                    NotificationManager.IMPORTANCE_HIGH
            );

            nt.setDescription("Notifica el día del cumpleaños");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(nt);

        }
    }
}
