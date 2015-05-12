package org.campbelll.android.moviebrowser;

import android.app.Activity;
import android.app.ListFragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by campbell on 12/05/2015.
 */
public class MovieListFragment extends ListFragment {
    private static final String TAG = "MovieListFragment";
    private static final int NO_INDEX = -1;

    private MovieSelectionListener movie_selection_listener;

    private int selected_index;

    private ArrayList<Movie> demo_list;
    private MovieListAdapter demo_adapter;

    public MovieListFragment() {
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        selected_index = position;
//        getListView().setItemChecked(position, true);
        movie_selection_listener.onMovieSelected(demo_adapter.getItem(position));
    }

    @Override
    public void onResume() {
        super.onResume();

        setListShown(true);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            movie_selection_listener = (MovieSelectionListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement MovieSelectionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        demo_list = new ArrayList<Movie>();
        Movie m;
        java.util.Random rand = new java.util.Random(System.currentTimeMillis());
        byte[] b = new byte[256];
        for (int i = 1; i <= 30; i++) {
            m = new Movie();
            m.title = "Hello World" + " " + i;
            m.mpaa = "PG-" + i;
            m.rating = i % 6;
            rand.nextBytes(b);
            m.description = new String(b);
            demo_list.add(m);
        }

        selected_index = NO_INDEX;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        demo_adapter = new MovieListAdapter(getActivity(), R.layout.movie_list_item, R.id.movie_title, demo_list);
        getListView().setAdapter(demo_adapter);

        // Restore selected index
        if (savedInstanceState != null) {
            selected_index = savedInstanceState.getInt("selected_index");
        }

        if (selected_index != NO_INDEX) {
            movie_selection_listener.onMovieSelected(demo_adapter.getItem(selected_index));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save selected index
        outState.putInt("selected_index", selected_index);
        super.onSaveInstanceState(outState);
    }
}
