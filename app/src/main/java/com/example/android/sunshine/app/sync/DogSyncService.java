package com.example.android.sunshine.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.android.sunshine.app.MyLogger;

public class DogSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static DogSyncAdapter DogSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("DogSyncService", "onCreate - DogSyncService");
        MyLogger.d("sunshine", "dog onCreate - DogSyncService");
        synchronized (sSyncAdapterLock) {
            if (DogSyncAdapter == null) {
                DogSyncAdapter = new DogSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return DogSyncAdapter.getSyncAdapterBinder();
    }
}