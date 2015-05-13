package org.campbelll.android.moviebrowser;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by campbell on 13/05/2015.
 */
public class TMDBClientFragment extends Fragment {
    private static final String TAG = "TMDBClientFragment";

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            tmdbResponseListener = (TMDBResponseListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement TMDBResponseListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        bitmapCache = new BitmapCache();
        imageLoader = new ImageLoader(requestQueue, bitmapCache);

        setRetainInstance(true);
    }

    public void cancelAllRequests() {
        requestQueue.cancelAll(this);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

}
