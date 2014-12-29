
package com.demo.simon;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SlideMenuFragment extends Fragment {
    enum UserOption {
        MY_ORDERS,
        MY_ADDRESS,
        EXPRESS_COMPANY,
        MY_NORIFICATION,
        SETTING
    };

    private ListView mUserOptionsView = null;
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UserOptionItem item = (UserOptionItem) view.getTag();
            switch (item.tag) {
                case MY_ORDERS:
                    break;
                case EXPRESS_COMPANY:
                    break;
                case MY_ADDRESS:
                case MY_NORIFICATION:
                case SETTING:
                default:
                    break;
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menu_list, null);
        mUserOptionsView = (ListView) rootView.findViewById(R.id.user_options);
        mUserOptionsView.setOnItemClickListener(mOnItemClickListener);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mUserOptionsView != null) {
            UserOptionAdapter adapter = new UserOptionAdapter(getActivity());
            adapter.add(new UserOptionItem(UserOption.MY_ORDERS, R.string.my_order, android.R.drawable.ic_menu_search));
            adapter.add(new UserOptionItem(UserOption.MY_ADDRESS, R.string.my_address, android.R.drawable.ic_menu_search));
            adapter.add(new UserOptionItem(UserOption.EXPRESS_COMPANY, R.string.express_company, android.R.drawable.ic_menu_search));
            adapter.add(new UserOptionItem(UserOption.MY_NORIFICATION, R.string.notification_center, android.R.drawable.ic_menu_search));
            adapter.add(new UserOptionItem(UserOption.SETTING, R.string.settings, android.R.drawable.ic_menu_search));
            mUserOptionsView.setAdapter(adapter);
        }
    }

    private class UserOptionItem {
        public UserOption tag;
        public int labelRes;
        public int iconRes;

        public UserOptionItem(UserOption tag, int labelRes, int iconRes) {
            this.tag = tag;
            this.labelRes = labelRes;
            this.iconRes = iconRes;
        }
    }

    private class UserOptionAdapter extends ArrayAdapter<UserOptionItem> {

        public UserOptionAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_item, null);
            }
            UserOptionItem option = getItem(position);
            ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
            icon.setImageResource(option.iconRes);
            TextView title = (TextView) convertView.findViewById(R.id.row_title);
            title.setText(option.labelRes);
            convertView.setTag(option);
            return convertView;
        }

    }
}
