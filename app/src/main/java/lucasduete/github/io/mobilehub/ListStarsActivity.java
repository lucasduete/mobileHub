package lucasduete.github.io.mobilehub;

import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import lucasduete.github.io.mobilehub.adapters.RepositoryAdapter;
import lucasduete.github.io.mobilehub.manager.MenuManage;
import lucasduete.github.io.mobilehub.models.Repository;
import lucasduete.github.io.mobilehub.utils.ConstManager;

public class ListStarsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ListStarsHandler listStarsHandle;

    private ArrayList<Repository> repositories = new ArrayList<>();
    private RepositoryAdapter adapter;
    private Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_stars);

        this.context = this;
        this.listStarsHandle = new ListStarsHandler();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        atualizarListView();

    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(context, ListStarsActivity.class);
        startService(intent);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class ListStarsHandler extends Handler {
        public ListStarsHandler() {

        }

        //TODO centralizar este codigo
        @Override
        public void handleMessage(Message msg) {
            ArrayList<Repository> repositoriesTemp = new ArrayList<>();
            JSONArray jsonArray = (JSONArray) msg.obj;

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

    private void atualizarListView() {
        ListView listView = (ListView) findViewById(R.id.listViewStars);

        adapter = new RepositoryAdapter(
                repositories,
                this
        );

        listView.setAdapter(adapter);
    }
}
