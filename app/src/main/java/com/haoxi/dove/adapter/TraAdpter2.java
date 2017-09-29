package com.haoxi.dove.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.callback.MyRvItemClickListener;
import com.haoxi.dove.newin.bean.InnerDoveData;

import java.util.ArrayList;
import java.util.List;

public class TraAdpter2 extends RecyclerView.Adapter<TraAdpter2.MyHolder> implements View.OnClickListener {
    private List<InnerDoveData> pigeonDatas = new ArrayList<>();
    private Context mContext;
    private MyRvItemClickListener onItemClickListener;
    private interface MyItemCheckListener {
        void itemChecked(View view, int count);
    }
    public void setOnItemClickListener(MyRvItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public TraAdpter2(Context context) {
        this.mContext = context;
    }
    public void addDatas(List<InnerDoveData> myPigeonBeen){
        this.pigeonDatas = myPigeonBeen;
        notifyDataSetChanged();
    }

    public void notifyDatas(List<InnerDoveData> myPigeonBeen) {
        this.pigeonDatas.clear();
        this.pigeonDatas = myPigeonBeen;
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_tra_pigeon, parent,false);
        MyHolder holder = new MyHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        final InnerDoveData bean = (InnerDoveData) pigeonDatas.get(position);
        holder.mPigeonName.setText("信鸽:" + bean.getDoveid());
        holder.root.setTag(position);
    }

    @Override
    public int getItemCount() {
        return pigeonDatas == null || pigeonDatas.size() == 0 ? 0 : pigeonDatas.size();
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (Integer) v.getTag(),false,pigeonDatas.get((int)v.getTag()));
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView mPigeonName;
        CheckBox mCheckBox;
        View root;
        public MyHolder(View itemView) {
            super(itemView);
            mPigeonName = (TextView) itemView.findViewById(R.id.item_rv_tra_tv);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.item_rv_tra_cb);
            this.root = itemView;
        }
    }
}
