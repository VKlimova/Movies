package com.amargodigits.movies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.amargodigits.movies.MainActivity.LOG_TAG;
import static com.amargodigits.movies.MainActivity.mAdapter;
import static com.amargodigits.movies.MainActivity.mRecyclerView;
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
    SQLiteDatabase mDb;
    static Toolbar toolbar;
    int moviePosition = mSharedPref.getInt("MoviePosition", DEFAULT_POSITION);
    final Movie mMovie = movieList.get(moviePosition);
    Drawable starDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_detail);
        } catch (Exception e) {
            Log.i(LOG_TAG, "DetailActivity onCreate Exception1 :" + e.toString());
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

        mContext = getApplicationContext();
        ButterKnife.bind(this);

        Log.i(LOG_TAG, "moviePosition= " + moviePosition + " mMovie=" + mMovie.getOriginalTitle());
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
                NetworkUtils.LoadReviewsTask mRAsyncTasc = new NetworkUtils.LoadReviewsTask(getApplicationContext());
                mRAsyncTasc.execute(mMovie.getId());

                NetworkUtils.LoadVideosTask mVAsyncTasc = new NetworkUtils.LoadVideosTask(getApplicationContext());
                mVAsyncTasc.execute(mMovie.getId());

                NetworkUtils.LoadCastTask mCAsyncTasc = new NetworkUtils.LoadCastTask(getApplicationContext());
                mCAsyncTasc.execute(mMovie.getId());


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

//        Picasso.with(this).setIndicatorsEnabled(true);

        Picasso.with(this).load(builder.build().toString())
                .placeholder(android.R.drawable.stat_sys_download)
                .error(android.R.drawable.ic_menu_report_image)
                .into(image_iv);

//        if (mMovie.getUnLiking()) { image_iv.setAlpha(0.5f); }

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
     *
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


        Log.i(LOG_TAG, "BuildConfig.SEARCH_STRING_1 = " + BuildConfig.SEARCH_STRING_1);
        if ((BuildConfig.SEARCH_STRING_1.contains("http://")) || (BuildConfig.SEARCH_STRING_1.contains("htts://"))) {

            linkTitle = BuildConfig.SEARCH_STRING_1 + movie.getEnglishTitle() + BuildConfig.SEARCH_STRING_2;
            Log.i(LOG_TAG, "Opening from BuildConfig: " + linkTitle);
//            Uri.Builder builder = new Uri.Builder();
//            builder.scheme("https")
//                    .authority("www.google.ru")
//                    .appendPath("search")
//                    .appendQueryParameter("q", movie.getEnglishTitle());
//            String myUrl = builder.build().toString();
//            Log.i(LOG_TAG, "builder.scheme " + myUrl);


        } else {

            linkTitle = "https://www.google.ru/search?q=" + movie.getEnglishTitle() + " " + BuildConfig.SEARCH_STRING_2;
            Log.i(LOG_TAG, "Opening default: " + linkTitle);

        }
        Log.i(LOG_TAG, "DetailActivity linkTitle = " + linkTitle);
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


    public void populateSearchBtn() {


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
                String strReviews = "Reviews: ";
                String strShow = " <Show reviews> \n\n";
                String strHide = " <Hide reviews> \n\n";
                if (reviewsTxt.getText().toString().contains(strHide)) {
                    reviewsTxt.setText(strReviews);
                    reviewsTxt.append(reviews.length + strShow);
                } else {
                    reviewsTxt.setText("Reviews: ");
                    reviewsTxt.append(reviews.length + strHide);
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
     * Populates the CAST list on the screen
     *
     * @param castAr The array with the reviews
     */
    public static SpannableStringBuilder castSpan = new SpannableStringBuilder();

    public static void addCast(Cast[] castAr) {
        castTxt.setText("\nCast:\n");
        for (Cast cast : castAr) {
                castTxt.append("* " + cast.getName() + "\n");
            }

//        castSpan.clear();
//        castSpan.append("Cast: ");
//        if (castAr == null) {
//            castSpan.append("No cast in base");
//        } else {
//            castSpan.append(castAr.length + "\n\n");
//            for (Cast cast : castAr) {
//                singleTextView(castTxt, "> ", cast.getName() + " ", "");
////                singleTextView(videosTxt, "> ", cast.getName() + " ", buildYoutubeUrl(cast.getKey()).toString());
//            }
//        }
    }



    /**
     * Populates the VIDEOS list on the screen
     *
     * @param videosAr The array with the reviews
     */
    public static SpannableStringBuilder spanText = new SpannableStringBuilder();

    public static void addVideos(Video[] videosAr) {
        spanText.clear();
        spanText.append("Videos: ");
        if (videosAr == null) {
            spanText.append("No videos in base");
        } else {
            spanText.append(videosAr.length + "\n\n");
            for (Video video : videosAr) {
                singleTextView(videosTxt, "> ", video.getName() + " ", buildYoutubeUrl(video.getKey()).toString());
            }
        }
    }

    /**
     * Show the Toast with error and close
     */
    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
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
     * adds Spanable strings to textView
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
                textPaint.setColor(textPaint.linkColor);    // you can use custom color
                textPaint.setUnderlineText(false);    // this remove the underline
            }
        }, spanText.length() - revName.length(), spanText.length(), 0);

        spanText.append("\n");

//        spanText.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//
//                // On Click Action
//                Toast.makeText(mContext, "Span 2 clicked", Toast.LENGTH_LONG).show();
//
//            }
//
//            @Override
//            public void updateDrawState(TextPaint textPaint) {
//                textPaint.setColor(textPaint.linkColor);    // you can use custom color
//                textPaint.setUnderlineText(false);    // this remove the underline
//            }
//        },spanText.length() - revUrl.length(), spanText.length(), 0);

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
//            Toast.makeText(this, "Saving ..."  + mMovie.getOriginalTitle()  , Toast.LENGTH_LONG).show();
                likeMovieClick(mMovie);
            } catch (Exception e) {
                Log.i(LOG_TAG, "like Exception: " + e.toString());
            }
        return true;
    }

    private long likeMovieClick(Movie movie) {
        long result;
//        String selection = MovieContract.MovieEntry.COLUMN_FILM_ID + "=" + movie.getId();
//        Cursor cursor = mDb.query(MovieContract.MovieEntry.TABLE_NAME, null, selection, null, null, null, null);
//        int count = cursor.getCount();
        ImageView image_iv = findViewById(R.id.image_iv);

        if (iLikeMovie(movie.getId())) {
            result = unLikeMovie(movie);
        } else {
            result = likeMovie(movie);
        }
        setStarColor();

        Log.i(LOG_TAG, "DetailActivity likeMovieClick mAdapter.notifyDataSetChanged();");
        return result;
    }


    /** iLikeMovie checks if the movie is liked
     * @param id -  film id
     * @return true if movie is liked, false if not
     **/

    private boolean iLikeMovie(int id) {
        String selection = MovieContract.MovieEntry.COLUMN_FILM_ID + "=" + id;
        Cursor cursor = mDb.query(MovieContract.MovieEntry.TABLE_NAME, null, selection, null, null, null, null);
        int count = cursor.getCount();
        if (count == 0) {
            return false;
        } else {
            return true;
        }
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
//        Toast.makeText(this, "Saving: " + cv.getAsString(COLUMN_ORIGINAL_TITLE), Toast.LENGTH_LONG).show();
        long ret =  mDb.insert(TABLE_NAME, null, cv);
//        NetworkUtils.LoadDataTask mAsyncTask = new NetworkUtils.LoadDataTask(getApplicationContext());
//        mAsyncTask.execute("liked","0");
        return ret;
    }

    /** unLikeMovie delete the record with "movie" from mDb
     * @param movie -  film
     * @return the number of deleted records
     **/

    private long unLikeMovie(Movie movie) {
//        Toast.makeText(this, "Unliking: " + movie.getOriginalTitle(), Toast.LENGTH_LONG).show();
        movie.unLike();
        String deleteSql = COLUMN_FILM_ID + "=" + movie.getId();
        long ret = mDb.delete(TABLE_NAME, deleteSql, null);
            movieList.remove(movie);
            mAdapter.notifyDataSetChanged();
//        NetworkUtils.LoadDataTask mAsyncTask = new NetworkUtils.LoadDataTask(getApplicationContext());
//        mAsyncTask.execute("liked","0");
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
        Log.i(LOG_TAG, "DetailActivity onBackPressed");
        super.onBackPressed();
        return;
    }
}

// DONE 1: Кнопку Share - easy
// TODO 2: Save to Google Drive account - difficult
// DONE 3: Fix bug with endless scroll
// DONE 4: endless scroll - difficult
// TODO 5: Preferencies to set search strings and share strings - difficult
// Postponed 6: Collapse many Videos - easy
// DONE 7: stars in Main and Detail should be different - easy
// TODO 8: languages: https://api.themoviedb.org/3/movie/76341?api_key=<<api_key>>&language=ru - with p.5
//  ... 8: https://api.themoviedb.org/3/configuration/languages?api_key=...&callback=
// Actor's movies: https://api.themoviedb.org/3/person/287/movie_credits?language=en-US&api_key=630ff3e04b429ffc01f65938c4190e7d'
// Have Brad Pitt and Edward Norton ever been in a movie together?
//        URL: /discover/movie?with_people=287,819&sort_by=vote_average.desc
//Cast and Crew of the movie: /movie/{movie_id}/credits