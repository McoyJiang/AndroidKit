import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Danny on 17/12/26.
 */

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";
    // 开始的倒数锁
    ResetableCountdownLatch begin = new ResetableCountdownLatch(1);

    // 结束的倒数锁
    ResetableCountdownLatch end = new ResetableCountdownLatch(10);

    // 十名选手
    final ExecutorService exec = Executors.newFixedThreadPool(10);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

    }

    public void start(View view) {
        Log.e(TAG,"Game Start");
        // begin减一，开始游戏
        begin.countDown();
    }

    public void prepare(View view) {
        Log.e(TAG, "prepared");
        for (int index = 0; index < 10; index++) {
            final int NO = index + 1;
            exec.submit(new GameBeginRunnable(NO));
        }

        new Thread(new GameOverRunnable()).start();
    }

    class GameBeginRunnable implements Runnable {

        protected final int number;

        public GameBeginRunnable(int number) {
            this.number = number;
        }
        @Override
        public void run() {
            // 如果当前计数为零，则此方法立即返回。
            // 等待
            try {
                begin.await();
                Log.e(TAG,"No." + number + " 起跑");
                Thread.sleep((long) (Math.random() * 10000));
                Log.e(TAG,"No." + number + " arrived");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 每个选手到达终点时，end就减一
                end.countDown();
            }
        }
    }


    class GameOverRunnable implements Runnable {
        @Override
        public void run() {
            // 等待end变为0，即所有选手到达终点
            try {

                end.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Log.e(TAG,"Game Over");
                //exec.shutdown();

                begin.reset();
                end.reset();
            }
        }
    }

}
