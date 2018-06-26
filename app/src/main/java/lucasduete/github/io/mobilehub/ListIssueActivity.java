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
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import lucasduete.github.io.mobilehub.adapters.IssueAdapter;
import lucasduete.github.io.mobilehub.manager.MenuManage;
import lucasduete.github.io.mobilehub.models.Issue;

public class ListIssueActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static MyHandle listIssueHandler;

    private ArrayList<Issue> issues = new ArrayList<>();
    private IssueAdapter adapter;
    private Context context;

    private String repoName;
    private String repoOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_issues);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.context = this;
        this.listIssueHandler = new MyHandle();
        this.repoName = getIntent().getStringExtra("repoName");
        this.repoOwner = getIntent().getStringExtra("repoOwner");

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

    public class MyHandle extends Handler {
        public MyHandle() {

        }

        @Override
        public void handleMessage(Message msg) {
            ArrayList<Issue> issuesTemp = new ArrayList<>();
            JSONArray jsonArray = (JSONArray) msg.obj;

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    Issue issue = new Issue();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    issue.setNome(jsonObject.getString("nome"));
                    issue.setNumero(jsonObject.getInt("numero"));
                    issue.setDescricao(jsonObject.getString("descricao"));
                    issue.setNomeAutor(jsonObject.getString("nomeAutor"));
                    issue.setFotoAutor(jsonObject.getString("fotoAutor"));

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            issues.clear();
            issues.addAll(issuesTemp);
            atualizarListView();
        }
    }

    private void atualizarListView() {
        ListView listView = (ListView) findViewById(R.id.listViewIssuesList);

        adapter = new IssueAdapter(
                issues,
                this,
                this.repoName,
                this.repoOwner
        );

        listView.setAdapter(adapter);
    }
}
