package tooshy.hufstalk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import tooshy.hufstalk.R;

/**
 * Created by USER on 2016-03-29.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slashlayout);

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), FaceloginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

}
