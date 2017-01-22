package pl.tajchert.exceptionwear;


import com.google.android.gms.wearable.DataMap;

public interface ExceptionWearHandler {
    void handleException(Throwable throwable, DataMap dataMap);
}
