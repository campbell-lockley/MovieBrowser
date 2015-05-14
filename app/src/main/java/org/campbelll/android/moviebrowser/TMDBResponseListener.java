package org.campbelll.android.moviebrowser;

import org.json.JSONObject;

/**
 * Implementers of this interface will handle both types of themoviedb.org api responses.
 *
 * onTMDBListResponse() accepts a JSON object representation of a list of movies generated from either "now_playing" or
 * "upcoming" themoviedb.org api calls.
 *
 * onTMDBDetailsResponse() accepts a JSON object representation of a movie containing the movie's overview text.
 *
 * Created by campbell on 13/05/2015.
 */
public interface TMDBResponseListener {
    public void onTMDBListResponse(JSONObject jsonObject);
    public void onTMDBDetailsResponse(JSONObject jsonObject);
}
