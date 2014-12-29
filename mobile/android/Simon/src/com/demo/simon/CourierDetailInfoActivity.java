package com.demo.simon;

import com.demo.simon.data.CourierData;
import com.demo.simon.datamodel.Courier;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CourierDetailInfoActivity extends Activity
{
	private Courier mCurCourier = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_courier_detail_info);

		init();
	}

	private void init()
	{
		mCurCourier = CourierData.curSelectedCourier;
		TextView nameTextView = (TextView) findViewById(R.id.courier_name);
		nameTextView.setText(mCurCourier.getName());

		TextView companyNameTextView = (TextView) findViewById(R.id.company_name);
		companyNameTextView.setText(mCurCourier.getCompanyName());

		// TextView siteNameTextView = (TextView) findViewById(R.id.site_name);
		// siteNameTextView.setText(mCurCourier.getSiteName());

		Button callCourierBtn = (Button) findViewById(R.id.call_courier_btn);
		callCourierBtn.setText(mCurCourier.getPhone());

		callCourierBtn.setOnClickListener(new View.OnClickListener()
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

		TextView lineDistanceTextView = (TextView) findViewById(R.id.line_distance);
		lineDistanceTextView.setText(getString(R.string.distance) + mCurCourier.getLineDistance() + getString(R.string.unit_m));

		if (CourierData.singleView)
		{
			findViewById(R.id.pre_courier_btn).setVisibility(View.GONE);
			findViewById(R.id.next_courier_btn).setVisibility(View.GONE);
			findViewById(R.id.confirm_btn).setVisibility(View.GONE);
		}
	}
}
