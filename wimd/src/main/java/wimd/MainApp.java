package wimd;

import android.app.Application;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

public class MainApp extends Application {

    private ArrayList<Location> locations;
    private LocationDatabaseHandler locationDatabaseHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        locationDatabaseHandler = new LocationDatabaseHandler(this);
        new WIMDActivity();
    }

    public String findLocation(String mac, int rssi){
        return locationDatabaseHandler.getLocation(mac, rssi);
    }

    public void clearDatabase() {
        locationDatabaseHandler.clear();
    }

    public void addLocation(Location location) {
        locationDatabaseHandler.addLocation(location);
    }
}