package com.haoxi.dove.widget;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.haoxi.dove.R;

/**
 * Created by lifei on 2016/10/28.
 */

public class ShareDialog extends Dialog implements View.OnClickListener {

    private String title, mConfirm, mCancel;
    private Context mContext;
    private String msg;
    private ClickListenerInterface clickListenerInterface;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.confirm_dialog_confirm:
                clickListenerInterface.doConfirm();

                break;
            case R.id.confirm_dialog_cancel:
                clickListenerInterface.doCancel();
                break;
        }


    }

    public interface ClickListenerInterface {
        public void doConfirm();

        public void doCancel();
    }


    public ShareDialog(Context context, String title, String msg, String confirm, String cancel) {
        super(context, R.style.DialogTheme);
        this.mContext = context;
        this.title = title;
        this.mConfirm = confirm;
        this.mCancel = cancel;
        this.msg = msg;
        setCancelable(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        //    View view = mInflater.inflate(R.layout.confirm_dialog,null);
        View view = mInflater.inflate(R.layout.share_dialog, null);

        setContentView(view);

        TextView tvTitle = (TextView) view.findViewById(R.id.confirm_dialog_title);
        TextView tvMsg = (TextView) view.findViewById(R.id.confirm_dialog_msg);
        TextView tvConfirm = (TextView) view.findViewById(R.id.confirm_dialog_confirm);
        TextView tvCancel = (TextView) view.findViewById(R.id.confirm_dialog_cancel);

        tvTitle.setText(title);
        tvMsg.setText(msg);
        tvConfirm.setText(mConfirm);
        tvCancel.setText(mCancel);

        tvConfirm.setOnClickListener(this);
        tvCancel.setOnClickListener(this);


        Window dialogWindow = getWindow();
//        WindowManager.LayoutParams params = dialogWindow.getAttributes();
//        DisplayMetrics d = mContext.getResources().getDisplayMetrics();
//        params.width = (int) (d.widthPixels * 0.8);
//        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);

    }

    public void setClickListenerInterface(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }
}
