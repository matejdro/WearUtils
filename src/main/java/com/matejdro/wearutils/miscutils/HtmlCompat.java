package com.matejdro.wearutils.miscutils;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

public class HtmlCompat {
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, 0);
        } else {
            //noinspection deprecation
            return Html.fromHtml(source);
        }
    }
}
