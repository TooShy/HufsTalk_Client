package tooshy.hufstalk.helper;

import android.view.KeyEvent;
import android.view.View;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;

/**
 * Created by Tokirin on 2016. 5. 22..
 */
public class Global {
    private static Global instance;
    public static String SESSION_TOKEN = "";
    public static String HOST_API_PREFIX = "http://125.209.199.214:3000/api/client/";
    public static String API_VERSION = "v1";
    public static Pusher pusher;
    public static synchronized Global getInstance(){
        if(instance == null) instance = new Global();
        return instance;
    }

    public Global(){
        PusherOptions options = new PusherOptions();
        options.setCluster("ap1");
        pusher = new Pusher("f5ad826261aeb8068be6", options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
                System.out.println("Connection State changed!");
            }

            @Override
            public void onError(String s, String s1, Exception e) {
                System.out.println(s);
                System.out.println(s1);
            }
        });

    }


}
