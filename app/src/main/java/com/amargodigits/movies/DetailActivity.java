package com.amargodigits.movies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amargodigits.movies.data.MovieContract;
import com.amargodigits.movies.data.MovieDbHelper;
import com.amargodigits.movies.model.Cast;
import com.amargodigits.movies.model.Movie;
import com.amargodigits.movies.model.Review;
import com.amargodigits.movies.model.Video;
import com.amargodigits.movies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.amargodigits.movies.MainActivity.LOG_TAG;
import static com.amargodigits.movies.MainActivity.mAdapter;
import static com.amargodigits.movies.MainActivity.mSharedPref;
import static com.amargodigits.movies.MainActivity.movieList;
import static com.amargodigits.movies.data.MovieContract.MovieEntry.*;
import static com.amargodigits.movies.utils.NetworkUtils.buildYoutubeUrl;
import static com.amargodigits.movies.utils.NetworkUtils.isOnline;

public class DetailActivity extends AppCompatActivity {
    private static final int DEFAULT_POSITION = -1;
    public static TextView videosTxt;
    public static TextView reviewsTxt;
    public static TextView castTxt;
    public static Context mContext;
    public static String strReviews;
    SQLiteDatabase mDb;
    static Toolbar toolbar;
    final int moviePosition = mSharedPref.getInt("MoviePosition", DEFAULT_POSITION);
    final Movie mMovie = movieList.get(moviePosition);
    Drawable starDrawable;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_detail);
        } catch (Exception e) {
            Log.i(LOG_TAG, "DetailActivity onCreate Exception1 :" + e.toString());
        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        strReviews = this.getString(R.string.reviews_lbl);
        mContext = getApplicationContext();
        ButterKnife.bind(this);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        populateUI(mMovie);
        try {
            // Create a DB helper (this will create the DB if run for the first time)
            MovieDbHelper dbHelper = new MovieDbHelper(this);
            mDb = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.i(LOG_TAG, "DetailActivity Exception " + e.toString());
        }
        reviewsTxt = findViewById(R.id.reviewsTxt);
        videosTxt = findViewById(R.id.videosTxt);
        castTxt = findViewById(R.id.castTxt);
        if (isOnline(getApplicationContext())) {
            try {
                Boolean show_rev = sp.getBoolean("show_rev", true);
                if (show_rev) {
                    NetworkUtils.LoadReviewsTask mRAsyncTask = new NetworkUtils.LoadReviewsTask(getApplicationContext());
                    mRAsyncTask.execute(mMovie.getId());
                }
                Boolean show_videos = sp.getBoolean("show_videos", true);
                if (show_videos)
                {
                    NetworkUtils.LoadVideosTask mVAsyncTask = new NetworkUtils.LoadVideosTask(getApplicationContext());
                    mVAsyncTask.execute(mMovie.getId());
                }
                Boolean show_cast = sp.getBoolean("show_cast", true);
                if (show_cast) {
                    NetworkUtils.LoadCastTask mCAsyncTask = new NetworkUtils.LoadCastTask(getApplicationContext());
                    mCAsyncTask.execute(mMovie.getId());
                }
            } catch (Exception e) {
                Log.i(LOG_TAG, e.toString());
            }
        } else {
            Toast.makeText(this, "Network connection required", Toast.LENGTH_LONG).show();
        }
        showBigImage();
    }

    private void showBigImage() {
        ImageView image_iv = findViewById(R.id.image_iv);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("w185")
                .appendPath(mMovie.getPosterPath());
        Picasso.with(this).load(builder.build().toString())
                .placeholder(android.R.drawable.stat_sys_download)
                .error(android.R.drawable.ic_menu_report_image)
                .into(image_iv);
        setTitle(mMovie.getOriginalTitle());
    }

    /**
     * Accept movie object and populate to TextViews
     */
    @BindView(R.id.originalTitle)
    TextView originalTitle;
    @BindView(R.id.englishTitle)
    TextView englishTitle;
    @BindView(R.id.overview)
    TextView overview;


    @BindView(R.id.popularity)
    TextView popularity;
    @BindView(R.id.releaseDate)
    TextView releaseDate;
    @BindView(R.id.search_button)
    Button search_button;
    @BindView(R.id.share_button)
    Button share_button;

    /**
     * Shows the movie information on the screen
     * @param movie The details on the movie
     */

    private void populateUI(Movie movie) {
        final String linkTitle;
        String popularityLbl;
        originalTitle.setText(movie.getOriginalTitle());
        overview.setText(movie.getOverview());
        popularityLbl = getResources().getString(R.string.popularity_description_label, (int) movie.getPopularity());
        popularity.setText(popularityLbl);
        releaseDate.setText(movie.getReleaseDate());
        englishTitle.setText(movie.getEnglishTitle());
        if (movie.getOriginalTitle().length() == movie.getEnglishTitle().length()) {
            englishTitle.setVisibility(View.GONE);
        }
        String searchString1, searchString2;
        try
        {
            sp.contains("search1");
        } catch (Exception e) {
            Log.i(LOG_TAG, " populateUI Exception - " + e.toString());
        }
        if (sp.contains("search1")){
            searchString1 = sp.getString("search1","");
            searchString2 = sp.getString("search2","");
        } else {
            searchString1 = BuildConfig.SEARCH_STRING_1;
            searchString2 = BuildConfig.SEARCH_STRING_2;
        }
        if ((searchString1.contains("http://")) || (searchString1.contains("htts://"))) {
            linkTitle = searchString1 + movie.getEnglishTitle() + searchString2;
        } else {
            linkTitle = "https://www.google.ru/search?q=" + movie.getEnglishTitle() + " " + BuildConfig.SEARCH_STRING_2;
        }
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch web intent
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkTitle));
                startActivity(intent);
            }
        });
        final String shareText=movie.getEnglishTitle() + "\n" +movie.getReleaseDate() + "\n" + movie.getOverview();
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch web intent
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                intent.setType("text/plain");
                startActivity(intent);
            }
        });
    }

    /**
     * Populates the REVIEWS list on the screen
     *
     * @param reviews The array with the reviews
     */

    public static void addReviews(final Review[] reviews) {
        reviewsTxt.append("Reviews: ");
        if (reviews == null) {
            reviewsTxt.append("No reviews yet");
        } else {
            reviewsTxt.append(reviews.length + " <Show reviews>\n\n");
        }
        reviewsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strShow = " <Show reviews> \n\n";
                String strHide = " <Hide reviews> \n\n";
                if (reviewsTxt.getText().toString().contains(strHide)) {
                    reviewsTxt.setText(strReviews);
                    reviewsTxt.append(Objects.requireNonNull(reviews).length + strShow);
                } else {
                    reviewsTxt.setText(strReviews);
                    reviewsTxt.append(Objects.requireNonNull(reviews).length + strHide);
                    for (Review review : reviews) {
                        reviewsTxt.append("===\n");
                        reviewsTxt.append(review.getAuthor() + "\n");
                        reviewsTxt.append("---\n");
                        reviewsTxt.append(review.getContent() + "\n\n");
                    }
                }
            }
        });
    }

    /**
     * Populates the CAST list on the screen*
     * @param castAr The array with the reviews
     */

    public static void addCast(Cast[] castAr) {
        castTxt.setText("\nCast:\n");
        for (Cast cast : castAr) {
                castTxt.append("* " + cast.getName() + "\n");
            }
    }
    public static final SpannableStringBuilder spanText = new SpannableStringBuilder();
    /**
     * Populates the VIDEOS list on the screen
     * @param videosAr The array with the reviews
     */
    public static void addVideos(Video[] videosAr) {
        spanText.clear();
        spanText.append("Videos: ");
        if (videosAr == null) {
            spanText.append("No videos in base");
        } else {
            spanText.append(String.valueOf(videosAr.length)).append("\n\n");
            for (Video video : videosAr) {
                singleTextView(videosTxt, "> ", video.getName() + " ", buildYoutubeUrl(video.getKey()).toString());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelable("Movie", mMovie);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Supplementary method used to populates the VIDEOS list on the screen
     * adds Spannable strings to textView
     * Inspired by:
     * https://gist.github.com/anuragdhunna/5aa36b2fb6e97bcaebca1a3bf20787d3#file-singletextview-java
     *
     * @param textView The textView object to be modified
     */
    private static void singleTextView(TextView textView, final String revSite, String revName, final String revUrl) {
        spanText.append(revSite);
        spanText.append(revName);
        spanText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Open link in new window
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(revUrl));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    Log.i(LOG_TAG, "Exception opening " + Uri.parse("http://ya.ru").toString() + " - " + e.toString());
                }
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(textPaint.linkColor);
                textPaint.setUnderlineText(false);    // this remove the underline
            }
        }, spanText.length() - revName.length(), spanText.length(), 0);

        spanText.append("\n");
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spanText, TextView.BufferType.SPANNABLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        starDrawable = menu.findItem(R.id.like).getIcon();
        setStarColor();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.like)
            try {
                likeMovieClick(mMovie);
            } catch (Exception e) {
                Log.i(LOG_TAG, "like Exception: " + e.toString());
            }
        return true;
    }

    private void likeMovieClick(Movie movie) {

        if (iLikeMovie(movie.getId())) {
             unLikeMovie(movie);
        } else {
            likeMovie(movie);
        }
        setStarColor();
    }

    /** iLikeMovie checks if the movie is liked
     * @param id -  film id
     * @return true if movie is liked, false if not
     **/
    private boolean iLikeMovie(int id) {
        String selection = MovieContract.MovieEntry.COLUMN_FILM_ID + "=" + id;
        Cursor cursor = mDb.query(MovieContract.MovieEntry.TABLE_NAME, null, selection, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count != 0;
    }

    /** likeMovie insert the record with "movie" to mDb
     * @param movie -  film
     * @return the number of inserted records
     **/
    private long likeMovie(Movie movie) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ENGLISH_TITLE, movie.getEnglishTitle());
        cv.put(COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        cv.put(COLUMN_OVERVIEW, movie.getOverview());
        cv.put(COLUMN_POPULARITY, movie.getPopularity());
        cv.put(COLUMN_POSTER_PATH, movie.getPosterPath());
        cv.put(COLUMN_RELEASE_DATE, movie.getReleaseDate());
        cv.put(COLUMN_FILM_ID, movie.getId());
        return  mDb.insert(TABLE_NAME, null, cv);
    }

    /** unLikeMovie delete the record with "movie" from mDb
     * @param movie -  film
     * @return the number of deleted records
     **/

    private long unLikeMovie(Movie movie) {
        movie.unLike();
        String deleteSql = COLUMN_FILM_ID + "=" + movie.getId();
        long ret = mDb.delete(TABLE_NAME, deleteSql, null);
            movieList.remove(movie);
            mAdapter.notifyDataSetChanged();
        return ret;
    }

    private void setStarColor() {
        try {

            if (starDrawable != null) {
                starDrawable.mutate();
                if (iLikeMovie(mMovie.getId())) {
                    starDrawable.setColorFilter(Color.rgb(255,153,51), PorterDuff.Mode.SRC_ATOP);
                } else {
                    starDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                }
            }
        } catch (Exception e) {
            Log.i(LOG_TAG, "DetailActivity onCreateOptionsMenu Exception " + e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        // do something on back.
        super.onBackPressed();
    }
}
