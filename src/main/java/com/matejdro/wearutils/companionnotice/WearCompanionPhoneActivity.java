package com.matejdro.wearutils.companionnotice;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.wearable.intent.RemoteIntent;
import com.matejdro.wearutils.R;

public abstract class WearCompanionPhoneActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, ResultCallback<CapabilityApi.GetCapabilityResult> {
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.CapabilityApi.getCapability(googleApiClient, getWatchAppPresenceCapability(), CapabilityApi.FILTER_ALL)
                .setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onResult(@NonNull CapabilityApi.GetCapabilityResult getCapabilityResult) {
        boolean installedOnWatch = !getCapabilityResult.getCapability().getNodes().isEmpty();
        onWatchAppInstalledResult(installedOnWatch);
    }

    protected void onWatchAppInstalledResult(boolean watchAppInstalled) {
        if (watchAppInstalled) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.no_watch_app_title)
                .setMessage(R.string.no_watch_app_description)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.no_watch_app_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openWatchPlayStorePage();
                    }
                })
                .show();
    }

    protected void openWatchPlayStorePage()  {
        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
        playStoreIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        playStoreIntent.setData(Uri.parse("market://details?id=" + getPackageName()));

        RemoteIntent.startRemoteActivity(this, playStoreIntent, null);
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public abstract String getWatchAppPresenceCapability();
}
