package server;

import android.net.Uri;
import android.os.Parcel;

import java.util.UUID;

/**
 * Created by rq on 16/3/12.
 */
public class Register extends Request{
    public static final Creator<Register> CREATOR = new Creator<Register>()
    {
        public Register createFromParcel(Parcel source)
        {
            return new Register(source);
        }

        public Register[] newArray(int size)
        {
            return new Register[size];
        }
    };
    public String mUsername;

    public Register(String serverUrl, UUID registrationId,
                    double longitude, double latitude, String username)
    {
        super(serverUrl, registrationId, 0, longitude, latitude);
        mUsername = username;
    }

    /*
     * Parcelable
     */

    public Register(Parcel parcel)
    {
        super(parcel);
        mUsername = parcel.readString();
    }

    @Override
    public Uri getRequestUri()
    {
        return Uri.parse(mServerUrl + "?username=" + mUsername + "&regid=" + mRegistrationId.toString());
    }

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags)
    {
        super.writeToParcel(parcel, flags);
        parcel.writeString(mUsername);
    }
}
