package org.tensorflow.lite.examples.detection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Pair;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class WifiService extends Service {

    private static Handler handler = new Handler();
    private static TextView statusTextView;
    private static final long intervalMillis = 2 * 1000; // 1 minute

    private final Runnable wifiCheckRunnable = new Runnable() {
        @Override
        public void run() {
            boolean connected = isConnectedToWifi(WifiService.this) || true;
            DetectorActivity.LOGGER.e("WIFI SERVICE A " + connected);
            if (connected) {
                if(!DetectorActivity.updates.isEmpty()) {
                    new RequestTask().execute(new Object[1]);
                }
            } else {

            }

            // Re-run after 1 minute
            handler.postDelayed(this, intervalMillis);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(wifiCheckRunnable);
        stopSelf();
        return START_NOT_STICKY;
    }

    // Utility method to check Wi-Fi status
    public static boolean isConnectedToWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);
                if (capabilities != null) {
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                }
            }
        }

        return false;
    }
}