package lucasduete.github.io.mobilehub;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

import lucasduete.github.io.mobilehub.dao.DatabaseHelper;
import lucasduete.github.io.mobilehub.dao.OrmLiteBaseCompactActivity;
import lucasduete.github.io.mobilehub.dao.RepositoryDao;
import lucasduete.github.io.mobilehub.manager.MenuManage;
import lucasduete.github.io.mobilehub.models.Repository;
import lucasduete.github.io.mobilehub.services.DownloadService;
import lucasduete.github.io.mobilehub.utils.ConstManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rb.popview.PopField;

public class RepositoryActivity extends OrmLiteBaseCompactActivity<DatabaseHelper>
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private String repoName = null;
    private String repoOwner = null;

    private String jsonString = "";

    private PopField popField;
    private int count = 0;

    private ImageView bookmarkImageView;
    private Repository myRepository = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository);

        this.context = this;
        this.popField = PopField.attach2Window(this);
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

            intent.putExtra("repoName", this.repoName);
            intent.putExtra("repoOwner", this.repoOwner);

            startActivity(intent);
        });

        Button filesButton = findViewById(R.id.buttonFiles);
        filesButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, FilesActivity.class);

            intent.putExtra("repoName", this.repoName);
            intent.putExtra("repoOwner", this.repoOwner);

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

        this.bookmarkImageView = findViewById(R.id.imageViewBookmark);

        this.bookmarkImageView.setOnClickListener(view -> {

            if (checkPinned()) {
                if (removePinned()) {
                    bookmarkImageView.setImageResource(R.drawable.baseline_bookmark_border_24);
                    popField.popView(bookmarkImageView, bookmarkImageView, true);
                }
                else
                    Toast.makeText(context, "Houve um problema ao DesPinnar este repositório", Toast.LENGTH_SHORT).show();
            } else {

                if (persistPinned()) {
                    bookmarkImageView.setImageResource(R.drawable.baseline_bookmark_24);
                    popField.popView(bookmarkImageView, bookmarkImageView, true);
                }
                else
                    Toast.makeText(context, "Houve um problema ao Pinnar este repositório", Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (checkPinned()) {

            bookmarkImageView.setImageResource(R.drawable.baseline_bookmark_24);
            updateView();
        } else {
            Log.d(ConstManager.TAG, "aqui 1");
            new Thread(() -> {
                while (true) {
                    Log.d(ConstManager.TAG, "aqui 2");
                    new RepositoryTask().execute(repoName);

                    try {
                        Thread.sleep(10000);
                        Log.d(ConstManager.TAG, "aqui 3");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(ConstManager.TAG, "aqui 4");
                    }
                    Log.d(ConstManager.TAG, "aqui 5");
                }
            }).start();

        }
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
            Repository repository = new Repository();

            try {
                repository.setStars(jsonObject.getInt("stars"));
                repository.setForks(jsonObject.getInt("forks"));
                repository.setNome(jsonObject.getString("nome"));
                repository.setFoto(jsonObject.getString("fotoAutor"));
                repository.setDescricao(jsonObject.getString("descricao"));
                repository.setNomeAutor(jsonObject.getString("nomeAutor"));

                myRepository = repository;
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(context, "Erro ao Recupear as Informações.", Toast.LENGTH_SHORT).show();
                myRepository = new Repository();
                return;
            }

            if(!jsonObject.toString().equals(jsonString)) {
                if (count != 0){
                    sendNotification();
                    jsonString = jsonObject.toString();
                }

                count++;
            }
            updateView();
        }
    }

    public void updateView() {
        TextView textView;

        textView = findViewById(R.id.textViewRepositoryName);
        textView.setText(this.myRepository.getNome());

        textView = findViewById(R.id.textViewRepositoryDescription);
        if (this.myRepository.getDescricao().equalsIgnoreCase("null"))
            textView.setText("(Nenhuma Descrição Fornecida)");
        else
            textView.setText(this.myRepository.getDescricao());

        textView = findViewById(R.id.textViewNumberStras);
        textView.setText(String.valueOf(this.myRepository.getStars()));

        textView = findViewById(R.id.textViewNumberForks);
        textView.setText(String.valueOf(this.myRepository.getForks()));
    };

    public boolean persistPinned() {

        if (this.myRepository == null) return false;

        try {
            RepositoryDao repositoryDao = new RepositoryDao(getConnectionSource());

            return repositoryDao.create(myRepository) == 1;
        } catch (SQLException ex) {

            Log.d(ConstManager.TAG, "Deu pau ao Persistir o Pin.");
            ex.printStackTrace();
            return false;
        }

    }

    private boolean removePinned() {

        if (this.myRepository == null) return false;

        try {
            RepositoryDao repositoryDao = new RepositoryDao(getConnectionSource());

            return repositoryDao.deleteById(this.repoName) == 1;
        } catch (SQLException ex) {

            Log.d(ConstManager.TAG, "Deu pau ao Remover o Pin.");
            ex.printStackTrace();
            return false;
        }
    }

    public boolean checkPinned() {

        try {

            RepositoryDao repositoryDao = new RepositoryDao(getConnectionSource());

            Repository repositoryBD = repositoryDao.queryForId(this.repoName);

            if (repositoryBD != null) {
                this.myRepository = repositoryBD;
                return true;
            }

        } catch (SQLException ex) {

            Log.d(ConstManager.TAG, "Deu pau na Verificação do Pin.");
            ex.printStackTrace();
        }

        return false;
    }

    private void sendNotification() {
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Repositório foi atualizado!")
                .setContentText("Repositório atualizado!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notification.vibrate = new long[]{150, 300, 150, 600};
        mNotificationManager.notify(1, notification);

    }
}
