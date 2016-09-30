package server;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import entity.WebMessage;

/**
 * Created by rq on 16/3/12.
 */
public class RestMethod {
    private static final String TAG = RestMethod.class.getSimpleName();

    public static Response perform(Register request)
            throws IOException
    {
        URL url = new URL(request.getRequestUri().toString());
        Log.d(TAG, "Register(): " + url);

        URLConnection urlConnection = url.openConnection();

        if (!(urlConnection instanceof HttpURLConnection))
        {
            throw new IOException("Not an HTTP connection!");
        }

        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
        initConnectionProperties(httpURLConnection, request);

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoInput(true);
        httpURLConnection.connect();

        /* get response */
        throwErrors(httpURLConnection);
        JsonReader reader = new JsonReader(new BufferedReader(
                new InputStreamReader(httpURLConnection.getInputStream())));
        Response response = request.getResponse(httpURLConnection, reader);
        reader.close();
        return response;
    }


    public static Response perform(PostMessage request,String text)
            throws IOException
    {
        URL url = new URL(request.getRequestUri().toString());
        Log.d(TAG, "Synchronize(): " + url);

        URLConnection urlConnection = url.openConnection();

        if (!(urlConnection instanceof HttpURLConnection))
        {
            throw new IOException("Not an HTTP connection!");
        }

        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

        httpURLConnection.setUseCaches(false);
        //httpURLConnection.setRequestProperty("CONNECTION", "Keep-Alive");
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);

        /* send request */
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
         httpURLConnection.addRequestProperty("X-latitude", "1.0");
        httpURLConnection.addRequestProperty("X-longitude", "1.0");
        try {
            JsonWriter writer = new JsonWriter(new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8")));


            writer.beginObject();
            writer.name("chatroom").value("_default");
            writer.name("timestamp").value(123);
            writer.name("text").value(text);

            writer.endObject();
            writer.flush();

            writer.close();
        }catch (IOException e){
            Log.e("json", "error:" + e);
        }
        /* return the response */
        throwErrors(httpURLConnection);

        Response response = request.getResponse(httpURLConnection, null);
        return response;
    }



    public static Response perform(Synchronize request, IStreamingOutput output)
            throws IOException
    {
        URL url = new URL(request.getRequestUri().toString());
        Log.d(TAG, "Synchronize(): " + url);

        URLConnection urlConnection = url.openConnection();

        if (!(urlConnection instanceof HttpURLConnection))
        {
            throw new IOException("Not an HTTP connection!");
        }

        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
        initConnectionProperties(httpURLConnection, request);

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);

        /* send request */
        if (!output.outputRequestEntity(httpURLConnection))
        {
            /* there is no new message to synchronize with the server */
            httpURLConnection.disconnect();
            return null;
        }
        /* return the response */
        throwErrors(httpURLConnection);
        return request.getResponse(httpURLConnection, null);
    }

    private static void initConnectionProperties(HttpURLConnection connection,
                                                 Request request)
    {
        connection.setUseCaches(false);
        connection.setRequestProperty("CONNECTION", "Keep-Alive");

        Map<String, String> headers = request.getRequestHeaders();
        for (Map.Entry<String, String> header : headers.entrySet())
        {
            connection.addRequestProperty(header.getKey(), header.getValue());
        }
    }

    private static void throwErrors(HttpURLConnection connection)
            throws IOException
    {
        final int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode > 300)
        {
            String exceptionMessage = "Error Response "
                    + responseCode + " " + connection.getResponseMessage()
                    + " for " + connection.getURL();

            throw new IOException(exceptionMessage);
        }
    }
}
