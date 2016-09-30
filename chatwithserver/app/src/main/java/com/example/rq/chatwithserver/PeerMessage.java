package com.example.rq.chatwithserver;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import adapter.PeerMessageCursorAdapter;
import provider.PeerContentProvider;

/**
 * Created by rq on 16/5/2.
 */
public class PeerMessage extends Activity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peermessage);
        Uri uri = Uri.parse(PeerContentProvider.CONTENT_URI.toString()+"/"+getIntent().getExtras().getString("name"));
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);

        PeerMessageCursorAdapter peerMessageCursorAdapter = new PeerMessageCursorAdapter(this,cursor);
        ListView listView = (ListView)findViewById(R.id.peerMessage);
        listView.setAdapter(peerMessageCursorAdapter);

    }
}
