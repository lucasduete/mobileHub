package lucasduete.github.io.mobilehub.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
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
        final Toast toast = Toast.makeText(this, "Error", Toast.LENGTH_SHORT);

        final BatteryStatusReceiver batteryReceiver = new BatteryStatusReceiver(this.downloadThread);

        this.downloadThread = new Thread(() -> {
            final String repoName = intent.getStringExtra("repoName");
            final String repoOwner = intent.getStringExtra("repoOwner");

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(String.format("https://codeload.github.com/%s/zip/master", repoName))
                    .get()
                    .build();

            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                toast.setText("Impossível Salvar Arquivo");
                toast.show();
                return;
            }

            try {
                toast.setText("Download Iniciado");
                toast.show();

                Response response = client.newCall(request).execute();

                String fileName = repoName.replaceFirst("/", "_").concat("-master.zip");
                String filePath = String.format("%s/%s", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

                FileOutputStream outputStream = new FileOutputStream(filePath);

                outputStream.write(response.body().bytes());

                outputStream.flush();
                outputStream.close();

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                toast.setText("Download Finalizado");
                toast.show();
            } catch (IOException ex) {

                ex.printStackTrace();

                toast.setText("Não foi possível baixar o repositório");
                toast.show();;
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
