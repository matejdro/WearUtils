package com.matejdro.wearutils.messages;

import androidx.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class DataUtils {
    @Nullable
    public static byte[] getByteArrayAsset(@Nullable DataItemAsset asset, GoogleApiClient googleApiClient) {
        if (asset == null || !googleApiClient.isConnected()) {
            return null;
        }

        InputStream inputStream = Wearable.DataApi.getFdForAsset(googleApiClient, asset).await(1, TimeUnit.SECONDS).getInputStream();

        byte[] data = readFully(inputStream);
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }

        return data;
    }

    @Nullable
    private static byte[] readFully(InputStream in) {
        if (in == null) {
            return null;
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        int bytesRead;
        byte[] buffer = new byte[1024];

        try {
            while ((bytesRead = in.read(buffer, 0, buffer.length)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException ignored) {
            return null;
        }

        return outStream.toByteArray();
    }


}
