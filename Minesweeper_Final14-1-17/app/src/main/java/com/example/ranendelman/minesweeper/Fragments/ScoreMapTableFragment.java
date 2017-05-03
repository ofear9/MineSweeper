package com.example.ranendelman.minesweeper.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ranendelman.minesweeper.Activities.ScoreTableActivity;
import com.example.ranendelman.minesweeper.DBLogic.dataBase;
import com.example.ranendelman.minesweeper.GPS.GPSTracker;
import com.example.ranendelman.minesweeper.GameLogic.Score;
import com.example.ranendelman.minesweeper.R;
import com.example.ranendelman.minesweeper.GameLogic.gameLevel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by ofear on 1/7/2017.
 */

public class ScoreMapTableFragment extends Fragment {

    private ArrayList<Score> scores;
    private dataBase db;
    private gameLevel level;
    private Score oneScoreOnly = null;
    MapView mMapView;
    private GoogleMap googleMap;
    private GPSTracker gpsTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_score, container, false);

        level = ((ScoreTableActivity) getActivity()).getLevel();


        /**
         * This line is if the fragment call from the table view with one specific score to show
         */
        if (getArguments() != null)
            oneScoreOnly = (Score) getArguments().getSerializable("score");

        db = new dataBase(getActivity());

        scores = db.getScoreTable(level);
        db.close();

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                gpsTracker = new GPSTracker(getActivity());

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                /**
                 * If there is only one score to show on map
                 */
                if (oneScoreOnly != null) {
                    addMarkOnMap(oneScoreOnly);
                }
                /**
                 * if there is a list of score to show on map
                 */
                else if (scores != null) {
                    for (int i = 0; i < scores.size(); i++) {
                        addMarkOnMap(scores.get(i));
                    }
                }


            }
        });

        return rootView;
    }

    /**
     * This method take a score and put a marker according to the score details
     */
    public void addMarkOnMap(Score score) {
        String latitude = score.getLatitude();
        String longitude = score.getLongitude();

        if (latitude != null && longitude != null) {
            LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            String address = score.getAddress();
            String country = score.getCountry();
            String city = score.getCity();
            String name = score.getName();
            Long scoreLong = score.getScore();
            String scoreStr = String.format("%02d:%02d", ((scoreLong % 3600) / 60), scoreLong % 60);
            String info = name + ", " + address + ", " + scoreStr + "," + country + ", " + city;
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            googleMap.addMarker(new MarkerOptions().position(location).title(info));
            googleMap.animateCamera(zoom);

        }
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}