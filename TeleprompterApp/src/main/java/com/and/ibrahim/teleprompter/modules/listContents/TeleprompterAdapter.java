package com.and.ibrahim.teleprompter.modules.listContents;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.callback.OnItemClickListener;
import com.and.ibrahim.teleprompter.callback.OnItemLongClickListener;
import com.and.ibrahim.teleprompter.callback.OnItemViewClickListener;
import com.and.ibrahim.teleprompter.mvp.model.DataObj;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeleprompterAdapter extends RecyclerView.Adapter<TeleprompterAdapter.Holder> {


    private final LayoutInflater mLayoutInflater;
    private final ArrayList<DataObj> dataObjArrayList = new ArrayList<>();
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnItemViewClickListener mOnItemViewClickListener;


    public TeleprompterAdapter(Context mC,LayoutInflater inflater,OnItemViewClickListener listner) {
        mLayoutInflater = inflater;
        mOnItemViewClickListener =listner;
        mC=mC;
    }



    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_text_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {

        holder.bind(dataObjArrayList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return dataObjArrayList.size();
    }

    public void addNewContent(List<DataObj> dataObjList) {
        dataObjArrayList.addAll(dataObjList);
        notifyDataSetChanged();
    }

    public void removeContent() {
        dataObjArrayList.clear();
        notifyDataSetChanged();
    }

    //create interface to goo another activity
    @SuppressWarnings("unused")
    public void setItemClickListener(OnItemClickListener listener) {
    }


    @SuppressWarnings("unused")
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }


    @SuppressWarnings("unused")
    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final Context mContext;
        @BindView(R.id.text_title)
        protected TextView mTextTitle;
        @BindView(R.id.edit_item)
        protected ImageView mImgEdit;
        @BindView(R.id.img_note)
        ImageView mIconImg;
        @BindView(R.id.lin_view)
        protected LinearLayout mLinearLayout;
        @BindView(R.id.check_item)
        protected CheckBox mCheckBox;
        @BindView(R.id.text_content)
        protected TextView mTextContent;
        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
            mImgEdit.setOnClickListener(this);
            mIconImg.setOnClickListener(this);
            mTextTitle.setOnClickListener(this);
            mLinearLayout.setOnClickListener(this);
            mCheckBox.setOnClickListener(this);

        }

        public void bind( DataObj dataObj, int position) {
            mTextTitle.setText(dataObj.getTextTitle());

              mTextContent.setText(dataObj.getTextContent());
            if (dataObj.getIsChecked()==0){
                mCheckBox.setChecked(false);
                mCheckBox.setVisibility(View.GONE);

            }else if (dataObj.getIsChecked()==1){
                mCheckBox.setChecked(true);
                mCheckBox.setVisibility(View.VISIBLE);
                Log.d("TAG", "myFlag in adapter " + String.valueOf(dataObj.getIsChecked()));

            }


        }


        @Override
        public void onClick(View view) {

            int id=view.getId();
            switch (id){
                case R.id.text_title:
                    mOnItemViewClickListener.onTextClickListener(getAdapterPosition(),view);
                    break;

                case R.id.edit_item:
                    mOnItemViewClickListener.onEditImgClickListener(getAdapterPosition(),view);

                    break;
                case R.id.img_note:
                    mOnItemViewClickListener.onImageClickListener(getAdapterPosition(),view);
                    break;
                case R.id.lin_view:
                    mOnItemViewClickListener.onViewGroupClickListener(getAdapterPosition(),view);
                    break;

                case R.id.check_item:

                    if (mCheckBox.isChecked()) {
                        mOnItemViewClickListener.onItemUncheckListener(getAdapterPosition(),view);
                        Log.d("adapter", "checked_item = unchecked");
                    } else {


                        mOnItemViewClickListener.onItemCheckListener(getAdapterPosition(),view);
                        Log.d("adapter", "checked_item = checked" );

                    }
                    default:
                        break;


            }

        }

        @Override
        public boolean onLongClick(View view) {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClicked(getAdapterPosition());
            }

            return false;
        }

    }

}

