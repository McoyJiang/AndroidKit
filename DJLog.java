package com.ef.smallstar.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Danny 姜 on 6/23/2015.
 */
public class DJLog {

    private final static boolean TURNED_ON = true;
    private final static boolean LOG_ON_SDCARD = true;

    private volatile static DJLog instance;
    private LogManager logManager = new LogManager();

    private DJLog() {}

    public static DJLog newInstance() {
        if (instance == null) {
            synchronized (DJLog.class) {
                if (instance == null)
                    instance = new DJLog();
            }
        }
        return instance;
    }

    public static void d(String tag, String msg) {
        if (TURNED_ON) android.util.Log.d(tag, msg == null ? "" : msg);
    }

    public static void d(String tag, String msg, Throwable throwable) {
        if (TURNED_ON) android.util.Log.d(tag, msg == null ? "" : msg, throwable);
    }

    public static void e(String tag, String msg) {
        if (TURNED_ON) android.util.Log.e(tag, msg == null ? "" : msg);
    }

    public static void e(String tag, String msg, Throwable throwable) {
        if (TURNED_ON) android.util.Log.e(tag, msg == null ? "" : msg, throwable);
    }

    public static void w(String tag, String msg) {
        if (TURNED_ON) android.util.Log.w(tag, msg == null ? "" : msg);
    }

    public static void w(String tag, String msg, Throwable throwable) {
        if (TURNED_ON) android.util.Log.w(tag, msg == null ? "" : msg, throwable);
    }

    public static void i(Context context, String tag, String msg) {
        if (TURNED_ON) android.util.Log.i(tag, msg == null ? "" : msg);
    }

    public static void i(Context context, String tag, String msg, Throwable throwable) {
        if (TURNED_ON) android.util.Log.i(tag, msg == null ? "" : msg, throwable);
    }

    public void toSdcard(Context context, String tag, String msg) {
        if (TURNED_ON) {
            android.util.Log.d(tag, msg == null ? "" : msg);
        }
        if (LOG_ON_SDCARD) {
            logManager.saveLog(context, msg == null ? "" : msg);
        }
    }

    public void toSdcard(Context context, String tag, String msg, Throwable throwable) {
        if (TURNED_ON) android.util.Log.i(tag, msg == null ? "" : msg, throwable);
        if (LOG_ON_SDCARD) logManager.saveLog(context, msg == null ? "" : msg);
    }

    public static void v(String tag, String msg) {
        if (TURNED_ON) android.util.Log.v(tag, msg == null ? "" : msg);
    }

    public static void v(String tag, String msg, Throwable throwable) {
        if (TURNED_ON) android.util.Log.v(tag, msg == null ? "" : msg, throwable);
    }

    public void sendLogs(Context context) {
        logManager.sendLogs(context);
    }

    private class LogManager {

        private BlockingQueue<FileLoader> tasks = new LinkedBlockingQueue<>();

        public void sendLogs(Context context) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ext.danny.jiang@ef.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Logs for " + new Date(System.currentTimeMillis()));

            Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "mediahub.txt"));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(intent, "Send logs to developer via email"));

        }

        public synchronized void saveLog(Context context, String str) {
            FileLoader fileLoader = new FileLoader(context, str);
            tasks.add(fileLoader);

            fileLoader = tasks.peek();
            if (fileLoader != null && fileLoader.getStatus() == AsyncTask.Status.PENDING) {
                startNewTask(fileLoader);
            }
        }

        public void onLoadComplete() {
            try {
                tasks.take(); // remove handled task
                startNewTask(tasks.peek()); // get a new one
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void startNewTask(FileLoader task) {
            if (task != null) {
                task.execute();
            }
        }
    }

    private class FileLoader extends AsyncTask<Void, Void, Void>{

        private Context context;
        private String string;

        public FileLoader(Context context, String string) {
            this.context = context;
            this.string = string;
        }

        @Override
        protected Void doInBackground(Void[] params) {
            if (context == null) return null;
            try {
                FileManager fileManager = new FileManager(context);
                File root = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                if (fileManager.isExternalStorageWritable()
                        && root != null && (root.mkdirs() || root.isDirectory())) {

                    File logs = new File(root, "small_star.txt");
                    if (!logs.exists()) logs.createNewFile();
                    /**if (needToCreateNewFile(logs)) {
                        logs.delete();
                        logs.createNewFile();
                    }*/

                    PrintWriter printWriter = new PrintWriter(logs, "UTF-8");
                    printWriter.println(string == null ? "" : string);
                    printWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            logManager.onLoadComplete();
        }

        private boolean needToCreateNewFile(File file) {
            final int maxSize = 10 * 1024 * 1024; // 10MB is maximum for logs
            return file.length() >= maxSize;
        }
    }
}
