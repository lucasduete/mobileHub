package lucasduete.github.io.mobilehub.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import lucasduete.github.io.mobilehub.SearchActivity;
import lucasduete.github.io.mobilehub.utils.ConstManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchService extends Service {

    public SearchService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(() -> {
            Log.d(ConstManager.TAG, "Chegou no Search Service");

            String keyword = intent.getStringExtra("keyword");

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(String.format("%s/%s?keyword=%s", ConstManager.URL_BASE, "repositories/search", keyword))
                    .get()
                    .build();

            JSONArray jsonArray = null;

            try {
                Response response = client.newCall(request).execute();
                Log.d("assd", "objeto");
                JSONObject jsonObject = new JSONObject(response.body().string());
                Log.d("asd", jsonObject.toString());

                jsonArray = jsonObject.getJSONArray("items");

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
                SearchActivity.seachHandle.sendMessage(msg);
            }

        }).start();

        return Service.START_NOT_STICKY;
    }
}
