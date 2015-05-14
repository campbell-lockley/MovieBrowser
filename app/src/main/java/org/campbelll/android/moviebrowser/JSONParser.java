package org.campbelll.android.moviebrowser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This is a utility class to parse the JSON responses from the themoviedb.org api.
 *
 * It parses two types of responses:
 * <ul>
 *     <li>Movie list responses from api calls using "now_playing" and "upcoming" to extract a list of movies,</li>
 *     <li>and movie details responses from api calls using movie/[id] to extract a movie's overview text.</li>
 * </ul>
 *
 * This code is adapted from org.stevej.android.moviebrowser.JSONParser
 *
 */
public class JSONParser {
    private static final String	TAG	= "JSONParser";

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String BASE_HOMEPAGE_URL = "https://www.themoviedb.org/movie/";
    // Use smallest image provided by api to reduce network bandwidth usage
    public static final String IMAGE_FORMAT = "w92";

    /**
     * Parses a movie JSON object and returns the resulting movie.
     *
     * @param json_movie JSON object representation of a movie.
     * @return The movie.
     */
    public static Movie parseMovieJSON(JSONObject json_movie) {
        Movie movie = new Movie();
        movie.title = json_movie.optString("title");
        movie.id = json_movie.optString("id");
        movie.webpage = BASE_HOMEPAGE_URL + movie.id;
        movie.thumb_url = BASE_IMAGE_URL + IMAGE_FORMAT + json_movie.optString("poster_path");
        movie.rating = (int)Math.round(json_movie.optDouble("vote_average") / 2);

        return movie;
    }

    /**
     * Parses a JSON response from themoviedb.org as a JSON array of movie objects, calling parseMovieJSON() on each
     * JSON movie object.
     *
     * @param json_movie_list JSON object representation of a list of movies.
     * @return List of movies contained in json_movie_list.
     */
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

    /**
     * Parses a JSON response from themoviedb.org and extracts the movie overview text.
     *
     * @param json_movie_details JSON object containing the overview text.
     * @return The overview text.
     */
    public static String parseMovieOverviewJSON(JSONObject json_movie_details) {
        return json_movie_details.optString("overview");
    }
}
