
package com.demo.simon;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.model.LatLng;
import com.demo.simon.utility.LocationUtility;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity implements
AMapLocationListener, Runnable {
    private MyOrderFragment mOrderFragment;
    private SendExpressFragment mSendExpressFragment;
    private String mCurrentFragmentTag;

    private LocationManagerProxy aMapLocManager = null;
	private AMapLocation aMapLocation;
	private Handler handler = new Handler();
	
	private static MainActivity instance = null;
	
	public static MainActivity getInstance()
	{
		return instance;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set the content view
        setContentView(R.layout.activity_main);
        // configure the SlidingMenu
        final SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow_vertical);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.fragment_slidingmenu);

        View userButton = findViewById(R.id.user_center);
        userButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (!menu.isMenuShowing()) {
                    menu.showMenu();
                } else {
                    menu.showContent();
                }
            }

        });
        mOrderFragment = new MyOrderFragment();
        mSendExpressFragment = new SendExpressFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, mOrderFragment, MyOrderFragment.getFragmentTag());
        mCurrentFragmentTag = MyOrderFragment.getFragmentTag();
        fragmentTransaction.commit();

        Button myOrdersBtn = (Button) findViewById(R.id.button_express_order);
        myOrdersBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MyOrderFragment.getFragmentTag().equalsIgnoreCase(mCurrentFragmentTag)) {
                    return;
                }
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, mOrderFragment, MyOrderFragment.getFragmentTag());
                mCurrentFragmentTag = MyOrderFragment.getFragmentTag();
                fragmentTransaction.commit();
            }

        });
        Button sendExpressBtn = (Button) findViewById(R.id.button_send_express);
        sendExpressBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (SendExpressFragment.getFragmentTag().equalsIgnoreCase(mCurrentFragmentTag)) {
                    return;
                }
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, mSendExpressFragment, SendExpressFragment.getFragmentTag());
                mCurrentFragmentTag = SendExpressFragment.getFragmentTag();
                fragmentTransaction.commit();
//            	Intent i = new Intent(MainActivity.this, CourierMapActivity.class);
//            	MainActivity.this.startActivity(i);
            }

        });
        
        initLocationService();
    }
    
    private void initLocationService()
    {
		aMapLocManager = LocationManagerProxy.getInstance(this);
		aMapLocManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 2000, 10, this);
		handler.postDelayed(this, 20000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public void onLocationChanged(Location location)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation location)
	{
		if (location != null) {
			this.aMapLocation = location;
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			String cityCode = "";
			String desc = "";
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			LocationUtility.locationString = geoLng + "," + geoLat;
			LocationUtility.street = LocationUtility.setStreetName(location.getStreet());
			LocationUtility.district = location.getDistrict();
			LocationUtility.city = location.getCity();
			LocationUtility.address = location.getAddress();
			LocationUtility.latlng = new LatLng(location.getLatitude(), location.getLongitude());
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		stopLocation();
	}
	
	private void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destory();
		}
		aMapLocManager = null;
	}
}
