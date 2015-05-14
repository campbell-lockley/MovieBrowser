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

/**
 * This is a demo movie viewer app. It shows 2 lists: a list of movies which are currently showing in theatres, as well
 * as a list of movies which will soon be released.
 *
 * The content in the lists are retrieved from themoviedb.org's web api:
 *      http://api.themoviedb.org/
 *
 * This activity uses 3 fragments:
 * <ul>
 *     <li>MovieListFragment: Displays a list of movies retrieved from themoviedb.org</li>
 *     <li>MovieDetailsFragment: Displays the details of the movie currently selected in MovieListFragment</li>
 *     <li>TMDBClientFragment: A non-UI fragment which sends api requests to themoviedb.org's api</li>
 * </ul>
 *
 * The action bar has a navigation list with the options:
 * <ul>
 *     <li>In Theatres - fetches and shows list of "now_playing" movies</li>
 *     <li>Opening - fetches and shows list of "upcoming" movies</li>
 * </ul>
 * The web browser action will launch a web browser intent pointing to the themoviedb.org page for the movie currently
 * selected in the MovieListFragment's movie list.
 *
 * @author Campbell Lockley
 * 
 */
public class MovieBrowserActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener,
        MovieSelectionListener, TMDBResponseListener {
    /** Tags for this activity and its fragments */
    private static final String TAG = "MovieBrowserActivity";
    private static final String TMDB_CLIENT_FRAGMENT_TAG = "TMDBClientFragment";
    private static final String MOVIE_DETAILS_FRAGMENT_TAG = "MovieDetailsFragment";
    private static final String MOVIE_LIST_FRAGMENT_TAG = "MovieListFragment";

    private static final int NO_INDEX = -1;

    /** Keeps track of selected nav list item for MovieListFragment's sake */
    private int selected_nav_index = NO_INDEX;
    private boolean nav_list_changed = false;

    private FragmentManager fragmentManager;

    private TMDBClientFragment tmdbClientFragment;

    /**
     * Handles response for movie list from themoviedb.org, passing response to MovieListFragment for it to generate
     * it's movie list with.
     *
     * @param jsonObject Response from themoviedb.org
     */
    @Override
    public void onTMDBListResponse(JSONObject jsonObject) {
        MovieListFragment movieListFragment = (MovieListFragment)fragmentManager
                .findFragmentByTag(MOVIE_LIST_FRAGMENT_TAG);
        if (movieListFragment != null) {
            movieListFragment.update(JSONParser.parseMovieListJSON(jsonObject), nav_list_changed);
        }
    }

    /**
     * Handles response for movie overview from themoviedb.org, passing response to MovieDetailsFragment for it to
     * update and show the overview text.
     *
     * @param jsonObject Response from themoviedb.org
     */
    @Override
    public void onTMDBDetailsResponse(JSONObject jsonObject) {
        MovieDetailsFragment movieDetailsFragment = (MovieDetailsFragment)fragmentManager
                .findFragmentByTag(MOVIE_DETAILS_FRAGMENT_TAG);
        if (movieDetailsFragment != null) {
            movieDetailsFragment.setMovieOverview(JSONParser.parseMovieOverviewJSON(jsonObject));
        }
    }

    /**
     * Acts as a message passer between MovieListFragment and MovieDetailsFragment to update MovieDetailsFragment when
     * a movie is selected in MovieListFragment's list.
     *
     * @param movie The details of the movie selected in MovieListFragment's list.
     */
    @Override
    public void onMovieSelected(Movie movie) {
        Log.d(TAG, "onMovieSelected");
        MovieDetailsFragment movieDetailsFragment = (MovieDetailsFragment)getFragmentManager()
                .findFragmentByTag(MOVIE_DETAILS_FRAGMENT_TAG);
        movieDetailsFragment.setContent(movie);
        tmdbClientFragment.getMovieOverview(movie.id);

        // Show details fragment
        SlidingPaneLayout slidingPaneLayout = (SlidingPaneLayout)findViewById(R.id.sliding_layout);
        slidingPaneLayout.closePane();
    }

    /**
     * Handles dropdown menu selection events.
     *
     * If a different dropdown menu item is selected, TMDBClientFragment gets the movie data for MovieListFragment.
     *
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     */
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
        MovieListFragment movieListFragment = (MovieListFragment)fragmentManager
                .findFragmentByTag(MOVIE_LIST_FRAGMENT_TAG);
        if (movieListFragment != null) {
            movieListFragment.setListShown(false);
        }

        // Clear MovieDetailsFragment
        if (nav_list_changed) {
            MovieDetailsFragment movieDetailsFragment =
                    (MovieDetailsFragment)fragmentManager.findFragmentByTag(MOVIE_DETAILS_FRAGMENT_TAG);
            if (movieDetailsFragment != null) {
                movieDetailsFragment.clear();
            }
        }
    }

    /** Do nothing */
    @Override
    public void onNothingSelected(AdapterView<?> parent) { return; }

    /**
     * Setup SlidingPaneLayout listener, start TMDBClientFragment running, setup the action bar, and restore the
     * currently selected navigation list item.
     *
     * @param savedInstanceState
     */
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

    /**
     * Save currently selected navigation list item.
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("selected_nav_index", selected_nav_index);
        super.onSaveInstanceState(outState);
    }

    /**
     * Setup the action bar.
     *
     */
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

    /**
     * Attach a non-UI TMDBClientFragment to this activity which will send the themoviedb.org api requests.
     *
     */
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
        getMenuInflater().inflate(R.menu.menu_movie_browser, menu);
        return true;
    }

    /**
     * Handle action bar item clicks.
     *
     * Clicking on "home" button when MovieDetailsFragment is showing (in portrait) will show the MovieListFragment.
     *
     * Clicking on the "browser" button will open a browser pointing to the themoviedb.org page of the selected movie.
     *
     * Clicking on "settings" or "legal notices" will show a toast; "settings" and "legal notices" are not implemented
     * yet.
     *
     * @param item the MenuItem instance representing the selected menu item
     * @return true if the event was handled and further processing should not occur
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "home pressed");
                SlidingPaneLayout slidingPaneLayout = (SlidingPaneLayout)findViewById(R.id.sliding_layout);
                if (!slidingPaneLayout.isOpen()) slidingPaneLayout.openPane();
                return true;
            case R.id.action_browser:
                Log.d(TAG, "Browser selected");
                // Send intent to web browser if list item is selected
                MovieListFragment movieListFragment =
                        (MovieListFragment)fragmentManager.findFragmentByTag(MOVIE_LIST_FRAGMENT_TAG);
                movieListFragment.gotoWebPage();
                return true;
            case R.id.action_settings:
                Log.d(TAG, "Settings selected");
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_legal:
                Log.d(TAG, "Legal selected");
                Toast.makeText(getApplicationContext(), "Legal Notices", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Make sure all pending network requests are cancelled when this activity stops.
     *
     */
    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        if (tmdbClientFragment != null) {
            tmdbClientFragment.cancelAllRequests();
        }
    }

    /**
     * When MovieDetailsFragment is showing (in portrait), show the MovieListFragment.
     *
     * Otherwise perform normal back operation.
     *
     */
    @Override
    public void onBackPressed() {
        SlidingPaneLayout slidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.sliding_layout);
        if (slidingPaneLayout.isOpen()) {
            super.onBackPressed();
        } else {
            slidingPaneLayout.openPane();
        }
    }

    /** Utility method to get ImageLoader from the TMDBClientFragment */
    public ImageLoader getImageLoader() { return tmdbClientFragment.getImageLoader(); }
}
