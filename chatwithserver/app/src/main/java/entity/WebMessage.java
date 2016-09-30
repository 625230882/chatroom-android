package entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import server.Response;

/**
 * Created by rq on 16/3/12.
 */
public class WebMessage implements Parcelable
{
    /* Database table schema */
    public static final String COL_ID = "_id";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_SEQUENCE_NO = "sequence_no";
    public static final String COL_LONGITUDE = "longitude";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_MESSAGE = "text";
    public static final String COL_SENDER_FK = "sender";
    public static final String COL_CHATROOM_FK = "chatroom";

    public static long getId(Cursor cursor)
    {
        return cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
    }

    public static Date getTimestamp(Cursor cursor)
    {
        return new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)));
    }

    public static void putTimestamp(ContentValues cv, Date timestamp)
    {
        cv.put(COL_TIMESTAMP, timestamp.getTime());
    }

    public static long getSequenceNo(Cursor cursor)
    {
        return cursor.getLong(cursor.getColumnIndexOrThrow(COL_SEQUENCE_NO));
    }

    public static void putSequenceNo(ContentValues cv, long sequenceNo)
    {
        cv.put(COL_SEQUENCE_NO, sequenceNo);
    }

    public static double getLongitude(Cursor cursor)
    {
        return Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(COL_LONGITUDE)));
    }

    public static void putLongitude(ContentValues cv, double longitude)
    {
        cv.put(COL_LONGITUDE, Double.toString(longitude));
    }

    public static double getLatitude(Cursor cursor)
    {
        return Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(COL_LATITUDE)));
    }

    public static void putLatitude(ContentValues cv, double latitude)
    {
        cv.put(COL_LATITUDE, Double.toString(latitude));
    }

    public static String getMessage(Cursor cursor)
    {
        return cursor.getString(cursor.getColumnIndexOrThrow(COL_MESSAGE));
    }

    public static void putMessage(ContentValues cv, String message)
    {
        cv.put(COL_MESSAGE, message);
    }

    public static long getSenderFK(Cursor cursor)
    {
        return cursor.getLong(cursor.getColumnIndexOrThrow(COL_SENDER_FK));
    }

    public static void putSenderFK(ContentValues cv, long senderId)
    {
        cv.put(COL_SENDER_FK, senderId);
    }

    public static long getChatroomFK(Cursor cursor)
    {
        return cursor.getLong(cursor.getColumnIndexOrThrow(COL_CHATROOM_FK));
    }

    public static void putChatroomFK(ContentValues cv, long chatroomId)
    {
        cv.put(COL_CHATROOM_FK, chatroomId);
    }

    public static final Parcelable.Creator<WebMessage> CREATOR = new Creator<WebMessage>()
    {
        public WebMessage createFromParcel(Parcel source)
        {
            return new WebMessage(source);
        }

        public WebMessage[] newArray(int size)
        {
            return new WebMessage[size];
        }
    };

    public long id;
    public Date timestamp;
    public long sequenceNum;
    public double longitude;
    public double latitude;
    public String message;
    public long senderFK;
    public long chatroomFK;

    public WebMessage(Date timestamp, long sequenceNum,
                      double longitude, double latitude, String message,
                      long senderId, long chatroomId)
    {
        this.timestamp = timestamp;
        this.sequenceNum = sequenceNum;
        this.longitude = longitude;
        this.latitude = latitude;
        this.message = message;
        this.senderFK = senderId;
        this.chatroomFK = chatroomId;
    }

    @Override
    public String toString()
    {
        return "{" + timestamp.getTime() + ", " + sequenceNum + ", "
                + longitude + ", " + latitude + message + ", "
                + senderFK + ", " + chatroomFK + "}";
    }

    public Map<String, String> getRequestHeaders()
    {
       Map<String,String> mHeaders = new HashMap<String, String>();
        mHeaders.put("X-latitude", latitude+"");
        mHeaders.put("X-longitude", longitude+"");
        return mHeaders;
    }
    /*
     * Parcelable
     */

    public WebMessage(Parcel parcel)
    {
        id = parcel.readLong();
        timestamp = new Date(parcel.readLong());
        sequenceNum = parcel.readLong();
        longitude = parcel.readDouble();
        latitude = parcel.readDouble();
        message = parcel.readString();
        senderFK = parcel.readLong();
        chatroomFK = parcel.readLong();
    }

    public void writeToParcel(Parcel dst, int flags)
    {
        dst.writeLong(id);
        dst.writeLong(timestamp.getTime());
        dst.writeLong(sequenceNum);
        dst.writeDouble(longitude);
        dst.writeDouble(latitude);
        dst.writeString(message);
        dst.writeLong(senderFK);
        dst.writeLong(chatroomFK);
    }

    public int describeContents()
    {
        return 0;
    }

    /*
     * Database operations
     */

    public void writeToProvider(ContentValues cv)
    {
        putTimestamp(cv, timestamp);
        putSequenceNo(cv, sequenceNum);
        putLongitude(cv, longitude);
        putLatitude(cv, latitude);
        putMessage(cv, message);
        putSenderFK(cv, senderFK);
        putChatroomFK(cv, chatroomFK);
    }

    public WebMessage(Cursor cursor)
    {
        id = getId(cursor);
        timestamp = getTimestamp(cursor);
        sequenceNum = getSequenceNo(cursor);
        longitude = getLongitude(cursor);
        latitude = getLatitude(cursor);
        message = getMessage(cursor);
        senderFK = getSenderFK(cursor);
        chatroomFK = getChatroomFK(cursor);
    }


    public Response getResponse(HttpURLConnection connection, JsonReader reader)
    {
        Response response = null;
        try
        {
            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED)
            {
                response = new Response.RegisterResponse(connection, reader);
                //mClientId = ((Response.RegisterResponse) response).mClientId;
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                //response = new Response.SynchronizeResponse(connection);
            }
        }
        catch (IOException e)
        {
            Log.e("webMessage:", "HttpURLConnection.getResponseCode() failed: " + e);
        }
        return response;
    }

}