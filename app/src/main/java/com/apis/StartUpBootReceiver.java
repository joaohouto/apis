package com.apis;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.util.Calendar;

public class StartUpBootReceiver extends BroadcastReceiver {

    private static String HOUR = "hour";
    private static String MINUTE = "minute";


    public static void setAlarm(Context context, int hour, int minute){
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putInt(HOUR, hour)
                .putInt(MINUTE, minute)
                .apply();
        setAlarm(context);
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            setAlarm(context);
            Toast.makeText(context, "Alarm set", Toast.LENGTH_LONG).show();
        }

    }

    private static void setAlarm(Context context) {

        int hour = getHour(context);
        int minute = getMinute(context);

        if(hour == -1 || minute == -1){
            //nenhum horário definido
            return;
        }

        // Cria um Calendar para o horário estipulado
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        //Se já passou
        if(isDateBeforeNow(calendar)){
            //adiciona um dia
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }


        //PendingIntent para lançar o serviço
        Intent serviceIntent = new Intent(context, BootService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //Cancela um possível alarme existente
        alarmManager.cancel(pendingIntent);

        //Alarme que se repete todos os dias a uma determinada hora
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }

    private static int getHour(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(HOUR, -1);
    }
    private static int getMinute(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(MINUTE, -1);
    }

    private static boolean isDateBeforeNow(Calendar calendar){
        return calendar.getTimeInMillis() <= System.currentTimeMillis();
    }
}