package com.amargodigits.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.amargodigits.movies.model.Movie;
import com.amargodigits.movies.utils.MovieAdapter;
import com.amargodigits.movies.utils.NetworkUtils.LoadDataTask;

import static com.amargodigits.movies.utils.MovieAdapter.gridColumnsNumber;
import static com.amargodigits.movies.utils.NetworkUtils.isOnline;

public class MainActivity extends AppCompatActivity {
    public static String LOG_TAG = "Movies Log";
    public static Movie[] movieList;
    public static RecyclerView mRecyclerView;
    //    public static TextView mPopularItem;
//    public static TextView mTopratedItem;
    public String sortOrder;
    public static SharedPreferences mSharedPref;
    static Toolbar mainToolbar;
    Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.movie_rv);
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        sortOrder = mSharedPref.getString("SORT", "popular");
        Log.i(LOG_TAG, "MainActivity onCreate sortOrder = " + sortOrder);
        if (!( sortOrder.contains("liked") || sortOrder.contains("top_rated") || sortOrder.contains("popular") )) {
            sortOrder = "top_rated";
        }
        mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        mainToolbar.setTitle(makeTitle(sortOrder));
        setSupportActionBar(mainToolbar);


        if (sortOrder.contains("liked")) {
            LoadDataTask mLikedAsyncTask = new LoadDataTask(getApplicationContext());
            mLikedAsyncTask.execute("liked");
        } else {
            if (isOnline(getApplicationContext())) {
                try {
                    LoadDataTask mAsyncTasc = new LoadDataTask(getApplicationContext());
                    mAsyncTasc.execute(sortOrder);
                } catch (Exception e) {
                    Log.i(LOG_TAG, "onCreateException " + e.toString());
                }
            } else {
                Toast.makeText(this, "Network connection required", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void doRecView(Context tContext, String sortOrder) {
        MovieAdapter mAdapter = new MovieAdapter(tContext, movieList);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(tContext, gridColumnsNumber(tContext));
        try {
            mainToolbar.setTitle(makeTitle(sortOrder));
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            Log.i("WD", "doRecView1 Exception " + e.toString());
        }

    }


    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        mMenu = menu;
        sortOrder = mSharedPref.getString("SORT", "popular");
        try {
            colorMenu(sortOrder);
        } catch (Exception e) {
            Log.i(LOG_TAG, "MainActivity onCreateOptionsMenu colorMenu Exception " + e.toString());
        }
        return true;
    }

    public void colorMenu(String sortOrder) {
//        mSharedPref = getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = mSharedPref.edit();
//        editor.putString("SORT", sortOrder);
//        editor.apply();
        Drawable likedDrawable = mMenu.findItem(R.id.sort_liked).getIcon();
        Drawable popularDrawable = mMenu.findItem(R.id.sort_popular).getIcon();
        Drawable top_ratedDrawable = mMenu.findItem(R.id.sort_toprated).getIcon();
        switch (sortOrder) {
            case "popular": {
                popularDrawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                top_ratedDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                likedDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                break;
            }
            case "top_rated": {
                popularDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                top_ratedDrawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                likedDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                break;
            }
            case "liked": {
                popularDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                top_ratedDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                likedDrawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                break;
            }
        }
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu.
     *
     * @param item The menu item that was selected by the user
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPref.edit();
        switch (id) {
            case R.id.sort_popular: {
                colorMenu("popular");
                editor.putString("SORT", "popular");
                editor.apply();
                LoadDataTask mAsyncTasc = new LoadDataTask(getApplicationContext());
                mAsyncTasc.execute("popular");
                return true;
            }
            case R.id.sort_toprated: {
                colorMenu("top_rated");
                editor.putString("SORT", "top_rated");
                editor.apply();
                LoadDataTask mAsyncTasc = new LoadDataTask(getApplicationContext());
                mAsyncTasc.execute("top_rated");
                return true;
            }
            case R.id.sort_liked: {
                colorMenu("liked");
                editor.putString("SORT", "liked");
                editor.apply();
                LoadDataTask mAsyncTask = new LoadDataTask(getApplicationContext());
                mAsyncTask.execute("liked");
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private static String makeTitle(String sortOrder) {
        sortOrder = sortOrder.replace("_", " ");
        return sortOrder.substring(0, 1).toUpperCase() + sortOrder.substring(1) + " movies";
    }

}