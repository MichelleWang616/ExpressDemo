
package com.demo.simon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class NotificationDialog extends DialogFragment {
    private final static String TAG = "NotificationDialog";

    public final static int POSITIVE_BUTTON = 0;
    public final static int NEGATIVE_BUTTON = 1;
    public final static int NEUTRAL_BUTTON = 2;

    public interface NotificationDialogListener {
        public void onDialogPositiveBtnClicked(DialogFragment dialog);

        public void onDialogNeutralBtnClicked(DialogFragment dialog);

        public void onDialogNegtiveBtnClicked(DialogFragment dialog);
    }

    private String mTitle;
    private String mMessage;
    private NotificationDialogListener mDialogListener = null;

    public NotificationDialog() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mTitle = "快递员响应";
        if (mTitle != null && mTitle.length() != 0) {
            builder.setTitle(mTitle);
        }
        builder.setMessage(mMessage)
                .setPositiveButton(R.string.reply_from_courier, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialogListener != null) {
                            mDialogListener.onDialogPositiveBtnClicked(NotificationDialog.this);
                        }
                    }
                })
                .setNeutralButton(R.string.previous, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialogListener != null) {
                            mDialogListener.onDialogNeutralBtnClicked(NotificationDialog.this);
                        }
                    }
                })
                .setNegativeButton(R.string.next, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialogListener != null) {
                            mDialogListener.onDialogNegtiveBtnClicked(NotificationDialog.this);
                        }
                    }
                });
        return builder.create();
    }

    // @Override
    // public void onAttach(Activity activity) {
    // super.onAttach(activity);
    // try {
    // mDialogListener = (AlertDialogListener) activity;
    // } catch (ClassCastException ex) {
    // LogUtil.w(TAG, ex.getMessage());
    // mDialogListener = null;
    // }
    // }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setNotificationDialogListener(NotificationDialogListener l) {
        mDialogListener = l;
    }
}
