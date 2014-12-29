
package com.demo.simon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

public class ManualOrderActivity extends Activity {

    private Spinner mCompanySpinner;
    private Spinner mSiteSpinner;
    private Spinner mCourierSpinner;
    private Button mSubmitBtn;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set the content view
        setContentView(R.layout.activity_manual_order);

        View toMapButton = findViewById(R.id.to_map_view);
        toMapButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ManualOrderActivity.this, CourierMapActivity.class);
                startActivity(intent);
            }

        });

        mCompanySpinner = (Spinner) findViewById(R.id.spinner_companies);
        mSiteSpinner = (Spinner) findViewById(R.id.spinner_sites);
        mCourierSpinner = (Spinner) findViewById(R.id.spinner_couriers);
        mSubmitBtn = (Button) findViewById(R.id.btn_submit);
        mSubmitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ManualOrderActivity.this, CourierMapActivity.class);
                startActivity(intent);
            }

        });
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

}
