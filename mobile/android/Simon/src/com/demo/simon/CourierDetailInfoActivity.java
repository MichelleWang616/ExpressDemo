
package com.demo.simon;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.simon.data.CourierData;
import com.demo.simon.datamodel.Courier;

public class CourierDetailInfoActivity extends Activity
{
    private TextView mNameView;
    private TextView mCompanyNameView;
    private Button mCallCourierBtn;
    private TextView mLineDistanceTextView;
    private Button mPreCourierBtn;
    private Button mConfirmCourierBtn;
    private Button mNextCourierBtn;

    private Courier mCurCourier = null;

    private DownloadTask mUpdateRequestInfoTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_detail_info);

        initView();
        updateCourierInfo();
    }

    private void updateCourierInfo() {
        mCurCourier = CourierData.getCurrentCourier();
        mNameView.setText(mCurCourier.getName());
        mCompanyNameView.setText(mCurCourier.getCompanyName());
        mCallCourierBtn.setText(mCurCourier.getPhone());
        mLineDistanceTextView.setText(getString(R.string.distance) + mCurCourier.getLineDistance()
                + getString(R.string.unit_m));

        if (CourierData.isSingleView())
        {
            mPreCourierBtn.setVisibility(View.GONE);
            mNextCourierBtn.setVisibility(View.GONE);
        }
    }

    private void initView()
    {
        mNameView = (TextView) findViewById(R.id.courier_name);

        mCompanyNameView = (TextView) findViewById(R.id.company_name);

        // TextView siteNameTextView = (TextView) findViewById(R.id.site_name);
        // siteNameTextView.setText(mCurCourier.getSiteName());

        mCallCourierBtn = (Button) findViewById(R.id.call_courier_btn);
        mCallCourierBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.CALL");
                intent.setData(Uri.parse("tel:" + mCurCourier.getPhone()));
                startActivity(intent);
            }
        });

        mLineDistanceTextView = (TextView) findViewById(R.id.line_distance);

        mPreCourierBtn = (Button) findViewById(R.id.pre_courier_btn);
        mPreCourierBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!CourierData.hasPreCourier()) {
                    Toast.makeText(CourierDetailInfoActivity.this, R.string.no_pre_courier, Toast.LENGTH_LONG).show();
                    return;
                }
                CourierData.moveToPreCourier();
                updateCourierInfo();
            }

        });

        mNextCourierBtn = (Button) findViewById(R.id.next_courier_btn);
        mNextCourierBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!CourierData.hasNextCourier()) {
                    Toast.makeText(CourierDetailInfoActivity.this, R.string.no_next_courier, Toast.LENGTH_LONG).show();
                    return;
                }
                CourierData.moveToNextCourier();
                updateCourierInfo();
            }

        });

        mConfirmCourierBtn = (Button) findViewById(R.id.confirm_btn);
        mConfirmCourierBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // sendRequest to close current order
                mUpdateRequestInfoTask = new DownloadTask(CourierDetailInfoActivity.this);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", CourierData.getRequestId()));
                params.add(new BasicNameValuePair("status", "close"));
                params.add(new BasicNameValuePair("courier_id", mCurCourier.getId()));
                mUpdateRequestInfoTask.setPostParams(params);
                mUpdateRequestInfoTask.execute(NetworkManager.getUpdateRequestInfoURL());
            }

        });
    }
}
