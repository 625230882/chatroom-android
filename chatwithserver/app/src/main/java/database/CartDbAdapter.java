package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import entity.WebMessage;
import server.PostMessage;

/**
 * Created by rq on 16/2/3.
 */
public class CartDbAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TEXT = "text";
    public static final String KEY_CHATROOM = "chatroom";
    public static final String KEY_TIME = "time";
    public static final String KEY_SEND = "send";

    private static final String TAG = "CartDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "rq123123";
    private static final String SQLITE_TABLE = "adfaa";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_CHATROOM + "," +
                    KEY_SEND + "," +
                    KEY_TIME + "," +
                    KEY_TEXT +" unique);";
    private static class DatabaseHelper extends SQLiteOpenHelper{

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
            db.execSQL("PRAGMA foreign_keys=ON;");
           // db.execSQL("CREATE INDEX AuthorsBookIndex ON "+SQLITE_TABLE2+"(book_fk);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }
    }
    public CartDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public CartDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }


    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }
    public long createBooks(WebMessage book) {

        ContentValues initialValues = new ContentValues();

        //KEY_TEXT,KEY_CHATROOM,KEY_TIME,KEY_ID
        initialValues.put(KEY_CHATROOM, "default");
        initialValues.put(KEY_TIME, book.timestamp+"");
        initialValues.put(KEY_TEXT, book.message);
        initialValues.put(KEY_SEND,0);
        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }

    public boolean deleteAllBooks() {

        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_TABLE, null, null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }

   public Cursor fetchBooksByName(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null  ||  inputText.length () == 0)  {
            mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,KEY_CHATROOM,KEY_TIME,KEY_TEXT },
                    null, null, null, null, null);

        }
        else {
            String query = "select *  from "+ SQLITE_TABLE + " where " + KEY_SEND +
                    " = 0";
            mCursor = mDb.rawQuery(query,null);


        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }


    public Cursor fetchDetails(Long inputText) throws SQLException {

        Cursor mCursor = null;
        if (inputText == null  )  {
            mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,KEY_CHATROOM,KEY_TIME,KEY_TEXT },
                    null, null, null, null, null);

        }
        else {
           /* String query = "select "+SQLITE_TABLE+".title,"+SQLITE_TABLE+"._id,GROUP_CONCAT(firstname,'|') as authors,price,isbn from "+ SQLITE_TABLE +" LEFT OUTER JOIN "+SQLITE_TABLE2+" on "+
                    SQLITE_TABLE+"."+KEY_ROWID + "=" + SQLITE_TABLE2 + "." + KEY_FK +" where "+SQLITE_TABLE
                    +"._id='"+inputText+"' GROUP BY "+SQLITE_TABLE+".isbn,"+SQLITE_TABLE+".title,"+SQLITE_TABLE+"._id";
            mCursor = mDb.rawQuery(query,null);*/


        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }


    public void deleteBooksByName(String title) throws SQLException {

          //  String query = "delete  from "+SQLITE_TABLE2+ " where "+KEY_FK+" in( select title from "+SQLITE_TABLE+" where title='"+title+"')";
          //  mDb.execSQL(query);
         //   query = "delete from "+SQLITE_TABLE+" where title='"+title+"'";
          //  mDb.execSQL(query);
    }



    public Cursor fetchAllBooks() {

        Cursor mCursor = null;
       /* String query = "select "+SQLITE_TABLE+".title,"+SQLITE_TABLE+"._id,GROUP_CONCAT(firstname,'|') as authors,price,isbn from "+ SQLITE_TABLE +" LEFT OUTER JOIN "+SQLITE_TABLE2+" on "+
                SQLITE_TABLE+"."+KEY_ROWID + "=" + SQLITE_TABLE2 + "." + KEY_FK +" GROUP BY "+SQLITE_TABLE+".isbn,"+SQLITE_TABLE+".title,"+SQLITE_TABLE+"._id";
        mCursor = mDb.rawQuery(query,null);


        if (mCursor != null) {
            mCursor.moveToFirst();
        }*/
        return mCursor;
    }

}

