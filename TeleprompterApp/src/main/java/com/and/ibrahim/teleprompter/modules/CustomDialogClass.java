package com.and.ibrahim.teleprompter.modules;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.modules.main.MainActivity;
import com.and.ibrahim.teleprompter.modules.main.TeleprompterAdapter;
import com.and.ibrahim.teleprompter.mvp.model.Teleprmpter;
import com.and.ibrahim.teleprompter.util.LinedEditText;
import com.and.ibrahim.teleprompter.util.getBakeUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {
    private static final String TAG="MainActivity";

    public Activity c;
    public Dialog d;
    @BindView(R.id.txt_add)
    protected TextView mAdd;
    @BindView(R.id.txt_cancel)
    protected TextView mCancel;
    @BindView(R.id.linededit_text_content)
    protected LinedEditText mEditContent;
    @BindView(R.id.edit_title)
    protected EditText mEditTitle;

    private Teleprmpter mTeleprmpter;
    private ArrayList<Teleprmpter> mArrayList;
    TeleprompterAdapter teleprompterAdapter;
    MainActivity activity;
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

       // mTeleprmpter=new Teleprmpter();
       // teleprompterAdapter = new TeleprompterAdapter( getLayoutInflater());

        mAdd.setOnClickListener(this);
        mCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_add:

                ContentValues values = new ContentValues();
                values.put(Contract.BakeEntry.COL_TITLE, mEditTitle.getText().toString());
                values.put(Contract.BakeEntry.COL_COTENTS, mEditContent.getText().toString());

                final Uri uriInsert = c.getContentResolver().insert(Contract.BakeEntry.PATH_TELEPROMPTER_URI, values);
                if (uriInsert != null) {
                    Log.d("contentResolver insert", "first added success");

                }

                teleprompterAdapter=new TeleprompterAdapter(getLayoutInflater());
                mArrayList=getBakeUtils.getTeleprmpters(c);
                teleprompterAdapter.addNewContent(mArrayList);
                teleprompterAdapter.notifyDataSetChanged();

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
