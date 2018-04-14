package com.amargodigits.movies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.amargodigits.movies.data.MovieDbHelper;
import android.database.sqlite.SQLiteDatabase;
import com.amargodigits.movies.data.MovieContract;
import android.text.TextUtils;
import android.util.Log;

import static com.amargodigits.movies.MainActivity.LOG_TAG;

public class LikedMoviesProvider extends ContentProvider {

    // // Uri
    // authority
    static final String AUTHORITY = "com.amargodigits.movies";
    static final String LIKED_PATH = "liked_movies";
    static final String LIKE_PATH = "like_movie";
    static final String UNLIKE_PATH = "unlike_movie";
    // Content Uri's
    public static final Uri LIKED_MOVIE_URI = Uri.parse("content://" + AUTHORITY + "/" + LIKED_PATH);
    public static final Uri LIKE_MOVIE_URI = Uri.parse("content://" + AUTHORITY + "/" + LIKE_PATH);
    public static final Uri UNLIKE_MOVIE_URI = Uri.parse("content://" + AUTHORITY + "/" + UNLIKE_PATH) ;

    // Strings
    static final String LIKED_CONTENT_TYPE = ".dir/vnd." + AUTHORITY + "." + LIKED_PATH;
    // one string
    static final String LIKED_CONTENT_ITEM_TYPE = ".item/vnd." + AUTHORITY + "." + LIKED_PATH;

    //// UriMatcher
    //  Uri to query list of movies
    static final int URI_MOVIES = 1;
    // Uri to query one movie by ID
    static final int URI_MOVIE_ID = 2;
    // Uri to add liked movie to DB
    static final int URI_MOVIE_LIKE = 4;
    // Uri to remove unliked movie from DB
    static final int URI_MOVIE_UNLIKE = 5;


    // Create UriMatcher
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, LIKED_PATH, URI_MOVIES);
        uriMatcher.addURI(AUTHORITY, LIKED_PATH + "/#", URI_MOVIE_ID);
        uriMatcher.addURI(AUTHORITY, LIKE_PATH, URI_MOVIE_LIKE); // to add movie to 'liked' database
        uriMatcher.addURI(AUTHORITY, UNLIKE_PATH+"/#", URI_MOVIE_UNLIKE); // to add movie to 'liked' database

    }

    MovieDbHelper dbHelper;
    SQLiteDatabase mDb;

    @Override
    public boolean onCreate() {
        Log.i(LOG_TAG, "Provider onCreate");
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String id;
        // check Uri
        switch (uriMatcher.match(uri)) {
            case URI_MOVIES: //  Uri for list
                // sorting by the name if not specified
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder =  MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " DESC";
                }
                break;
            case URI_MOVIE_ID: // Uri with ID
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = MovieContract.MovieEntry.COLUMN_FILM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + MovieContract.MovieEntry.COLUMN_FILM_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        mDb = dbHelper.getWritableDatabase();
        Cursor cursor = mDb.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), LIKED_MOVIE_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.i(LOG_TAG, "Provider getType"+ uri.toString());

        switch (uriMatcher.match(uri)) {
            case URI_MOVIES:
                return LIKED_CONTENT_TYPE;
            case URI_MOVIE_ID:
                return LIKED_CONTENT_ITEM_TYPE;
        }
        return null;
    }


    // provides insert the record to like movie to DB functionality
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.i(LOG_TAG, "Provider insert uri = " + uri.toString() );
       if (uriMatcher.match(uri) != URI_MOVIE_LIKE)
            throw new IllegalArgumentException("Wrong URI: " + uri);
        mDb = dbHelper.getWritableDatabase();
        long rowID = mDb.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
        Uri resultUri = ContentUris.withAppendedId(LIKE_MOVIE_URI, rowID);
        Log.i(LOG_TAG, "Provider insert resultUri: " + resultUri.toString() );
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }


    // provides delete the record to unlike movie in DB functionality
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (uriMatcher.match(uri)!=URI_MOVIE_UNLIKE)
                throw new IllegalArgumentException("Wrong URI: " + uri);
        mDb = dbHelper.getWritableDatabase();
        String id = uri.getLastPathSegment();
        selection = MovieContract.MovieEntry.COLUMN_FILM_ID + "=?";
        selectionArgs = new String[]{String.valueOf(id)};
        int cnt = mDb.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    // No need for update functionality
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
