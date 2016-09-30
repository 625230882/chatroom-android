package server;

import android.net.Uri;
import android.os.Parcel;
import android.util.JsonWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.UUID;

/**
 * Created by rq on 16/3/13.
 */
public class PostMessage extends Request{
    public static final Creator<PostMessage> CREATOR = new Creator<PostMessage>()
    {
        public PostMessage createFromParcel(Parcel source)
        {
            /* skip the type tag */
            return new PostMessage(source);
        }

        public PostMessage[] newArray(int size)
        {
            return new PostMessage[size];
        }
    };
    public long mSequenceNum;

    public PostMessage(String serverUrl, UUID registrationId, long clientId,
                       double longitude, double latitude)
    {
        super(serverUrl, registrationId, clientId, longitude, latitude);
    }

    public PostMessage(Parcel parcel)
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
        return Uri.parse(mServerUrl + "/" + mClientId + "?regid=" + mRegistrationId.toString() + "&seqnum=" + mSequenceNum);
    }

    public void outputRequestEntity(HttpURLConnection connection)
            throws IOException
    {
        connection.setRequestProperty("Content-Type", "application/json");

        JsonWriter writer = new JsonWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8")));

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
