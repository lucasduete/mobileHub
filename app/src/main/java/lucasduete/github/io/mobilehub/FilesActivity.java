package lucasduete.github.io.mobilehub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.squareup.picasso.Downloader;

import lucasduete.github.io.mobilehub.manager.MenuManage;
import lucasduete.github.io.mobilehub.services.DownloadService;

public class FilesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String repoName = null;
    String repoOwner = null;
    private Context context;
//    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        this.context = this;
        this.repoName = getIntent().getStringExtra("repoName");
        this.repoOwner = getIntent().getStringExtra("repoOwner");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button issuesButton = findViewById(R.id.buttonIssues);
        issuesButton.setOnClickListener((View view) -> {
            Intent intent = new Intent(context, ListIssueActivity.class);
            startActivity(intent);
        });

        Button repositoryButton = findViewById(R.id.buttonRepository);
        repositoryButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, RepositoryActivity.class);
            finish();
            startActivity(intent);
        });

        Button downloadButton = findViewById(R.id.buttonDownload);
        downloadButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, DownloadService.class);

            intent.putExtra("repoName", this.repoName);
            intent.putExtra("repoOwner", this.repoOwner);

            startService(intent);
        });

//        ListView listView = (ListView) findViewById(R.id.listViewFiles);
//        ArrayList<String> arrayFiles = new ArrayList<>();
//        arrayFiles.addAll(Arrays.asList(getResources().getStringArray(R.array.static_list_files)));
//
//        adapter = new ArrayAdapter<String>(
//                FilesActivity.this,
//                android.R.layout.simple_list_item_1,
//                arrayFiles
//        );
//
//        listView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        WebView webView = findViewById(R.id.webViewFiles);
        webView.loadUrl(String.format("https://github.com/%s/%s", repoOwner, repoName));
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

        if (id == R.id.action_settings) return true;

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
}
