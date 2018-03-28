package com.amargodigits.movies.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amargodigits.movies.model.Cast;
import com.amargodigits.movies.model.Movie;
import com.amargodigits.movies.model.Review;
import com.amargodigits.movies.model.Video;

import static com.amargodigits.movies.MainActivity.LOG_TAG;
import static com.amargodigits.movies.MainActivity.movieList;

/**
 * JSONUtils to work with JSON data
 */

class JsonUtils {
    /** takes as the input raw Json string, fills in the movieList[] array, and returns the length of the array
     * @param rawJsonStr - raw string with JSON data
     */
    public static void getMovieListStringsFromJson(String rawJsonStr, String pageNum)
            throws JSONException {
        JSONObject rawJson = new JSONObject(rawJsonStr);
        JSONArray moviesJsonArr = rawJson.getJSONArray("results");
        int startIndex;
        if (pageNum.equals("1")) {
            startIndex = 0;
        } else {
            startIndex = movieList.size();
        }
        for (int i = 0; (i < moviesJsonArr.length()); i++) {
            /* Get the JSON object representing the movie */
            JSONObject movieObj = moviesJsonArr.getJSONObject(i);
            movieList.add(new Movie(
                    movieObj.getString("original_title"),
                    movieObj.getString("title"),
                    movieObj.getString("overview"),
                    movieObj.getString("poster_path").substring(1), // the first sybmol is '/', we don't need it
                    movieObj.getLong("popularity"),
                    movieObj.getString("release_date"),
                    movieObj.getInt("id")
                    )
            );
        }
    }

    /** takes as the input raw Json string and returns the Cast array
     * @param rawJsonStr - raw string with JSON data
     * @return the array with Cast converted from JSON data
     */
    public static Cast[] getCastListStringsFromJson(String rawJsonStr)
            throws JSONException {
        Cast[] castList;

        JSONObject rawJson = new JSONObject(rawJsonStr);
        JSONArray castJsonArr = rawJson.getJSONArray("cast");
        int castNum;

        // We don't want the entire list of 40+ cast :)
        if (castJsonArr.length()<8){
            castNum=castJsonArr.length();
        } else {
            castNum=8;
        }
        castList = new Cast[castNum];

        for (int i = 0; (i < castNum); i++) {
            /* Get the JSON object representing the review */
            JSONObject castObj = castJsonArr.getJSONObject(i);
            castList[i] = new Cast(
                    castObj.getString("id"),
                    castObj.getString("name")
            );
        }
        return castList;
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
}
