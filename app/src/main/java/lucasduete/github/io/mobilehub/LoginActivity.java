package lucasduete.github.io.mobilehub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import lucasduete.github.io.mobilehub.utils.ConstManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int MODE_BASIC = 1;
    private static final int MODE_OAUTH = 2;
    private static final int MODE_AUTHORIZATE = 3;
    private static final int MODE_ERROR = 0;

    private String code = null;
    private int execMode = 0;

    private Context context = null;
    private String url = null;
    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.sharedPreferences = getSharedPreferences(ConstManager.PREFS_NAME, MODE_PRIVATE);
        this.context = this;

        Button buttonOauth = findViewById(R.id.buttonOauth);
        buttonOauth.setOnClickListener((view) -> new LoginTask().execute(MODE_OAUTH));

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener((view) -> login());
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri data = getIntent().getData();
        if (data != null) {
            this.code = data.getQueryParameter("code");
            Log.d(ConstManager.TAG, this.code);

            callLoginSerice(MODE_AUTHORIZATE);
        } else
            Log.d(ConstManager.TAG, "\nSem code ainda");
    }

    private void callLoginSerice(int operation) {
        new LoginTask().execute(operation);
    }

    private void login() {
        Intent intentMainActivity = new Intent(context, MainActivity.class);
        finish();
        startActivity(intentMainActivity);
    }

    private String basicMode(OkHttpClient client, RequestBody requestBody) {
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

        execMode = MODE_BASIC;
        return jsonObject.toString();
    }

    private String oauthMode(OkHttpClient client) {
        Request request = new Request.Builder()
                .url(String.format("%s/%s", ConstManager.URL_BASE, "login/oauth2"))
                .get()
                .build();

        String url = null;

        try {
            Response response = client.newCall(request).execute();
            url = response.body().string();
        } catch (IOException ex) {
            Log.d(ConstManager.TAG, "\n\nDeu pau na conexão");
            ex.printStackTrace();
            failLogin();
        }

        execMode = MODE_OAUTH;
        return url;
    }

    private String authorizationMode(OkHttpClient client) {
        Request request = new Request.Builder()
                .url(String.format("%s/%s?code=%s", ConstManager.URL_BASE, "login/authorize", this.code))
                .get()
                .build();

        String token = null;

        try {
            Response response = client.newCall(request).execute();
            token = response.body().string();
        } catch (IOException ex) {
            Log.d(ConstManager.TAG, "\n\nDeu na conexão");
            ex.printStackTrace();
            failLogin();
        }

        execMode = MODE_AUTHORIZATE;
        return token;
    }

    private String failLogin() {
        execMode = MODE_ERROR;
        return new String();
    }

    private class LoginTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... integers) {
            Log.d(ConstManager.TAG, "Chegou no Service de Login");

            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json; charset=utf8");
            //TODO realemnte é necessário? qual json será injetado?
            RequestBody requestBody = RequestBody.create(mediaType, "ALGUM JSON AQ");

            String value = null;
            int mode = integers[0];

            switch (mode) {
                case MODE_BASIC:
                    value = basicMode(client, requestBody);
                    break;
                case MODE_OAUTH:
                    value = oauthMode(client);
                    break;
                case MODE_AUTHORIZATE:
                    value = authorizationMode(client);
                    break;
                default:
                    failLogin();
                    break;
            }

            return value;
        }

        @Override
        protected void onPostExecute(String value) {

            switch (execMode) {
                case MODE_BASIC:
                    login();
                    break;
                case MODE_OAUTH:
                    url = value;
                    Log.d(ConstManager.TAG, "URL aqui: " + url);

                    Intent intentBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intentBrowser);

                    break;
                case MODE_AUTHORIZATE:
                    String token = value;
                    Log.d(ConstManager.TAG, token);

                    sharedPreferences.edit().putString("token", token).commit();
                    login();
                    break;
            }
        }
    }
}
