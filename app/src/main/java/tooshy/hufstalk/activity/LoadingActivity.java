package tooshy.hufstalk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import tooshy.hufstalk.R;

/**
 * Created by seunghyun on 2016-04-09.
 */
public class LoadingActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);
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
