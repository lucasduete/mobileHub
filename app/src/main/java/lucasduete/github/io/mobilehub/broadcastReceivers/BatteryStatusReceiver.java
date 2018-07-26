package lucasduete.github.io.mobilehub.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryStatusReceiver extends BroadcastReceiver {

    private final Thread thread;

    public BatteryStatusReceiver(Thread thread) {
        super();
        this.thread = thread;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BATTERY_LOW:
                if (!this.thread.isInterrupted()) this.thread.interrupt();
            case Intent.ACTION_BATTERY_OKAY:
                if (!this.thread.isAlive()) this.thread.start();
        }
    }

}
