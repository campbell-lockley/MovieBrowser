package org.campbelll.android.moviebrowser;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Fragment works in background, sends api requests to themoviedb.org's api via the com.android.volley framework.
 *
 * This fragment persists beyond an activities lifetime, so the same fragment is used when the device rotates, etc.
 *
 * getMovieList(req_type) will request an entire list of either "now_playing" or "upcoming" movies.
 * getMovieOverview(movie_id) will request the overview text for a given movie.
 *
 * getMovieList() is used to fill MovieListFragment's list with movie items.
 * getMovieOverview() is used to update MovieDetailsFragment with the matching overview text when an item in
 * MovieListFragment's list is selected.
 *
 * themoviedb.org's api does not supply a movie's overview text when "now_playing" or "upcoming" is requested, hence it
 * must be fetched later for the MovieDetailsFragment.
 *
 * NOTE: the themoviedb.org api defaults to serving "page=1" on a "now_playing" or "upcoming" request. Fetching more
 * pages with either api request is possible, but for the purposes of this app - especially as it is a 'demo' app - it
 * was decided that only fetching the first page from the api would be sufficient.
 *
 * @author Campbell Lockley
 *
 */
public class TMDBClientFragment extends Fragment {
    private static final String TAG = "TMDBClientFragment";

    /** TODO: comment */
    public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String IN_THEATRES_URL = "now_playing";
    public static final String OPENING_URL = "upcoming";
    public static final String API_ARGS = "?api_key=";
    public static final String API_KEY = "d4e013f144a2f75f2a40c5a9f7825ae6";

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private BitmapCache bitmapCache;

    private TMDBResponseListener tmdbResponseListener;

    public TMDBClientFragment() {
    }

    /**
     * Send api request to get list of "now_playing" or "upcoming" movies, setting the response handler to be the
     * activity.
     *
     * @param req_type Must be either "In Theatres" to get the api's "now_playing" movie list, or "Opening" to get the
     *                 api's "upcoming" movie list.
     */
    public void getMovieList(String req_type) {
        String urlRequest;
        // Construct api call
        switch (req_type) {
            case "In Theatres":
                urlRequest = BASE_URL + IN_THEATRES_URL + API_ARGS + API_KEY;
                break;
            case "Opening":
                urlRequest = BASE_URL + OPENING_URL + API_ARGS + API_KEY;
                break;
            default:
                throw new IllegalArgumentException("req_type must be \"In Theates\" or \"Opening\"");
        }

        // Setup listeners for response
        JsonObjectRequest request = new JsonObjectRequest(Method.GET, urlRequest, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                tmdbResponseListener.onTMDBListResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tmdbResponseListener.onTMDBListResponse(null);
            }
        });

        // Send request to volley's queue
        requestQueue.add(request);
    }

    /**
     * Uses the api's movie_id to get the movie overview text for the corresponding movie, setting the response handler
     * to be the activity.
     *
     * @param movie_id themoviedb.org api's id for the movie to request the overview for.
     */
    public void getMovieOverview(String movie_id) {
        // Construct api call
        final String urlRequest = BASE_URL + movie_id + API_ARGS + API_KEY;

        // Setup listeners for response
        JsonObjectRequest request = new JsonObjectRequest(Method.GET, urlRequest, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                tmdbResponseListener.onTMDBDetailsResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tmdbResponseListener.onTMDBDetailsResponse(null);
            }
        });

        // Send request to volley's queue
        requestQueue.add(request);
    }

    /** Set the activity to be the TMDBResponseListener */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            tmdbResponseListener = (TMDBResponseListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement TMDBResponseListener");
        }
    }

    /** Setup Volley.RequestQueue and Volley.toolbox.ImageLoader, and set fragment to persist beyond activity */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        bitmapCache = new BitmapCache();
        imageLoader = new ImageLoader(requestQueue, bitmapCache);

        setRetainInstance(true);
    }

    /** Cancel all pending Volley network requests */
    public void cancelAllRequests() {
        requestQueue.cancelAll(this);
    }

    /** Utility method to get ImageLoader */
    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
