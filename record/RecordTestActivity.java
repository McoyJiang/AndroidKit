import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by Danny on 17/12/28.
 */

public class RecordTestActivity extends AppCompatActivity {

    private static final String TAG = "RecordTestActivity";

    private String outPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/temp_audio.wav";

    private WavRecorder wavRecorder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_record);

        wavRecorder = new WavRecorder();

        wavRecorder.setOutputFile(outPath);

    }

    public void record(View view) {
        Log.i(TAG, "record");
        wavRecorder.prepare();

        wavRecorder.start();
    }

    public void stop(View view) {
        Log.i(TAG, "stop");
        wavRecorder.stop();
    }
}
