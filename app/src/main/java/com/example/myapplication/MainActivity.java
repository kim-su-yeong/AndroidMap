package com.example.myapplication;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.overlay.PolygonOverlay;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private NaverMap naverMap;
    Spinner spinner_maptype;
    CheckBox check_mapcadastral;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        spinner_maptype = (Spinner) findViewById(R.id.spinner_maptype);

        ArrayAdapter maptype = ArrayAdapter.createFromResource(this, R.array.maptype, android.R.layout.simple_spinner_dropdown_item);
        maptype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_maptype.setAdapter(maptype);

        check_mapcadastral = (CheckBox) findViewById(R.id.check_mapcadastral);

    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setMapType(NaverMap.MapType.Satellite);

        spinner_maptype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                    naverMap.setMapType(NaverMap.MapType.Basic);
                    //Toast.makeText(getApplicationContext(),"Basic", Toast.LENGTH_SHORT).show();
                else if(position==1)
                    naverMap.setMapType(NaverMap.MapType.Satellite);
                //Toast.makeText(getApplicationContext(),"Satellite", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        check_mapcadastral.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, isChecked);
            }
        });

        LatLng location = new LatLng(35.944409, 126.682839);
        CameraPosition cameraPosition = new CameraPosition(location, 15);
        naverMap.setCameraPosition(cameraPosition);

        // 군산대학교
        Marker marker = new Marker();
        marker.setPosition(new LatLng(35.944409, 126.682839));
        marker.setMap(naverMap);

        // 군산시청
        Marker marker2 = new Marker();
        marker2.setPosition(new LatLng(35.967536, 126.736837));
        marker2.setMap(naverMap);

        // 군산항
        Marker marker3 = new Marker();
        marker3.setPosition(new LatLng(35.969751, 126.617286));
        marker3.setMap(naverMap);

        PolygonOverlay polygon = new PolygonOverlay();
        polygon.setCoords(Arrays.asList(
                new LatLng(35.944409, 126.682839),
                new LatLng(35.967536, 126.736837),
                new LatLng(35.969751, 126.617286)
        ));
        polygon.setColor(Color.parseColor("#50FF0000"));
        polygon.setOutlineWidth(4);
        polygon.setOutlineColor(Color.RED);
        polygon.setMap(naverMap);

        naverMap.setOnMapClickListener((point, coord) ->
                Toast.makeText(this, "위도 : " +coord.latitude + "\n경도 : " + coord.longitude, Toast.LENGTH_SHORT).show());
    }
}