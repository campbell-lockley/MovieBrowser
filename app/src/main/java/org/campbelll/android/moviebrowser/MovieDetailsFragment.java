package org.campbelll.android.moviebrowser;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by campbell on 12/05/2015.
 */
public class MovieDetailsFragment extends Fragment {
    private static final String TAG = "MovieDetailsFragment";

    public MovieDetailsFragment() {
    }

    public void setContent(Movie movie) {
        TextView title_view = (TextView)getView().findViewById(R.id.movie_details_title);
//        TextView mpaa_view = (TextView)getView().findViewById(R.id.movie_details_mpaa);
        TextView description_view = (TextView)getView().findViewById(R.id.movie_details_description);
        title_view.setText(movie.title);
//        mpaa_view.setText(movie.mpaa);
        description_view.setText(movie.description);
        ImageView star_view;
        // Clear image representation of rating
        switch (movie.rating) {
            case 0:
                star_view = (ImageView)getView().findViewById(R.id.movie_details_star_1);
                star_view.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
            case 1:
                star_view = (ImageView)getView().findViewById(R.id.movie_details_star_2);
                star_view.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
            case 2:
                star_view = (ImageView)getView().findViewById(R.id.movie_details_star_3);
                star_view.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
            case 3:
                star_view = (ImageView)getView().findViewById(R.id.movie_details_star_4);
                star_view.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
            case 4:
                star_view = (ImageView)getView().findViewById(R.id.movie_details_star_5);
                star_view.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
        }
        // Set image representation of rating
        switch (movie.rating) {
            case 5:
                star_view = (ImageView)getView().findViewById(R.id.movie_details_star_5);
                star_view.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
            case 4:
                star_view = (ImageView)getView().findViewById(R.id.movie_details_star_4);
                star_view.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
            case 3:
                star_view = (ImageView)getView().findViewById(R.id.movie_details_star_3);
                star_view.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
            case 2:
                star_view = (ImageView)getView().findViewById(R.id.movie_details_star_2);
                star_view.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
            case 1:
                star_view = (ImageView)getView().findViewById(R.id.movie_details_star_1);
                star_view.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
        }

        // Set attribution for tmdb. See https://www.themoviedb.org/documentation/api/terms-of-use
        ImageView tmdb_logo_view = (ImageView)getView().findViewById(R.id.movie_details_tmdb_logo);
        TextView tmdb_note_view = (TextView)getView().findViewById(R.id.movie_details_tmdb_note);
        tmdb_logo_view.setImageResource(R.drawable.tmdb_logo);
        tmdb_note_view.setText(R.string.tmdb_notification);
    }

    public void clear() {
        TextView title_view = (TextView)getView().findViewById(R.id.movie_details_title);
        TextView description_view = (TextView)getView().findViewById(R.id.movie_details_description);
        ImageView star_view_1 = (ImageView)getView().findViewById(R.id.movie_details_star_1);
        ImageView star_view_2 = (ImageView)getView().findViewById(R.id.movie_details_star_2);
        ImageView star_view_3 = (ImageView)getView().findViewById(R.id.movie_details_star_3);
        ImageView star_view_4 = (ImageView)getView().findViewById(R.id.movie_details_star_4);
        ImageView star_view_5 = (ImageView)getView().findViewById(R.id.movie_details_star_5);
        TextView tmdb_note_view = (TextView)getView().findViewById(R.id.movie_details_tmdb_note);
        ImageView tmdb_logo_view = (ImageView)getView().findViewById(R.id.movie_details_tmdb_logo);

        title_view.setText("");
        description_view.setText("");
        star_view_1.setImageResource(android.R.color.transparent);
        star_view_2.setImageResource(android.R.color.transparent);
        star_view_3.setImageResource(android.R.color.transparent);
        star_view_4.setImageResource(android.R.color.transparent);
        star_view_5.setImageResource(android.R.color.transparent);
        tmdb_note_view.setText("");
        tmdb_logo_view.setImageResource(android.R.color.transparent);

    }

    public void setMovieOverview(String overview) {
        TextView description_view = (TextView)getView().findViewById(R.id.movie_details_description);
        description_view.setText(overview);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailsView = inflater.inflate(R.layout.fragment_details, container, false);
        return detailsView;
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
}
