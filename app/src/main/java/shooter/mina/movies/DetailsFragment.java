package shooter.mina.movies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import shooter.mina.movies.DataBasesModel.MovieDB;
import shooter.mina.movies.Models.Reviews;
import shooter.mina.movies.Models.Trailers;

/**
 * Created by Mina on 03/12//2016.
 */

public class DetailsFragment extends Fragment implements View.OnClickListener{
    public boolean favs;
    String url = "http://api.themoviedb.org/3/movie/";
    Activity obj = new Activity();
    String key = "api_key="+obj.MovieAPIKey;

    TextView textView_title, textView_rating, textView_overView, textView_date;
    ImageView imageView_poster;
    RatingBar ratingBar;
    ProgressBar progressBar;
    int movieId;
    LinearLayout dynamicTrailersView, dynamicReviewsView;
    Trailers mtrailers;
    Reviews mreview;
    String title, date, poster, overView;
    double rating;
    boolean trailersFlag = false, reviewsFlag = false;
    public DetailsFragment() {
    }

    Realm realm;
    @Override
    public void onStart() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        if (getArguments() != null) {
            title = getArguments().getString("title");
            favs = getArguments().getBoolean("activityFlag");
            date = getArguments().getString("date");
            poster = getArguments().getString("poster");
            rating = getArguments().getDouble("rating", 1.5);
            overView = getArguments().getString("overView");
            movieId = getArguments().getInt("movieId", 1);
        }
    }

    View view;
    Button button_favorite;
    ImageButton share_button;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_details);
        progressBar.setVisibility(View.GONE);
        imageView_poster = (ImageView) view.findViewById(R.id.image_poster);
        Picasso.with(getContext()).load(poster).into(imageView_poster);
        textView_title = (TextView) view.findViewById(R.id.textView_title);
        textView_title.setText(title);
        textView_rating = (TextView) view.findViewById(R.id.textView_Rating);
        textView_rating.setText(rating + " / 10.0");
        textView_overView = (TextView) view.findViewById(R.id.textView_overView);
        textView_overView.setText(overView);
        textView_date = (TextView) view.findViewById(R.id.textView_date);
        textView_date.setText(date);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingBar.setRating((float) rating);

        button_favorite = (Button) view.findViewById(R.id.button_favorite);
        button_favorite.setOnClickListener(this);
        RealmQuery<MovieDB> result = realm.where(MovieDB.class).equalTo("movieId", movieId);
        if (result.count() != 0) {
            Drawable fav = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_black_24dp);
            fav.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            button_favorite.setCompoundDrawablesWithIntrinsicBounds(null, fav, null, null);
            button_favorite.setTextColor(Color.RED);
        } else {
            Drawable fav = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_border_black_24dp);
            fav.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            button_favorite.setCompoundDrawablesWithIntrinsicBounds(null, fav, null, null);
            button_favorite.setTextColor(Color.GRAY);
        }

        dynamicReviewsView = (LinearLayout) view.findViewById(R.id.linearLayout_reviews);
        dynamicTrailersView = (LinearLayout) view.findViewById(R.id.linearLayout_trailers);

        view.findViewById(R.id.button_reviews).setOnClickListener(this);
        view.findViewById(R.id.button_trailers).setOnClickListener(this);

        share_button = (ImageButton) view.findViewById(R.id.share_button);
        share_button.setOnClickListener(this);

        return view;
    }

    private void getReviews() {
        progressBar.setVisibility(View.VISIBLE);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(getContext(), url + movieId + "/reviews?" + key, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new Gson();
                mreview = gson.fromJson(new String(responseBody), Reviews.class);
                final List<Reviews.ResultsBean> results = mreview.getResults();
                if (results.isEmpty()) {
                    TextView textView = new TextView(getContext());
                    textView.setText(R.string.no_reviews);
                    dynamicReviewsView.addView(textView);
                }
                for (int i = 0; i < results.size(); i++) {
                    TextView textViewAuthor = new TextView(getContext());
                    textViewAuthor.setText(results.get(i).getAuthor());
                    textViewAuthor.setLayoutParams(lp);
                    if (Build.VERSION.SDK_INT < 23) {
                        textViewAuthor.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
                    } else {
                        textViewAuthor.setTextAppearance(android.R.style.TextAppearance_Large);
                    }
                    dynamicReviewsView.addView(textViewAuthor);
                    TextView textViewContent = new TextView(getContext());
                    textViewContent.setText(results.get(i).getContent());
                    textViewContent.setLayoutParams(lp);
                    textViewContent.setPadding(16, 8, 8, 8);
                    if (Build.VERSION.SDK_INT < 23) {
                        textViewAuthor.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
                    } else {
                        textViewAuthor.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
                    }
                    dynamicReviewsView.addView(textViewContent);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Snackbar.make(view, R.string.no_net , Snackbar.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                reviewsFlag = false;
            }

        });
    }

    List<Trailers.ResultsBean> trailers;

    private void getTrailers() {
        progressBar.setVisibility(View.VISIBLE);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(getContext(), url + movieId + "/videos?" + key, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Gson gson = new Gson();
                mtrailers = gson.fromJson(new String(responseBody), Trailers.class);
                trailers = mtrailers.getResults();
                if (trailers.isEmpty()) {
                    TextView textView = new TextView(getContext());
                    textView.setText(R.string.no_trailers);
                    dynamicReviewsView.addView(textView);
                }
                for (int i = 0; i < trailers.size(); i++) {
                    Button btn = new Button(getContext());
                    btn.setId(i + 1);
                    btn.setText(trailers.get(i).getName());
                    btn.setLayoutParams(lp);
                    final int finalI = i;
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getContext(), view.getId() + "", Toast.LENGTH_SHORT).show();
                            trailers.get(finalI).getKey();
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" +trailers.get(finalI).getKey())));
                            } catch (ActivityNotFoundException ex) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" +trailers.get(finalI).getKey())));
                            }
                        }
                    });
                    dynamicTrailersView.addView(btn);
                }
                progressBar.setVisibility(View.GONE);
                share_button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Snackbar.make(view, R.string.no_net, Snackbar.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                trailersFlag = false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_favorite) {
            addMovieToFav();
        } else if (view.getId() == R.id.button_trailers) {
            //To check if the trailers already loaded!
            if (!trailersFlag) {
                getTrailers();
                trailersFlag = true;
            }
        } else if (view.getId() == R.id.button_reviews) {
            if (!reviewsFlag) {
                getReviews();
                reviewsFlag = true;
            }
        } else if (view.getId() == R.id.share_button) {
            if (!trailers.isEmpty()) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v="+ trailers.get(0).getKey());
                startActivity(Intent.createChooser(i, "Share URL"));
            }
        }
    }

    @Override
    public void onStop() {
        realm.close();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        super.onStop();
    }

    private void addMovieToFav() {
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    MovieDB movieDb = realm.createObject(MovieDB.class, movieId);
                    movieDb.name = title;
                    movieDb.poster = poster;
                    movieDb.movieId = movieId;
                    movieDb.rating = rating;
                    movieDb.overView = overView;
                    movieDb.date = date;
                }
            });
            Drawable fav = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_black_24dp);
            fav.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            button_favorite.setCompoundDrawablesWithIntrinsicBounds(null, fav, null, null);
            button_favorite.setTextColor(Color.RED);

            Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
        } catch (RealmPrimaryKeyConstraintException exception) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<MovieDB> result = realm.where(MovieDB.class).equalTo("movieId", movieId).findAll();
                    result.deleteAllFromRealm();
                }
            });
            Drawable fav = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_border_black_24dp);
            fav.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            button_favorite.setCompoundDrawablesWithIntrinsicBounds(null, fav, null, null);
            button_favorite.setTextColor(Color.GRAY);

            Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();

        }
    }
}
