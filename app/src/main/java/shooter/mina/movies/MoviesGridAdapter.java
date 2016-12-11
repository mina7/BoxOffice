package shooter.mina.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


class MoviesGridAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> PosterURL;
    private ArrayList<String> Title;
    private ArrayList<Double> Rating;
    private LayoutInflater inflater;

    MoviesGridAdapter(Context c, ArrayList<String> url, ArrayList<String> title, ArrayList<Double> rating) {
        this.context = c;
        this.PosterURL = url;
        this.Title = title;
        this.Rating = rating;
        inflater = (LayoutInflater.from(context));
    }

    public int getCount() {
        return PosterURL.size();
    }

    public Object getItem(int x) {
        return PosterURL.get(x);
    }

    public long getItemId(int id) {
        return id;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.one_item, null);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_itemPoster);
        Picasso.with(context).load(PosterURL.get(position)).into(imageView);

        TextView textView = (TextView) convertView.findViewById(R.id.textView_itemTitle);
        textView.setText(Title.get(position));

        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar_itemRating);
        double ratingDouble = Rating.get(position);
        ratingBar.setRating((float) ratingDouble);

        return convertView;
    }

}
