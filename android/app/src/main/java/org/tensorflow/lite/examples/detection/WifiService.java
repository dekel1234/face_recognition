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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.tflite.TFLiteObjectDetectionAPIModel;

public class WifiService extends Service {

    private Handler handler = new Handler();
    private TextView statusTextView;
    private final long intervalMillis = 2 * 1000; // 1 minute

    private final Runnable wifiCheckRunnable = new Runnable() {
        @Override
        public void run() {
            boolean connected = isConnectedToWifi(WifiService.this);
            DetectorActivity.LOGGER.e("WIFI SERVICE A " + connected);
            if (connected) {

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
                    return capabilities.hasTransport(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                }
            }
        }

        return false;
    }
}