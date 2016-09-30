package provider;

/**
 * Created by rq on 16/2/16.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

public class PeerContentProvider extends ContentProvider {
    // fields for my content provider
    public static final String PROVIDER_NAME = "edu.stevens.provider.PeerContentProvider";
    public static final String URL = "content://" + PROVIDER_NAME + "/Message";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    // fields for the database
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TEXT = "text";
    public static final String KEY_CHATROOM = "chatroom";
    public static final String KEY_TIME = "time";
    public static final String KEY_SEND = "send";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";

    // integer values used in content URI
    static final int Message = 1;
    static final int Message_ID = 2;

    private DBHelper dbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "mmmmmm";
    private static final String SQLITE_TABLE = "nnn";
    private static final int DATABASE_VERSION = 1;

    // projection map for a query
    private static HashMap<String, String> BirthMap;

    // maps content URI "patterns" to the integer values that were set above
    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "Message", Message);
        uriMatcher.addURI(PROVIDER_NAME, "Message/#", Message_ID);
    }

    // database declarations
    private SQLiteDatabase database;
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_SEND + "," +
                    KEY_TEXT + "," +
                    KEY_TIME + "," +
                    KEY_LATITUDE + "," +
                    KEY_LONGITUDE + "," +
                    KEY_CHATROOM + ");";
   
    // class that creates and manages the provider's database
    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(DATABASE_CREATE);
            db.execSQL("PRAGMA foreign_keys=ON;");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            Log.w(DBHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ". Old data will be destroyed");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }

    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        Context context = getContext();
        dbHelper = new DBHelper(context);
        // permissions to be writable
        database = dbHelper.getWritableDatabase();

        if (database == null)
            return false;
        else
            return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // the TABLE_NAME to query on
        queryBuilder.setTables(SQLITE_TABLE);

        Cursor cursor = null;

        switch (uriMatcher.match(uri)) {
            // maps all database column names
            case Message:
                String query = "select *  from "+ SQLITE_TABLE ;
                cursor =  database.rawQuery(query, null);
                break;
                //queryBuilder.setProjectionMap(BirthMap);

            case Message_ID:

                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) throws SQLException{
        // TODO Auto-generated method stub
        switch (uriMatcher.match(uri)) {
            case Message:
                long r = database.insert(SQLITE_TABLE, null, values);

                // If record is added successfully
                if (r > 0) {
                    Uri newUri = ContentUris.withAppendedId(CONTENT_URI, r);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    return newUri;
                }
                break;
            case Message_ID:
                break;
        }
       // throw new SQLException("Fail to add a new record into " + uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case Message:
                count = database.update(SQLITE_TABLE, values, selection, selectionArgs);
                break;
            case Message_ID:
                count = database.update(SQLITE_TABLE, values, KEY_ROWID +
                        " = " + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case Message:
                // delete all the records of the table
                count = database.delete(SQLITE_TABLE, selection, selectionArgs);
                break;
            case Message_ID:
                String id = uri.getLastPathSegment();    //gets the id
                count = database.delete(SQLITE_TABLE, KEY_ROWID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;


    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        switch (uriMatcher.match(uri)) {
            // Get all friend-birthday records
            case Message:
                return "vnd.android.cursor.dir/vnd.example.Message";
            // Get a particular friend
            case Message_ID:
                return "vnd.android.cursor.item/vnd.example.Message";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }


}