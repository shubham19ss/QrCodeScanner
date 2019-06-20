package com.e.qrscanner;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dell on 06-06-2019.
 */

public class QrData implements Parcelable{
    String type;
    String data;
    String description;
    public QrData(String t, String d, String desc)
    {
        type=t;
        data=d;
        description=desc;
    }

    protected QrData(Parcel in) {
        type = in.readString();
        data = in.readString();
        description = in.readString();
    }

    public static final Creator<QrData> CREATOR = new Creator<QrData>() {
        @Override
        public QrData createFromParcel(Parcel in) {
            return new QrData(in);
        }

        @Override
        public QrData[] newArray(int size) {
            return new QrData[size];
        }
    };

    public String getType()
    {
        return type;
    }
    public String getData()
    {
        return data;
    }
    public String getDescription()
    {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(data);
        parcel.writeString(description);
    }


}
