package com.and.ibrahim.teleprompter.modules.listContents;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.interfaces.OnCheckBoxChangeListner;
import com.and.ibrahim.teleprompter.interfaces.OnItemClickListener;
import com.and.ibrahim.teleprompter.interfaces.OnItemLongClickListener;
import com.and.ibrahim.teleprompter.interfaces.OnItemViewClickListner;
import com.and.ibrahim.teleprompter.mvp.model.DataObj;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeleprompterAdapter extends RecyclerView.Adapter<TeleprompterAdapter.Holder> {


    private final LayoutInflater mLayoutInflater;
    private final ArrayList<DataObj> dataObjArrayList = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnItemViewClickListner mOnItemViewClickListner;
    private OnCheckBoxChangeListner mOnCheckBoxChangeListner;

    private Context mC;
    public TeleprompterAdapter(Context mC,LayoutInflater inflater,OnItemViewClickListner listner,OnCheckBoxChangeListner listner2) {
        mLayoutInflater = inflater;
        mOnItemViewClickListner=listner;
        mOnCheckBoxChangeListner=listner2;
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
        holder.isCheked.setOnCheckedChangeListener(null);

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
        mOnItemClickListener = listener;
    }
    public void setItemViewClickListener(OnItemViewClickListner listener) {
        mOnItemViewClickListner = listener;
    }

    @SuppressWarnings("unused")
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }


    @SuppressWarnings("unused")
    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener,CompoundButton.OnCheckedChangeListener {
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
        protected CheckBox isCheked;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
            mImgEdit.setOnClickListener(this);
            mIconImg.setOnClickListener(this);
            mTextTitle.setOnClickListener(this);
            mLinearLayout.setOnClickListener(this);
            isCheked.setOnCheckedChangeListener(this);

        }

        public void bind( DataObj dataObj, int position) {
            mTextTitle.setText(dataObj.getTextTitle());

            //  mTextContent.setText(dataObj.getTextContent());
            if (dataObj.getIsChecked()==0){
                isCheked.setChecked(false);
                isCheked.setVisibility(View.GONE);

            }else if (dataObj.getIsChecked()==1){
                isCheked.setChecked(true);
                isCheked.setVisibility(View.VISIBLE);
                Log.d("TAG", "myFlag in adapter " + String.valueOf(dataObj.getIsChecked()));

            }


        }


        @Override
        public void onClick(View view) {

            int id=view.getId();
            switch (id){
                case R.id.text_title:
                    mOnItemViewClickListner.onTextClickListner(getAdapterPosition(),view);
                    break;

                case R.id.edit_item:
                    mOnItemViewClickListner.onEditImgClickListner(getAdapterPosition(),view);

                    break;
                case R.id.img_note:
                    mOnItemViewClickListner.onImageClickListner(getAdapterPosition(),view);
                    break;
                case R.id.lin_view:
                    mOnItemViewClickListner.onViewGroupClickListner(getAdapterPosition(),view);
                    break;

                case R.id.check_item:

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

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mOnCheckBoxChangeListner.onChekedListner(getAdapterPosition(),compoundButton,b);
            Log.d("TAG", "myFlag in listner " + String.valueOf(b));

        }
    }

}

