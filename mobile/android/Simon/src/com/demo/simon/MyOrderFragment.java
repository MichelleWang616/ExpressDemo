
package com.demo.simon;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demo.simon.DownloadTask.DownloadTaskListener;
import com.demo.simon.utility.Utility;

public class MyOrderFragment extends Fragment {
    private final static String TAG = "MyOrderFragment";
    private TextView mEmptyHistoryPrompt = null;
    private ListView mOrderList = null;
    private ProgressBar mProgressBar = null;
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO: show order's details
        }

    };

    public static String getFragmentTag() {
        return "Order";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders, null);
        mOrderList = (ListView) rootView.findViewById(R.id.order_list);
        mOrderList.setOnItemClickListener(mOnItemClickListener);
        mEmptyHistoryPrompt = (TextView) rootView.findViewById(R.id.no_orders);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mOrderList != null) {
            mDownloadHistoryOrdersTask = new DownloadTask(getActivity());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("device_id", Utility.getDeviceSerial()));
            mDownloadHistoryOrdersTask.setPostParams(params);
            mDownloadHistoryOrdersTask.setOnDownloadTaskListener(mDownloadTaskListener);
            mDownloadHistoryOrdersTask.execute(NetworkManager.getAllHistoryOrderURL());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mDownloadHistoryOrdersTask != null && !mDownloadHistoryOrdersTask.isCancelled()) {
            mDownloadHistoryOrdersTask.cancel(true);
            mDownloadHistoryOrdersTask.setOnDownloadTaskListener(null);
            mDownloadHistoryOrdersTask = null;
        }
    }

    private class OrderItem {
        public String orderId;
        public String orderTime;
        public String orderStatus;
        public String fromAddress;
        public String toAddress;
        public String courierName;

        public OrderItem(String id, String time, String status, String from, String to, String name) {
            orderId = id;
            orderTime = time;
            orderStatus = status;
            fromAddress = from;
            toAddress = to;
            courierName = name;
        }
    }

    private class OrderAdapter extends ArrayAdapter<OrderItem> {

        public OrderAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_order_item, null);
            }
            OrderItem option = getItem(position);
            TextView id = (TextView) convertView.findViewById(R.id.order_id);
            id.setText(getContext().getString(R.string.request_id) + option.orderId);
            TextView time = (TextView) convertView.findViewById(R.id.order_time);
            time.setText(option.orderTime);
            TextView status = (TextView) convertView.findViewById(R.id.order_status);
            status.setText(option.orderStatus);
            TextView fromAddress = (TextView) convertView.findViewById(R.id.from_address);
            fromAddress.setText(getContext().getString(R.string.from_address) + option.fromAddress);
            TextView toAddress = (TextView) convertView.findViewById(R.id.to_address);
            toAddress.setText(getContext().getString(R.string.to_address) + option.toAddress);
            TextView title = (TextView) convertView.findViewById(R.id.courier_name);
            title.setText(option.courierName);

            return convertView;
        }

    }

    private DownloadTask mDownloadHistoryOrdersTask = null;
    private DownloadTaskListener mDownloadTaskListener = new DownloadTaskListener() {

        @Override
        public void onTaskPreExecute() {
            mEmptyHistoryPrompt.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.bringToFront();
        }

        @Override
        public void onTaskPostExecute(String result) {
            mProgressBar.setVisibility(View.INVISIBLE);
            JSONArray objects = null;
            try {
                objects = new JSONArray(result);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            if (objects == null || objects.length() == 0) {
                mEmptyHistoryPrompt.setVisibility(View.VISIBLE);
                return;
            }
            OrderAdapter adapter = new OrderAdapter(getActivity());
            int count = objects.length();
            for (int i = 0; i < count; i++) {
                try {
                    JSONObject obj = objects.getJSONObject(i);
                    adapter.add(fromJSONData(obj));
                } catch (JSONException ex) {
                    Log.w(TAG, ex.getLocalizedMessage());
                }
            }
            mOrderList.setVisibility(View.VISIBLE);
            mOrderList.setAdapter(adapter);
        }

        @Override
        public void onTaskCanceled() {
            mEmptyHistoryPrompt.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            if (mDownloadHistoryOrdersTask != null) {
                mDownloadHistoryOrdersTask.setOnDownloadTaskListener(null);
                mDownloadHistoryOrdersTask = null;
            }
        }

    };

    private OrderItem fromJSONData(JSONObject obj) {
        String id = obj.optString("id");
        String time = obj.optString("time");
        String status = obj.optString("status");
        String from = obj.optString("from_address");
        String to = obj.optString("to_address");
        // TODO: replace with name
        String courier_id = obj.optString("courier_id");
        return new OrderItem(id, time, status, from, to, courier_id);
    }
}
