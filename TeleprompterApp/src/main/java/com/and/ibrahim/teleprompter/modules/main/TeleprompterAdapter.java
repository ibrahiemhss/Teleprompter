package com.and.ibrahim.teleprompter.modules.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.mvp.model.Teleprmpter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeleprompterAdapter extends RecyclerView.Adapter<TeleprompterAdapter.Holder> {



    private final LayoutInflater mLayoutInflater;
    private final ArrayList<Teleprmpter> teleprmpterArrayList = new ArrayList<>();
    private OnBakeClickListener mBakeClickListener;

    public TeleprompterAdapter( LayoutInflater inflater) {
        mLayoutInflater = inflater;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_text_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.bind(teleprmpterArrayList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return teleprmpterArrayList.size();
    }

    public void addNewContent(List<Teleprmpter> teleprmpterList) {
        teleprmpterArrayList.addAll(teleprmpterList);
        notifyDataSetChanged();
    }

    //create interface to goo another activity
    public void setBakeClickListener(OnBakeClickListener listener) {
        mBakeClickListener = listener;
    }

    public interface OnBakeClickListener {

        void onClick(int position);
    }

    @SuppressWarnings("unused")
    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final Context mContext;
        @BindView(R.id.text_title)
        TextView mTextTitle;
        @BindView(R.id.text_content)
        TextView mTextContent;

        public Holder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Teleprmpter teleprmpter, int position) {
            mTextTitle.setText(teleprmpter.getTextTitle());
            mTextContent.setText(teleprmpter.getTextContent());



        }


        @Override
        public void onClick(View view) {
            if (mBakeClickListener != null) {
                mBakeClickListener.onClick(getAdapterPosition());
            }
        }
    }
}

