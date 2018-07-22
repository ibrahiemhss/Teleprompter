package com.and.ibrahim.teleprompter.modules;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    @BindView(R.id.txt_add)
    protected TextView mAdd;
    @BindView(R.id.txt_cancel)
    protected TextView mCancel;

    public CustomDialogClass(Activity a, int pauseDialog) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_item);
        ButterKnife.bind(this);
        mAdd.setOnClickListener(this);
        mCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_add:
              //  c.finish();
                break;
            case R.id.txt_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
