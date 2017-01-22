package com.matejdro.wearutils.miscutils;

import android.support.annotation.Nullable;

public class ArrayUtils {
    /**
     * Parses comma separated string list into long array
     * @return parsed long array or {@code null} if parsing failed
     */
    public static @Nullable long[] parseLongArray(String listString)
    {
        String[] split = listString.split(",");
        long[] out = new long[split.length];

        try
        {
            for (int i = 0; i < split.length; i++)
            {
                out[i] = Long.parseLong(split[i].trim());
            }
        }
        catch (NumberFormatException ignored) {
            return null;
        }

        return out;
    }
}
