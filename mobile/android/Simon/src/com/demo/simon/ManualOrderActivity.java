
package com.demo.simon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.demo.simon.datamodel.ExpressCompany;

public class ManualOrderActivity extends Activity {

    private Spinner mCompanySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set the content view
        setContentView(R.layout.activity_manual_order);

        TextView userLocation = (TextView) findViewById(R.id.locationInfo);
        userLocation.setText("");

        View searchButton = findViewById(R.id.searchBtn);
        searchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ManualOrderActivity.this, CourierMapActivity.class);
                startActivity(intent);
            }

        });

        mCompanySpinner = (Spinner) findViewById(R.id.spinner_companies);
        mCompanySpinner.setAdapter(getCompanyData());
    }

    private SimpleAdapter getCompanyData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<ExpressCompany> companies = ExpressManager.getInstance().getCompanies();
        if (companies != null) {
            for (ExpressCompany company : companies) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("logo", company.getLogoBitmap());
                map.put("name", company.getDisplayName());
                list.add(map);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.spinner_item_company,
                new String[] {
                        "logo", "name"
                },
                new int[] {
                        R.id.company_logo, R.id.company_name
                });
        return adapter;
    }
}
