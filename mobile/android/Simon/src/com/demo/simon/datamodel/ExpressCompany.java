
package com.demo.simon.datamodel;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.demo.simon.NetworkManager;
import com.demo.simon.StorageManager;

public class ExpressCompany {

    private final static String ID = "id";
    private final static String INTERNAL_NAME = "internal_name";
    private final static String DISPLAY_NAME = "display_name";

    private final String mId;
    private final String mInternalName;
    private final String mDisplayName;
    private Bitmap mLogoBitmap;

    public ExpressCompany(String id, String internalName, String displayName) {
        mId = id;
        mInternalName = internalName;
        mDisplayName = displayName;
    }

    public String getId() {
        return mId;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public Bitmap getLogoBitmap() {
        return mLogoBitmap;
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

    public void downloadLogo(Context context) throws ClientProtocolException, IOException {
        File logoFile = StorageManager.getCompanyLogoFile(context, mId);
        if (logoFile.exists()) {
            return;
        }
        String logoUrl = NetworkManager.getExpressCompanyLogoUrl(mId);
        NetworkManager.downloadToLocal(context, logoUrl, logoFile);

        if (logoFile.exists()) {
            mLogoBitmap = StorageManager.loadImage(context, Uri.fromFile(logoFile));
        }
    }
}
