package server;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import entity.WebMessage;
import provider.MessageContentProvider;
import provider.PeerContentProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.Date;

/**
 * Created by rq on 16/3/12.
 */
public class RequestProcessor implements IStreamingOutput{
    private static final String TAG = RequestProcessor.class.getSimpleName();
    private Context mContext;

    public interface ShowMessage{
        public void showMessage(String message,double latitude,double longitude);
    }
    ShowMessage listener;
    public RequestProcessor(Context context)
    {

        mContext = context;
    }

    public long perform(Register request)
    {
        try
        {
            Log.d(TAG, "processing register...");
            Response.RegisterResponse response = (Response.RegisterResponse)
                    RestMethod.perform(request);
            Log.d(TAG, "Register finished: clientId=" + response.mClientId);
            return response.mClientId;
        }
        catch (IOException e)
        {
            Log.e(TAG, "RestMethod.perform(Register) failed: " + e);
            return 0;
        }
    }

    public void perform(PostMessage postMessage,String text)
    {

        try
        {
            Log.d(TAG, "processing sending...");
            Response.PostMessageResponse response = (Response.PostMessageResponse)
                    RestMethod.perform(postMessage,text);

            JsonReader reader = new JsonReader(new BufferedReader(
                    new InputStreamReader(response.mHttpURLConnection.getInputStream())));
            reader.beginObject();
            if (reader.hasNext()){
                final String name = reader.nextName();

                int num = reader.nextInt();
                Log.e("name",name+":"+num);
            }
            reader.endObject();
        }
        catch (IOException e)
        {
            Log.e(TAG, "RestMethod.perform(send message) failed: " + e);
        }
    }

    public long perform(Synchronize request,double a,double o)
    {

//        ContentValues contentValues = new ContentValues();
//        contentValues.put("chatroom","default");
//        contentValues.put("time",0);
//        contentValues.put("send",0);
//        contentValues.put("text","balbalbal");
//        contentValues.put("latitude",a);
//        contentValues.put("longitude",o);
//
//        mContext.getContentResolver().insert(MessageContentProvider.CONTENT_URI,contentValues);
        long sequenceNum=0;
        try {
            Response.SynchronizeResponse response = (Response.SynchronizeResponse)
                    RestMethod.perform(request,this);
            if(response==null){
                return 0;
            }
            JsonReader reader = new JsonReader(new BufferedReader(
                    new InputStreamReader(response.mHttpURLConnection.getInputStream())));
            reader.beginObject();
            Response.matchName("clients", reader);
            // clients array
            reader.skipValue();
            Response.matchName("messages", reader);
            int messageCount = 0;
            reader.beginArray();
            while (reader.hasNext()) {
                messageCount++;
                reader.beginObject();
                Response.matchName("chatroom", reader);
                String chatroomName = reader.nextString();
                Response.matchName("timestamp", reader);
                Date timestamp = new Date(reader.nextLong());
                Response.matchName("latitude", reader);
                double latitude = reader.nextDouble();
                Response.matchName("longitude", reader);
                double longitude = reader.nextDouble();
                Response.matchName("seqnum", reader);
                sequenceNum = reader.nextLong();
                Response.matchName("sender", reader);
                String sender = reader.nextString();
                /* get senderFK via sender */
                Response.matchName("text", reader);
                String text = reader.nextString();
                Log.w("text", text);
                Log.w("chatroom", chatroomName);
                Log.w("timestamp", timestamp + "");
                Log.w("latitude", latitude + "");
                Log.w("longitude", longitude + "");
                Log.w("name", sender + "");

                ContentValues contentValues1 = new ContentValues();
                contentValues1.put("chatroom",chatroomName);
                contentValues1.put("time", timestamp+"");
                contentValues1.put("name", sender);
                contentValues1.put("text", text);
                contentValues1.put("latitude", latitude);
                contentValues1.put("longitude", longitude);
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("name",sender);
                mContext.getContentResolver().insert(PeerContentProvider.CONTENT_URI2,contentValues2);
                mContext.getContentResolver().insert(PeerContentProvider.CONTENT_URI, contentValues1);


                reader.endObject();
            }
            reader.endArray();
            reader.endObject();
        }catch (IOException e) {
            e.printStackTrace();

        }
        return sequenceNum;

    }


    public boolean outputRequestEntity(HttpURLConnection connection)
            throws IOException
    {
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setChunkedStreamingMode(0);

        /* construct request entity according to query result */
        final JsonWriter writer = new JsonWriter(new BufferedWriter(
                new OutputStreamWriter(connection.getOutputStream(), "UTF-8")));

        Cursor cursor = mContext.getContentResolver().query(MessageContentProvider.CONTENT_URI,null,null,null,null,null);
        if (cursor.getCount() == 0)
        {
             /* there is no new message to synchronize with the server */
            return false;
        }

        try
        {
            writer.beginArray();
            if (cursor.moveToFirst())
            {
                do
                {
                    Long id = cursor.getLong(cursor.getColumnIndex("_id"));
                    String chatroomName = cursor.getString(cursor.getColumnIndex("chatroom"));
                    String text = cursor.getString(cursor.getColumnIndex("text"));
                    String time = cursor.getString(cursor.getColumnIndex("time"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));

                    double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                    double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));

                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put("chatroom","default");
                    contentValues1.put("name", name);
                    contentValues1.put("time", time);
                    contentValues1.put("send", 0);
                    contentValues1.put("text",text);
                    contentValues1.put("latitude", latitude);
                    contentValues1.put("longitude", longitude);
                  //  mContext.getContentResolver().insert(PeerContentProvider.CONTENT_URI, contentValues1);

                    Uri uri = Uri.parse(MessageContentProvider.CONTENT_URI.toString()+"/"+id);
                    mContext.getContentResolver().delete(uri, null, null);
                    writer.beginObject();
                    writer.name("chatroom");
                    writer.value(chatroomName);
                    writer.name("timestamp");
                    Date date = new Date();
                    writer.value(date.getTime());
                    writer.name("latitude").value(latitude);
                    writer.name("longitude").value(longitude);
                    writer.name("text");
                    writer.value(text);
                    writer.endObject();
                }
                while (cursor.moveToNext());
            }
            writer.endArray();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Output synchronization Request Entity failed: " + e);
        }

        cursor.close();
        writer.flush();
        writer.close();
        return true;
    }


}
