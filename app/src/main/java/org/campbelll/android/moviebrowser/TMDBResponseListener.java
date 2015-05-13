package org.campbelll.android.moviebrowser;

import org.json.JSONObject;

/**
 * Created by campbell on 13/05/2015.
 */
public interface TMDBResponseListener {
    public void onTMDBListResponse(JSONObject jsonObject);

    public void onTMDBDetailsResponse(JSONObject jsonObject);
}
