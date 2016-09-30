package com.example.rq.chatwithserver;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import provider.MessageContentProvider;
import provider.PeerContentProvider;

/**
 * Created by rq on 16/4/17.
 */
public class Peer extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peer);
        final Cursor cursor = getContentResolver().query(PeerContentProvider.CONTENT_URI2, null, null, null, null, null);
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,R.layout.list_show,cursor,new String[]{"name"},new int[]{R.id.showPeer});
        final ListView listView = (ListView)findViewById(R.id.msgList);
        listView.setAdapter(simpleCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor1 = (Cursor)listView.getAdapter().getItem(position);
                Toast.makeText(Peer.this,cursor1.getString(cursor.getColumnIndex("name")),Toast.LENGTH_LONG).show();
                startActivity(new Intent(Peer.this,PeerMessage.class).putExtra("name",cursor1.getString(cursor.getColumnIndex("name"))));
            }

        });
    }
}
