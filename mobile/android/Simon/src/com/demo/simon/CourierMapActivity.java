
package com.demo.simon;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.demo.simon.data.CourierData;
import com.demo.simon.datamodel.Courier;
import com.demo.simon.utility.LocationUtility;

public class CourierMapActivity extends Activity
{
    private AMap aMap;
    private MapView mapView;
    private ListView mCourierList = null;
    private Button mSwitchViewBtn = null;

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	CourierData.curSelectedCourier = SendExpressFragment.mRespondedCourier.get(position);
        	goDetailView();
        }

    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_map);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        init();
        
        mCourierList = (ListView) findViewById(R.id.courier_list);
        mCourierList.setOnItemClickListener(mOnItemClickListener);
        
        initListView();
        
        mSwitchViewBtn = (Button) findViewById(R.id.switch_view_btn);
        mSwitchViewBtn.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if (mCourierList.getVisibility() == View.GONE)
				{
					mCourierList.setVisibility(View.VISIBLE);
					mSwitchViewBtn.setText(R.string.switch_to_mapview);
				}
				else
				{
					mCourierList.setVisibility(View.GONE);
					mSwitchViewBtn.setText(R.string.switch_to_listview);
				}
			}
		});
    }
    
    private void goDetailView()
    {
    	Intent i = new Intent(MainActivity.getInstance(), CourierDetailInfoActivity.class);
    	MainActivity.getInstance().startActivity(i);
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
//                    Dialog dlg = new Dialog(CourierMapActivity.this);
//                    dlg.show();
                	CourierData.curSelectedCourier = (Courier)arg0.getObject();
                	goDetailView();
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
                    .snippet(courier.getCompanyName()).draggable(false)).setObject(courier);
    	}
    }

    private void initListView()
    {
    	 CourierAdapter adapter = new CourierAdapter(this);
//         mCourierList.setVisibility(View.VISIBLE);
         mCourierList.setAdapter(adapter);
    }

    private class CourierAdapter extends ArrayAdapter<Courier> {

        public CourierAdapter(Context context) {
            super(context, 0);
        }

        @Override
		public Courier getItem(int position)
		{
        	return SendExpressFragment.mRespondedCourier.get(position);
		}

		@Override
		public int getCount()
		{
			return SendExpressFragment.mRespondedCourier.size();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.courier_item, null);
            }
            Courier courier = SendExpressFragment.mRespondedCourier.get(position);
            TextView courier_name = (TextView) convertView.findViewById(R.id.courier_name);
            courier_name.setText(courier.getName());
            TextView company_name = (TextView) convertView.findViewById(R.id.company_name);
            company_name.setText(courier.getCompanyName());
            TextView line_distance = (TextView) convertView.findViewById(R.id.line_distance);
            Context context = parent.getContext();
            line_distance.setText(context.getString(R.string.distance) + courier.getLineDistance() + context.getString(R.string.unit_m));
            return convertView;
        }
    }
}
