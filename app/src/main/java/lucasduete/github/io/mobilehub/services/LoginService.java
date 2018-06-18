package lucasduete.github.io.mobilehub.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import lucasduete.github.io.mobilehub.LoginActivity;
import lucasduete.github.io.mobilehub.MainActivity;
import lucasduete.github.io.mobilehub.utils.ConstManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginService extends Service {

    public LoginService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(() -> {
            Log.d(ConstManager.TAG, "Chegou no Service de Login");

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json; charset=utf8");
            RequestBody requestBody = RequestBody.create(mediaType, "json");

            Request request = new Request.Builder()
                    .url(String.format("%s/%s", ConstManager.URL_BASE, "login/basic"))
                    .post(requestBody)
                    .build();

            JSONObject jsonObject = null;

            try {
                Response response = client.newCall(request).execute();
                jsonObject = new JSONObject(response.body().string());
            } catch (IOException ex) {
                Log.d(ConstManager.TAG, "\n\nDeu na conexão");
                ex.printStackTrace();
                failLogin();
            } catch (JSONException ex) {
                Log.d(ConstManager.TAG, "\n\nDeu ruim no JSON");
                ex.printStackTrace();
                failLogin();
            }

            if (jsonObject != null) {
                Message msg = new Message();
                msg.obj = true;
                LoginActivity.loginHandle.sendMessage(msg);
            }

        }).start();

        return Service.START_NOT_STICKY;
    }

    private void failLogin() {
        Message msg = new Message();
        msg.obj = false;
        LoginActivity.loginHandle.sendMessage(msg);
    }
}
