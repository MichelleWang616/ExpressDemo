
package com.demo.simon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {

    private final static String TAG = "AlertDialogFragment";

    public final static int POSITIVE_BUTTON = 0;
    public final static int NEGATIVE_BUTTON = 1;

    public interface AlertDialogListener {
        public void onDialogPositiveBtnClicked(DialogFragment dialog);

        public void onDialogNegtiveBtnClicked(DialogFragment dialog);
    }

    private String mTitle;
    private String mMessage;
    private String mPositiveButtonString;
    private String mNegtiveButtonString;
    private boolean mHasNegativeButton = true;
    private AlertDialogListener mDialogListener = null;

    public AlertDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mMessage)
                .setPositiveButton(mPositiveButtonString, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialogListener != null) {
                            mDialogListener.onDialogPositiveBtnClicked(AlertDialogFragment.this);
                        }
                    }
                });
        if (mTitle != null && mTitle.length() != 0) {
            builder.setTitle(mTitle);
        }
        if (mHasNegativeButton) {
            builder.setNegativeButton(mNegtiveButtonString, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mDialogListener != null) {
                        mDialogListener.onDialogNegtiveBtnClicked(AlertDialogFragment.this);
                    }
                }
            });
        }
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

    public void setHasNegativeBtn(boolean hasNegativeBtn) {
        mHasNegativeButton = hasNegativeBtn;
    }

    public void setButton(String positiveLabel, String negativeLabel, AlertDialogListener listener) {
        mPositiveButtonString = positiveLabel;
        mNegtiveButtonString = negativeLabel;
        mDialogListener = listener;
    }
}
