package com.amargodigits.movies.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.amargodigits.movies.model.Movie;
import com.amargodigits.movies.model.Review;
import com.amargodigits.movies.model.Video;

import static com.amargodigits.movies.MainActivity.LOG_TAG;
import static com.amargodigits.movies.MainActivity.movieList;

/**
 * JSONUtils to work with JSON data
 * @author vklimova
 *
 */

public class JsonUtils {
    /** takes as the input raw Json string and returns the string array
     * @param rawJsonStr - raw string with JSON data
     * @return the number of string converted to movieList array from JSON data
     */
    public static int getMovieListStringsFromJson(String rawJsonStr)
            throws JSONException {
        JSONObject rawJson = new JSONObject(rawJsonStr);
        JSONArray moviesJsonArr = rawJson.getJSONArray("results");
        movieList = new Movie[moviesJsonArr.length()];
        for (int i = 0; (i < moviesJsonArr.length()); i++) {
            /* Get the JSON object representing the movie */
            JSONObject movieObj = moviesJsonArr.getJSONObject(i);
            movieList[i] = new Movie(
                    movieObj.getString("original_title"),
                    movieObj.getString("title"),
                    movieObj.getString("overview"),
                    movieObj.getString("poster_path").substring(1), // the first sybmol is '/', we don't need it
                    movieObj.getLong("popularity"),
                    movieObj.getString("release_date"),
                    movieObj.getInt("id")
            );
//            Log.i(LOG_TAG, "Movie " + movieList[i].toString());
        }
       return movieList.length;
    }

    /** takes as the input raw Json string and returns the Review array
     * @param rawJsonStr - raw string with JSON data
     * @return the array of Review converted from JSON data
     */
    public static Review[] getReviewListStringsFromJson(String rawJsonStr)
            throws JSONException {
        Review[] reviewList;

        JSONObject rawJson = new JSONObject(rawJsonStr);
        JSONArray reviewsJsonArr = rawJson.getJSONArray("results");
        reviewList = new Review[reviewsJsonArr.length()];

        for (int i = 0; (i < reviewsJsonArr.length()); i++) {
            /* Get the JSON object representing the review */
            JSONObject reviewObj = reviewsJsonArr.getJSONObject(i);
            reviewList[i] = new Review(
                    reviewObj.getString("author"),
                    reviewObj.getString("content")
            );
   //         Log.i(LOG_TAG, "reviewList=" + reviewList[i].getAuthor());
        }

        return reviewList;
    }

    /** takes as the input raw Json string and returns the array
     * @param rawJsonStr - raw string with JSON data
     * @return the array of video strings from JSON data
     */

    // {"id":"571cb2c0c3a36843150006ed",
    // "iso_639_1":"en",
    // "iso_3166_1":"US",
    // "key":"zQ2XkyDTW34",
    // "name":"Have a Donut",
    // "site":"YouTube",
    // "size":1080,
    // "type":"Clip"}
    public static Video[] getVideoListStringsFromJson(String rawJsonStr)
            throws JSONException {
        Video[] videoList;

        JSONObject rawJson = new JSONObject(rawJsonStr);
        JSONArray videoJsonArr = rawJson.getJSONArray("results");
        videoList = new Video[videoJsonArr.length()];

        for (int i = 0; (i < videoJsonArr.length()); i++) {
            /* Get the JSON object representing the video */
            JSONObject videoObj = videoJsonArr.getJSONObject(i);
            videoList[i] = new Video(
                    videoObj.getString("key"),
                    videoObj.getString("name"),
                    videoObj.getString("site")
            );
//            Log.i(LOG_TAG, "reviewList=" + videoList[i].getName());
        }

        return videoList;
    }
}