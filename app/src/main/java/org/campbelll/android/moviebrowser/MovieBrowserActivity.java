package org.campbelll.android.moviebrowser;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import org.json.JSONObject;


public class MovieBrowserActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, MovieSelectionListener, TMDBResponseListener {
    // Tags for this activity and its fragments
    private static final String TAG = "MovieBrowserActivity";
    private static final String TMDB_CLIENT_FRAGMENT_TAG = "TMDBClientFragment";
    private static final String MOVIE_DETAILS_FRAGMENT_TAG = "MovieDetailsFragment";
    private static final String MOVIE_LIST_FRAGMENT_TAG = "MovieListFragment";

    private static final int NO_INDEX = -1;

    // Keeps track of selected nav list item for MovieListFragment's sake
    private int selected_nav_index = NO_INDEX;
    private boolean nav_list_changed = false;

    private FragmentManager fragmentManager;

    private TMDBClientFragment tmdbClientFragment;

    @Override
    public void onTMDBListResponse(JSONObject jsonObject) {
        MovieListFragment movieListFragment = (MovieListFragment)fragmentManager.findFragmentByTag(MOVIE_LIST_FRAGMENT_TAG);
        if (movieListFragment != null) {
            movieListFragment.update(JSONParser.parseMovieListJSON(jsonObject), nav_list_changed);
        }
    }

    @Override
    public void onTMDBDetailsResponse(JSONObject jsonObject) {
        MovieDetailsFragment movieDetailsFragment = (MovieDetailsFragment)fragmentManager.findFragmentByTag(MOVIE_DETAILS_FRAGMENT_TAG);
        if (movieDetailsFragment != null) {
            movieDetailsFragment.setMovieOverview(JSONParser.parseMovieOverviewJSON(jsonObject));
        }
    }

    @Override
    public void onMovieSelected(Movie movie) {
        Log.d(TAG, "onMovieSelected");
        MovieDetailsFragment movieDetailsFragment = (MovieDetailsFragment)getFragmentManager().findFragmentByTag(MOVIE_DETAILS_FRAGMENT_TAG);
        movieDetailsFragment.setContent(movie);
        tmdbClientFragment.getMovieOverview(movie.id);

        // Show details fragment
        SlidingPaneLayout slidingPaneLayout = (SlidingPaneLayout)findViewById(R.id.sliding_layout);
        slidingPaneLayout.closePane();
    }

    // TODO: update: onNavigationListener to handle the navigation list selections
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] view_list = getResources().getStringArray(R.array.view_list);
        String selected = view_list[position];
        Log.d(TAG, selected + " selected");

        nav_list_changed = (selected_nav_index != position);
        selected_nav_index = position;

        // Fill list with items from api
        tmdbClientFragment.getMovieList(selected);

        // If sliding layout is showing MovieDetailsFragment, show MovieListAdapter
        SlidingPaneLayout slidingPaneLayout = (SlidingPaneLayout)findViewById(R.id.sliding_layout);
        if (!slidingPaneLayout.isOpen()) slidingPaneLayout.openPane();

        // Set MovieListFragment to show loading
        MovieListFragment movieListFragment = (MovieListFragment)fragmentManager.findFragmentByTag(MOVIE_LIST_FRAGMENT_TAG);
        if (movieListFragment != null) {
            movieListFragment.setListShown(false);
        }

        // Clear MovieDetailsFragment
        if (nav_list_changed) {
            MovieDetailsFragment movieDetailsFragment = (MovieDetailsFragment) fragmentManager.findFragmentByTag(MOVIE_DETAILS_FRAGMENT_TAG);
            if (movieDetailsFragment != null) {
                movieDetailsFragment.clear();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_browser);

        // Start with list fragment showing
        SlidingPaneLayout slidingPaneLayout = (SlidingPaneLayout)findViewById(R.id.sliding_layout);
        slidingPaneLayout.setSliderFadeColor(getResources().getColor(android.R.color.transparent));
        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.SimplePanelSlideListener() {
            @Override
            public void onPanelOpened(View panel) {
                getSupportActionBar().setHomeButtonEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }

            @Override
            public void onPanelClosed(View panel) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });
        slidingPaneLayout.openPane();

        addNonUIFragments();

        if (savedInstanceState != null) {
            selected_nav_index = savedInstanceState.getInt("selected_nav_index");
        }

        setupActionBar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("selected_nav_index", selected_nav_index);
        super.onSaveInstanceState(outState);
    }

    private void setupActionBar() {
        final ActionBar actionBar = getSupportActionBar();

        // Use custom menu
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar);

        // Display icon and disable title
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);

        // Populate navigation list
        Spinner view_list = (Spinner)findViewById(R.id.nav_list_spinner);
        ArrayAdapter view_list_adapter = ArrayAdapter.createFromResource(this, R.array.view_list,
                android.R.layout.simple_spinner_dropdown_item);
        view_list_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        view_list.setAdapter(view_list_adapter);
        view_list.setOnItemSelectedListener(this);
    }

    private void addNonUIFragments() {
        fragmentManager = getFragmentManager();

        // Add TMDBClientFragment if it doesn't exist
        tmdbClientFragment = (TMDBClientFragment)fragmentManager.findFragmentByTag(TMDB_CLIENT_FRAGMENT_TAG);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (tmdbClientFragment == null) {
            tmdbClientFragment = new TMDBClientFragment();
            ft.add(tmdbClientFragment, TMDB_CLIENT_FRAGMENT_TAG);
        }

        ft.commit();
        fragmentManager.executePendingTransactions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                SlidingPaneLayout slidingPaneLayout = (SlidingPaneLayout)findViewById(R.id.sliding_layout);
                if (!slidingPaneLayout.isOpen()) slidingPaneLayout.openPane();
                return true;
            case R.id.action_browser:
                Log.d(TAG, "Browser selected");
                // Send intent to web browser if list item is selected
                MovieListFragment movieListFragment = (MovieListFragment)fragmentManager.findFragmentByTag(MOVIE_LIST_FRAGMENT_TAG);
                movieListFragment.gotoWebPage();
                return true;
            case R.id.action_settings:
                Log.d(TAG, "Settings selected");
                Toast.makeText(getApplicationContext(), "Settings Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_legal:
                Log.d(TAG, "Legal selected");
                Toast.makeText(getApplicationContext(), "Legal Notices Selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // make sure all pending network requests are cancelled when this activity stops
    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        if (tmdbClientFragment != null) {
            tmdbClientFragment.cancelAllRequests();
        }
    }

    @Override
    public void onBackPressed() {
        SlidingPaneLayout slidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.sliding_layout);
        if (slidingPaneLayout.isOpen()) {
            super.onBackPressed();
        } else {
            slidingPaneLayout.openPane();
        }
    }

    public ImageLoader getImageLoader() {
        return tmdbClientFragment.getImageLoader();
    }
}
