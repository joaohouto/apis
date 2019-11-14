package com.apis;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import androidx.annotation.Nullable;

public class BootService extends IntentService {

    private PowerManager.WakeLock wakeLock;
    String CHANNEL_ID = "main.notifications";

    public BootService() {
        super("name");
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "BootService");
        wakeLock.acquire();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //Notificação aqui.

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(wakeLock.isHeld()){
            //Verificou-se que o iluminar do ecrã
            //não acontecia devido ao WakeLock ser
            //rapidamente libertado(apesar de PowerManager.ON_AFTER_RELEASE !?).
            try {
                //Atrasa a libertação do WakeLock
                //de forma a permitir a iluminação do ecrâ.
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                wakeLock.release();
            }
        }
    }
}