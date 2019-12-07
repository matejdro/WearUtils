package com.matejdro.wearutils.logging;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.Wearable;
import com.matejdro.wearutils.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import timber.log.Timber;

@SuppressLint("Registered") //Transmitter does not need to be registered if it is not used
public class LogTransmitter extends IntentService {
    public static final String EXTRA_TARGET_NODE_ID = "TargetNode";
    public static final String EXTRA_TARGET_PATH = "TargetPath";
    public static final String LOG_RETRIEVAL_CHANNEL = "LOG_RETRIEVAL";

    public LogTransmitter() {
        super("WearLogTransmitter");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.d("SENDLOGS TRANSMITTER");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel logRetrievalChannel = new NotificationChannel(LOG_RETRIEVAL_CHANNEL,
                    getString(R.string.logs),
                    NotificationManager.IMPORTANCE_MIN);

            //noinspection ConstantConditions
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(logRetrievalChannel);
        }

        @SuppressLint("WrongConstant") Notification foregroundNotification
                = new NotificationCompat.Builder(this, LOG_RETRIEVAL_CHANNEL)
                .setContentText(getString(R.string.logs))
                .setContentTitle(getString(R.string.logs))
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(9999, foregroundNotification);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        googleApiClient.blockingConnect();

        String targetNode = intent.getStringExtra(EXTRA_TARGET_NODE_ID);
        String targetPath = intent.getStringExtra(EXTRA_TARGET_PATH);

        ChannelApi.OpenChannelResult openChannelResult = Wearable.ChannelApi.openChannel(googleApiClient, targetNode, targetPath).await();
        if (!openChannelResult.getStatus().isSuccess()) {
            Timber.e("Log transmitting failed! %s", openChannelResult.getStatus().getStatusMessage());
            return;
        }

        Channel channel = openChannelResult.getChannel();
        Channel.GetOutputStreamResult getOutputStreamResult = channel.getOutputStream(googleApiClient).await();
        if (!getOutputStreamResult.getStatus().isSuccess()) {
            Timber.e("Log transmitting failed! %s", getOutputStreamResult.getStatus().getStatusMessage());
            return;
        }

        DataOutputStream outputStream = new DataOutputStream(getOutputStreamResult.getOutputStream());

        FileLogger.getInstance(this).deactivate();

        try {
            File[] logFiles = FileLogger.getInstance(this).getLogsFolder().listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.matches("log_[0-9]+\\.log");
                }
            });

            outputStream.writeInt(logFiles.length);

            for (File file : logFiles) {
                writeFile(file, outputStream);
            }

        } catch (IOException e) {
            Timber.e(e, "Log transmitting failed!");
        } finally {
            try {
                outputStream.close();
            } catch (IOException ignored) {
            }
            channel.close(googleApiClient).await();
            googleApiClient.disconnect();

            FileLogger.getInstance(this).activate();
        }
    }

    private void writeFile(File file, DataOutputStream outputStream) throws IOException {
        int fileSize = (int) file.length();
        outputStream.writeInt(fileSize);

        byte[] buffer = new byte[1024];
        FileInputStream inputStream = new FileInputStream(file);
        int readBytes;
        while ((readBytes = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, readBytes);
        }

        inputStream.close();
    }
}
