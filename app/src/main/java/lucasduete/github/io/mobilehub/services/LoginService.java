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

    public static final int MODE_BASIC = 1;
    public static final int MODE_OAUTH = 2;
    public static final int MODE_AUTHORIZATE = 3;
    public static final int MODE_ERROR = 0;

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

            int mode = intent.getIntExtra("mode", 0);


            switch (mode) {
                case MODE_BASIC:
                    basicMode(client, requestBody);
                    break;
                case MODE_OAUTH:
                    oauthMode(client);
                    break;
                case MODE_AUTHORIZATE:
                    authorizationMode(client, requestBody);
                    break;
                default:
                    failLogin();
                    break;
            }

        }).start();

        return Service.START_NOT_STICKY;
    }

    private void basicMode(OkHttpClient client, RequestBody requestBody) {
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
            msg.what = MODE_BASIC;
            msg.obj = true;
            LoginActivity.loginHandle.sendMessage(msg);
        }
    }

    private void oauthMode(OkHttpClient client) {
        Request request = new Request.Builder()
                .url(String.format("%s/%s", ConstManager.URL_BASE, "login/oauth"))
                .get()
                .build();

        String url = null;

        try {
            Response response = client.newCall(request).execute();
            url = response.body().string();
        } catch (IOException ex) {
            Log.d(ConstManager.TAG, "\n\nDeu na conexão");
            ex.printStackTrace();
            failLogin();
        }

        if (url != null) {
            Message msg = new Message();
            msg.what = MODE_OAUTH;
            msg.obj = url;
            LoginActivity.loginHandle.sendMessage(msg);
        }
    }

    private void authorizationMode(OkHttpClient client, RequestBody requestBody) {
        Request request = new Request.Builder()
                .url(String.format("%s/%s", ConstManager.URL_BASE, "login/authorizate"))
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
            msg.what = MODE_AUTHORIZATE;
            msg.obj = true;
            LoginActivity.loginHandle.sendMessage(msg);
        }
    }

    private void failLogin() {
        Message msg = new Message();
        msg.what = 0;
        msg.obj = false;
        LoginActivity.loginHandle.sendMessage(msg);
    }
}
