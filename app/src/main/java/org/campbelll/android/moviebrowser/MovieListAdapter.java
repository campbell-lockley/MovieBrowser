package org.campbelll.android.moviebrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Custom ArrayAdapter implementation for the list in MovieListAdapter.
 *
 * Inflates and updates custom layout: R.layout.movie_list_item.
 *
 * @author Campbell Lockley
 *
 */
public class MovieListAdapter extends ArrayAdapter<Movie> {
    private LayoutInflater layout_inflater;
    private ImageLoader imageLoader;

    /**
     * Constructor
     *
     * @param context The current context.
     * @param item_layout_id The resource ID for a layout file containing a layout to use when
     *                 instantiating views.
     * @param default_text_id The id of the TextView within the layout resource to be populated
     * @param movies The movies to represent in the ListView.
     * @param imageLoader The image loader to set com.android.volley.toolbox.NetworkImageView images with.
     */
    public MovieListAdapter(Context context, int item_layout_id, int default_text_id, List<Movie> movies,
                            ImageLoader imageLoader) {
        super(context, item_layout_id, default_text_id, movies);
        layout_inflater = LayoutInflater.from(context);
        this.imageLoader = imageLoader;
    }

    /**
     * Get a View that displays the movie at the specified position in the data set.
     * Inflates views from R.layout.movie_list_item.
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     * @param parent The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layout_inflater.inflate(R.layout.movie_list_item, null);
            holder = new ViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.movie_title);
            holder.thumbnail = (NetworkImageView)convertView.findViewById(R.id.movie_thumbnail);
            holder.star_1 = (ImageView)convertView.findViewById(R.id.movie_star_1);
            holder.star_2 = (ImageView)convertView.findViewById(R.id.movie_star_2);
            holder.star_3 = (ImageView)convertView.findViewById(R.id.movie_star_3);
            holder.star_4 = (ImageView)convertView.findViewById(R.id.movie_star_4);
            holder.star_5 = (ImageView)convertView.findViewById(R.id.movie_star_5);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        Movie movie = this.getItem(position);
        holder.title.setText(movie.title);
        holder.thumbnail.setImageUrl(movie.thumb_url, imageLoader);
        // Clear image representation of rating
        switch (movie.rating) {
            case 0:
                holder.star_1.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
            case 1:
                holder.star_2.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
            case 2:
                holder.star_3.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
            case 3:
                holder.star_4.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
            case 4:
                holder.star_5.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
        }
        // Set image representation of rating
        switch (movie.rating) {
            case 5:
                holder.star_5.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
            case 4:
                holder.star_4.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
            case 3:
                holder.star_3.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
            case 2:
                holder.star_2.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
            case 1:
                holder.star_1.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
        }

        return convertView;
    }

    /** Utility view holder */
    static class ViewHolder {
        TextView title;
        NetworkImageView thumbnail;
        ImageView star_1;
        ImageView star_2;
        ImageView star_3;
        ImageView star_4;
        ImageView star_5;
    }
}
