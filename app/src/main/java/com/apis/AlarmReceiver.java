package com.apis;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    String CHANNEL_ID = "main.notifications";

    private String nomeAnimal;
    private String nomeLote;
    private int idAnimal;
    private int idLote;

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.hasExtra("animal_nome") && intent.hasExtra("animal_id") && intent.hasExtra("lote_id")){
            nomeAnimal = intent.getStringExtra("animal_nome");
            idAnimal = intent.getIntExtra("animal_id", 9999);
            idLote = intent.getIntExtra("lote_id", 9999);
            nomeLote = intent.getStringExtra("lote_nome");

        }

        Intent intentA = new Intent(context, AdicionarComportamento.class);
        intentA.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentA.putExtra("animal_nome", nomeAnimal);
        intentA.putExtra("animal_id", idAnimal);
        intentA.putExtra("lote_id", idLote);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, idAnimal, intentA, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_notification)
                .setContentTitle("Lembrete de atualização")
                .setContentText(nomeLote+": Atualize o comportamento de "+nomeAnimal+".")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(idAnimal, builder.build());


    }

}
