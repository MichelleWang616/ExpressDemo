
package com.demo.simon;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity {
    private MyOrderFragment mOrderFragment;
    private SendExpressFragment mSendExpressFragment;
    private String mCurrentFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set the content view
        setContentView(R.layout.activity_main);
        // configure the SlidingMenu
        final SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow_vertical);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.fragment_slidingmenu);

        View userButton = findViewById(R.id.user_center);
        userButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (!menu.isMenuShowing()) {
                    menu.showMenu();
                } else {
                    menu.showContent();
                }
            }

        });
        mOrderFragment = new MyOrderFragment();
        mSendExpressFragment = new SendExpressFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, mOrderFragment, MyOrderFragment.getFragmentTag());
        mCurrentFragmentTag = MyOrderFragment.getFragmentTag();
        fragmentTransaction.commit();

        Button myOrdersBtn = (Button) findViewById(R.id.button_express_order);
        myOrdersBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MyOrderFragment.getFragmentTag().equalsIgnoreCase(mCurrentFragmentTag)) {
                    return;
                }
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, mOrderFragment, MyOrderFragment.getFragmentTag());
                mCurrentFragmentTag = MyOrderFragment.getFragmentTag();
                fragmentTransaction.commit();
            }

        });
        Button sendExpressBtn = (Button) findViewById(R.id.button_send_express);
        sendExpressBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (SendExpressFragment.getFragmentTag().equalsIgnoreCase(mCurrentFragmentTag)) {
                    return;
                }
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, mSendExpressFragment, SendExpressFragment.getFragmentTag());
                mCurrentFragmentTag = SendExpressFragment.getFragmentTag();
                fragmentTransaction.commit();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
