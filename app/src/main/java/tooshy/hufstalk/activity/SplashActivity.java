package tooshy.hufstalk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import tooshy.hufstalk.R;

/**
 * Created by USER on 2016-03-29.
 */
public class SplashActivity extends Activity {
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slashlayout);
        final Button login = (Button) findViewById(R.id.login);
        findViewById(R.id.login).setOnClickListener(mClickListener);

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
               login.setVisibility(View.VISIBLE);
            }
        }, 2000);
    }
    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
