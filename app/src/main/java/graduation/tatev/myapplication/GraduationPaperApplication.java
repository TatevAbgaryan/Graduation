package graduation.tatev.myapplication;

import android.app.Application;
import android.util.Log;

/**
 * Created by Tatka on 4/17/2016.
 */
public class GraduationPaperApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ConnectionService.initialize();
    }
}
