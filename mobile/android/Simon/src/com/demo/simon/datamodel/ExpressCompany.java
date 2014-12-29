
package com.demo.simon.datamodel;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class ExpressCompany {

    private final static String ID = "id";
    private final static String INTERNAL_NAME = "internal_name";
    private final static String DISPLAY_NAME = "display_name";

    private final String mId;
    private final String mInernalName;
    private final String mDisplayName;

    private ExpressCompany(String id, String internalName, String displayName) {
        mId = id;
        mInernalName = internalName;
        mDisplayName = displayName;
    }

    public String getId() {
        return mId;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public static ExpressCompany fromJSONData(JSONObject object) {
        ExpressCompany company = null;
        try {
            String id = object.getString(ID);
            String internalName = object.getString(INTERNAL_NAME);
            String displayName = object.getString(DISPLAY_NAME);
            company = new ExpressCompany(id, internalName, displayName);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return company;
    }

    public void downloadLogo(Context context) {

    }
}
