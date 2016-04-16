package tooshy.hufstalk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import tooshy.hufstalk.R;

/**
 * Created by USER on 2016-04-09.
 */
public class TopicActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_activity);
        final int[] topicount = {0};
        final int[] count ={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        Button[] btn = new Button[20];
        final Integer[] btnID = {
                R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn10, R.id.btn11, R.id.btn12, R.id.btn13,
                R.id.btn14, R.id.btn15, R.id.btn16, R.id.btn17, R.id.btn18, R.id.btn19, R.id.btn20
        };
        for(int i=0; i < btnID.length; i++) {
            final int idx = i;
            btn[i] = (Button)findViewById(btnID[i]);
            btn[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    count[idx] = 1;
                    topicount[0]=0;
                    for (int j = 0; j < btnID.length; j++) {
                        topicount[0] = topicount[0] + count[j];
                    }
                    if (topicount[0] == 3) {
                        System.out.println("pop");
                        Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });


        }

        Button btn_topic_next = (Button)findViewById(R.id.btn_topic_next);
        btn_topic_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

