package com.demo.simon.utility;

import com.amap.api.maps2d.model.LatLng;
import com.demo.simon.MainActivity;
import com.demo.simon.R;

public class LocationUtility
{
	public static String locationString = "";
	public static String street = "";
	public static String district = "";
	public static String city = "";
	public static String address = "";
	public static LatLng latlng = new LatLng(31.219821, 121.5258);

	public static String setStreetName(String streetStr)
	{
		String[] prefixList = MainActivity.getInstance().getResources().getStringArray(R.array.street_prefix);
		for(String prefix : prefixList)
		{
			if (streetStr.contains(prefix))
			{
				return streetStr.substring(0, streetStr.indexOf(prefix) + prefix.length());
			}
		}
		return streetStr;
	}
}
