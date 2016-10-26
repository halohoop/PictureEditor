/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * SimpleCustomDialog.java
 *
 * Custom dialog when sth need to be confirmed.
 *
 * Author huanghaiqi, Created at 2016-10-11
 *
 * Ver 1.0, 2016-10-11, huanghaiqi, Create file.
 */

package com.halohoop.pictureeditor.pieces;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.halohoop.pictureeditor.R;

public class SimpleCustomDialog extends Dialog {

    private SimpleCustomDialog(Context context) {
        super(context);
    }

    private SimpleCustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private SimpleCustomDialog(Context context, boolean cancelable, OnCancelListener
            cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private Context mContext;
        private String mMessage;
        private String mPositiveButtonText;
        private String mNegativeButtonText;
        private OnClickListener mPositiveButtonClickListener;
        private OnClickListener mNegativeButtonClickListener;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        public Builder setPositiveButtonText(String positiveButtonText) {
            this.mPositiveButtonText = positiveButtonText;
            return this;
        }

        public Builder setNegativeButtonText(String negativeButtonText) {
            this.mNegativeButtonText = negativeButtonText;
            return this;
        }

        public Builder setPositiveButtonClickListener(OnClickListener positiveOnClickListener) {
            this.mPositiveButtonClickListener = positiveOnClickListener;
            return this;
        }

        public Builder setNegativeButtonClickListener(OnClickListener negativeOnClickListener) {
            this.mNegativeButtonClickListener = negativeOnClickListener;
            return this;
        }

        public SimpleCustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View contentView = inflater.inflate(R.layout.editor_alert_dialog, null);
            TextView tvAlertDialogMessage = (TextView) contentView.findViewById(R.id
                    .tv_alert_dialog_message);
            TextView tvPositive = (TextView) contentView.findViewById(R.id.tv_positive);
            TextView tvNegetive = (TextView) contentView.findViewById(R.id.tv_negetive);
            setTextViewText(tvAlertDialogMessage, mMessage);
            setTextViewText(tvPositive, mPositiveButtonText);
            setTextViewText(tvNegetive, mNegativeButtonText);
            final SimpleCustomDialog simpleCustomDialog = new SimpleCustomDialog(mContext);
            simpleCustomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            simpleCustomDialog.addContentView(contentView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            if (mPositiveButtonClickListener != null) {
                tvPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        simpleCustomDialog.dismiss();
                        mPositiveButtonClickListener.onClick(simpleCustomDialog,
                                DialogInterface.BUTTON_POSITIVE);
                    }
                });
            }
            if (mNegativeButtonClickListener != null) {
                tvNegetive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        simpleCustomDialog.dismiss();
                        mNegativeButtonClickListener.onClick(simpleCustomDialog,
                                DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            }
            return simpleCustomDialog;
        }

        private void setTextViewText(TextView tv, String text) {
            if (!TextUtils.isEmpty(text)) {
                tv.setText(text);
            }
        }
    }
}