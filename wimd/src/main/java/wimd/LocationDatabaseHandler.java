package wimd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


// TODO Raminta: rename
public class LocationDatabaseHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "jku_mapping";

    private static final String TABLE_DATA = "DATA";
    private static final String ID = "ID";
    private static final String LOCATION = "LOCATION";
    private static final String MAC = "MAC";
    private static final String RSSI = "RSSI";
    private static final String TIMESTAMP = "TIMESTAMP";

    private static final String CREATE_DATA_TABLE = "CREATE TABLE " + TABLE_DATA + "("
                                                    + ID + " INTEGER PRIMARY KEY,"
                                                    + LOCATION + " TEXT,"
                                                    + MAC + " TEXT,"
                                                    + RSSI + " INTEGER,"
                                                    + TIMESTAMP + " INTEGER" + ")";

    public LocationDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        String CREATE_DATA_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_DATA + "("
                + ID + " INTEGER PRIMARY KEY,"
                + LOCATION + " TEXT,"
                + MAC + " TEXT,"
                + RSSI + " INTEGER,"
                + TIMESTAMP + " INTEGER" + ")";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_DATA_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        // Create tables again
        onCreate(db);
    }

    public void addLocation(Location location) {
        if(getLocation(location.getMac(), location.getRSSI())!=null) return; //nothing to add

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues locationValues = new ContentValues();
        locationValues.put(LOCATION, location.getLocation());
        locationValues.put(MAC, location.getMac());
        locationValues.put(RSSI, location.getRSSI());
        locationValues.put(TIMESTAMP, location.getTimestamp());

        long locationID = db.insert(TABLE_DATA, null, locationValues);

        db.close();
    }

    public String getLocation(String mac, int rssi) {
        String location = null;

        SQLiteDatabase db = this.getReadableDatabase();

        // SQL query
        Cursor c = db.rawQuery("Select " + LOCATION + " from " + TABLE_DATA
                + " where " + MAC + " = '" + mac + "' AND " + RSSI + " = " + rssi, null);

        if(c.moveToFirst()) location = c.getString(0);

        c.close();
        db.close();

        return location;
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION+1);
    }
}