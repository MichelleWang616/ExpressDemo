
package com.demo.simon;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;

public class CourierMapActivity extends Activity
{
    private AMap aMap;
    private MapView mapView;
    private Marker marker2;
    private LatLng latlng = new LatLng(36.061, 103.834);

    private LatLng CHENGDU = new LatLng(30.679879, 104.064855);
    private LatLng XIAN = new LatLng(34.341568, 108.940174);
    private LatLng ZHENGZHOU = new LatLng(34.7466, 113.625367);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_map);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        init();
    }

    private void init()
    {
        if (aMap == null)
        {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap()
    {
        aMap.setOnMapLoadedListener(new OnMapLoadedListener()
        {

            @Override
            public void onMapLoaded()
            {
                LatLngBounds bounds = new LatLngBounds.Builder().include(XIAN).include(CHENGDU).include(latlng)
                        .include(ZHENGZHOU).build();
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
            }

        });
        aMap.setOnMarkerClickListener(new OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker arg0)
            {
                if (arg0.equals(marker2))
                {
                    if (aMap != null)
                    {
                        jumpPoint(arg0);
                    }
                }

                if (arg0.isInfoWindowShown())
                {
                    Dialog dlg = new Dialog(CourierMapActivity.this);
                    dlg.show();
                }
                else
                {
                    arg0.showInfoWindow();
                }
                return false;
            }

        });
        addMarkersToMap();
    }

    public void jumpPoint(final Marker marker)
    {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        Point startPoint = proj.toScreenLocation(XIAN);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * XIAN.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * XIAN.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                aMap.invalidate();
                if (t < 1.0)
                {
                    handler.postDelayed(this, 16);
                }
            }
        });

    }

    private void addMarkersToMap()
    {
        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(CHENGDU).title("chengdu")
                .snippet("info of chengdu").draggable(true));

        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(ZHENGZHOU).title("zhengzhou")
                .snippet("info of zhengzhou").draggable(true));

        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(latlng).title("xxx")
                .snippet("info of xxx").draggable(true));
    }
}
