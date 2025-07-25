package org.tensorflow.lite.examples.detection;

import android.os.AsyncTask;
import android.util.Pair;

import org.tensorflow.lite.examples.detection.DetectorActivity;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
public class RequestTask extends AsyncTask {

    private static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        Pair p = DetectorActivity.updates.peek();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{\"timeAdded\": " + p.second + ", \"name\": \""  + p.first + "\"}");

        Request request = new Request.Builder()
                .url("https://botify-uc53.onrender.com/update-time")
                .post(requestBody)
                .build();

        DetectorActivity.LOGGER.i("REQUESTING" + bodyToString(request));
        try {
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                DetectorActivity.updates.remove();
                DetectorActivity.LOGGER.i("SUCCESS!");
            } else {
                DetectorActivity.LOGGER.i("UMMMMM" + response.body().string());
            }
        } catch (Exception e) {
            DetectorActivity.LOGGER.e("FAILED " + e.toString());
        }
        return null;
    }
}
