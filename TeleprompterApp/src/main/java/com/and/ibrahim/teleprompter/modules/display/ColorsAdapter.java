package com.and.ibrahim.teleprompter.modules.display;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.callback.OnItemClickListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.Holder> {

    Context mContext;
    int[] colorBackgroundArray;
    int[] colorTextArray;
    private int lastPosition = -1;
    private OnItemClickListener mOnItemClickListener;


    private final LayoutInflater mLayoutInflater;
    private final ArrayList<Integer> dataObjArrayList = new ArrayList<>();

    public ColorsAdapter(Context context, LayoutInflater inflater) {
        mLayoutInflater = inflater;
        mContext = context;

        colorBackgroundArray = mContext.getResources().getIntArray(R.array.background_colors);
        colorTextArray = mContext.getResources().getIntArray(R.array.text_colors);
        for (int i : colorTextArray) {
            dataObjArrayList.add(i);
            // colorBackgroundArray.add(i);

        }

    }

    @NonNull
    @Override
    public ColorsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_theme_colors, parent, false);

        return new ColorsAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorsAdapter.Holder holder, int position) {

        holder.bind(position);
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);


        lastPosition = position;
        lastPosition = position;

        //viewHolder.mTxtColorName.setText(colorNameArray[position]);
    }

    @Override
    public int getItemCount() {
        return dataObjArrayList.size();
    }


    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final Context mContext;
        @BindView(R.id.img_background_color)
        RoundedImageView mBackgroundColor;
        @BindView(R.id.img_text_color)
        RoundedImageView mTextColor;

        // @BindView(R.id.text_content)
        //TextView mTextContent;

        public Holder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        public void bind(int position) {
            mBackgroundColor.setBackgroundColor(colorBackgroundArray[position]);
            mTextColor.setBackgroundColor(colorTextArray[position]);


        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
               // mOnItemClickListener.onClick(getAdapterPosition());
            }
        }
    }
}

