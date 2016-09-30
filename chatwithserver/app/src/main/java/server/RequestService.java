package server;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.rq.chatwithserver.MainActivity;

import entity.WebMessage;

/**
 * Created by rq on 16/3/12.
 */
public class RequestService extends IntentService
{
    public static final String KEY_CLIENT_ID
            = RequestService.class.getCanonicalName() + ".client_id";

    private static final String TAG = RequestService.class.getSimpleName();

    private RequestProcessor mProcessor = new RequestProcessor(this);

    public RequestService()
    {
        super(RequestService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        int requestType = intent.getIntExtra(ServiceHelper.KEY_REQUEST, 0);
        Log.w("requestService", "onHandleIntent() requestType=" + requestType);

        switch (requestType)
        {
            case ServiceHelper.REQUEST_REGISTER:
                Register register = intent.getParcelableExtra(
                        ServiceHelper.KEY_REGISTER);
                ResultReceiver resultReceiver = intent.getParcelableExtra(
                        ServiceHelper.KEY_RESULT_RECEIVER);
                long clientId = mProcessor.perform(register);

                /* send back the client id of registration */
                Bundle resultData = new Bundle();
                resultData.putLong(KEY_CLIENT_ID, clientId);
                resultReceiver.send(1, resultData);
                break;

            case ServiceHelper.REQUEST_MESSAGE:
                PostMessage postMessage = intent.getParcelableExtra(
                        ServiceHelper.KEY_MESSAGE);
                String text  = intent.getStringExtra(ServiceHelper.KEY_REGISTER);
                mProcessor.perform(postMessage,text);
                break;
            case ServiceHelper.REQUEST_SYNCHRONIZE:
                Synchronize request = intent.getParcelableExtra(
                        ServiceHelper.KEY_SYNCHRONIZE);
                ResultReceiver resultReceiver2 = intent.getParcelableExtra(
                        ServiceHelper.KEY_RESULT_RECEIVER);
                double latitude= intent.getExtras().getDouble("latitude");
                double longitude= intent.getExtras().getDouble("longitude");

                long seq=mProcessor.perform(request,latitude,longitude);
                Bundle resultData2 = new Bundle();
                resultData2.putLong(KEY_CLIENT_ID, seq);
                resultReceiver2.send(2, resultData2);
                break;

            default:
                Log.w(TAG, "handle unknown request.");
                break;
        }
    }

}
