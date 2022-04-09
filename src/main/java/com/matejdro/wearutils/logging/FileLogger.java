package com.matejdro.wearutils.logging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import kotlin.ExceptionsKt;
import timber.log.Timber;

public class FileLogger extends Timber.AppTaggedDebugTree {
    @SuppressLint("StaticFieldLeak")
    // There is supposedly no memory leak if we only store global application context .
    private static FileLogger instance;

    public static FileLogger getInstance(Context context) {
        if (instance == null) {
            instance = new FileLogger(context.getApplicationContext());
        }

        return instance;
    }

    private static final String PREFERENCES_NAME = "file_log";
    private static final String CURRENT_FILE_PREFERENCE = "CurrentFile";

    private static final int MESSAGE_FLUSH_LOG = 0;
    private static final int FLUSH_DELAY = 3000;

    private static final int NUM_FILES = 4;
    private static final int MAX_LOG_FILE_SIZE = 30000;

    private static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("MM.dd HH:mm:ss.SSS", Locale.getDefault());

    private final SharedPreferences preferences;
    private final Handler flushHandler;

    private BufferedWriter writer;
    private File currentFile;

    private final File logsFolder;

    public FileLogger(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        logsFolder = new File(context.getCacheDir(), "logs");
        flushHandler = new FlushHandler(this);
    }

    public boolean isActive() {
        return writer != null;
    }

    public File getLogsFolder() {
        return logsFolder;
    }

    public synchronized void activate() {
        int currentFileIndex = preferences.getInt(CURRENT_FILE_PREFERENCE, 0);
        openFile(currentFileIndex, true);

        checkCurrentFileSize();
    }

    private void openFile(int fileIndex, boolean append) {
        if (!logsFolder.exists()) {
            if (!logsFolder.mkdir()) {
                throw new RuntimeException("Cannot create logging folder!");
            }
        }

        String filename = "log_" + fileIndex + ".log";
        currentFile = new File(logsFolder, filename);
        try {
            writer = new BufferedWriter(new FileWriter(currentFile, append));
        } catch (IOException ignored) {
            writer = null;
        }

        preferences.edit().putInt(CURRENT_FILE_PREFERENCE, fileIndex).apply();
    }

    private boolean checkCurrentFileSize() {
        if (currentFile.length() > MAX_LOG_FILE_SIZE) {
            flushHandler.removeMessages(MESSAGE_FLUSH_LOG);

            try {
                writer.close();
                openNextFile();
            } catch (IOException ignored) {
                writer = null;
                return false;
            }

            return false;
        }

        return true;
    }

    private void openNextFile() {
        int currentFileIndex = preferences.getInt(CURRENT_FILE_PREFERENCE, 0);
        currentFileIndex = (currentFileIndex + 1) % NUM_FILES;
        openFile(currentFileIndex, false);
    }

    @Override
    protected synchronized void log(int priority, String tag, String message, Throwable throwable) {
        if (!isActive()) {
            return;
        }

        try {
            Calendar calendar = Calendar.getInstance();
            writer.write(
                    getLogPriorityAbbreviation(priority) + " " +
                            LOG_DATE_FORMAT.format(calendar.getTime()) + " [" +
                            tag + "] " +
                            message
            );
            writer.newLine();

            if (throwable != null) {
                writer.write(ExceptionsKt.stackTraceToString(throwable));
            }

            flushHandler.removeMessages(MESSAGE_FLUSH_LOG);

            if (checkCurrentFileSize()) {
                // Batch buffer flushes together
                flushHandler.sendEmptyMessageDelayed(MESSAGE_FLUSH_LOG, FLUSH_DELAY);
            }
        } catch (IOException ignored) {
            //noinspection EmptyCatchBlock
            try {
                writer.close();
            } catch (IOException ignored2) {
            }
            writer = null;
        }
    }

    private void flushLog() {
        if (!isActive()) {
            return;
        }

        try {
            writer.flush();
        } catch (IOException e) {
            writer = null;
            e.printStackTrace();
        }
    }

    private static char getLogPriorityAbbreviation(int priority) {
        switch (priority) {
            case Log.ASSERT:
                return 'A';
            case Log.DEBUG:
                return 'D';
            case Log.ERROR:
                return 'E';
            case Log.INFO:
                return 'I';
            case Log.WARN:
                return 'W';
            default:
                return 'V';
        }
    }

    public synchronized void deactivate() {
        if (writer == null) {
            return;
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        writer = null;

    }

    private static class FlushHandler extends Handler {
        private final WeakReference<FileLogger> fileLogReference;

        public FlushHandler(FileLogger fileLogger) {
            this.fileLogReference = new WeakReference<>(fileLogger);
        }

        @Override
        public void handleMessage(Message msg) {
            FileLogger fileLogger = fileLogReference.get();
            if (fileLogger == null) {
                return;
            }

            switch (msg.what) {
                case MESSAGE_FLUSH_LOG:
                    fileLogger.flushLog();
                    break;
            }
        }
    }
}
