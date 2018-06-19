package lucasduete.github.io.mobilehub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import lucasduete.github.io.mobilehub.adapters.RepositoryAdapter;
import lucasduete.github.io.mobilehub.manager.MenuManage;
import lucasduete.github.io.mobilehub.models.Repository;
import lucasduete.github.io.mobilehub.services.SearchService;
import lucasduete.github.io.mobilehub.utils.ConstManager;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static SearchHandle seachHandle;

    private ArrayList<Repository> repositories = new ArrayList<>();
    private RepositoryAdapter adapter;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        this.context = this;
        seachHandle = new SearchHandle();

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

        TextInputEditText inputText = (TextInputEditText) findViewById(R.id.inputSearch);
        inputText.setOnEditorActionListener((view, actionId, event) -> {
            Log.d(ConstManager.TAG, "ENTROU");
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                Log.d(ConstManager.TAG, "\n\nENTROUUUU NO IFFFF");
                Intent intent = new Intent(context, SearchService.class);
                intent.putExtra("keyword", inputText.getText().toString());
                startService(intent);
            }
            return true;
        });

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

    public class SearchHandle extends Handler {
        public SearchHandle() {

        }

        @Override
        public void handleMessage(Message msg) {
            ArrayList<Repository> repositoriesTemp = new ArrayList<>();
            JSONArray jsonArray = (JSONArray) msg.obj;

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    Repository repository = new Repository();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    repository.setNome(jsonObject.getString("full_name"));
                    repository.setDescricao(jsonObject.getString("description"));
                    repository.setNomeAutor(jsonObject.getJSONObject("owner").getString("login"));
                    repository.setFoto(jsonObject.getJSONObject("owner").getString("avatar_url"));

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
        ListView listView = (ListView) findViewById(R.id.listViewSearch);

        adapter = new RepositoryAdapter(
                repositories,
                this
        );

        listView.setAdapter(adapter);
    }

}
