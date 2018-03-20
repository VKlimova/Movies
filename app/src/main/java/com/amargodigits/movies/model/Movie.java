package com.amargodigits.movies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vklimova on 21.02.18.
 * This module contains Movie object description, that holds data for one Movie
 */

public class Movie implements Parcelable {
    private String  originalTitle;
    private String  englishTitle;
    private String  overview;
    private String  posterPath;
    private float   popularity;
    private String  releaseDate;
    private int id;
    private Boolean unLiking;

    public Movie(String originalTitle, String englishTitle, String overview, String posterPath, float popularity, String releaseDate, int id)
     {
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.posterPath = posterPath;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
        this.englishTitle = englishTitle;
        this.id = id;
      this.unLiking = false;
    }

    public Movie(Parcel in)
    {
        String[] data = new String[6];
        in.readStringArray(data);
        this.originalTitle = data[0];
        this.overview = data[1];
        this.posterPath = data[2];
        this.popularity = Float.parseFloat(data[3]);
        this.releaseDate = data[4];
        this.englishTitle = data[5];
        this.id = Integer.parseInt(data[6]);
        this.unLiking = false;
    }

    public String getOriginalTitle() { return this.originalTitle;}
    public String getEnglishTitle() { return this.englishTitle;}
    public String getOverview() { return this.overview;}
    public String getPosterPath() { return this.posterPath; }
    public float getPopularity() {return this.popularity; }
    public String getReleaseDate() {return this.releaseDate; }
    public int getId() {return this.id; }
    public boolean getUnLiking() {return this.unLiking; }
    public void unLike() {this.unLiking=true;}
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {originalTitle, englishTitle, overview, posterPath,
                String.valueOf(popularity), releaseDate, String.valueOf(id), String.valueOf(unLiking)});
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}

