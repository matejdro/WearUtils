package com.matejdro.wearutils.logging;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.Wearable;
import com.matejdro.wearutils.R;
import com.matejdro.wearutils.messages.MessagingUtils;
import com.matejdro.wearutils.messages.SingleChannelReceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import timber.log.Timber;

/**
 * AsyncTask that retrieves logs from the watch, combines them with phone logs into zip file and
 * sends them to the email client.
 */
public class LogRetrievalTask extends AsyncTask<Void, Void, Boolean> {
    private WeakReference<Context> contextReference;
    private String sendLogsMessagePath;
    private String supportMail;
    private File targetFile;

    private WeakReference<ProgressDialog> loadingDialogReference;

    public LogRetrievalTask(Context context,
                            String sendLogsMessagePath,
                            String supportMail,
                            File targetFile) {
        this.contextReference = new WeakReference<>(context);
        this.sendLogsMessagePath = sendLogsMessagePath;
        this.supportMail = supportMail;
        this.targetFile = targetFile;
    }

    @Override
    protected void onPreExecute() {
        Context context = contextReference.get();
        if (context == null) {
            return;
        }

        loadingDialogReference = new WeakReference<>(
                ProgressDialog.show(context, null, context.getString(R.string.getting_watch_logs), true)
        );
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Context context = contextReference.get();
        if (context == null) {
            return false;
        }

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult = googleApiClient.blockingConnect();
        if (!connectionResult.isSuccess()) {
            GoogleApiAvailability.getInstance().showErrorNotification(context, connectionResult);
            return false;
        }

        SingleChannelReceiver singleChannelReceiver = new SingleChannelReceiver(googleApiClient);

        Wearable.MessageApi.sendMessage(googleApiClient,
                MessagingUtils.getOtherNodeId(googleApiClient),
                sendLogsMessagePath,
                null).await();

        Channel channel;
        try {
            channel = singleChannelReceiver.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            return false;
        } catch (TimeoutException ignored) {
            return false;
        }

        boolean success = new LogReceiver(googleApiClient, "watch").receiveLogs(channel);

        channel.close(googleApiClient).await();
        googleApiClient.disconnect();

        if (!success) {
            return false;
        }

        FileLogger.getInstance(context).deactivate();

        File logsFolder = FileLogger.getInstance(context).getLogsFolder();
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(targetFile));

            for (File file : logsFolder.listFiles()) {
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOutputStream.putNextEntry(zipEntry);

                byte[] buffer = new byte[1024];
                FileInputStream inputStream = new FileInputStream(file);
                int readBytes;
                while ((readBytes = inputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, readBytes);
                }

                inputStream.close();
                zipOutputStream.closeEntry();
            }

        } catch (Exception e) {
            Timber.e(e, "Zip writing error");
            return false;
        } finally {
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (IOException ignored) {
                }
            }

            FileLogger.getInstance(context).activate();
        }


        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        ProgressDialog loadingDialog = loadingDialogReference.get();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }

        final Context context = contextReference.get();
        if (context == null) {
            return;
        }

        if (success) {
            String filename = targetFile.getName();

            AlertDialog logsInstructionsDialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.logs_attachment)
                    .setMessage(context.getString(R.string.logs_attachment_explanation, filename))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showEmailActivity(context);
                        }
                    })
                    .create();

            logsInstructionsDialog.show();
        } else {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.log_sending_failed)
                    .setMessage(R.string.log_retrieval_failed)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }

    private void showEmailActivity(Context context) {
        try {
            Intent activityIntent = new Intent(Intent.ACTION_SENDTO);
            activityIntent.setType("application/octet-stream");
            activityIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(targetFile));

            activityIntent.setData(Uri.parse("mailto: " + supportMail));
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(activityIntent);
        } catch (ActivityNotFoundException e) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.log_sending_failed)
                    .setMessage(R.string.no_email_app_found)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }
}
