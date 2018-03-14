package com.amargodigits.movies.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.amargodigits.movies.BuildConfig;
import com.amargodigits.movies.DetailActivity;
import com.amargodigits.movies.MainActivity;
import com.amargodigits.movies.R;
import com.amargodigits.movies.data.MovieDbHelper;
import com.amargodigits.movies.model.Review;
import com.amargodigits.movies.model.Video;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import static com.amargodigits.movies.MainActivity.LOG_TAG;
import static com.amargodigits.movies.data.MovieDbHelper.makeMovieArrayFromSQLite;

/**
 * This module was inspired by NetworkUtils from Sunshine Udacity project
 * These utilities will be used to communicate with the api.themoviedb.org
 *  The methods are to build URL, get response from HTTP Url, Load Data with Async Task,
 *  check if the device is online
 */
public final class NetworkUtils {
    private static final String BASE_URLp1 = "https://api.themoviedb.org/3/movie/";
    private static final String BASE_URLp2 = "?api_key=";
    private static final String BASE_URLp3 = "&callback=";
    private static final String BASE__YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    /**
     * Builds the URL used to get movies list from the TMDB server.
     *
     * @param sortOrder The order for sorting queue results.
     * @return The URL to use to query the movie server.
     */
    public static URL buildUrl(String sortOrder) {
        if (sortOrder.equals("")) {
            sortOrder = "top_rated";
        }
        Uri builtUri = Uri.parse(BASE_URLp1 + sortOrder + BASE_URLp2 + BuildConfig.MOVIESDB_API_KEY + BASE_URLp3 + sortOrder).buildUpon().build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Builds the URL used to get REVIEWS list from the TMDB server.
     *
     * @param movieId The id of the movie
     * @return The URL to use to query the movie server.
     */

    //    https://api.themoviedb.org/3/movie/269149/reviews?api_key=xxxxxxxxxxxxx

    public static URL buildReviewUrl(int movieId) {

        Uri builtUri = Uri.parse(BASE_URLp1 + String.valueOf(movieId) + "/reviews" + BASE_URLp2 + BuildConfig.MOVIESDB_API_KEY + BASE_URLp3).buildUpon().build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    /**
     * Builds the URL used to get REVIEWS list from the TMDB server.
     *
     * @param movieId The id of the movie
     * @return The URL to use to query the movie server.
     */

    //    https://api.themoviedb.org/3/movie/269149/videos?api_key=xxxx&callback=

    public static URL buildVideoUrl(int movieId) {

        Uri builtUri = Uri.parse(BASE_URLp1 + String.valueOf(movieId) + "/videos" + BASE_URLp2 + BuildConfig.MOVIESDB_API_KEY + BASE_URLp3).buildUpon().build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    /**
     * Builds the URL used to get REVIEWS list from the TMDB server.
     *
     * @param youtubeKey The key of the youtube video
     * @return The URL to use to query the movie server.
     */

    //    https://www.youtube.com/watch?v=zQ2XkyDTW34

    public static URL buildYoutubeUrl(String youtubeKey) {

        Uri builtUri = Uri.parse(BASE__YOUTUBE_URL + String.valueOf(youtubeKey));
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * This method creates AsyncTask to make a Network request in background
     * To load movies list to movieList global variable
     */

    public static class LoadDataTask extends AsyncTask<String, Void, Integer> {
        Context mContext;
        String mSortOrder;
        public LoadDataTask(Context context) {
            mContext = context;
        }

        /**
         * This method makes a Network request in background
         * Load movies list to movieList
         * @return The number of movies in the array
         */
        @Override
        protected Integer doInBackground(String... params) {

            if (params.length == 0) {
                return 0;
            }
            mSortOrder = params[0];
            Log.i(LOG_TAG, "List of movies selected: " + params[0].toString().toUpperCase());
            if (params[0]=="liked") { // Liked movies to be displayed

                SQLiteDatabase mDb;
                // Create a DB helper (this will create the DB if run for the first time)
                MovieDbHelper dbHelper = new MovieDbHelper(mContext);
                // Keep a reference to the mDb until paused or killed. Get a writable database
                // because you will be adding restaurant customers
                mDb = dbHelper.getWritableDatabase();
                return makeMovieArrayFromSQLite(mDb, "");

            } else { // Popular or top-rated movies
                if (isOnline(mContext)) {
                    try {
                        URL scheduleRequestUrl = NetworkUtils.buildUrl(params[0]);
                        Log.i(LOG_TAG, "20 movies RequestUrl = " + scheduleRequestUrl.toString());
                        String moviesResponse = NetworkUtils
                                .getResponseFromHttpUrl(scheduleRequestUrl);
                        int jsonStart = moviesResponse.indexOf("(") + 1;
                        String jsonMoviesResponse = moviesResponse.substring(jsonStart, moviesResponse.length() - 1);
                        return JsonUtils.getMovieListStringsFromJson(jsonMoviesResponse);
                    } catch (Exception e) {
                        Log.i(LOG_TAG, R.string.error_message + e.toString());
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(mContext, R.string.no_data, Toast.LENGTH_LONG).show();
                }
            }
            return 0;
        }
        /**
         * This method executes after
         * loading movies list
         */
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Log.i(LOG_TAG, "LoadDataTask onPostExecute get result =" + result.toString());
            if (result > 0) {
                MainActivity.doRecView(mContext, mSortOrder);
            } else {
                Toast.makeText(mContext, R.string.no_data, Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * This method creates AsyncTask to make a Network request in background
     * To load reviews list
     */

    public static class LoadReviewsTask extends AsyncTask<Integer, Void, Review[]> {
        Context mContext;
        public LoadReviewsTask(Context context) {
            mContext = context;
        }

        /**
         * This method make a Network request in background
         * Load reviews list to reviewList
         * @return Review[] -  the reviews  array
         */
        @Override
        protected Review[] doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }
            if (isOnline(mContext)) {
                try {
                    URL scheduleRequestUrl = NetworkUtils.buildReviewUrl(params[0]);
                    Log.i(LOG_TAG, "Reviews RequestUrl = " + scheduleRequestUrl.toString());
                    Review[] reviewsArr;
                    String reviewResponse = NetworkUtils
                            .getResponseFromHttpUrl(scheduleRequestUrl);
                    int jsonStart = reviewResponse.indexOf("(") + 1;
                    String jsonReviewResponse = reviewResponse.substring(jsonStart, reviewResponse.length() - 1);
                    reviewsArr= JsonUtils.getReviewListStringsFromJson(jsonReviewResponse);
                    return reviewsArr;
                } catch (Exception e) {
                    Log.i(LOG_TAG, R.string.error_message + e.toString());
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mContext, R.string.no_data, Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Review[] result) {
            super.onPostExecute(result);
            DetailActivity.addReviews(result);
        }
    }


    /**
     * This method creates AsyncTask to make a Network request in background
     * To load videos list
     */

    public static class LoadVideosTask extends AsyncTask<Integer, Void, Video[]> {
        Context mContext;
        public LoadVideosTask(Context context) {
            mContext = context;
        }

        /**
         * This method make a Network request in background
         * Load reviews list to reviewList
         * @return Review[] -  the reviews  array
         */
        @Override
        protected Video[] doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }
            if (isOnline(mContext)) {
                try {
                    URL scheduleRequestUrl = NetworkUtils.buildVideoUrl(params[0]);
                    Log.i(LOG_TAG, "Video Request Url = " + scheduleRequestUrl.toString());
                    Video[] videosArr;
                    String videoResponse = NetworkUtils
                            .getResponseFromHttpUrl(scheduleRequestUrl);
                    int jsonStart = videoResponse.indexOf("(") + 1;
                    String jsonReviewResponse = videoResponse.substring(jsonStart, videoResponse.length() - 1);
                    videosArr= JsonUtils.getVideoListStringsFromJson(jsonReviewResponse);
                    return videosArr;
                } catch (Exception e) {
                    Log.i(LOG_TAG, R.string.error_message + e.toString());
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mContext, R.string.no_data, Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Video[] result) {
            super.onPostExecute(result);
            DetailActivity.addVideos(result);
        }
    }
    /**
     * Checks if the device has network connection
     * @param tContext - context variable
     * @return true if the device is connected to network, otherwise returns false
     */
    public static boolean isOnline(Context tContext) {
        ConnectivityManager cm =
                (ConnectivityManager) tContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}