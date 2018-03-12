package com.amargodigits.movies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vklimova on 21.02.18.
 * This module contains Video object description, that holds data for one Video
 */

// {"id":"571cb2c0c3a36843150006ed",
// "iso_639_1":"en",
// "iso_3166_1":"US",
// "key":"zQ2XkyDTW34",
// "name":"Have a Donut",
// "site":"YouTube",
// "size":1080,
// "type":"Clip"}

public class Video implements Parcelable {
    private String iso_639_1;
    private String iso_3166_1;
    private String key;
    private String name;
    private String site;
    private String size;
    private String type;
    private int id;

    public Video(String iso_639_1, String iso_3166_1, String key, String name, String site, String size, String type, int id) {
        this.iso_639_1 = iso_639_1;
        this.iso_3166_1 = iso_3166_1;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
        this.id = id;
    }

    public Video(String key, String name, String site) {
        this.key = key;
        this.name = name;
        this.site = site;
    }


    public Video(Parcel in) {
        String[] data = new String[6];
        in.readStringArray(data);
        this.iso_639_1 = data[0];
        this.iso_3166_1 = data[1];
        this.key = data[2];
        this.name = data[3];
        this.site = data[4];
        this.size = data[5];
        this.type = data[6];
        this.id = Integer.parseInt(data[7]);
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getSite() {
        return this.site;
    }


    public int getId() {
        return this.id;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{iso_639_1, iso_3166_1, key, name, site, size, type, String.valueOf(id)});
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {

        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}

