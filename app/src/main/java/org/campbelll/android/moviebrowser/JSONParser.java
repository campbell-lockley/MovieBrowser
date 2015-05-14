package org.campbelll.android.moviebrowser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

;

/**
 * This code is adapted from org.stevej.android.moviebrowser.JSONParser
 *
 * Created by campbell on 13/05/2015.
 */
public class JSONParser {
    private static final String	TAG	= "JSONParser";

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String BASE_HOMEPAGE_URL = "https://www.themoviedb.org/movie/";
    // Use smallest image provided to reduce network bandwidth usage
    public static final String IMAGE_FORMAT = "w92";

    public static Movie parseMovieJSON(JSONObject json_movie) {

        Movie movie = new Movie();
        movie.title = json_movie.optString("title");
//        movie.mpaa = json_movie.optString("mpaa_rating");
        movie.id = json_movie.optString("id");
        movie.webpage = BASE_HOMEPAGE_URL + movie.id;
        movie.thumb_url = BASE_IMAGE_URL + IMAGE_FORMAT + json_movie.optString("poster_path");

//        movie.description = json_movie.optString("synopsis");

//        JSONObject json_ratings = json_movie.optJSONObject("ratings");
        movie.rating = (int)Math.round(json_movie.optDouble("vote_average") / 2);

//        JSONObject json_links = json_movie.optJSONObject("links");
//        movie.rt_url = json_links.optString("alternate");

//        JSONObject json_posters = json_movie.optJSONObject("posters");
//        movie.thumb_url = json_posters.optString("thumbnail");

        return movie;
    }

    public static ArrayList<Movie> parseMovieListJSON(JSONObject json_movie_list) {
        ArrayList<Movie> movies = new ArrayList<Movie>();

        try {
            Log.d(TAG, json_movie_list.toString(2));
            JSONArray movie_list = json_movie_list.getJSONArray("results");

            for (int i = 0; i < movie_list.length(); i++) {

                JSONObject movie = movie_list.getJSONObject(i);

                movies.add(parseMovieJSON(movie));
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException");
            e.printStackTrace();
            return movies;
        }
        return movies;
    }

    public static String parseMovieOverviewJSON(JSONObject json_movie_details) {
        return json_movie_details.optString("overview");
    }

}
