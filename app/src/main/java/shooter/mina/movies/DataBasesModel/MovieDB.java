package shooter.mina.movies.DataBasesModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Mina on 03/12//2016.
 */

public class MovieDB extends RealmObject {
        public String name;
        public String date;
        public String overView;
        public double rating;
        @PrimaryKey
        public int movieId;
        public String poster;

        public MovieDB(){
        }

        public MovieDB(String name, String date, String overView, double rating, int movieId, String poster){
            this.name = name;
            this.date = date;
            this.overView = overView;
            this.rating = rating;
            this.movieId = movieId;
            this.poster = poster;
        }
}
