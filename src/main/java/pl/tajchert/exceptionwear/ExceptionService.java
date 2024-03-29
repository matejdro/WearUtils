package pl.tajchert.exceptionwear;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.google.android.gms.wearable.DataMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import pl.tajchert.exceptionwear.wear.SendByteArrayToNode;
import timber.log.Timber;


@SuppressLint("Registered")
public class ExceptionService extends JobIntentService {
    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_STACK_TRACE = "stack_trace";
    private static final String EXTRA_TYPE = "type";

    private ByteArrayOutputStream bos;
    private ObjectOutputStream oos;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        try {
            new SendByteArrayToNode(createExceptionInformation(intent).toByteArray(), ExceptionService.this).start();
        } finally {
            try {
                if (oos != null)
                    oos.close();
            } catch (IOException exx) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException exx) {
                // ignore close exception
            }
        }
    }

    private DataMap createExceptionInformation(Intent intent) {

        bos = new ByteArrayOutputStream();
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(intent.getSerializableExtra(EXTRA_STACK_TRACE));
        } catch (IOException e) {
            Timber.e("createExceptionInformation error while getting exception information.");
        }

        byte[] exceptionData = bos.toByteArray();
        DataMap dataMap = new DataMap();

        // Add a bit of information on the Wear Device to pass a long with the exception
        dataMap.putString("board", Build.BOARD);
        dataMap.putString("fingerprint", Build.FINGERPRINT);
        dataMap.putString("model", Build.MODEL);
        dataMap.putString("manufacturer", Build.MANUFACTURER);
        dataMap.putString("product", Build.PRODUCT);
        dataMap.putString("api_level", Integer.toString(Build.VERSION.SDK_INT));
        dataMap.putString("message", intent.getStringExtra(EXTRA_MESSAGE));
        dataMap.putString("type", intent.getStringExtra(EXTRA_TYPE));

        dataMap.putByteArray("stack_trace", exceptionData);

        return dataMap;
    }

    public static void reportException(Context context, Throwable ex) {
        Intent errorIntent = new Intent();
        errorIntent.putExtra(EXTRA_MESSAGE, ex.getMessage());
        errorIntent.putExtra(EXTRA_TYPE, ex.getClass().getName());
        errorIntent.putExtra(EXTRA_STACK_TRACE, ex.getStackTrace());
        JobIntentService.enqueueWork(context, ExceptionService.class, 7777, errorIntent);
    }
}
