package com.stepbystep.bossapp.foottraffic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stepbystep.bossapp.DO.Traffic;
import com.stepbystep.bossapp.MainActivity;
import com.stepbystep.bossapp.R;

import java.util.ArrayList;

public class FootTraffic extends Fragment {

    private MainActivity mainActivity;
    //구글맵 지도를 위한 변수
    private GoogleMap map;
    private SupportMapFragment supportMapFragment;
    private LocationManager locationManager;
    private LocationListener listener;
    //자기 위치 저장 변수
    private LatLng myLatLng;
    //데이터 참조 변수
    private DatabaseReference databaseReference;
    //자기 자신의 위치와 상권의 위치를 저장하기 위한 변수
    private android.location.Location myLocation;
    private android.location.Location trafficLocation;
    //자기 자신의 위치와 트럭의 위치 사이의 거리를 저장하기 위한 변수
    private double distance;
    //위치를 gps로 불러왔는지 network로 불러왔는지 확인해주는 변수
    private int flag;
    //MapAdapter 변수
    private MapAdapter mapAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //자신을 담고 있는 메인액티비티 받아오기
        mainActivity = (MainActivity) getActivity();
        //위치 권한 승인 받기
        ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        //위치 관리자 객체 생성, 현재 위치 설정
        locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        flag = 0;
        //내 위치 불러오기 위치 불러오는 방식에 따라 flag 정해짐
        if(myLocation == null){
            myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            flag = 1;
            //내 위치를 결국 못불러오면 기본 위치로 설정
            if (myLocation == null){
                myLocation = new Location("myLocation");
                myLocation.setLatitude(37.56667);
                myLocation.setLongitude(126.97806);
                flag = 2;
                Toast.makeText(mainActivity,"위치 정보를 불러올수 없습니다.",Toast.LENGTH_SHORT).show();
            }
        }
        myLatLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
        //파이어베이스 데이터 참조 객체 생성
        databaseReference = FirebaseDatabase.getInstance().getReference("FootTraffic");
        //상권 데이터를 담을 객체 생성
        trafficLocation = new Location("trafficLocation");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_foot_traffic, container, false);
        //리싸이클러 뷰 생성, MapAdapter 생성, 기본 설정
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_foot_traffic_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        mapAdapter = new MapAdapter();
        mapAdapter.setContext(mainActivity);
        recyclerView.setAdapter(mapAdapter);
        //MapAdapter item 리스너 설정
        mapAdapter.setListener(new onMapItemClickListener() {
            @Override
            public void onItemClick(MapAdapter.ViewHolder holder, View view, int position) {

            }
        });
        //map fragment 시작
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_foot_traffic_google_map);
        //async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            //맵이 로드 된 뒤 다음 호출
            public void onMapReady(@NonNull GoogleMap googleMap) {
                map = googleMap;
                //카메라 줌하고  circle 그리기
                CameraUpdate cameraUpdate;
                if(flag == 2){
                    cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(37.56667,126.97806),8);
                }
                else{
                    cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng,15);
                }
                if(checkLocationPermission() == false){
                    return;
                }
                map.setMyLocationEnabled(true);
                map.moveCamera(cameraUpdate);
                drawCircle(map,myLatLng);
                try {
                    if(checkLocationPermission() == false){
                        return;
                    }
                    //맵 ui 세팅
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                    map.getUiSettings().setZoomControlsEnabled(true);
                    //줌 컨트롤러 위치 설정
                    int zoomControlId = 0x1;
                    View zoomControls = supportMapFragment.getView().findViewById(zoomControlId);
                    if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                        //구글맵은 relativeLayout으로 구성됨
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();
                        // top left로 설정
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        //dp -> px로 변환하여 margin 설정
                        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
                        params.setMargins(margin, margin *6, margin, margin);
                    }
                    //마커 클릭 리스너 설정 , 마커를 클릭하면 거기에 해당되는 리싸이클 뷰 아이템 레이아웃이 표시되게 한다.
                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {
                            LatLng markerPosition = marker.getPosition();;
                            int selectedMarker = 0;
                            ArrayList<Traffic> items = mapAdapter.getItems();
                            for (int i = 0; i < items.size(); i++) {
                                if (marker.getTag().equals(items.get(i).getHubName())) {
                                    selectedMarker = i;
                                    break;
                                }
                            }
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(markerPosition).zoom(map.getCameraPosition().zoom).build();
                            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            recyclerView.scrollToPosition(selectedMarker);
                            return true;
                        }
                    });
                }catch (Exception e){

                }
                //위치 리스너 설정
                setMyLocationListener();
                //위치 서비스 시작
                startLocationService();

                //데이터베이스 변화 리스너 설정
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Traffic traffic = dataSnapshot.getValue(Traffic.class);
                            //푸드트럭 위치 자기 자신위치 비교
                            trafficLocation.setLongitude(traffic.getLongitude());
                            trafficLocation.setLatitude(traffic.getLatitude());
                            distance = myLocation.distanceTo(trafficLocation);
                            //자기와 20km 이내인것만 표시
                            if(distance <20*1000){
                                setMarker(traffic);
                                traffic.setDistance(distance);
                                mapAdapter.addItem(traffic);
                            }
                        }
                        mapAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("location",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("latitude",(float) myLocation.getLatitude());
        editor.putFloat("longitude",(float) myLocation.getLongitude());
        editor.commit();
    }


    //권한 확인 여부 체크 메서드
    private boolean checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        else{
            return true;
        }
    }

    //마커 추가 메서드
    private void setMarker(Traffic traffic) {
        int height = 120;
        int width = 120;
        LatLng latLng = new LatLng(traffic.getLatitude(),traffic.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        Marker marker = map.addMarker(markerOptions);
        marker.setTag(traffic.getHubName());
        marker.setTitle(traffic.getHubName());
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.walk);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
    }

    //자기 자신 위치 리스너 설정
    private void setMyLocationListener(){
        listener = new LocationListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationChanged(@NonNull Location location) {
                myLocation = location;
                if(myLatLng == null){
                    myLatLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                }
                try {
                    map.setMyLocationEnabled(true);
                    drawCircle(map,myLatLng);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng,15);
                    map.moveCamera(cameraUpdate);
                }catch (Exception e){

                }
            }
        };
    }

    //위치 리스너 위치매니저에 등록 100m마다 and 3분이 지났을 때마다 location을 갱신해준다.
    private void startLocationService() {
        long minTime = 60*3;
        float minDistance = 100;
        checkLocationPermission();
        if(flag == 0){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener);
        }
        else if(flag == 1){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, listener);
        }
    }

    //자기 위치에 동그라미 그리기 20km 이내인 것만 표시
    public static void drawCircle(GoogleMap map, LatLng latLng) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(1000*20);
        circleOptions.strokeWidth(0);
        circleOptions.fillColor(Color.parseColor("#2271cce7"));
        map.addCircle(circleOptions);
    }

}