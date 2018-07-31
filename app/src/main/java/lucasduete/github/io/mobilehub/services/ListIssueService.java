package lucasduete.github.io.mobilehub.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import lucasduete.github.io.mobilehub.ListIssueActivity;
import lucasduete.github.io.mobilehub.SearchActivity;
import lucasduete.github.io.mobilehub.utils.ConstManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListIssueService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(() -> {
            Log.d(ConstManager.TAG, "\nChegou no Service de ListIssues");

            String repo = intent.getStringExtra("repo");
            String owner = intent.getStringExtra("owner");

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(String.format("%s/issues/repository?owner=%s&repo=%s",
                            ConstManager.URL_BASE, owner, repo))
                    .get()
                    .build();

            JSONArray jsonArray = null;

            try {
                Response response = client.newCall(request).execute();
                jsonArray = new JSONArray(response.body().string());
                Log.d(ConstManager.TAG, "\n\nObjeto :\n" + jsonArray.toString());

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
                ListIssueActivity.listIssueHandler.sendMessage(msg);
            }

        }).start();

        return Service.START_NOT_STICKY;
    }
}
