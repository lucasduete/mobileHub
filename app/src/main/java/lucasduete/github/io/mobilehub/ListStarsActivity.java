package lucasduete.github.io.mobilehub;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import lucasduete.github.io.mobilehub.adapters.RepositoryAdapter;
import lucasduete.github.io.mobilehub.manager.MenuManage;
import lucasduete.github.io.mobilehub.models.Repository;
import lucasduete.github.io.mobilehub.utils.ConstManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListStarsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<Repository> repositories = new ArrayList<>();
    private RepositoryAdapter adapter;
    private Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_stars);

        this.context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        new ListStarsTask().execute();
        super.onStart();
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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
        ListView listView = findViewById(R.id.listViewStars);

        adapter = new RepositoryAdapter(
                repositories,
                this
        );

        listView.setAdapter(adapter);
    }

    private class ListStarsTask extends AsyncTask<String, Integer, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            Log.d(ConstManager.TAG, "Chegou na Task de ListStars");

            String token = getSharedPreferences(ConstManager.PREFS_NAME, MODE_PRIVATE)
                    .getString("token", null);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(String.format("%s/repositories/stars?token=%s", ConstManager.URL_BASE, token))
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

            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            if (jsonArray == null || jsonArray.length() <= 0) {
                Toast.makeText(context, "Problema com a conexão com a internet",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Repository> repositoriesTemp = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    Repository repository = new Repository();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Log.d(ConstManager.TAG, "\n\n\n OBJETO: " + jsonObject + "\n\n");

                    repository.setNome(jsonObject.getString("nome"));
                    repository.setDescricao(jsonObject.getString("descricao"));
                    repository.setNomeAutor(jsonObject.getString("nomeAutor"));
                    repository.setFoto(jsonObject.getString("fotoAutor"));

                    repositoriesTemp.add(repository);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            repositories.clear();
            repositories.addAll(repositoriesTemp);
            atualizarListView();
        }
    }
}
