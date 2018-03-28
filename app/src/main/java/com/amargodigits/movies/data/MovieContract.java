package com.amargodigits.movies.data;

import android.provider.BaseColumns;


public  class MovieContract {
    public static final class MovieEntry implements BaseColumns{
        public static final String TABLE_NAME="likedFilms";
        public static final String COLUMN_FILM_ID = "filmId";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_ENGLISH_TITLE = "englishTitle";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_RELEASE_DATE =  "releaseDate";
    }
}
