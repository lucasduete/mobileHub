package lucasduete.github.io.mobilehub;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lucasduete.github.io.mobilehub.models.UserLocation;
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

    private Location location;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.context = this;
        this.sharedPreferences = getSharedPreferences(ConstManager.PREFS_NAME, MODE_PRIVATE);
        this.locationManager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (sharedPreferences.getString("token", null) != null && checkAccess() == true) {
            login();
        }

        Button buttonOauth = findViewById(R.id.buttonOauth);
        buttonOauth.setOnClickListener((view) -> new LoginTask().execute(MODE_OAUTH));

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener((view) -> getLocation());
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

    private UserLocation getLocation() {
        Log.d(ConstManager.TAG, "Entrou method");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(ConstManager.TAG, "Entrou if");
            return null;
        }

        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider: providers) {
            Location tempLocation = locationManager.getLastKnownLocation(provider);

            if (tempLocation == null) continue;

            if (bestLocation == null || tempLocation.getAccuracy() < bestLocation.getAccuracy())
                bestLocation = tempLocation;
        }

        UserLocation userLocation = UserLocation.of(bestLocation.getAltitude(), bestLocation.getLatitude(), bestLocation.getLongitude());

        Log.d("LOCATION", userLocation.toString());

        return userLocation;
    }

    private boolean checkAccess() {
        //TODO get userLocations from Database
        List<UserLocation> allowedLocations = new ArrayList<>();
        UserLocation userLocation = new UserLocation();

        for (UserLocation location: allowedLocations) {

            if (userLocation.getAltitude() <= (location.getAltitude() + 2)
                    && userLocation.getAltitude() >= (location.getAltitude() - 2)
                    && userLocation.getLatitude() <= (location.getLatitude() + 2)
                    && userLocation.getLatitude() >= (location.getLatitude() - 2)
                    && userLocation.getLongitude() <= (location.getLongitude() + 2)
                    && userLocation.getLongitude() >= (location.getLongitude() - 2)) {

                return true;
            }

        }

        return false;
    }

    private boolean saveLocation(UserLocation userLocation) {
        //TODO Persist userLocation

        return true;
    }
}
