package shooter.mina.movies;

/**
 * Created by Mina on 03/12//2016.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import shooter.mina.movies.Models.MovieTune;

public class MoviesFragment extends Fragment implements AdapterView.OnItemClickListener{

    int flag, LoadMore = 0, index;
    Context context = getContext();
    SwipeRefreshLayout swipe;
    Gson gson;
    MovieTune movieTune;
    ArrayList<String> poster = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> overView = new ArrayList<>();
    ArrayList<Integer> movieId = new ArrayList<>();
    ArrayList<Double> rating = new ArrayList<>();
    private String url;
    TextView textView;
    MoviesGridAdapter movieAdapter;
    GridView moviesGrid;
    private int page;
    ProgressBar progressBar;
    Activity obj=new Activity();
    String API_KEY = "api_key="+obj.MovieAPIKey;

    Bundle savedInstanceState;

    @Override
    public void onSaveInstanceState(Bundle outPut) {
        index = moviesGrid.getFirstVisiblePosition();
        outPut.putInt("index", index);
        outPut.putStringArrayList("poster", poster);
        outPut.putStringArrayList("date", date);
        outPut.putStringArrayList("title", title);
        outPut.putStringArrayList("overView", overView);
        outPut.putIntegerArrayList("movieId", movieId);

        double[] ratingArray = new double[rating.size()];
        for (int i = 0; i < ratingArray.length; i++) {
            ratingArray[i] = rating.get(i);
        }

        outPut.putDoubleArray("rating", ratingArray);
        outPut.putInt("flag", flag);
        outPut.putInt("page", page);
        outPut.putInt("LoadMore", LoadMore);
        super.onSaveInstanceState(outPut);
    }

    public MoviesFragment() {
    }

    @Override
    public void onStart() {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Box Office");
        super.onStart();
        if (!obj.isOnline()){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.activity_main, new NoInternetFragment()).commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (!obj.isOnline()){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.activity_main, new NoInternetFragment()).commit();
        }

        if (savedInstanceState!= null) {
            index = savedInstanceState.getInt("index", 3);
            flag = savedInstanceState.getInt("flag");
            LoadMore = savedInstanceState.getInt("LoadMore");
            poster = savedInstanceState.getStringArrayList("poster");
            date = savedInstanceState.getStringArrayList("date");
            title = savedInstanceState.getStringArrayList("title");
            overView = savedInstanceState.getStringArrayList("overView");
            movieId = savedInstanceState.getIntegerArrayList("movieId");
            page = savedInstanceState.getInt("page");

            double[] firstValue = savedInstanceState.getDoubleArray("rating");
            assert firstValue != null;
            for(double d : firstValue) rating.add(d);

            movieAdapter = new MoviesGridAdapter(getContext(), poster, title, rating);
        } else {
            poster = new ArrayList<>();
            movieId = new ArrayList<>();
            title = new ArrayList<>();
            overView = new ArrayList<>();
            date = new ArrayList<>();
            rating = new ArrayList<>();
        }
    }
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        setHasOptionsMenu(true);
        moviesGrid = (GridView) view.findViewById(R.id.moviesGrid);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        textView = (TextView) view.findViewById(R.id.textView_popOrTop);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        if (savedInstanceState!=null) {
            moviesGrid.setAdapter(movieAdapter);
            moviesGrid.setOnScrollListener(new EndlessScrollListener(10, page) {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    loadMoreData(page);
                    return true;
                }
            });
            progressBar.setVisibility(View.GONE);
            moviesGrid.setSelection(index);
        } else {
            poster = new ArrayList<>();
            movieId = new ArrayList<>();
            title = new ArrayList<>();
            overView = new ArrayList<>();
            date = new ArrayList<>();
            rating = new ArrayList<>();
            page=0;
            moviesGrid.setOnScrollListener(new EndlessScrollListener(10, page) {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    loadMoreData(page);
                    return true;
                }
            });
            swipe.setRefreshing(true);
            flag = 0;
            url = "http://api.themoviedb.org/3/movie/popular?page=1&"+API_KEY;
            progressBar.setVisibility(View.VISIBLE);
            filterList(url);
            swipe.setRefreshing(false);
        }

        moviesGrid.setOnItemClickListener(this);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
            }
        });
        return view;
    }

    private void filterList(String url) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        swipe.setRefreshing(true);
        asyncHttpClient.get(context, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                gson = new Gson();
                movieTune = gson.fromJson(new String(responseBody), MovieTune.class);
                List<MovieTune.ResultsBean> resultsList = movieTune.getResults();
                for (MovieTune.ResultsBean result : resultsList) {
                    poster.add("https://image.tmdb.org/t/p/w342" + result.getPoster_path());
                    movieId.add(result.getId());
                    title.add(result.getTitle());
                    overView.add(result.getOverview());
                    date.add(result.getRelease_date());
                    rating.add(result.getVote_average());
                    movieAdapter = new MoviesGridAdapter(getActivity(), poster, title, rating);
                    moviesGrid.setAdapter(movieAdapter);
                }
                swipe.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Snackbar.make(view, R.string.no_net, Snackbar.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.activity_main, new NoInternetFragment()).commit();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadMoreData(int page) {
        this.page= page;
        swipe.setRefreshing(true);
        if (LoadMore == 0)
            url = "http://api.themoviedb.org/3/movie/popular?page=" + page + "&" + API_KEY;
        else if (LoadMore == 1)
            url = "http://api.themoviedb.org/3/movie/top_rated?page=" + page + "&" + API_KEY;

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(context, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipe.setRefreshing(false);
                gson = new Gson();
                movieTune = gson.fromJson(new String(responseBody), MovieTune.class);
                List<MovieTune.ResultsBean> resultsList = movieTune.getResults();
                for (MovieTune.ResultsBean result : resultsList) {
                    poster.add("https://image.tmdb.org/t/p/w342" + result.getPoster_path());
                    movieId.add(result.getId());
                    title.add(result.getOriginal_title());
                    overView.add(result.getOverview());
                    date.add(result.getRelease_date());
                    rating.add(result.getVote_average());
                }
                movieAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Snackbar.make(view, R.string.no_net , Snackbar.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.activity_main, new NoInternetFragment()).commit();
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        DetailsFragment DF = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString("title", title.get(position));
        args.putString("date", date.get(position));
        args.putString("poster", "https://image.tmdb.org/t/p/w185" + poster.get(position));
        args.putDouble("rating", rating.get(position));
        args.putString("overView", overView.get(position));
        args.putInt("movieId", movieId.get(position));
        args.putBoolean("activityFlag", false);
        DF.setArguments(args);
        if (getArguments() != null) {
            if (getArguments().getBoolean("bool")) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.rightFrame, DF).addToBackStack("moviesDetails").commit();
            } else {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.activity_main, DF).addToBackStack("moviesDetails").commit();
            }
        }
        else {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.activity_main, DF).addToBackStack("moviesDetails").commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuFilter){
            if(flag == 1) {
                flag=0;
                LoadMore=0;
                clearResources();
                textView.setText(R.string.popular);
                url = "http://api.themoviedb.org/3/movie/popular?page=1&"+API_KEY;
                filterList(url);
                return super.onOptionsItemSelected(item);
            }
            else if(flag == 0) {
                flag = 1;
                LoadMore=1;
                textView.setText(R.string.top);
                clearResources();
                url = "http://api.themoviedb.org/3/movie/top_rated?page=1&"+API_KEY;
                filterList(url);
                return super.onOptionsItemSelected(item);
            }
        }
        else if (item.getItemId() == R.id.favorites){
            startActivity(new Intent(getContext(), Favorites.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearResources() {
        poster.clear();
        movieId.clear();
        title.clear();
        overView.clear();
        date.clear();
        rating.clear();
        page = 1;
        moviesGrid.setOnScrollListener(new EndlessScrollListener(10, page) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadMoreData(page);
                return true;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.menuFilter);
        menuItem.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        menuItem = menu.findItem(R.id.favorites);
        menuItem.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        super.onCreateOptionsMenu(menu, inflater);
    }
}