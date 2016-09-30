package server;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by rq on 16/3/20.
 */
public interface IStreamingOutput
{
    public boolean outputRequestEntity(HttpURLConnection connection)
            throws IOException;
}
