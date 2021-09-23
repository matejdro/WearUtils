package pl.tajchert.exceptionwear;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class WatchException extends Exception {
    public WatchException(String message) {
        super(message);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public WatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static WatchException create(String message, String type, StackTraceElement[] stackTrace) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String mergedMessage = type + ": " + message;
            WatchException e = new WatchException(mergedMessage);
            e.setStackTrace(stackTrace);

            return e;
        } else {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append(type);
            messageBuilder.append(": ");
            messageBuilder.append(message);
            messageBuilder.append('\n');

            for (StackTraceElement element : stackTrace) {
                messageBuilder.append(element.toString());
                messageBuilder.append('\n');
            }

            return new WatchException(messageBuilder.toString());
        }
    }
}
