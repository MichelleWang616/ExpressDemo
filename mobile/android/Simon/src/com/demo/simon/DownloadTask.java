
package com.demo.simon;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadTask extends AsyncTask<String, Void, String> {
    public interface DownloadTaskListener {
        public void onTaskPreExecute();

        public void onTaskPostExecute(String result);

        public void onTaskCanceled();
    }

    private final static String TAG = "DownloadTask";
    private Context mContext;
    private DownloadTaskListener mDownloadTaskListener;
    private List<NameValuePair> mPostParams = null;

    public DownloadTask(Context context) {
        mContext = context;
    }

    public void setOnDownloadTaskListener(DownloadTaskListener l) {
        mDownloadTaskListener = l;
    }

    public void setPostParams(List<NameValuePair> params) {
        mPostParams = params;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "DownloadManifestTask onPreExecute");
        if (!NetworkManager.isOnline(mContext)) {
            cancel(true);
            if (mDownloadTaskListener != null) {
                mDownloadTaskListener.onTaskCanceled();
            }
        }
    }

    @Override
    protected String doInBackground(String... urls) {
        Log.d(TAG, "DownloadManifestTask doInBackground");
        if (isCancelled()) {
            return null;
        }
        String result = null;
        if (mPostParams == null) {
            result = NetworkManager.executeGetRequest(mContext, urls[0]);
        } else {
            result = NetworkManager.executePostRequest(mContext, urls[0], mPostParams);
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (mDownloadTaskListener != null) {
            mDownloadTaskListener.onTaskPostExecute(result);
        }
    }
}
