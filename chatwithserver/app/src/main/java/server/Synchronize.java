package server;

import android.net.Uri;
import android.os.Parcel;
import android.util.JsonWriter;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by rq on 16/3/16.
 */
public class Synchronize extends Request
{
    public static final Creator<Synchronize> CREATOR = new Creator<Synchronize>()
    {
        public Synchronize createFromParcel(Parcel source)
        {
            /* skip the type tag */
            return new Synchronize(source);
        }

        public Synchronize[] newArray(int size)
        {
            return new Synchronize[size];
        }
    };
    public long mSequenceNum;
    public double latitude;
    public double longitude;

    public Synchronize(long seq,String serverUrl, UUID registrationId, long clientId,
                       double longitude, double latitude)
    {
        super(serverUrl, registrationId, clientId, longitude, latitude);
        this.mSequenceNum = seq;
    }

    public Synchronize(Parcel parcel)
    {
        super(parcel);
        mSequenceNum = parcel.readLong();
    }

    /*
     * Parcelable
     */

    @Override
    public Uri getRequestUri()
    {
        Log.w("sequm:",mSequenceNum+"");
        return Uri.parse(mServerUrl + "/" + mClientId + "?regid=" + mRegistrationId.toString() + "&seqnum=" + mSequenceNum);
    }

    public void outputRequestEntity(HttpURLConnection connection)
            throws IOException
    {
        connection.setRequestProperty("Content-Type", "application/json");

        JsonWriter writer = new JsonWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8")));

        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = formatter.format(Calendar.getInstance().getTime());

        writer.beginArray();
        writer.beginObject();
        writer.name("chatroom").value("_default");
        writer.name("timestamp").value(123);
        writer.name("X-latitude").value(1.0);
        writer.name("X-longitude").value(1.0);
        writer.name("text").value(s);
        writer.endObject();
        writer.endArray();

        writer.flush();
        writer.close();
    }

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags)
    {
        super.writeToParcel(parcel, flags);
        parcel.writeLong(mSequenceNum);
    }
}