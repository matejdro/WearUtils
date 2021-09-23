package pl.tajchert.exceptionwear;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import pl.tajchert.exceptionwear.wear.WearExceptionTools;

/**
 * Updated ExceptionWear library from https://github.com/tajchert/ExceptionWear
 * @author tajchert
 *
 * To use this library, add following code to the manifest:
 *
 * Phone side:
 * {@code <service
            android:name="pl.tajchert.exceptionwear.ExceptionDataListenerService"
            tools:node="replace"  >
            <intent-filter>
                <action android:name="com.google.android.gms.wearable.MESSAGE_RECEIVED" />
                <data android:scheme="wear" android:host="*" android:pathPrefix="/exceptionwear/wear_error" />
            </intent-filter>
        </service>}
  * Watch side:
 *
 * {@code <service
    android:name="pl.tajchert.exceptionwear.ExceptionService"
    android:process=":error"
    tools:node="replace"/>}
 */

@SuppressLint("Registered")
public class ExceptionDataListenerService extends WearableListenerService {
    private static ExceptionWearHandler mExceptionWearHandler;

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if (messageEvent.getPath().equals("/exceptionwear/wear_error")) {
            DataMap map = DataMap.fromByteArray(messageEvent.getData());
            readException(map);
        }
    }

    private void readException(DataMap map) {
        ByteArrayInputStream bis = new ByteArrayInputStream(map.getByteArray("stack_trace"));
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            StackTraceElement[] throwableStackTrace = (StackTraceElement[]) ois.readObject();

            String message = map.getString("message");
            String type = map.getString("type");

            Exception throwableException = WatchException.create(message, type, throwableStackTrace);

            if (mExceptionWearHandler != null) {
                mExceptionWearHandler.handleException(throwableException, map);
            } else {
                Log.e(WearExceptionTools.EXCEPTION_WEAR_TAG, "Error from Wear: " + throwableException.getMessage()
                        + ", manufacturer: " + map.getString("manufacturer")
                        + ", model: " + map.getString("model")
                        + ", product: " + map.getString("product")
                        + ", board: " + map.getString("board")
                        + ", fingerprint: " + map.getString("fingerprint")
                        + ", api_level: " + map.getString("api_level"));
                throw new RuntimeException(throwableException);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setHandler(ExceptionWearHandler exceptionWearHandler){
        mExceptionWearHandler = exceptionWearHandler;
    }
}