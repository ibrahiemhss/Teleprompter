package com.and.ibrahim.teleprompter.modules.display;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.callback.OnItemClickListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.Holder> {

    private final int[] colorBackgroundArray;
    private final int[] colorTextArray;
    private final LayoutInflater mLayoutInflater;
    private final ArrayList<Integer> dataObjArrayList = new ArrayList<>();

    public ColorsAdapter(Context context, LayoutInflater inflater) {
        mLayoutInflater = inflater;
        colorBackgroundArray = context.getResources().getIntArray(R.array.background_colors);
        colorTextArray = context.getResources().getIntArray(R.array.text_colors);
        for (int i : colorTextArray) {
            dataObjArrayList.add(i);
        }
    }

    @NonNull
    @Override
    public ColorsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_theme_colors, parent, false);

        return new ColorsAdapter.Holder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ColorsAdapter.Holder holder, int position) {

        holder.bind(position);

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

        Holder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        void bind(int position) {
            mBackgroundColor.setBackgroundColor(colorBackgroundArray[position]);
            mTextColor.setBackgroundColor(colorTextArray[position]);
        }

        @Override
        public void onClick(View view) {
        }
    }
}

