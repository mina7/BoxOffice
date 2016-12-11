package shooter.mina.movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MainActivity extends AppCompatActivity {
    boolean bool = false;

    @Override
    protected void onStart(){
        super.onStart();
        RealmConfiguration Config;
                Config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(Config);
        Stetho.initialize(Stetho.newInitializerBuilder(this).enableDumpapp(Stetho.defaultDumperPluginsProvider(this)).enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build()).build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Activity obj = new Activity();

        if (obj.isOnline()) {
            if (savedInstanceState == null) {
                if (findViewById(R.id.tablet_Frames) != null) { //tablet
                    bool = true;
                    MoviesFragment moviesFragment = new MoviesFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("Details", bool);
                    moviesFragment.setArguments(args);
                    getSupportFragmentManager().beginTransaction().replace(R.id.leftFrame, moviesFragment).commit();
                    getSupportFragmentManager().beginTransaction().replace(R.id.rightFrame, new DetailsFragment()).commit();
                } else {
                    bool = false;
                    getSupportFragmentManager().beginTransaction().add(R.id.gridview, new MoviesFragment()).commit();
                }
            }

      }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_main, new NoInternetFragment()).commit();
        }
     /*   final ArrayList<String> posters = new ArrayList<>();
        String url = "http://api.themoviedb.org/3/movie/"+"api_key="+Activity.MovieAPIKey;
        final GridView gridview = (GridView) findViewById(R.id.gridview);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(MainActivity.this, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Gson gson = new Gson();
                MovieModel moviemodel;
                moviemodel = gson.fromJson(new String(responseBody), MovieModel.class);
                List<MovieModel.ResultsBean> resultsList = moviemodel.getResults();
                for (MovieModel.ResultsBean i : resultsList) {
                    posters.add("https://image.tmdb.org/t/p/w342" + i.getPoster_path());
                    MoviesGridAdapter MGA = new MoviesGridAdapter(MainActivity.this, posters);
                    gridview.setAdapter(MGA);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
*/
    }

}



