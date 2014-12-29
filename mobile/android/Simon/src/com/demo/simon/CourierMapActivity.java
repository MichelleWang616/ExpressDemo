
package com.demo.simon;

import java.util.List;

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
import com.demo.simon.datamodel.Courier;
import com.demo.simon.utility.LocationUtility;

public class CourierMapActivity extends Activity
{
    private AMap aMap;
    private MapView mapView;

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
            	List<Courier> courierList = SendExpressFragment.mRespondedCourier;
            	LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for (Courier courier : courierList)
				{
					builder.include(courier.getLatLng());
				}
				try
				{
					LatLngBounds bounds = builder.build();
					aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
				} catch (Exception ex)
				{
					builder.include(LocationUtility.latlng);
					LatLngBounds bounds = builder.build();
					aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
				}
            }

        });
        aMap.setOnMarkerClickListener(new OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker arg0)
            {
//                if (arg0.equals(marker2))
//                {
//                    if (aMap != null)
//                    {
//                        jumpPoint(arg0);
//                    }
//                }

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

//    public void jumpPoint(final Marker marker)
//    {
//        final Handler handler = new Handler();
//        final long start = SystemClock.uptimeMillis();
//        Projection proj = aMap.getProjection();
//        Point startPoint = proj.toScreenLocation(XIAN);
//        startPoint.offset(0, -100);
//        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//        final long duration = 1500;
//
//        final Interpolator interpolator = new BounceInterpolator();
//        handler.post(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float) elapsed / duration);
//                double lng = t * XIAN.longitude + (1 - t) * startLatLng.longitude;
//                double lat = t * XIAN.latitude + (1 - t) * startLatLng.latitude;
//                marker.setPosition(new LatLng(lat, lng));
//                aMap.invalidate();
//                if (t < 1.0)
//                {
//                    handler.postDelayed(this, 16);
//                }
//            }
//        });
//
//    }

    private void addMarkersToMap()
    {
    	List<Courier> courierList = SendExpressFragment.mRespondedCourier;
    	for (Courier courier : courierList)
    	{
    		aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(courier.getLatLng()).title(courier.getName())
                    .snippet(courier.getCompanyName()).draggable(false));
    	}
    }
}
