package com.example.rq.chatwithserver;

/**
 * Created by rq on 16/4/16.
 */
public class GooglePlayServiceContract {
    public static double DEFAULT_LONGITUDE = 40.7439905;
    public static double DEFAULT_LATITUDE = -74.0323626;

    public static long LOCATION_UPDATE_INTERVAL = 10 * 1000;
    public static long LOCATION_UPDATE_FASTEST_INTERVAL = 5 * 1000;
    public static float LOCATION_UPDATE_DISPLACEMENT = 10;

    public static int REQ_CODE_GOOGLE_PLAY_SERVICE = 2000;
    public static int REQ_CODE_REQ_LOCATION_UPDATE = 2001;
}
