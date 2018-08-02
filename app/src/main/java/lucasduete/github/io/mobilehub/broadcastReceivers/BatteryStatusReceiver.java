package lucasduete.github.io.mobilehub.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import lucasduete.github.io.mobilehub.services.DownloadService;

public class BatteryStatusReceiver extends BroadcastReceiver {

    private final DownloadService downloadService;

    public BatteryStatusReceiver(DownloadService downloadService) {
        super();
        this.downloadService = downloadService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Thread thread = downloadService.downloadThread;
        switch (intent.getAction()) {
            case Intent.ACTION_BATTERY_LOW:
                if (!thread.isInterrupted()) thread.interrupt();
                break;
            case Intent.ACTION_BATTERY_OKAY:
                if (thread.isInterrupted()) thread.start();
                break;
        }
    }

}
