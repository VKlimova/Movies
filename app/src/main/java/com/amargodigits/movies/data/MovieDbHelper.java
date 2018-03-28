package com.amargodigits.movies.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.amargodigits.movies.data.MovieContract.*;
import com.amargodigits.movies.model.Movie;

import static com.amargodigits.movies.MainActivity.LOG_TAG;
import static com.amargodigits.movies.MainActivity.movieList;
import static com.amargodigits.movies.data.MovieContract.MovieEntry.*;

public class MovieDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "movies.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_FILM_ID + " TEXT NOT NULL, "
                + COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, "
                + COLUMN_ENGLISH_TITLE + " TEXT NOT NULL, "
                + COLUMN_OVERVIEW + " TEXT NOT NULL, "
                + COLUMN_POPULARITY + " TEXT NOT NULL, "
                + COLUMN_POSTER_PATH + " TEXT NOT NULL, "
                + COLUMN_RELEASE_DATE + " TEXT NOT NULL"
                + "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * Fills in the movieList[] array, and returns the length of the array
     */
    public static void makeMovieArrayFromSQLite(SQLiteDatabase sqLiteDatabase)
            {
// This projection  specifies which columns from the database
// we will actually use in this query.
        String[] projection = {
                MovieEntry.COLUMN_ENGLISH_TITLE,
                MovieEntry.COLUMN_FILM_ID,
                MovieEntry.COLUMN_ORIGINAL_TITLE,
                MovieEntry.COLUMN_OVERVIEW,
                MovieEntry.COLUMN_POPULARITY,
                MovieEntry.COLUMN_POSTER_PATH,
                MovieEntry.COLUMN_RELEASE_DATE
        };

        String sortOrder = MovieEntry.COLUMN_RELEASE_DATE + " DESC";
                int i=0;
                try (Cursor cursor = sqLiteDatabase.query(MovieEntry.TABLE_NAME, projection, null, null, null, null, sortOrder)) {
                    while (cursor.moveToNext()) {

                        try {
                            movieList.add(new Movie(
                                            cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_TITLE)),
                                            cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_ENGLISH_TITLE)),
                                            cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)),
                                            cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH)),
                                            cursor.getFloat(cursor.getColumnIndex(MovieEntry.COLUMN_POPULARITY)),
                                            cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)),
                                            cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_FILM_ID))
                                    )
                            );
                        } catch (Exception e) {
                            Log.i(LOG_TAG, "makeMovieArrayFromSQLite Exception = " + e.toString());
                        }
                        i++;
                    }
                }
    }
}
