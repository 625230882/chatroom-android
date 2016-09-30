package com.example.rq.test;

import android.content.ContentProviderClient;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import provider.PeerContentProvider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContentProviderClient yourCR = getContentResolver().acquireContentProviderClient(Uri.parse("content://edu.stevens.provider.PeerContentProvider/Message"));
        Cursor cursor = null;
        try {
            cursor = yourCR.query(Uri.parse("content://edu.stevens.provider.PeerContentProvider/Message"), null, null, null, null, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        while(cursor.moveToNext()) {

            Log.w("latitude",cursor.getDouble(cursor.getColumnIndex("latitude"))+"");
        }

        Button showMap = (Button) findViewById(R.id.showmap);
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,ShowMap.class));
            }
        });
    }
}
