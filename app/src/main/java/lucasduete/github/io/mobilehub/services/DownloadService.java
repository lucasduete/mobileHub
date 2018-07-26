package lucasduete.github.io.mobilehub.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import lucasduete.github.io.mobilehub.broadcastReceivers.BatteryStatusReceiver;
import lucasduete.github.io.mobilehub.utils.ConstManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadService extends Service {

    private Thread downloadThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final BatteryStatusReceiver batteryReceiver = new BatteryStatusReceiver(this.downloadThread);

        this.downloadThread = new Thread(() -> {
            final String repo = intent.getStringExtra("repo");
            final String owner = intent.getStringExtra("owner");

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(String.format("https://codeload.github.com/%s/%s/zip/master", owner, repo))
                    .get()
                    .build();

            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                Toast.makeText(this, "Impossível Salvar Arquivo", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Response response = client.newCall(request).execute();

                File file = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        String.format("%s/%s-master.zip", owner, repo));
                FileOutputStream outputStream = new FileOutputStream(file);

                outputStream.write(response.body().bytes());

                outputStream.flush();
                outputStream.close();
            } catch (IOException ex) {
                Toast.makeText(this, "Não foi possível baixar o repositório", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }

            unregisterReceiver(batteryReceiver);
        });


        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
        intentFilter.addAction(Intent.ACTION_BATTERY_OKAY);

        registerReceiver(batteryReceiver, intentFilter);


        downloadThread.start();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
