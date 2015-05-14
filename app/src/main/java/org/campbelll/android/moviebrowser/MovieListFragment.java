package org.campbelll.android.moviebrowser;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Fragment displays a list of movies which are obtained from themoviedb.org's web api. Works with MovieDetailsFragment
 * in a sliding layout.
 *
 * Selecting a movie in MovieListFragment's list will display that movie's details in MovieDetailsFragment.
 *
 * @author Campbell Lockley
 *
 */
public class MovieListFragment extends ListFragment {
    private static final String TAG = "MovieListFragment";
    private static final int NO_INDEX = -1;

    private MovieSelectionListener movie_selection_listener;

    private int selected_index = NO_INDEX;

    private ArrayList<Movie> movie_list = new ArrayList<>();
    private MovieListAdapter movie_adapter;

    public MovieListFragment() {
    }

    /**
     * Handles list items being selected.
     *
     * Uses MovieSelectionListener to update MovieDetailsFragment with the selected movie's details.
     *
     * @param l The ListView where the click happened
     * @param v The view that was clicked within the ListView
     * @param position The position of the view in the list
     * @param id The row id of the item that was clicked
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        selected_index = position;
        getListView().setItemChecked(position, true);
        movie_selection_listener.onMovieSelected(movie_adapter.getItem(position));
    }

    /**
     * Update MovieListFragment's list with the list of movies retrieved from themoviedb.org.
     *
     * @param retrievedMovies List of movies retrieved from themoviedb.org.
     * @param navListChanged Whether the selected navigation list item has changed.
     */
    public void update(ArrayList<Movie> retrievedMovies, boolean navListChanged) {
        Log.d(TAG, "update()");

        setListShown(true);

        movie_list.clear();
        movie_list.addAll(retrievedMovies);
        movie_adapter.notifyDataSetChanged();

        if (navListChanged) {
            // Select first item if showing different list
            getListView().setItemChecked(selected_index, false);
            selected_index = NO_INDEX;
            getListView().setSelection(0);
        } else if (movie_adapter.getCount() > 0 && selected_index != NO_INDEX) {
            // If showing same list, restore selection
            getListView().setItemChecked(selected_index, true);
            getListView().setSelection(selected_index);
            getListView().smoothScrollToPositionFromTop(selected_index, 200, 0);
            movie_selection_listener.onMovieSelected(movie_adapter.getItem(selected_index));
        }
    }

    /**
     * Launches an intent to go to the web page of the currently selected movie in the list.
     *
     */
    public void gotoWebPage() {
        if (movie_adapter.getCount() > 0 && selected_index != NO_INDEX) {
            Intent startBrowser = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(movie_adapter.getItem(selected_index).webpage));

            if (startBrowser.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(startBrowser);
            }
        }
    }

    /** Restore selection in list */
    @Override
    public void onResume() {
        super.onResume();

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setListShown(true);

        if (movie_adapter.getCount() > 0 && selected_index != NO_INDEX) {
            getListView().setItemChecked(selected_index, true);
            getListView().setSelection(selected_index);
            getListView().smoothScrollToPositionFromTop(selected_index, 200, 0);
            movie_selection_listener.onMovieSelected(movie_adapter.getItem(selected_index));
        }
    }

    /** Set activity as the MovieSelectionListener */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            movie_selection_listener = (MovieSelectionListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement MovieSelectionListener");
        }
    }

    /** Restore currently selected index */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            selected_index = savedInstanceState.getInt("selected_index");
        }
    }

    /** Setup MovieListAdapter */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        movie_adapter = new MovieListAdapter(getActivity(), R.layout.movie_list_item, R.id.movie_title, movie_list,
                ((MovieBrowserActivity)getActivity()).getImageLoader());
        getListView().setAdapter(movie_adapter);
    }

    /** Save currently selected index */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("selected_index", selected_index);
        super.onSaveInstanceState(outState);
    }
}
