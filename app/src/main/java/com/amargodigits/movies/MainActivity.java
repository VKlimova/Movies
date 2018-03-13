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
    public static TextView mPopularItem;
    public static TextView mTopratedItem;
    public String sortOrder;
    public static SharedPreferences mSharedPref;
    Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.movie_rv);
        mPopularItem = findViewById(R.id.sort_popular);
        mTopratedItem =  findViewById(R.id.sort_toprated);
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        sortOrder = mSharedPref.getString("SORT", "popular");
        if (!((sortOrder=="liked")||(sortOrder=="top_rated")||(sortOrder=="popular"))){
            sortOrder="top_rated";
        }
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

    public static void doRecView(Context tContext) {
        MovieAdapter mAdapter = new MovieAdapter(tContext, movieList);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(tContext, gridColumnsNumber(tContext));
        try {
            mRecyclerView.setLayoutManager(mLayoutManager);
        } catch (Exception e) {
            Log.i("WD", "doRecView1 Exception " + e.toString());
        }
        mRecyclerView.setAdapter(mAdapter);
//        try {
//            Log.i(LOG_TAG, "popularItem.getText() = " + mPopularItem.getText().toString());
//        } catch (Exception e) {
//            Log.i(LOG_TAG, "doRecView2 Exception " +e.toString());
//        }
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
        switch (sortOrder) {
            case "popular": {
                MenuItem topitem = menu.getItem(0);
                topitem.setTitle("> Popular <");
                try {
                    onOptionsItemSelected(topitem);
                } catch (Exception e) {
                    Log.i(LOG_TAG, "onCreateOptionsMenu Exception " + e.toString());
                }
                break;
            }
            case "top_rated": {
                MenuItem topitem = menu.getItem(1);
                topitem.setTitle("> Top rated <");
                try {
                    onOptionsItemSelected(topitem);
                } catch (Exception e) {
                    Log.i(LOG_TAG, "onCreateOptionsMenu Exception " + e.toString());
                }
                break;
            }
            case "liked": {
                MenuItem topitem = menu.getItem(2);
                topitem.setTitle("> Liked <");
                try {
                    onOptionsItemSelected(topitem);
                } catch (Exception e) {
                    Log.i(LOG_TAG, "onCreateOptionsMenu Exception " + e.toString());
                }
                break;
            }
        }
        return true;
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
        TextView popularItem = findViewById(R.id.sort_popular);
        TextView topratedItem = findViewById(R.id.sort_toprated);
        TextView likedItem = findViewById(R.id.liked);

        Drawable drawable = mMenu.findItem(R.id.liked).getIcon();
        if (drawable != null) {
            drawable.mutate();
        }

        if (id == R.id.sort_popular) {
            editor.putString("SORT", "popular");
            editor.apply();
            popularItem.setText(R.string.menu_popular_selected);
            popularItem.setTextColor(Color.YELLOW);
            topratedItem.setText(R.string.menu_top_rated);
            topratedItem.setTextColor(Color.GRAY);
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            LoadDataTask mAsyncTasc = new LoadDataTask(getApplicationContext());
            mAsyncTasc.execute("popular");
            return true;
        }
        if (id == R.id.sort_toprated) {
            editor.putString("SORT", "top_rated");
            editor.commit();
            popularItem.setText(R.string.menu_popular);
            popularItem.setTextColor(Color.GRAY);
            topratedItem.setText(R.string.menu_top_rated_selected);
            topratedItem.setTextColor(Color.YELLOW);
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            LoadDataTask mAsyncTasc = new LoadDataTask(getApplicationContext());
            mAsyncTasc.execute("top_rated");
            return true;
        }

        if (id==R.id.liked){
            editor.putString("SORT", "liked");
            editor.commit();
            popularItem.setText(R.string.menu_popular);
            popularItem.setTextColor(Color.GRAY);
            topratedItem.setText(R.string.menu_top_rated_selected);
            topratedItem.setTextColor(Color.GRAY);
            likedItem.setTextColor(Color.YELLOW);
            drawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
            LoadDataTask mAsyncTask = new LoadDataTask(getApplicationContext());
            mAsyncTask.execute("liked");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}