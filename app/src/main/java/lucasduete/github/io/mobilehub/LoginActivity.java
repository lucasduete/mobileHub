package lucasduete.github.io.mobilehub;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lucasduete.github.io.mobilehub.dao.DatabaseHelper;
import lucasduete.github.io.mobilehub.dao.UserLocationDao;
import lucasduete.github.io.mobilehub.models.UserLocation;
import lucasduete.github.io.mobilehub.utils.ConstManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private static final int MODE_BASIC = 1;
    private static final int MODE_OAUTH = 2;
    private static final int MODE_AUTHORIZATE = 3;
    private static final int MODE_ERROR = 0;

    private int execMode = 0;
    private String code = null;

    private String url = null;
    private Context context = null;
    MyLocationListener locationListener = null;
    private LocationManager locationManager = null;
    private UserLocationDao userLocationDao = null;
    private SharedPreferences sharedPreferences = null;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.context = this;
        this.progress = new ProgressDialog(context);
        this.locationListener = new MyLocationListener();
        this.sharedPreferences = getSharedPreferences(ConstManager.PREFS_NAME, MODE_PRIVATE);
        this.locationManager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        try {
            this.userLocationDao = new UserLocationDao(getConnectionSource());
        } catch (SQLException e) {
            e.printStackTrace();
            progress.dismiss();
        }

        if (sharedPreferences.getString("token", null) != null && checkAccess() == true)
            login();

        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);

        Button buttonOauth = findViewById(R.id.buttonOauth);
        buttonOauth.setOnClickListener((view) -> {
            new LoginTask().execute(MODE_OAUTH);
            if (!progress.isShowing()) progress.show();
        });

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener((view) -> {
            new LoginTask().execute(MODE_BASIC);
            if (!progress.isShowing()) progress.show();
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.locationManager.removeUpdates(this.locationListener);
        this.locationManager = null;
        this.locationListener = null;
    }

    private void callLoginSerice(int operation) {
        new LoginTask().execute(operation);
    }

    private void login() {
        if(progress.isShowing())
            progress.dismiss();

        Intent intentMainActivity = new Intent(context, MainActivity.class);
        finish();
        startActivity(intentMainActivity);
    }

    private String basicMode(OkHttpClient client, RequestBody requestBody) {
        Request request = new Request.Builder()
                .url(String.format("%s/%s", ConstManager.URL_BASE, "login/basic"))
                .post(requestBody)
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


        execMode = MODE_BASIC;
        return token;
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
        progress.dismiss();
        execMode = MODE_ERROR;
        return new String();
    }

    private class LoginTask extends AsyncTask<Integer, Integer, String> {

        String username;
        String password;

        @Override
        protected String doInBackground(Integer... integers) {
            Log.d(ConstManager.TAG, "Chegou no Service de Login");

            OkHttpClient client = new OkHttpClient();

            String value = null;
            int mode = integers[0];

            switch (mode) {
                case MODE_BASIC:
                    EditText editText;

                    editText = findViewById(R.id.editTextUsername);
                    username = editText.getText().toString();

                    editText = findViewById(R.id.editTextPassword);
                    password = editText.getText().toString();

                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody requestBody = RequestBody
                            .create(
                                mediaType,
                                String.format("username=%s,password=%s", username, password)
                            );

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

            String token = value;
            UserLocation userLocation;

            switch (execMode) {
                case MODE_BASIC:
                    Log.d(ConstManager.TAG, "\n\nToken " + token);
                    Log.d(ConstManager.TAG, "\nUsername " + username);
                    Log.d(ConstManager.TAG, "\nPassword " + password + "\n");

                    userLocation = getLocation();

                    if (userLocation != null) {
                        saveLocation(userLocation);
                        sharedPreferences.edit().putString("token", token).commit();
                        sharedPreferences.edit().putString("username", username).commit();
                        sharedPreferences.edit().putString("password", password).commit();

                        login();
                    } else {
                        progress.dismiss();
                    }

                    break;
                case MODE_OAUTH:
                    url = value;
                    Log.d(ConstManager.TAG, "URL aqui: " + url);

                    Intent intentBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intentBrowser);

                    break;
                case MODE_AUTHORIZATE:
                    token = value;
                    Log.d(ConstManager.TAG, token);

                    userLocation = getLocation();

                    if (userLocation != null) {
                        saveLocation(userLocation);
                        sharedPreferences.edit().putString("token", token).commit();

                        login();
                    } else {
                        progress.dismiss();
                    }

                    break;
            }
        }
    }

    private UserLocation getLocation() {

        Log.d(ConstManager.TAG, "Entrou method");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(context, "Por favor, ative as permissõs de GPS", Toast.LENGTH_LONG).show();
            Log.d(ConstManager.TAG, "Não tem permissões");

            return null;
        }

        for (String provider : locationManager.getProviders(true))
            this.locationManager.requestLocationUpdates(provider, 100, 0, this.locationListener);

        UserLocation userLocation = null;

        for (String provider : locationManager.getProviders(true)) {
            Location location = locationManager.getLastKnownLocation(provider);

            if (location == null)
                continue;

            userLocation = UserLocation.of(location.getLatitude(), location.getLongitude());
            break;
        }

        if (userLocation == null)
            Toast.makeText(context, "Houve um problema ao recuperar seu GPS, tente novamente mais tarde", Toast.LENGTH_LONG).show();

        return userLocation;
    }

    private boolean checkAccess() {

        List<UserLocation> allowedLocations = new ArrayList<>();

        try {
            allowedLocations = userLocationDao.queryForAll();

        } catch (SQLException ex) {

            ex.printStackTrace();
        }

        UserLocation userLocation = getLocation();

        if (userLocation == null)
            return false;

        for (UserLocation location : allowedLocations) {

            if (userLocation.getLatitude() <= (location.getLatitude() + 2)
                    && userLocation.getLatitude() >= (location.getLatitude() - 2)
                    && userLocation.getLongitude() <= (location.getLongitude() + 2)
                    && userLocation.getLongitude() >= (location.getLongitude() - 2)) {

                return true;
            }

        }

        return false;
    }

    private boolean saveLocation(UserLocation userLocation) {

        try {

            return userLocationDao.create(userLocation) == 1;
        } catch (SQLException ex) {

            ex.printStackTrace();
            return false;
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            UserLocation localLocation = UserLocation.of(location.getLatitude(), location.getLongitude());
            Log.d(ConstManager.TAG, "\nLocal Location : " + localLocation.toString());

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }

    }
}

