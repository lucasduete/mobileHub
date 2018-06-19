package lucasduete.github.io.mobilehub;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import lucasduete.github.io.mobilehub.services.LoginService;
import lucasduete.github.io.mobilehub.utils.ConstManager;

public class LoginActivity extends AppCompatActivity {

    public static MyHandle loginHandle;
    private Context context;
    private String url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginHandle = new MyHandle();
        this.context = this;

        Button buttonOauth = (Button) findViewById(R.id.buttonOauth);
        buttonOauth.setOnClickListener((view) -> callLoginSerice(LoginService.MODE_OAUTH));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri data = getIntent().getData();
        if (data != null) {
            String code = data.getQueryParameter("code");
            Log.d(ConstManager.TAG, code);

            callLoginSerice(LoginService.MODE_AUTHORIZATE);
        } else
            Log.d(ConstManager.TAG, "\nSem code ainda");
    }

    private void callLoginSerice(int operation) {
        Intent intent = new Intent(context, LoginService.class);
        intent.putExtra("mode", operation);

        if (operation == LoginService.MODE_AUTHORIZATE)
            intent.putExtra("code",
                    getIntent().getData().getQueryParameter("code")
            );

        startService(intent);
    }

    public class MyHandle extends Handler {
        public MyHandle() {

        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(ConstManager.TAG, "Chegou no Handle");

            switch (msg.what) {
                case LoginService.MODE_BASIC:
                    Boolean result = (Boolean) msg.obj;
                    Log.d(ConstManager.TAG, String.format("O valor foi: %s", result.toString()));
                    if (result) {
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "Deu ruim no Login", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case LoginService.MODE_OAUTH:
                    url = (String) msg.obj;
                    Log.d(ConstManager.TAG, "URL aqui: " + url);

                    Intent intentBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intentBrowser);

                    break;
                case LoginService.MODE_AUTHORIZATE:
                    String token = (String) msg.obj;
                    Log.d(ConstManager.TAG, token);

                    Intent intentMainActivity = new Intent(context, MainActivity.class);
                    finish();
                    startActivity(intentMainActivity);

                    break;
            }

        }
    }
}
