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
 *
 */

public class JsonUtils {
    /** takes as the input raw Json string, fills in the movieList[] array, and returns the length of the array
     * @param rawJsonStr - raw string with JSON data
     * @return the number of string converted to movieList array from JSON data
     */
    public static int getMovieListStringsFromJson(String rawJsonStr, String pageNum)
            throws JSONException {

        Log.i(LOG_TAG, "JsonUtils getMovieListStringsFromJson pageNum=" + pageNum);
        JSONObject rawJson = new JSONObject(rawJsonStr);
        JSONArray moviesJsonArr = rawJson.getJSONArray("results");
        int startIndex;
        if (pageNum=="1") {
            startIndex = 0;
            movieList = new Movie[moviesJsonArr.length()];
        } else {
            startIndex = movieList.length;
            movieList = (Movie[]) resizeArray(movieList, (movieList.length+moviesJsonArr.length()));
        }
        Log.i(LOG_TAG, "JsonUtils getMovieListStringsFromJson startIndex=" + startIndex);
        for (int i = 0; (i < moviesJsonArr.length()); i++) {
            /* Get the JSON object representing the movie */
            JSONObject movieObj = moviesJsonArr.getJSONObject(i);
            movieList[startIndex + i] = new Movie(
                    movieObj.getString("original_title"),
                    movieObj.getString("title"),
                    movieObj.getString("overview"),
                    movieObj.getString("poster_path").substring(1), // the first sybmol is '/', we don't need it
                    movieObj.getLong("popularity"),
                    movieObj.getString("release_date"),
                    movieObj.getInt("id")
            );
            Log.i(LOG_TAG, "JsonUtils movieList[" +String.valueOf(startIndex + i)+"] = " + movieList[startIndex + i].getEnglishTitle() );
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
        }
        return reviewList;
    }
    /** takes as the input raw Json string and returns the array
     * @param rawJsonStr - raw string with JSON data
     * @return the array of video strings from JSON data
     */
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
        }
        return videoList;
    }

    /**
     * Origin: http://www.source-code.biz/snippets/java/3.htm
     *
     * Reallocates an array with a new size, and copies the contents
     * of the old array to the new array.
     * @param oldArray  the old array, to be reallocated.
     * @param newSize   the new array size.
     * @return          A new array with the same contents.
     */
    private static Object resizeArray (Object oldArray, int newSize) {
        int oldSize = java.lang.reflect.Array.getLength(oldArray);
        Class elementType = oldArray.getClass().getComponentType();
        Object newArray = java.lang.reflect.Array.newInstance(
                elementType, newSize);
        int preserveLength = Math.min(oldSize, newSize);
        if (preserveLength > 0)
            System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
        return newArray; }
}
