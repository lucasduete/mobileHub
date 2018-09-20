package lucasduete.github.io.mobilehub;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lucasduete.github.io.mobilehub.adapters.FeedAdapter;
import lucasduete.github.io.mobilehub.manager.MenuManage;
import lucasduete.github.io.mobilehub.models.Feed;
import lucasduete.github.io.mobilehub.utils.ConstManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private FeedAdapter adapter;

    private String username;
    private String password;

    private List<Feed> feedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.context = this;
        this.feedList = new ArrayList<>();

        this.username = getSharedPreferences(ConstManager.PREFS_NAME, MODE_PRIVATE)
                .getString("username", null);

        this.password = getSharedPreferences(ConstManager.PREFS_NAME, MODE_PRIVATE)
                .getString("password", null);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        atualizarListView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        startActivity(
                MenuManage.getActivity(this, item)
        );

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void atualizarListView() {
        TextView textView = findViewById(R.id.textViewAlert);
        ListView listView = findViewById(R.id.listViewRepositories);

        if (username == null || password == null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText("NÃO SUPORTADO EM LOGIN OAUTH");
            textView.setTextColor(Color.RED);


            listView.setVisibility(View.INVISIBLE);
        } else {

            textView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);

            adapter = new FeedAdapter(
                    feedList,
                    this
            );

            listView.setAdapter(adapter);
        }
    }

    private class UpdateFeedTask extends AsyncTask<String, Integer, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            Log.d(ConstManager.TAG, "Chegou na UpdateFeed Task");

            String username = getSharedPreferences(ConstManager.PREFS_NAME, MODE_PRIVATE)
                    .getString("username", null);

            String password = getSharedPreferences(ConstManager.PREFS_NAME, MODE_PRIVATE)
                    .getString("password", null);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(String.format("%s/user/feed", ConstManager.URL_BASE))
                    .post(
                            RequestBody
                                    .create(
                                            MediaType.parse("application/x-www-form-urlencoded"),
                                            String.format("username=%s,password=%s", username, password)
                                    )
                    )
                    .build();

            JSONArray jsonArray = null;
            String jsonString = null;

            try {
                Response response = client.newCall(request).execute();
                jsonString = response.body().string();
                jsonArray = new JSONArray(jsonString);
                Log.d(ConstManager.TAG, "\n\nObjeto :\n" + jsonArray.toString());

            } catch (IOException ex) {
                Log.d(ConstManager.TAG, "\n\nDeu pau na conexão");
                ex.printStackTrace();
            } catch (JSONException ex) {
                Log.d(ConstManager.TAG, "\n\nDeu pau na Conversão de JSON");
                ex.printStackTrace();
            }

            Log.d(ConstManager.TAG, jsonString);

            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (jsonArray == null || jsonArray.length() <= 0) {
                Toast.makeText(context, "Problema com a conexão com a internet",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Feed> feedsTemp = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    Feed feed = new Feed();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Log.d(ConstManager.TAG, "\n\n\n OBJETO: " + jsonObject + "\n\n");

                    feed.setTitle(jsonObject.getString("title"));
                    feed.setAvatarUrl(jsonObject.getString("avatarUrl"));

                    feedsTemp.add(feed);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            feedList.clear();
            feedList.addAll(feedsTemp);
            atualizarListView();
        }
    }
}
