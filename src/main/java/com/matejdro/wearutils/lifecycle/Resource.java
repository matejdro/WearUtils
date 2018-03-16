package com.matejdro.wearutils.lifecycle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

//a generic class that describes a data with a status
public class Resource<T> {
    @NonNull public final Status status;
    @Nullable public final T data;
    @Nullable public final String message;
    @Nullable public final Object errorData;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message, @Nullable Object errorData) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.errorData = errorData;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return error(msg, data, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data, @Nullable Object errorData) {
        return new Resource<>(Status.ERROR, data, msg, errorData);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null, null);
    }

    public enum Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", errorData=" + errorData +
                '}';
    }
}


