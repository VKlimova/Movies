package com.amargodigits.movies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This module contains Cast object description, that holds data for one Cast person
 */

public class Cast implements Parcelable {
    private String id;
    private String name;

    public Cast(String id, String name)
     {
        this.id = id;
        this.name = name;
    }

    private Cast(Parcel in)
    {
        String[] data = new String[2];
        in.readStringArray(data);
        this.id = data[0];
        this.name = data[1];
    }

    public String getId() { return this.id;}
    public String getName() { return this.name;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {id, name});
    }

    public static final Creator<Cast> CREATOR = new Creator<Cast>() {

        @Override
        public Cast createFromParcel(Parcel source) {
            return new Cast(source);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };
}