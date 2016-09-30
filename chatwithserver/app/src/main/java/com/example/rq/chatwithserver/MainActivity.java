package com.example.rq.chatwithserver;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import adapter.DetailCursorAdapter;
import provider.MessageContentProvider;
import provider.PeerContentProvider;
import server.RequestProcessor;
import server.RequestService;
import server.ServiceHelper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends ActionBarActivity implements LocationListener, RequestProcessor.ShowMessage {

    private LoaderManager manager;
    private ListView listView;
    private DetailCursorAdapter detailCursorAdapter ;
    private ServiceHelper mServiceHelper;
    private String mUserName;
    private String mServerUrl;
    private long mClientId;
    private long seq;
    private UUID mRegistrationId;
    private EditText editText;
    private EditText editText2;
    private PendingIntent mSyncingIntent;
    static String TAG = "tag";
    private GoogleApiClient mGoogleApiClient;
    private double mLongitude, mLatitude;
    private Integer MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private ArrayList<String> list = new ArrayList<String>();
    private GoogleMap map;
    private Location location;
    private double[] l = new double[2];

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    if (resultCode == 2) {
                        seq = resultData.getLong(RequestService.KEY_CLIENT_ID);
                        Log.w("seqnumber: ", seq + "");
                    }
                }
            };
            Cursor cursor = getContentResolver().query(MessageContentProvider.CONTENT_URI, new String[]{
                    MessageContentProvider.KEY_CHATROOM
            }, null, null, null, null);
            int count = 0;
            while (cursor.moveToNext()) {
                count++;
            }
            Toast.makeText(MainActivity.this, count + "", Toast.LENGTH_LONG).show();


            mServiceHelper.synchronize(seq, mServerUrl, mRegistrationId, mClientId, mLatitude, mLongitude, resultReceiver);
        }

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.peer:
                startActivity(new Intent(MainActivity.this, Peer.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        //  intentFilter.addAction(getResources().getString(R.string.action_syncing));
        Log.d("start", ":register");
        registerReceiver(broadcastReceiver, intentFilter);
    }


    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mServiceHelper = new ServiceHelper(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mRegistrationId = UUID.randomUUID();
                /*UUID.fromString(preferences.getString(
                getResources().getString(R.string.preference_register_id),
                getResources().getString(R.string.register_id)));*/
        Format formatter = new SimpleDateFormat("ss");
        mUserName =mRegistrationId.toString();
                //formatter.format(Calendar.getInstance().getTime());
        /*preferences.getString(
                getResources().getString(R.string.preference_key_username),
                getResources().getString(R.string.default_username));*/
        editText2  = (EditText)findViewById(R.id.reg);
        /*mClientId = Long.parseLong(preferences.getString(
                getResources().getString(R.string.preference_key_client_id),
                getResources().getString(R.string.default_client_id)));*/

        //put string into preferences
        String register_id = getResources().getString(R.string.register_id);
        preferences.edit().putString(register_id, mRegistrationId.toString());

        manager = this.getLoaderManager();
        manager.initLoader(0, null, myLoader);

        editText = (EditText) findViewById(R.id.input);
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        if (resultCode == 1) {
                            mClientId = resultData.getLong(RequestService.KEY_CLIENT_ID);
                            Toast.makeText(getBaseContext(),
                                    resultData.getLong(RequestService.KEY_CLIENT_ID) + "",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                mServerUrl = editText2.getText().toString();
                mServiceHelper.register(mServerUrl, mRegistrationId,
                        mLatitude, mLongitude, mUserName, resultReceiver);

                Toast.makeText(MainActivity.this, mRegistrationId.toString(), Toast.LENGTH_LONG).show();
            }
        });

        Button showMap = (Button) findViewById(R.id.showMap);
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, ShowMap.class).putExtra("location", l));
            }
        });

        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* String text = editText.getText().toString();
                //mServiceHelper.synchronize(mServerUrl, mRegistrationId, mClientId, 1.0, 1.0, null);
                Intent intent =  new Intent("android.intent.action.MAIN");
                mSyncingIntent = PendingIntent.getBroadcast(MainActivity.this,
                        0,
                        intent,
                        0);
                AlarmManager alarmMan = (AlarmManager)
                        getSystemService(Context.ALARM_SERVICE);
                alarmMan.setRepeating(AlarmManager.RTC, new Date().getTime(),
                        2 * 1000, mSyncingIntent);

                /*mServiceHelper.postMessage(new Date(),text, 1.0, 1.0, mServerUrl, 1, mRegistrationId);
                */
                ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        if (resultCode == 2) {
                            seq = resultData.getLong(RequestService.KEY_CLIENT_ID);
                            manager.restartLoader(0, null, myLoader);
                            Log.w("seqnumber: ", seq + "");
                        }
                    }
                };
                String text = editText.getText().toString();
                Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = formatter.format(Calendar.getInstance().getTime());
                        ContentValues contentValues = new ContentValues();
                 contentValues.put("chatroom","default");
                contentValues.put("time",time);
                contentValues.put("send",0);
                contentValues.put("text",text);
                contentValues.put("latitude",mLatitude);
                contentValues.put("longitude",mLongitude);

                getContentResolver().insert(MessageContentProvider.CONTENT_URI,contentValues);
                mServiceHelper.synchronize(seq, mServerUrl, mRegistrationId, mClientId, mLatitude, mLongitude, resultReceiver);

            }
        });


        listView = (ListView)findViewById(R.id.main_list);
        Cursor cursor1 = null;
        cursor1 = getContentResolver().query(PeerContentProvider.CONTENT_URI,null,null,null,null);
        detailCursorAdapter = new DetailCursorAdapter(this,cursor1);
        listView.setAdapter(detailCursorAdapter);

        if (isGooglePlayServiceAvailable()) {
            final LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(
                    GooglePlayServiceContract.LOCATION_UPDATE_INTERVAL);
            locationRequest.setFastestInterval(
                    GooglePlayServiceContract.LOCATION_UPDATE_FASTEST_INTERVAL);
            locationRequest.setSmallestDisplacement(
                    GooglePlayServiceContract.LOCATION_UPDATE_DISPLACEMENT);

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                public void onConnected(Bundle bundle) {
                                    // request location update
                                    Log.d(TAG, "onConnected() Google Play Services connected. "
                                            + "Requesting Location Update...");


                                    int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION);

                                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                                    }
                                    PendingResult<Status> result = LocationServices
                                            .FusedLocationApi.requestLocationUpdates(
                                                    mGoogleApiClient, locationRequest,
                                                    MainActivity.this);
                                    result.setResultCallback(new ResultCallback<Status>() {
                                        public void onResult(Status status) {
                                            if (!status.isSuccess()) {
                                                Log.d(TAG, "onConnected() RequestLocationUpdate failed:"
                                                        + status.getStatusMessage());
                                                if (status.hasResolution()) {
                                                    try {
                                                        status.startResolutionForResult(
                                                                MainActivity.this,
                                                                GooglePlayServiceContract
                                                                        .REQ_CODE_REQ_LOCATION_UPDATE);
                                                    } catch (IntentSender.SendIntentException e) {
                                                        Log.d(TAG, "onConnected() startResolutionForResult:"
                                                                + e);
                                                    }
                                                }
                                            }
                                        }
                                    });

                                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                    if (location == null) {
                                        Log.w("location==null","updating");
                                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, MainActivity.this);
                                    }else{
                                        Log.w("location==",location.toString());
                                    }


                                }

                                public void onConnectionSuspended(int i)
                                {
                                    mGoogleApiClient.connect();
                                }
                            })
                    .addOnConnectionFailedListener(
                            new GoogleApiClient.OnConnectionFailedListener()
                            {
                                public void onConnectionFailed(
                                        ConnectionResult connectionResult)
                                {
                                    Log.e(TAG, "onCreate() GoogleApiClient.connect() failed: "
                                            + connectionResult.getErrorCode());
                                }
                            })
                    .build();

            Log.d(TAG, "onCreate() GoogleApiClient has been built.");
        }


    }

    private boolean isGooglePlayServiceAvailable() {
        int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (statusCode == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isGooglePlayServicesAvailable() SUCCESS.");
            return true;
        }

        if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
            Log.d(TAG, "isGooglePlayServicesAvailable() getErrorDialog.");
            GooglePlayServicesUtil.getErrorDialog(statusCode, this,
                    GooglePlayServiceContract.REQ_CODE_GOOGLE_PLAY_SERVICE).show();
        } else {
            Log.d(TAG, "isGooglePlayServicesAvailable() Google play service is not supported.");
            Toast.makeText(getApplicationContext(),
                    "Google play service is not supported.",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        return false;
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        // connect google play service
        if (mGoogleApiClient != null)
        {
            Log.d(TAG, "onStart() Connecting Google Play Service");
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        // remove location update
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);

        // disconnect google play service
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
        {
            Log.d(TAG, "onStop() Disconnecting Google Play Service");
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged()");
        mLongitude = location.getLongitude();
        mLatitude = location.getLatitude();
        l[0] = mLatitude;
        l[1] = mLongitude;
        this.location = location;
        Log.w("location", location.toString());
    }


    public void showMessage(String message,double latitude,double longitude){

    }

    private LoaderManager.LoaderCallbacks<Cursor> myLoader = new LoaderManager.LoaderCallbacks<Cursor>() {

        public Loader<Cursor> onCreateLoader(int id, Bundle args) {


            String[] a = {
                    "_id","text","name"
            };
            return new CursorLoader(MainActivity.this, PeerContentProvider.CONTENT_URI, a, null, null,
                    null);
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            detailCursorAdapter.swapCursor(cursor);
            listView.setAdapter(detailCursorAdapter);
            scrollMyListViewToBottom();
        }

        public void onLoaderReset(Loader<Cursor> arg0) {
        }


        private void scrollMyListViewToBottom() {
            listView.post(new Runnable() {
                @Override
                public void run() {
                    // Select the last row so it will scroll into view...
                    listView.setSelection(detailCursorAdapter.getCount() - 1);
                }
            });
        }
    };
}
