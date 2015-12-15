package wy.rxsync;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.rxsync.ICmdSubScribe;
import com.rxsync.RxMappingProxy;
import com.rxsync.SyncWorker;
import com.rxsync.annotations.Param;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "RxAndroidSamples";
    private EditText mCmdEditText;
    private ICmdSubScribe subScribe;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCmdEditText = (EditText) findViewById(R.id.input_text);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SyncWorker worker = new SyncWorker();
//                List<String> cmds = new ArrayList<>();
//                cmds.add("cmd://www/wangyao/1");
//                cmds.add("cmd://www/wangyao2/2");
//                cmds.add("cmd://www/wangyao3/3");
//                worker.sync(cmds);

                String cmd = mCmdEditText.getText().toString();
                SyncWorker worker = new SyncWorker();
                worker.sync(cmd);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        subScribe = new ICmdSubScribe() {
            @Override
            public void shutdown(String id, String time) {
                Toast.makeText(MainActivity.this, "id:" + id + ",time:"+time, Toast.LENGTH_LONG).show();
            }
        };
        try {
            RxMappingProxy.getInstance().register(subScribe);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        RxMappingProxy.getInstance().unRegister(subScribe);
    }
}
