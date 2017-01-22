package com.matejdro.wearutils.logging;

import android.app.Application;

import pl.tajchert.exceptionwear.ExceptionService;
import timber.log.Timber;

public class TimberExceptionWear extends Timber.Tree {
    private final Application application;

    public TimberExceptionWear(Application application) {
        this.application = application;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (t != null) {
            ExceptionService.reportException(application, t);
        }
    }
}
