package com.matejdro.wearutils.messages;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Convenience class for easier parcelable packing for Phone &lt;&gt; wear transfer.
 */
public class ParcelPacker
{
    /**
     * Marshall parcelable object into byte array
     */
    public static byte[] getData(@NonNull Parcelable object)
    {
        Parcel parcel = Parcel.obtain();
        try
        {
            object.writeToParcel(parcel, 0);
            return parcel.marshall();
        }
        finally
        {
            parcel.recycle();
        }
    }

    /**
     * Unmarshall byte array back into parcelable object.
     * @param data Byte array that was marshaled from parcel
     * @param creator Creator of the object that will be created.
     * @return Unmarshaled object.
     */
    public static <T extends Parcelable> T getParcelable(@NonNull byte[] data, @NonNull Parcelable.Creator<T> creator)
    {
        Parcel parcel = Parcel.obtain();
        try
        {
            parcel.unmarshall(data, 0, data.length);
            parcel.setDataPosition(0);
            return creator.createFromParcel(parcel);
        }
        finally
        {
            parcel.recycle();
        }
    }

}
