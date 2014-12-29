
package com.demo.simon;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
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
					CameraPosition cp = new CameraPosition(LocationUtility.latlng, 18, 0, 0);
					aMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
				}
            }

        });
        aMap.setOnMarkerClickListener(new OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker arg0)
            {
                if (arg0.isInfoWindowShown())
                {
                    Dialog dlg = new Dialog(CourierMapActivity.this);
                    dlg.show();
                }
                else
                {
                    arg0.showInfoWindow();
                }
                return true;
            }

        });
        addMarkersToMap();
    }

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
