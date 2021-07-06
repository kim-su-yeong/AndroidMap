package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.naver.maps.geometry.GeoConstants;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    Spinner spinner_maptype;
    CheckBox check_mapcadastral;
    int count = 0;
    List<LatLng> polycoords = new ArrayList<>();
    Marker clickmarker, infomarker, addressmarker;
    PolygonOverlay clickpolygon;
    Button btnreset, btncam, btndia;
    List<Marker> markerall = new ArrayList<>();
    List<PolygonOverlay> polygonall = new ArrayList<>();
    InfoWindow infoWindow;
    NaverGeocoding naverGeocoding;
    

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

        btnreset = (Button) findViewById(R.id.btnreset);
        btncam = (Button) findViewById(R.id.btncam);
        btndia = (Button) findViewById(R.id.btndia);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        infomarker = new Marker();
        addressmarker = new Marker();
        infoWindow = new InfoWindow();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
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


        // 군산대학교, 군산시청, 군산항 다각형연결. 다각형 색 빨간(투명도 50%)

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
        

        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(PointF pointF, LatLng latLng) {
                clickmarker = new Marker();
                clickmarker.setPosition(new LatLng(latLng.latitude, latLng.longitude));
                clickmarker.setMap(naverMap);
                markerall.add(clickmarker);

                clickpolygon = new PolygonOverlay();
                polycoords.add(count, new LatLng(latLng.latitude, latLng.longitude));
                //Toast.makeText(getApplicationContext(), "카운트 : " + count, Toast.LENGTH_SHORT).show();
                clickpolygon.setColor(Color.parseColor("#50FF0000"));
                clickpolygon.setOutlineWidth(4);
                clickpolygon.setOutlineColor(Color.RED);
                polygonall.add(clickpolygon);

                count++;
                //Toast.makeText(getApplicationContext(), "카운트 : " + count, Toast.LENGTH_SHORT).show();

                if(count >= 3) {
                    clickpolygon.setCoords(polycoords);
                    clickpolygon.setMap(naverMap);
                }
            }
        });
        
        btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<markerall.size(); i++) {
                    polygonall.get(i).setMap(null);
                    markerall.get(i).setMap(null);
                }
            }
        });

        btncam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(35.968643, 126.709838));
                cameraUpdate = CameraUpdate.zoomTo(11);
                naverMap.moveCamera(cameraUpdate);
            }
        });

        naverMap.setOnMapLongClickListener(new NaverMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                infomarker.setPosition(new LatLng(latLng.latitude, latLng.longitude));
                infomarker.setMap(naverMap);
                naverGeocoding = new NaverGeocoding(MainActivity.this);

                naverGeocoding.execute(infomarker.getPosition());
                //Toast.makeText(getApplication(), address, Toast.LENGTH_SHORT).show();
            }
        });

        btndia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog, null);

                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("주소 입력 창");
                dlg.setView(dialogView);
                EditText dlgEdtAddress = (EditText) dialogView.findViewById(R.id.addressedit);
                String address = dlgEdtAddress.getText().toString();
                dlg.setPositiveButton("마커 찍기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplication(), address, Toast.LENGTH_SHORT).show();
                                //addressmarker.setPosition();
                                //addressmarker.setMap(naverMap);
                            }
                });
                dlg.show();
            }
        });
    }


    void viewAddress(String address) {
        //TODO infowindow에 메시지를 입력하는 부분 구현
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplication()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return address;
            }
        });

        //Toast.makeText(getApplication(), address, Toast.LENGTH_SHORT).show();

        infoWindow.open(infomarker);
    }
}