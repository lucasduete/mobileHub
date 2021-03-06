package lucasduete.github.io.mobilehub;

import android.content.Context;
import android.content.Intent;
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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Downloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import lucasduete.github.io.mobilehub.manager.MenuManage;
import lucasduete.github.io.mobilehub.models.Repository;
import lucasduete.github.io.mobilehub.services.DownloadService;
import lucasduete.github.io.mobilehub.utils.ConstManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

        new RepositoryTask().execute(repoName);

        WebView webView = findViewById(R.id.webViewFiles);
        webView.loadUrl(String.format("https://github.com/%s?files=1", repoName));
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

    private class RepositoryTask extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            Log.d(ConstManager.TAG, "Chegou na Task de Repository");

            String repoName = strings[0];

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(String.format("%s/repositories/%s", ConstManager.URL_BASE, repoName))
                    .get()
                    .build();

            JSONObject jsonObject = null;

            try {
                Response response = client.newCall(request).execute();
                jsonObject = new JSONObject(response.body().string());
                Log.d(ConstManager.TAG, "\nObjeto: " + jsonObject.toString());

            } catch (IOException ex) {
                Log.d(ConstManager.TAG, "Deu pau na conxão");
                ex.printStackTrace();
            } catch (JSONException ex) {
                Log.d(ConstManager.TAG, "Deu pau na conversão de Json");
                ex.printStackTrace();
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            TextView textView;
            Repository repository = new Repository();

            try {
                repository.setStars(jsonObject.getInt("stars"));
                repository.setForks(jsonObject.getInt("forks"));
                repository.setNome(jsonObject.getString("nome"));
                repository.setFoto(jsonObject.getString("fotoAutor"));
                repository.setDescricao(jsonObject.getString("descricao"));
                repository.setNomeAutor(jsonObject.getString("nomeAutor"));

            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(context, "Erro ao Recupear as Informações.", Toast.LENGTH_SHORT).show();
                return;
            }

            textView = findViewById(R.id.textViewRepositoryName);
            textView.setText(repository.getNome());

            textView = findViewById(R.id.textViewRepositoryDescription);
            if (repository.getDescricao().equalsIgnoreCase("null"))
                textView.setText("(Nenhuma Descrição Fornecida)");
            else
                textView.setText(repository.getDescricao());

            textView = findViewById(R.id.textViewNumberStras);
            textView.setText(String.valueOf(repository.getStars()));

            textView = findViewById(R.id.textViewNumberForks);
            textView.setText(String.valueOf(repository.getForks()));

        }
    }
}
