
package com.demo.simon;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.demo.simon.AlertDialogFragment.AlertDialogListener;
import com.demo.simon.DownloadTask.DownloadTaskListener;
import com.demo.simon.datamodel.Courier;
import com.demo.simon.datamodel.ExpressCompany;
import com.demo.simon.utility.LocationUtility;
import com.demo.simon.utility.Utility;

public class ManualOrderActivity extends Activity {
    private final static String TAG = "ManualOrderActivity";

    public static List<Courier> mRespondedCourier = new ArrayList<Courier>();
    public static String mRequestId;

    private Spinner mCompanySpinner;
    private ProgressBar mProgressBar;
    private ImageView mSearchBtn;

    private String mUserInputAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set the content view
        setContentView(R.layout.activity_manual_order);

        TextView userLocation = (TextView) findViewById(R.id.locationInfo);
        String[] address = LocationUtility.userInputStreetInfo;
        mUserInputAddress = LocationUtility.city + LocationUtility.district + address[0] + address[1];
        userLocation.setText(mUserInputAddress);

        mSearchBtn = (ImageView) findViewById(R.id.searchBtn);
        mSearchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mSearchBtn.setEnabled(false);

                ExpressCompany company = (ExpressCompany) mCompanySpinner.getSelectedItem();
                mRequestCourierTask = new DownloadTask(ManualOrderActivity.this);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("device_id", Utility.getDeviceSerial()));
                params.add(new BasicNameValuePair("user_name", ""));
                String location = LocationUtility.latlng.latitude + ", " + LocationUtility.latlng.longitude;
                params.add(new BasicNameValuePair("location", location));
                params.add(new BasicNameValuePair("max_distance", String.valueOf(Utility.getAllowedMaxDistance())));
                params.add(new BasicNameValuePair("from_address", mUserInputAddress));
                params.add(new BasicNameValuePair("to_address", ""));
                params.add(new BasicNameValuePair("street", LocationUtility.street));
                params.add(new BasicNameValuePair("street_number", ""));
                params.add(new BasicNameValuePair("company_id", company.getId()));
                params.add(new BasicNameValuePair("comments", ""));
                mRequestCourierTask.setPostParams(params);
                mRequestCourierTask.setOnDownloadTaskListener(mRequestCourierTaskListener);
                mRequestCourierTask.execute(NetworkManager.getProperCouriersURL());
            }

        });

        mCompanySpinner = (Spinner) findViewById(R.id.spinner_companies);
        mCompanySpinner.setAdapter(getCompanyData());
        mCompanySpinner.setSelection(0);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.post(new Runnable() {

            @Override
            public void run() {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

        });
    }

    private DownloadTask mRequestCourierTask = null;
    private DownloadTaskListener mRequestCourierTaskListener = new DownloadTaskListener() {

        @Override
        public void onTaskPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.bringToFront();
        }

        @Override
        public void onTaskPostExecute(String result) {
            JSONArray courierObjects = null;
            try {
                JSONObject resultObj = new JSONObject(result);
                mRequestId = resultObj.getString("request_id");
                courierObjects = resultObj.getJSONArray("courier_list");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            mRespondedCourier = new ArrayList<Courier>();
            if (courierObjects != null && courierObjects.length() != 0) {

                int count = courierObjects.length();
                for (int i = 0; i < count; i++) {
                    try {
                        JSONObject obj = courierObjects.getJSONObject(i);
                        mRespondedCourier.add(Courier.fromJSONData(obj));
                    } catch (JSONException ex) {
                        Log.w(TAG, ex.getLocalizedMessage());
                    }
                }
            }
            mProgressBar.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    if (mRespondedCourier.size() == 0) {
                        // no proper couriers
                        final AlertDialogFragment alert = new AlertDialogFragment();
                        alert.setCancelable(false);
                        Resources resources = getResources();
                        alert.setTitle(resources.getString(R.string.confirm));
                        alert.setMessage(resources.getString(R.string.no_matched_courier_please_choose_manully));
                        alert.setHasNegativeBtn(false);
                        alert.setButton(resources.getString(R.string.confirm), null, new AlertDialogListener() {

                            @Override
                            public void onDialogPositiveBtnClicked(DialogFragment dialog) {
                                alert.dismiss();
                            }

                            @Override
                            public void onDialogNegtiveBtnClicked(DialogFragment dialog) {
                            }

                        });
                        alert.show(getFragmentManager(), "alert");
                    } else {
                        Intent intent = new Intent(ManualOrderActivity.this, CourierMapActivity.class);
                        startActivity(intent);
                    }

                    mSearchBtn.setEnabled(true);
                }

            }, 3000);

        }

        @Override
        public void onTaskCanceled() {
            mProgressBar.setVisibility(View.INVISIBLE);
            mSearchBtn.setEnabled(true);
            if (mRequestCourierTask != null) {
                mRequestCourierTask = null;
            }
        }

    };

    private CompanyAdapter getCompanyData() {
        List<ExpressCompany> list = new ArrayList<ExpressCompany>();

        // add select all option
        ExpressCompany allItem = new ExpressCompany("-1", "", getResources().getString(R.string.option_no_limitation));
        list.add(allItem);

        List<ExpressCompany> companies = ExpressManager.getInstance().getCompanies();
        if (companies != null) {
            for (ExpressCompany company : companies) {
                list.add(company);
            }
        }

        CompanyAdapter adapter = new CompanyAdapter(this, list);
        return adapter;
    }

    private class CompanyAdapter extends BaseAdapter {
        private Context mContext;
        private List<ExpressCompany> mCompanies;

        public CompanyAdapter(Context context, List<ExpressCompany> companies) {
            mContext = context;
            mCompanies = companies;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ManualOrderActivity.this)
                        .inflate(R.layout.spinner_item_company, null);
            }
            ExpressCompany company = mCompanies.get(position);
            ((TextView) convertView.findViewById(R.id.company_id)).setText(company.getId());
            ((ImageView) convertView.findViewById(R.id.company_logo)).setImageBitmap(company.getLogoBitmap());
            ((TextView) convertView.findViewById(R.id.company_name)).setText(company.getDisplayName());

            return convertView;
        }

        @Override
        public int getCount() {
            return mCompanies != null ? mCompanies.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mCompanies != null ? mCompanies.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            String id = mCompanies != null ? mCompanies.get(position).getId() : "-1";
            return Long.parseLong(id);
        }
    }
}
