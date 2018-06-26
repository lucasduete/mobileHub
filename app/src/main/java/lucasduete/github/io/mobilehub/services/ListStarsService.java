package lucasduete.github.io.mobilehub.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import lucasduete.github.io.mobilehub.ListStarsActivity;
import lucasduete.github.io.mobilehub.utils.ConstManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListStarsService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(() -> {
            Log.d(ConstManager.TAG, "Chegou no Search Service");

            String token = getSharedPreferences(ConstManager.PREFS_NAME, MODE_PRIVATE)
                    .getString("token", null);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(String.format("%s/repositories/stars", ConstManager.URL_BASE))
                    .addHeader("Authorization", String.format("Bearer %s", token))
                    .get()
                    .build();

            JSONArray jsonArray = null;

            try {
                Response response = client.newCall(request).execute();
                jsonArray = new JSONArray(response.body().string());
                Log.d(ConstManager.TAG, "\n\nObjeto: \n" + jsonArray.toString());

            } catch (IOException ex) {
                Log.d(ConstManager.TAG, "\n\nDeu pau na conexão");
                ex.printStackTrace();
            } catch (JSONException ex) {
                Log.d(ConstManager.TAG, "\n\nDeu pau na Conversão de JSON");
                ex.printStackTrace();
            }

            if (jsonArray != null) {
                Log.d(ConstManager.TAG, jsonArray.toString());
                Message msg = new Message();
                msg.obj = jsonArray;
                ListStarsActivity.listStarsHandle.sendMessage(msg);
            }
        }).start();

        return Service.START_NOT_STICKY;
    }
}
