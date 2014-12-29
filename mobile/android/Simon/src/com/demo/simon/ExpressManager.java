
package com.demo.simon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.demo.simon.DownloadTask.DownloadTaskListener;
import com.demo.simon.datamodel.ExpressCompany;
import com.demo.simon.utility.Utility;

public final class ExpressManager {
    private final static String TAG = "ExpressManager";
    private static ExpressManager mExpressManager;
    private boolean mInitialized;
    private Context mContext;

    private List<ExpressCompany> mExpressCampanies;

    public static ExpressManager getInstance() {
        if (mExpressManager == null) {
            mExpressManager = new ExpressManager();
        }

        return mExpressManager;
    }

    private ExpressManager() {
    }

    public Context getContext() {
        return mContext;
    }

    public List<ExpressCompany> getCompanies() {
        return mExpressCampanies;
    }

    public void initialize(Context context) {
        if (mInitialized) {
            return;
        }

        if (!StorageManager.isExternalStorageAvailable()) {
            Toast.makeText(context, R.string.makesure_enough_space, Toast.LENGTH_LONG).show();
        }
        if (!NetworkManager.isOnline(context)) {
            Toast.makeText(context, R.string.check_network, Toast.LENGTH_LONG).show();
        }

        mContext = context.getApplicationContext();

        createExpressCompany();
        mInitialized = true;
    }

    private void createExpressCompany() {
        downloadCompanyProfile();
    }

    public void downloadCompanyProfile() {
        if (mCompanyProfileDownloaded || isDownloadingProfile()) {
            return;
        }
        mDownloadProfileTask = new DownloadTask(mContext);
        mDownloadProfileTask.setOnDownloadTaskListener(mDownloadTaskListener);
        mDownloadProfileTask.execute(NetworkManager.getAllCompanyUrl());
    }

    private DownloadTask mDownloadProfileTask;
    private boolean mCompanyProfileDownloaded = false;
    private DownloadTaskListener mDownloadTaskListener = new DownloadTaskListener() {

        @Override
        public void onTaskPreExecute() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTaskPostExecute(String result) {
            mCompanyProfileDownloaded = true;
            mDownloadProfileTask = null;
            String newMD5 = null;
            if (result != null) {
                newMD5 = Utility.generateMD5OfString(result);
            }
            File file = StorageManager.getDownloadedCompanyProfile();
            String oldMD5 = null;
            if (file != null && file.exists()) {
                try {
                    String oldProfile = StorageManager.readTextFile(file);
                    oldMD5 = Utility.generateMD5OfString(oldProfile);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (newMD5 != null && !newMD5.equals(oldMD5)) {
                JSONArray objects = null;
                try {
                    objects = new JSONArray(result);
                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (objects == null) {
                    return;
                }
                try {
                    StorageManager.writeStringToFile(file, result);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mExpressCampanies = parseExpressCompany(objects);
                // download thumbnails
                downloadCompanyLogos();
            }
        }

        @Override
        public void onTaskCanceled() {
            if (mDownloadProfileTask != null) {
                mDownloadProfileTask = null;
            }
        }

    };

    private boolean isDownloadingProfile() {
        return mDownloadProfileTask != null;
    }

    private List<ExpressCompany> parseExpressCompany(JSONArray objects) {
        List<ExpressCompany> companies = new ArrayList<ExpressCompany>();
        int count = objects.length();
        for (int i = 0; i < count; i++) {
            try {
                JSONObject obj = objects.getJSONObject(i);
                companies.add(ExpressCompany.fromJSONData(obj));

            } catch (JSONException ex) {
                Log.w(TAG, ex.getLocalizedMessage());
            }
        }
        return companies;
    }

    public void downloadCompanyLogos() {
        if (mCompanyLogoDownloaded || isDownloadingCompanyLogos()) {
            return;
        }
        mDownloadCompanyLogoTask = new DownloadCompanyLogoTask(mExpressCampanies);
        mDownloadCompanyLogoTask.execute();
    }

    private DownloadCompanyLogoTask mDownloadCompanyLogoTask;
    private boolean mCompanyLogoDownloaded = false;

    private class DownloadCompanyLogoTask extends AsyncTask<Void, Void, Void> {
        List<ExpressCompany> mCompanies;

        public DownloadCompanyLogoTask(List<ExpressCompany> companies) {
            mCompanies = companies;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "DownloadCompanyLogoTask onPreExecute");
            if (!NetworkManager.isOnline(mContext)) {
                cancelDownloadCompanyLogos();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "DownloadCompanyLogoTask doInBackground");
            if (this.isCancelled()) {
                return null;
            }

            for (ExpressCompany company : mCompanies) {
                if (this.isCancelled()) {
                    return null;
                }
                try {
                    company.downloadLogo(mContext);
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "DownloadCompanyLogoTask onCancelled");
            onDownloadCompanyLogosExecuted();
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "DownloadCompanyLogoTask onPostExecute");
            mCompanyLogoDownloaded = true;
            onDownloadCompanyLogosExecuted();
        }
    }

    public boolean isDownloadingCompanyLogos() {
        return mDownloadCompanyLogoTask != null;
    }

    private void onDownloadCompanyLogosExecuted() {
        mDownloadCompanyLogoTask = null;
    }

    private void cancelDownloadCompanyLogos() {
        if (mDownloadCompanyLogoTask != null) {
            mDownloadCompanyLogoTask.cancel(true);
            mDownloadCompanyLogoTask = null;
        }
    }
}
