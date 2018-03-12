package com.amargodigits.movies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vklimova on 21.02.18.
 * This module contains Review object description, that holds data for one Movie
 */

public class Review implements Parcelable {
    private String  author;
    private String  content;

    public Review(String author, String content)
     {
        this.author = author;
        this.content = content;
    }

    public Review(Parcel in)
    {
        String[] data = new String[2];
        in.readStringArray(data);
        this.author = data[0];
        this.content = data[1];
    }

    public String getAuthor() { return this.author;}
    public String getContent() { return this.content;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {author, content});
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {

        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}