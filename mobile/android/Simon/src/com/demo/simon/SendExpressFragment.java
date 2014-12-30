
package com.demo.simon;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demo.simon.AlertDialogFragment.AlertDialogListener;
import com.demo.simon.DownloadTask.DownloadTaskListener;
import com.demo.simon.data.CourierData;
import com.demo.simon.datamodel.Courier;
import com.demo.simon.utility.LocationUtility;
import com.demo.simon.utility.Utility;

public class SendExpressFragment extends Fragment {
    private final static String TAG = "SendExpressFragment";

    // sender
    private TextView mCityText = null;
    private TextView mDistrictText = null;
    private EditText mStreetText = null;
    private EditText mStreetNumberText = null;

    private Button mSubmitBtn = null;
    private ProgressBar mProgressBar = null;

    public static List<Courier> mRespondedCourier = null;
    private int mCurrentCourierIndex = 0;

    public static String getFragmentTag() {
        return "SendExpress";
    }

    public void onLocationLoaded() {
        mCityText.setText(LocationUtility.city);
        mDistrictText.setText(LocationUtility.district);
        mStreetText.setText(LocationUtility.street);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send_express, null);
        mCityText = (TextView) rootView.findViewById(R.id.label_city);
        mDistrictText = (TextView) rootView.findViewById(R.id.label_district);
        mStreetText = (EditText) rootView.findViewById(R.id.edit_street);
        mStreetNumberText = (EditText) rootView.findViewById(R.id.edit_street_number);
        mSubmitBtn = (Button) rootView.findViewById(R.id.btn_submit);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSubmitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String city = mCityText.getText().toString();
                String district = mDistrictText.getText().toString();
                String street = mStreetText.getText().toString();
                String streetNumber = mStreetNumberText.getText().toString();

                mAutoRequestCourierTask = new DownloadTask(getActivity());
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("device_id", Utility.getDeviceSerial()));
                params.add(new BasicNameValuePair("user_name", ""));
                params.add(new BasicNameValuePair("location", LocationUtility.locationString));
                params.add(new BasicNameValuePair("max_distance", String.valueOf(Utility.getAllowedMaxDistance())));
                params.add(new BasicNameValuePair("from_address", LocationUtility.address));
                params.add(new BasicNameValuePair("to_address", ""));
                params.add(new BasicNameValuePair("street", street));
                params.add(new BasicNameValuePair("street_number", streetNumber));
                params.add(new BasicNameValuePair("company_id", "-1"));
                params.add(new BasicNameValuePair("comments", ""));
                mAutoRequestCourierTask.setPostParams(params);
                mAutoRequestCourierTask.setOnDownloadTaskListener(mAutoRequestCourierTaskListener);
                mAutoRequestCourierTask.execute(NetworkManager.getProperCouriersURL());
            }

        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mAutoRequestCourierTask != null && !mAutoRequestCourierTask.isCancelled()) {
            mAutoRequestCourierTask.cancel(true);
            mAutoRequestCourierTask.setOnDownloadTaskListener(null);
            mAutoRequestCourierTask = null;
        }
    }

    private DownloadTask mAutoRequestCourierTask = null;
    private DownloadTaskListener mAutoRequestCourierTaskListener = new DownloadTaskListener() {

        @Override
        public void onTaskPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.bringToFront();
        }

        @Override
        public void onTaskPostExecute(String result) {
            String requestId = "-1";
            JSONArray courierObjects = null;
            try {
                JSONObject resultObj = new JSONObject(result);
                requestId = resultObj.getString("request_id");
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

            CourierData.setCourierData(mRespondedCourier, requestId, false);
            mProgressBar.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    showNotificationDialog();
                }

            }, 3000);
        }

        @Override
        public void onTaskCanceled() {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (mAutoRequestCourierTask != null) {
                mAutoRequestCourierTask = null;
            }
        }
    };

    private void showNotificationDialog() {
        if (mRespondedCourier == null || mRespondedCourier.size() <= 0) {
            final AlertDialogFragment alert = new AlertDialogFragment();
            alert.setCancelable(false);
            Resources resources = getActivity().getResources();
            alert.setTitle(resources.getString(R.string.confirm));
            alert.setMessage(resources.getString(R.string.no_matched_courier_please_choose_manully));
            alert.setHasNegativeBtn(false);
            alert.setButton(resources.getString(R.string.confirm), null, new AlertDialogListener() {

                @Override
                public void onDialogPositiveBtnClicked(DialogFragment dialog) {
                    // redirect to manual ui
                    alert.dismiss();
                    Intent intent = new Intent(getActivity(), ManualOrderActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onDialogNegtiveBtnClicked(DialogFragment dialog) {
                }

            });
            alert.show(getActivity().getFragmentManager(), "alert");
            return;
        } else {
            Intent i = new Intent(MainActivity.getInstance(), CourierDetailInfoActivity.class);
            MainActivity.getInstance().startActivity(i);
        }
    }
}
