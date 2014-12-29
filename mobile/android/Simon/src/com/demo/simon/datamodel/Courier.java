
package com.demo.simon.datamodel;

import org.json.JSONException;
import org.json.JSONObject;

public class Courier {

    private final static String ID = "id";
    private final static String NAME = "name";
    private final static String PHONE = "phone";
    private final static String COMPANY_ID = "company_id";
    private final static String COMPANY_NAME = "company_name";
    private final static String SITE_NAME = "site_name";
    private final static String REG_TIME = "reg_time";
    private final static String LINE_DISTANCE = "line_distance";

    private final String mId;
    private final String mName;
    private final String mPhone;
    private final String mCompanyId;
    private final String mCompanyName;
    private final String mSiteName;
    private final String mRegTime;
    private final Long mLineDistance;

    private Courier(String id, String name, String phone, String companyId,
            String companyName, String siteName, String regTime, Long lineDistance) {
        mId = id;
        mName = name;
        mPhone = phone;
        mCompanyId = companyId;
        mCompanyName = companyName;
        mSiteName = siteName;
        mRegTime = regTime;
        mLineDistance = lineDistance;
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
            Long lineDistance = object.getLong(LINE_DISTANCE);
            courier = new Courier(id, name, phone, companyId, companyName, siteName, regTime, lineDistance);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return courier;
    }
}
