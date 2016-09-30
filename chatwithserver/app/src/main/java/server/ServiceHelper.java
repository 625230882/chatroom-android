package server;

import android.app.Activity;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;
import entity.WebMessage;
import java.util.Date;
import java.util.UUID;

/**
 * Created by rq on 16/3/12.
 */
public class ServiceHelper {
    // different request types for service
    public static final int REQUEST_REGISTER = 1;
    public static final int REQUEST_MESSAGE = 2;
    public static final int REQUEST_SYNCHRONIZE = 3;

    private static final String PREFIX = ServiceHelper.class.getCanonicalName();
    public static final String KEY_REQUEST = "register_request";
    public static final String KEY_REGISTER = "register_register";
    public static final String KEY_MESSAGE = "request_message";
    public static final String KEY_SYNCHRONIZE = PREFIX + ".key_synchronize";
    public static final String KEY_RESULT_RECEIVER =  "register_resultreceiver";

    private static final String TAG = ServiceHelper.class.getSimpleName();

    private Activity mActivity;

    public ServiceHelper(Activity activity)
    {
        mActivity = activity;
    }

    public void register(String serverUrl, UUID registrationId,
                         double longitude, double latitude,
                         String username, ResultReceiver resultReceiver)
    {
        Log.d(TAG, "Register: " + " server=" + serverUrl
                + " regId=" + registrationId + " username=" + username);
        Register request = new Register(serverUrl, registrationId,
                longitude, latitude, username);

        Intent intent = new Intent(mActivity, RequestService.class);
        intent.putExtra(KEY_REQUEST, REQUEST_REGISTER);
        intent.putExtra(KEY_REGISTER, request);
        intent.putExtra(KEY_RESULT_RECEIVER, resultReceiver);
        mActivity.startService(intent);
    }

    public void postMessage(Date timestamp,String text, double longitude, double latitude,String url, long chatroomId,UUID uuid)
    {
       // Log.d(TAG, "postMessage(): senderFK=" + senderId + ", text=" + text);
        String uri = "http://yangreningdembp.home:8080/chat/1?username=renqing_yang&regid=0f14d0ab-9605-4a62-a9e4-5ed26688389b&seqnum=1";

        PostMessage postMessage = new PostMessage(url, uuid,1, 1.0, 1.0);
        Intent intent = new Intent(mActivity, RequestService.class);
        intent.putExtra(KEY_REQUEST, REQUEST_MESSAGE);
        intent.putExtra(KEY_REGISTER, text);
        intent.putExtra(KEY_MESSAGE, postMessage);
        mActivity.startService(intent);
    }

    public void synchronize(long seq,String serverUrl, UUID registrationId, long clientId,
                            double latitude, double longitude,
                            ResultReceiver receiver)
    {
        Log.d(TAG, "synchronize: " + " server=" + serverUrl
                + " regId=" + registrationId + " clientId=" + clientId);

        Synchronize request = new Synchronize(seq,serverUrl, registrationId, clientId,
                latitude, longitude);

        Intent intent = new Intent(mActivity, RequestService.class);
        intent.putExtra(KEY_REQUEST, REQUEST_SYNCHRONIZE);
        intent.putExtra(KEY_SYNCHRONIZE, request);
        intent.putExtra(KEY_RESULT_RECEIVER, receiver);
        intent.putExtra("latitude",latitude);
        intent.putExtra("longitude",longitude);

        mActivity.startService(intent);
    }
}
