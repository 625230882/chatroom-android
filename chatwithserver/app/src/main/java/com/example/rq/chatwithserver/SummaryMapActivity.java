package com.example.rq.chatwithserver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import provider.PeerContentProvider;

public class SummaryMapActivity extends Fragment implements  OnMapReadyCallback{

    MapView mapView;
    GoogleMap map;
    private double latitude,longitude;
    public interface Pos{
        public double[] getPos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mapfragment, container, false);

        Pos listener = (Pos)getActivity();
        double[] a = listener.getPos();
        latitude = a[0];
        longitude = a[1];
        Log.w("location-----------",latitude+"--------"+longitude);
        // Gets the MapView from the XML layout and creates it

       /* SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map_frag);
        mapFragment.getMapAsync(this);
        */
        // Gets to GoogleMap from the MapView and does initialization stuff
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        mapView = (MapView) view.findViewById(R.id.map_);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }
    public List<double[]> getPosArray(){
        List<double[]> res = new ArrayList<double[]>();
        Cursor cursor = getActivity().getContentResolver().query(PeerContentProvider.CONTENT_URI, null, null, null, null, null);
        loop:while(cursor.moveToNext()) {
            double[] temp = new double[2];
            temp[0] = cursor.getDouble(cursor.getColumnIndex("latitude"));
            temp[1] = cursor.getDouble(cursor.getColumnIndex("longitude"));
            for(double[] d:res){
                if(d[0]==temp[0] && d[1]==temp[1]){
                    continue loop;
                }
            }
            res.add(temp);
        }
        return res;
    }


    @Override
    public void onMapReady(final GoogleMap Mmap) {
        this.map = Mmap;

        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }

        List<double[]> posList = getPosArray();
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(double[] d : posList){
            Log.w("location-----------:", d[0] + "--------:" + d[1]);

            map.addMarker(new MarkerOptions()
                    .position(new LatLng(d[0],
                            d[1]))
                    .draggable(true)
                    .title(d[0] + "," + d[1]));
        }
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(latitude,
                        longitude));
        LatLng PERTH = new LatLng(latitude,
                longitude);

        CameraUpdate zoom=CameraUpdateFactory.zoomTo(1);

        map.addMarker(new MarkerOptions()
                .position(PERTH)
                .draggable(true)
                .title(latitude+","+longitude));
        map.setMyLocationEnabled(true);
        map.moveCamera(center);
        map.animateCamera(zoom);

    }
}