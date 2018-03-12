package com.amargodigits.movies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amargodigits.movies.model.Movie;
import com.amargodigits.movies.model.Review;
import com.amargodigits.movies.model.Video;
import com.amargodigits.movies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.amargodigits.movies.MainActivity.LOG_TAG;
import static com.amargodigits.movies.MainActivity.mSharedPref;
import static com.amargodigits.movies.MainActivity.movieList;
import static com.amargodigits.movies.utils.NetworkUtils.buildYoutubeUrl;
import static com.amargodigits.movies.utils.NetworkUtils.isOnline;

public class DetailActivity extends AppCompatActivity {
    private static final int DEFAULT_POSITION = -1;
    public static TextView videosTxt;
    public static TextView reviewsTxt;
    public static Context mContext;
    Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mContext = getApplicationContext();
        ButterKnife.bind(this);
        ImageView image_iv = findViewById(R.id.image_iv);
        int moviePosition = mSharedPref.getInt("MoviePosition", DEFAULT_POSITION);
        Movie mMovie = movieList[moviePosition];
        Log.i(LOG_TAG, "moviePosition= " + moviePosition + " mMovie=" + mMovie.getOriginalTitle());
        populateUI(mMovie);



        reviewsTxt = findViewById(R.id.reviewsTxt);
        videosTxt = findViewById(R.id.videosTxt);

        if (isOnline(getApplicationContext())) {
            try {
                NetworkUtils.LoadReviewsTask mRAsyncTasc = new NetworkUtils.LoadReviewsTask(getApplicationContext());
                mRAsyncTasc.execute(mMovie.getId());

                NetworkUtils.LoadVideosTask mVAsyncTasc = new NetworkUtils.LoadVideosTask(getApplicationContext());
                mVAsyncTasc.execute(mMovie.getId());

            } catch (Exception e) {
                Log.i(LOG_TAG, e.toString());
            }
        } else {
            Toast.makeText(this, "Network connection required", Toast.LENGTH_LONG).show();
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("w185")
                .appendPath(mMovie.getPosterPath());
        Picasso.with(this)
                .load(builder.build().toString())
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
    }


    public void populateSearchBtn() {


    }


    /**
     * Populates the REVIEWS list on the screen
     * @param reviews The array with the reviews
     */

    public static void addReviews(final Review[] reviews) {
        reviewsTxt.append("Reviews: ");
        if (reviews == null) {
            reviewsTxt.append("No reviews yet");
        } else {

            reviewsTxt.append(reviews.length +  " <Show reviews>\n\n");
//            for (Review review : reviews) {
//                reviewsTxt.append("===\n");
//                reviewsTxt.append(review.getAuthor() + "\n");
//                reviewsTxt.append("---\n");
//                reviewsTxt.append(review.getContent() + "\n\n");
//            }
        }
        reviewsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strReviews="Reviews: ";
                String strShow=" <Show reviews> \n\n";
                String strHide=" <Hide reviews> \n\n";
                if (reviewsTxt.getText().toString().contains(strHide)){
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
     * Populates the VIDEOS list on the screen
     * @param videosAr The array with the reviews
     */
    public static SpannableStringBuilder spanText = new SpannableStringBuilder();

    public static void addVideos(Video[] videosAr) {
        spanText.clear();
        spanText.append("Videos: ");
        if (videosAr == null)  {
            spanText.append("No videos in base");
        } else {
            spanText.append(videosAr.length + "\n\n");
            for (Video video : videosAr) {
                singleTextView(videosTxt,"> ", video.getName() + " ", buildYoutubeUrl(video.getKey()).toString() );
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
                }catch (Exception e){
                    Log.i(LOG_TAG, "Exception opening " +  Uri.parse("http://ya.ru").toString() + " - " + e.toString());
                }
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(textPaint.linkColor);    // you can use custom color
                textPaint.setUnderlineText(false);    // this remove the underline
            }
        }, spanText.length() - revName.length(), spanText.length(), 0);

        spanText.append( "\n");

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
        textView.setText( spanText, TextView.BufferType.SPANNABLE);

    }
}