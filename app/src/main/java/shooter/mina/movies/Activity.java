package shooter.mina.movies;

import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

public class Activity extends AppCompatActivity {

    public final String  MovieAPIKey="a0222d0d350d043d587990525c501889";

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
}
