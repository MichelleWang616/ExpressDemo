
package com.demo.simon.datamodel;

import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.maps2d.model.LatLng;

public class Courier {

    private final static String ID = "id";
    private final static String NAME = "name";
    private final static String PHONE = "phone";
    private final static String COMPANY_ID = "company_id";
    private final static String COMPANY_NAME = "company_name";
    private final static String SITE_NAME = "site_name";
    private final static String REG_TIME = "reg_time";
    private final static String LOCATION = "latest_location";
    private final static String LINE_DISTANCE = "line_distance";

    private final String mId;
    private final String mName;
    private final String mPhone;
    private final String mCompanyId;
    private final String mCompanyName;
    private final String mSiteName;
    private final String mRegTime;
    private final String mLocation;
    private final double mLineDistance;
    private LatLng mLatLng = null;

    private Courier(String id, String name, String phone, String companyId,
            String companyName, String siteName, String regTime, String location, double lineDistance) {
        mId = id;
        mName = name;
        mPhone = phone;
        mCompanyId = companyId;
        mCompanyName = companyName;
        mSiteName = siteName;
        mRegTime = regTime;
        mLocation = location;
        mLineDistance = lineDistance;
        
        try
        {
        	String[] strAll = mLocation.split("\\,");
        	float a = Float.parseFloat(strAll[0]);
        	float b = Float.parseFloat(strAll[1]);
        	mLatLng = new LatLng(b, a);
        }
        catch(Exception ex)
        {
        }
    }
    
    public LatLng getLatLng()
    {
    	return mLatLng;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getCompanyName() {
        return mCompanyName;
    }

    public static Courier fromJSONData(JSONObject object) {
        Courier courier = null;
        try {
            String id = object.getString(ID);
            String name = object.getString(NAME);
            String phone = object.getString(PHONE);
            String companyId = object.getString(COMPANY_ID);
            String companyName = object.getString(COMPANY_NAME);
            String siteName = object.getString(SITE_NAME);
            String regTime = object.getString(REG_TIME);
            String location = object.getString(LOCATION);
            double lineDistance = object.getDouble(LINE_DISTANCE);
            courier = new Courier(id, name, phone, companyId, companyName, siteName, regTime, location, lineDistance);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return courier;
    }
}
