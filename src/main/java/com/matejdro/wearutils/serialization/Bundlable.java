package com.matejdro.wearutils.serialization;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Constructor;

import timber.log.Timber;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public abstract class Bundlable {
    public static final String CLASS_KEY = "Bundlable.ClassName";

    @SuppressWarnings("UnusedParameters")
    public Bundlable(@NonNull PersistableBundle bundle) {
    }

    public Bundlable() {

    }

    public static @Nullable
    <T extends Bundlable> T deserialize(@NonNull PersistableBundle bundle) {
        String className = bundle.getString(CLASS_KEY);
        if (className == null) {
            return null;
        }

        try {
            @SuppressWarnings("unchecked")
            Class<T> cls = (Class<T>) Class.forName(className);
            Constructor<T> constructor = cls.getConstructor(PersistableBundle.class);
            return constructor.newInstance(bundle);
        } catch (ReflectiveOperationException e) {
            Timber.e(e, "Bundlable error");
            e.printStackTrace();
        }

        return null;
    }

    public @NonNull
    PersistableBundle serialize() {
        PersistableBundle bundle = new PersistableBundle();
        writeToBundle(bundle);
        return bundle;
    }

    @CallSuper
    protected void writeToBundle(@NonNull PersistableBundle bundle) {
        bundle.putString(CLASS_KEY, this.getClass().getCanonicalName());
    }
}
