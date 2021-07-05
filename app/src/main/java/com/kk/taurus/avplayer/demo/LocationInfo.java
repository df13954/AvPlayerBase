package com.kk.taurus.avplayer.demo;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationInfo implements Parcelable {
    public int x;
    public int y;
    public int w;
    public int h;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.x);
        dest.writeInt(this.y);
        dest.writeInt(this.w);
        dest.writeInt(this.h);
    }

    public void readFromParcel(Parcel source) {
        this.x = source.readInt();
        this.y = source.readInt();
        this.w = source.readInt();
        this.h = source.readInt();
    }

    public LocationInfo() {
    }

    protected LocationInfo(Parcel in) {
        this.x = in.readInt();
        this.y = in.readInt();
        this.w = in.readInt();
        this.h = in.readInt();
    }

    public static final Parcelable.Creator<LocationInfo> CREATOR = new Parcelable.Creator<LocationInfo>() {
        @Override
        public LocationInfo createFromParcel(Parcel source) {
            return new LocationInfo(source);
        }

        @Override
        public LocationInfo[] newArray(int size) {
            return new LocationInfo[size];
        }
    };
}
