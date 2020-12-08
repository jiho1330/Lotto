package com.baram.lotto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Permission;
import java.util.HashMap;
import java.util.Map;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private FragmentManager fm;
    private MapFragment mapFragment;
    private static NaverMap mNaverMap;
    private UiSettings uiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        fm = getSupportFragmentManager();
        mapFragment = (MapFragment)fm.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        // AndroidManifest.xml을 수정하지 않고 API를 호출해 클라이언트 ID를 지정할 수도 있음
//        NaverMapSdk.getInstance(this).setClient(
//                new NaverMapSdk.NaverCloudPlatformClient("s43jhlawk0"));

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.mNaverMap = naverMap;
        uiSettings = naverMap.getUiSettings();
        // 현위치 버튼 사용여부
        uiSettings.setLocationButtonEnabled(true);

        // 실내지도 활성화
        naverMap.setIndoorEnabled(true);

        // NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정
        //naverMap.setLocationSource(locationSource);

        // 위치 추적 모드 : 위치를 추적하면서 카메라의 좌표와 베어링도 따라 움직이는 모드
        //naverMap.setLocationTrackingMode(LocationTrackingMode.Face);

        // 카메라 영역지정
        naverMap.setExtent(new LatLngBounds(new LatLng(31.43, 122.37), new LatLng(44.35, 132)));

        // 최소 및 최대 줌 지정
        naverMap.setMinZoom(5.0);
        naverMap.setMaxZoom(20.0);

        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(37.566678, 126.978409))
                .animate(CameraAnimation.Linear);
        naverMap.moveCamera(cameraUpdate);


        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        Log.d("Test", locationOverlay.getPosition().latitude + " : " + locationOverlay.getPosition().longitude);

        Marker marker = new Marker();
        marker.setPosition(new LatLng(37.5670135, 126.9783740));
//        marker.setOnClickListener(overlay -> {
//            Toast.makeText(getApplicationContext(), "마커 클릭됨", Toast.LENGTH_SHORT).show();
//            return true;
//        });
        marker.setMap(naverMap);

        InfoWindow infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplicationContext()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return "복권 판매점";
            }
        });

        // 지도를 클릭하면 정보 창을 닫음
        naverMap.setOnMapClickListener((coord, point) -> {
            infoWindow.close();
        });

        // 마커를 클릭하면:
        Overlay.OnClickListener listener = overlay -> {
            Marker mMarker = (Marker)overlay;

            if (mMarker.getInfoWindow() == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(mMarker);
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close();
            }

            return true;
        };

        marker.setOnClickListener(listener);

        //searchPlace("로또");
//        CameraPosition cameraPosition = new CameraPosition(
//                new LatLng(33.38, 126.55),  // 위치 지정
//                9                           // 줌 레벨
//        );
//        naverMap.setCameraPosition(cameraPosition);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // 마커 표시
    private void setMark(Marker marker, double lat, double lng, int resourceID)
    {
        //원근감 표시
        marker.setIconPerspectiveEnabled(false);
        //아이콘 지정
        marker.setIcon(OverlayImage.fromResource(resourceID));
        //마커의 투명도
        marker.setAlpha(0.8f);
        //마커 위치
        marker.setPosition(new LatLng(lat, lng));
        //마커 우선순위
        marker.setZIndex(10);
        //마커 표시
        marker.setMap(mNaverMap);
    }

    // 네이버 장소 검색
    public String searchPlace(String keyword){
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("encoding fail!",e);
        }

        String apiURL = "https://openapi.naver.com/v1/search/local.json?query="+keyword+"&display=20&start=1&sort=random";    // json 결과
        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-NCP-APIGW-API-KEY-ID", "s43jhlawk0");
        requestHeaders.put("X-NCP-APIGW-API-KEY", "UexfyjgnocdVBePjvvgvqf8zs8FYcJ25h3v83LeZ");
//        requestHeaders.put("X-Naver-Client-Id", "s43jhlawk0");
//        requestHeaders.put("X-Naver-Client-Secret", "UexfyjgnocdVBePjvvgvqf8zs8FYcJ25h3v83LeZ");
        String responseBody = get(apiURL,requestHeaders);

        System.out.println("네이버에서 받은 결과 = " + responseBody);
        System.out.println("-----------------------------------------");

        return responseBody;
//        return convertData(responseBody);
    }

    // request를 전송하고 response를 받는 메소드
    private String get(String apiUrl, Map<String, String> requestHeaders){
        Log.d("Test", "get called");
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    // Http url Connect 메소드
    private HttpURLConnection connect(String apiUrl){
        Log.d("Test", "connect called");
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    // response의 body를 읽는 메소드
    private String readBody(InputStream body){
        Log.d("Test", "readBody called");
        InputStreamReader streamReader = new InputStreamReader(body, StandardCharsets.UTF_8);

        try (
                BufferedReader lineReader = new BufferedReader(streamReader)
        ) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}