package lucasduete.github.io.mobilehub.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class LoginService extends Service {

    public LoginService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(() -> {

        }).start();

        return Service.START_NOT_STICKY;
    }
}
