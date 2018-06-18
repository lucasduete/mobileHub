package lucasduete.github.io.mobilehub;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import lucasduete.github.io.mobilehub.utils.ConstManager;

public class LoginActivity extends AppCompatActivity {

    public static MyHandle loginHandle;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.context = this;
    }

    private class MyHandle extends Handler {
        public MyHandle() {

        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(ConstManager.TAG, "Chegou no Handle");
            Boolean result = (Boolean) msg.obj;

            Log.d(ConstManager.TAG, String.format("O valor foi: %s", result.toString()));

            if (result) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            } else
                Toast.makeText(context, "Deu ruim no Login", Toast.LENGTH_SHORT).show();
        }
    }
}
