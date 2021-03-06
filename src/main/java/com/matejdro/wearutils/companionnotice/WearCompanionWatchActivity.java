package com.matejdro.wearutils.companionnotice;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Wearable;

public abstract class WearCompanionWatchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, ResultCallback<CapabilityApi.GetCapabilityResult> {
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
        Wearable.CapabilityApi.getCapability(googleApiClient, getPhoneAppPresenceCapability(), CapabilityApi.FILTER_ALL)
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

        startActivity(new Intent(this, PhoneAppNoticeActivity.class));
        finish();
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public abstract String getPhoneAppPresenceCapability();
}
