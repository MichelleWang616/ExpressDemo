
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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.demo.simon.AlertDialogFragment.AlertDialogListener;
import com.demo.simon.DownloadTask.DownloadTaskListener;
import com.demo.simon.NotificationDialog.NotificationDialogListener;
import com.demo.simon.datamodel.Courier;
import com.demo.simon.utility.Utility;

public class SendExpressFragment extends Fragment {
    private final static String TAG = "SendExpressFragment";

    // sender
    private Spinner mCitySpinner = null;
    private Spinner mDistrictSpinner = null;
    private EditText mStreetText = null;
    private EditText mSenderNameText = null;
    private EditText mSenderPhoneNumberText = null;
    // receiver
    private Spinner mCitySpinner2 = null;
    private Spinner mDistrictSpinner2 = null;
    private EditText mStreetText2 = null;
    private EditText mReceiverNameText = null;
    private EditText mReceiverPhoneNumberText = null;
    private EditText mCommentText = null;

    private Button mSubmitBtn = null;
    private ProgressBar mProgressBar = null;

    private List<Courier> mRespondedCourier = null;
    private String mRequestId;
    private int mCurrentCourierIndex = 0;

    private String mCityShanghai = "";
    private String mCityBeijing = "";

    public static String getFragmentTag() {
        return "SendExpress";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send_express, null);
        mCitySpinner = (Spinner) rootView.findViewById(R.id.spinner_city);
        mDistrictSpinner = (Spinner) rootView.findViewById(R.id.spinner_district);
        mStreetText = (EditText) rootView.findViewById(R.id.edit_street);
        mSenderNameText = (EditText) rootView.findViewById(R.id.edit_receiver_name);
        mSenderPhoneNumberText = (EditText) rootView.findViewById(R.id.edit_receiver_phone);

        mCitySpinner2 = (Spinner) rootView.findViewById(R.id.spinner_city2);
        mDistrictSpinner2 = (Spinner) rootView.findViewById(R.id.spinner_district2);
        mStreetText2 = (EditText) rootView.findViewById(R.id.edit_street2);
        mReceiverNameText = (EditText) rootView.findViewById(R.id.edit_receiver_name2);
        mReceiverPhoneNumberText = (EditText) rootView.findViewById(R.id.edit_receiver_phone2);

        mCommentText = (EditText) rootView.findViewById(R.id.comment);
        mSubmitBtn = (Button) rootView.findViewById(R.id.btn_submit);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mNotificationDialog.setNotificationDialogListener(mNotificationDialogListener);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCityShanghai = this.getString(R.string.shanghai);
        mCityBeijing = this.getString(R.string.beijing);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.cities,
                android.R.layout.simple_spinner_item);
        mCitySpinner.setAdapter(adapter);
        mCitySpinner.setSelection(0);
        mCitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                String city = parent.getItemAtPosition(position).toString();
                if (mCityShanghai.equals(city)) {
                    ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.districts_shanghai,
                            android.R.layout.simple_spinner_item);
                    mDistrictSpinner.setAdapter(districtAdapter);
                }
                if (mCityBeijing.equals(city)) {
                    ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.districts_beijing,
                            android.R.layout.simple_spinner_item);
                    mDistrictSpinner.setAdapter(districtAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        mCitySpinner2.setAdapter(adapter);
        mCitySpinner2.setSelection(1);
        mCitySpinner2.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                String city = parent.getItemAtPosition(position).toString();
                if (mCityShanghai.equals(city)) {
                    ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.districts_shanghai,
                            android.R.layout.simple_spinner_item);
                    mDistrictSpinner2.setAdapter(districtAdapter);
                }
                if (mCityBeijing.equals(city)) {
                    ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.districts_beijing,
                            android.R.layout.simple_spinner_item);
                    mDistrictSpinner2.setAdapter(districtAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        mSubmitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String city = mCitySpinner.getSelectedItem().toString();
                String district = mDistrictSpinner.getSelectedItem().toString();
                String street = mStreetText.getText().toString();
                String name = mSenderNameText.getText().toString();
                String phone = mSenderPhoneNumberText.getText().toString();
                String senderAddress = city + district + street;

                String city2 = mCitySpinner2.getSelectedItem().toString();
                String district2 = mDistrictSpinner2.getSelectedItem().toString();
                String street2 = mStreetText2.getText().toString();
                String receiverAddress = city2 + district2 + street2;

                String comments = mCommentText.getText().toString();

                mAutoRequestCourierTask = new DownloadTask(getActivity());
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("device_id", Utility.getDeviceSerial()));
                params.add(new BasicNameValuePair("user_name", ""));
                params.add(new BasicNameValuePair("location", Utility.getGPSLocation()));
                params.add(new BasicNameValuePair("max_distance", String.valueOf(Utility.getAllowedMaxDistance())));
                params.add(new BasicNameValuePair("from_address", senderAddress));
                params.add(new BasicNameValuePair("to_address", receiverAddress));
                String[] streetInfo = Utility.getCurrentLocationStreetInfo();
                params.add(new BasicNameValuePair("street", streetInfo[0]));
                params.add(new BasicNameValuePair("street_number", streetInfo[1]));
                params.add(new BasicNameValuePair("company_id", "-1"));
                params.add(new BasicNameValuePair("comments", comments));
                mAutoRequestCourierTask.setPostParams(params);
                mAutoRequestCourierTask.setOnDownloadTaskListener(mAutoRequestCourierTaskListener);
                mAutoRequestCourierTask.execute(NetworkManager.getProperCouriersURL());
                mCurrentCourierIndex = 0;
                mRequestId = "-1";
            }

        });
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
            JSONArray courierObjects = null;
            try {
                JSONObject resultObj = new JSONObject(result);
                mRequestId = resultObj.getString("request_id");
                courierObjects = resultObj.getJSONArray("courier_list");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            if (courierObjects != null && courierObjects.length() != 0) {
                mRespondedCourier = new ArrayList<Courier>();
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

    private NotificationDialog mNotificationDialog = new NotificationDialog();
    private NotificationDialogListener mNotificationDialogListener = new NotificationDialogListener() {

        @Override
        public void onDialogPositiveBtnClicked(DialogFragment dialog) {
            mNotificationDialog.dismiss();
            final Courier courier = mRespondedCourier.get(mCurrentCourierIndex);
            // confirm
            AlertDialogFragment alert = new AlertDialogFragment();
            alert.setCancelable(false);
            alert.setTitle(alert.getString(R.string.confirm));
            alert.setMessage(alert.getString(R.string.request_submitted));
            alert.setHasNegativeBtn(false);
            alert.setButton("确认", null, new AlertDialogListener() {

                @Override
                public void onDialogPositiveBtnClicked(DialogFragment dialog) {
                    // sendRequest to close current order
                    mUpdateRequestInfoTask = new DownloadTask(getActivity());
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id", mRequestId));
                    params.add(new BasicNameValuePair("status", "close"));
                    params.add(new BasicNameValuePair("courier_id", courier.getId()));
                    mUpdateRequestInfoTask.setPostParams(params);
                    mUpdateRequestInfoTask.execute(NetworkManager.getUpdateRequestInfoURL());
                }

                @Override
                public void onDialogNegtiveBtnClicked(DialogFragment dialog) {
                }

            });
            alert.show(getActivity().getFragmentManager(), "alert");
        }

        @Override
        public void onDialogNeutralBtnClicked(DialogFragment dialog) {
            mNotificationDialog.dismiss();
            if (mCurrentCourierIndex <= 0) {
                Toast.makeText(getActivity(), R.string.no_pre_courier, Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getActivity(), R.string.getting_info, Toast.LENGTH_LONG).show();
            mCurrentCourierIndex--;
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    showNotificationDialog();
                }

            }, 5000);
        }

        @Override
        public void onDialogNegtiveBtnClicked(DialogFragment dialog) {
            mNotificationDialog.dismiss();
            int count = mRespondedCourier.size();
            if (mCurrentCourierIndex >= count - 1) {
                Toast.makeText(getActivity(), R.string.no_next_courier, Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getActivity(), R.string.getting_info, Toast.LENGTH_LONG).show();
            mCurrentCourierIndex++;
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    showNotificationDialog();
                }

            }, 5000);
        }

    };

    private void showNotificationDialog() {
        // if (mRespondedCourier == null || mRespondedCourier.size() <= 0) {
        final AlertDialogFragment alert = new AlertDialogFragment();
        alert.setCancelable(false);
        alert.setTitle(alert.getString(R.string.confirm));
        alert.setMessage(alert.getString(R.string.no_matched_courier_please_choose_manully));
        alert.setHasNegativeBtn(false);
        alert.setButton(alert.getString(R.string.confirm), null, new AlertDialogListener() {

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
        // }
        // int count = mRespondedCourier.size();
        // if (mCurrentCourierIndex < 0 || mCurrentCourierIndex >= count) {
        // return;
        // }
        //
        // Courier currentCourier = mRespondedCourier.get(mCurrentCourierIndex);
        // mNotificationDialog.setMessage(currentCourier.getCompanyName() + this.getString(R.string.courier)
        // + currentCourier.getName() + this.getString(R.string.can_come_to_fetch));
        //
        // mNotificationDialog.show(getActivity().getFragmentManager(), "notification");
        // updateNotificationDialogBtnStatus();
    }

    // private void updateNotificationDialogBtnStatus() {
    // AlertDialog dialog = (AlertDialog) mNotificationDialog.getDialog();
    // if (dialog == null) {
    // return;
    // }
    // Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
    // Button negativeBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
    // if (mCurrentCourierIndex <= 0) {
    // if (positiveBtn != null) {
    // positiveBtn.setEnabled(false);
    // }
    // } else {
    // if (positiveBtn != null) {
    // positiveBtn.setEnabled(true);
    // }
    // }
    // int count = mRespondedCourier.size();
    // if (mCurrentCourierIndex >= count - 1) {
    // if (negativeBtn != null) {
    // negativeBtn.setEnabled(false);
    // }
    // } else {
    // if (negativeBtn != null) {
    // negativeBtn.setEnabled(true);
    // }
    // }
    // }

    private DownloadTask mUpdateRequestInfoTask = null;
}
