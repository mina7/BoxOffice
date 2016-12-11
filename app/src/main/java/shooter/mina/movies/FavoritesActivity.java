package shooter.mina.movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import shooter.mina.movies.DataBasesModel.MovieDB;

public class FavoritesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    ArrayList<String> posters = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> overView = new ArrayList<>();
    ArrayList<Integer> movieId = new ArrayList<>();
    ArrayList<Double> rating = new ArrayList<>();
    MoviesGridAdapter movieAdapter;
    Realm realm;
    GridView favoritesGrid;

    @Override
    protected void onStart() {
        super.onStart();
        RealmResults<MovieDB> favorites = realm.where(MovieDB.class).findAll();
        for (MovieDB result : favorites) {
            posters.add(result.poster);
            movieId.add(result.movieId);
            title.add(result.name);
            overView.add(result.overView);
            date.add(result.date);
            rating.add(result.rating);
            movieAdapter = new MoviesGridAdapter(this, posters, title, rating);
            favoritesGrid.setAdapter(movieAdapter);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        getSupportActionBar().setTitle("Favorites");

        realm= Realm.getDefaultInstance();

        favoritesGrid = (GridView) findViewById(R.id.favoritesGrid);
        favoritesGrid.setOnItemClickListener(this);
    }

    @Override
    protected void onStop() {
        posters.clear();
        movieId.clear();
        title.clear();
        overView.clear();
        date.clear();
        rating.clear();
        super.onStop();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        DetailsFragment DF = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString("title", title.get(position));
        args.putString("date", date.get(position));
        args.putString("poster", posters.get(position));
        args.putBoolean("activityFlag", true);
        args.putDouble("rating", rating.get(position));
        args.putString("overView", overView.get(position));
        args.putInt("movieId", movieId.get(position));
        DF.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.favorites_activity, DF).addToBackStack("favorites").commit();
    }
}
