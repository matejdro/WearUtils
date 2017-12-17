package com.matejdro.wearutils.logging;

import android.support.annotation.WorkerThread;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Channel;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import timber.log.Timber;

public class LogReceiver {
    private final GoogleApiClient googleApiClient;
    private final String logSuffix;

    public LogReceiver(GoogleApiClient googleApiClient, String logSuffix) {
        this.googleApiClient = googleApiClient;
        this.logSuffix = logSuffix;
    }

    @WorkerThread
    public boolean receiveLogs(Channel channel) {
        Channel.GetInputStreamResult getInputStreamResult = channel.getInputStream(googleApiClient).await();
        if (!getInputStreamResult.getStatus().isSuccess()) {
            Timber.e("Log receiving failed! %s", getInputStreamResult.getStatus().getStatusMessage());
            return false;
        }

        DataInputStream inputStream = new DataInputStream(getInputStreamResult.getInputStream());
        File logsFolder = FileLogger.getInstance(googleApiClient.getContext()).getLogsFolder();
        //noinspection ResultOfMethodCallIgnored
        logsFolder.mkdirs();

        try
        {
            int numFiles = inputStream.readInt();
            for (int i = 0; i < numFiles; i++) {
                String filename = "log_" + logSuffix + "_" + i + ".log";
                File file = new File(logsFolder, filename);
                readFile(inputStream, file);
            }
        }
        catch (IOException e) {
            Timber.d(e, "Log receiving error");
            return false;
        } finally {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }

        return true;
    }

    private void readFile(DataInputStream inputStream, File file) throws IOException {
        int fileSizeLeft = inputStream.readInt();

        byte[] buffer = new byte[1024];
        FileOutputStream outputStream = new FileOutputStream(file);

        while (fileSizeLeft > 0) {
            int numToRead = Math.min(buffer.length, fileSizeLeft);
            int readBytes = inputStream.read(buffer, 0, numToRead);

            fileSizeLeft -= readBytes;

            outputStream.write(buffer, 0, readBytes);
        }

        outputStream.close();

    }
}
