package com.baram.lotto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MapViewActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener,
        MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.POIItemEventListener {
    private static final String LOG_TAG = "MapViewActivity";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int RADIUS = 2000;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION};

    private MapView mapView;
    private List<Location.Document> DocList;
    private int totalCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setCurrentLocationEventListener(this);  // 현재위치 이벤트 리스너 설정
        mapView.removeAllPOIItems();    // 모든 Marker 제거
        mapView.removeAllCircles();     // 모든 Circle 제거
        mapView.setPOIItemEventListener(this);  // Marker 이벤트 설정
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());    // 콜아웃 벌룬 어뎁터 설정
        
        // 검색 결과를 담을 리스트
        DocList = new ArrayList<>();

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

    }

    public void MapMarker(String place_name, double startX, double startY) {
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord( startY, startX );
        MapPOIItem marker = new MapPOIItem();
        marker.setTag(mapView.getPOIItems().length);
        marker.setItemName(place_name); // 마커 클릭 시 컨테이너에 담길 내용
        marker.setMapPoint(mapPoint); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setMarkerType( MapPOIItem.MarkerType.RedPin ); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setSelectedMarkerType( MapPOIItem.MarkerType.BluePin ); // 마커를 클릭했을때, 기본으로 제공하는 BluePin 마커 모양.
        mapView.addPOIItem(marker);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView.setShowCurrentLocationMarker(false);
        DocList = null;
    }

    private void callPlaceList(int page, double x, double y) {
        AddrSearchRepository.getINSTANCE().getAddressList(page, x, y, new AddrSearchRepository.AddressResponseListener() {
            @Override
            public void onSuccessResponse(Location locationData) {
                Log.d(LOG_TAG, "Total: " +  locationData.meta.getTotal_count());
                if (totalCount == 0) {
                    totalCount = locationData.meta.getTotal_count();
                }
                Toast.makeText(getApplicationContext(), String.format("반경 %dm 내 판매점 수: %d", RADIUS, totalCount), Toast.LENGTH_SHORT).show();
                //initAdapter(locationData);

                for (Location.Document doc:locationData.documentsList) {
                    Log.d(LOG_TAG,  mapView.getPOIItems().length + ")place_name: " + doc.getPlace_name() + " x: " + doc.getX() + " y: " + doc.getY());
                    DocList.add(doc);
                    MapMarker(doc.getPlace_name(), Double.parseDouble(doc.getX()), Double.parseDouble(doc.getY()));
                }

                // 마지막 페이지가 아니면
                if (!locationData.meta.isIs_end()) {
                    callPlaceList(page + 1, x, y);
                }
            }

            @Override
            public void onFailResponse() {
                Toast.makeText(getApplicationContext(), "데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.fail_result), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        try {
            Log.d(LOG_TAG, "Tag: " + mapPOIItem.getTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        int tag = mapPOIItem.getTag();
        String phone_number = DocList.get(tag).getPhone();
        if (phone_number.equals("")){
            Toast.makeText(getApplicationContext(), "전화번호가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "전화번호가 복사되었습니다.", Toast.LENGTH_SHORT).show();

            // 클립보드에 저장
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("phone", phone_number);
            clipboardManager.setPrimaryClip(clipData);
        }
        
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));

        // 현재 위치에 원이 없는 경우
        if (mapView.getCircles().length == 0) {
            // 현재 위치를 기준으로 원을 그림
            MapCircle mCircle = new MapCircle(
                MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude), // center
                    RADIUS, // radius
                    Color.argb(220, 255, 255, 102), // strokeColor
                    Color.argb(100, 255, 255, 204) // fillColor
            );
            mCircle.setTag(0);
            mapView.addCircle(mCircle);

            // Circle 전체가 맵에 나오게 설정
            MapPointBounds[] mapPointBoundsArray = { mCircle.getBound()};
            MapPointBounds mapPointBounds = new MapPointBounds(mapPointBoundsArray);
            int padding = 50; // px 여백
            mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));

            callPlaceList(1, mapPointGeo.longitude, mapPointGeo.latitude);
        }
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
        Toast.makeText(MapViewActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {
                Log.d("@@@", "start");
                //위치 값을 가져올 수 있음
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(MapViewActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(MapViewActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    void checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MapViewActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)

            // 3.  위치 값을 가져올 수 있음
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
            //mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapViewActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MapViewActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MapViewActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MapViewActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapViewActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // CalloutBalloonAdapter 인터페이스 구현
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem mapPOIItem) {
            int tag = mapPOIItem.getTag();
            Location.Document document = DocList.get(tag);

            ((ImageView) mCalloutBalloon.findViewById(R.id.badge)).setImageResource(R.drawable.ic_baseline_place_24);
            ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(mapPOIItem.getItemName());
            ((TextView) mCalloutBalloon.findViewById(R.id.desc)).setText(document.getDistance() + "m");
            ((TextView) mCalloutBalloon.findViewById(R.id.road_address_name)).setText(document.getRoad_address_name());
            ((TextView) mCalloutBalloon.findViewById(R.id.address_name)).setText(document.getAddress_name());
            ((TextView) mCalloutBalloon.findViewById(R.id.phone_number)).setText(document.getPhone());

            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }
}