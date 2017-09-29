package com.haoxi.dove.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BasicRvHolder;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.MyItemLongClickListener;
import com.haoxi.dove.newin.bean.InnerRing;

import java.util.ArrayList;
import java.util.List;

public class MatePigeonsAdapter extends RecyclerView.Adapter<MatePigeonsAdapter.MateHolder>{

    //条目点击监听
    private MyItemClickListener mItemClickListener;
    //长按条目监听
    private MyItemLongClickListener mItemLongClickListener;
    private List<InnerRing> mRingLists = new ArrayList<>();
    private Context mContext;
    public MatePigeonsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void addRingDatas(List<InnerRing> mRingLists){
        this.mRingLists = mRingLists;
        notifyDataSetChanged();
    }

    @Override
    public MateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_mate_pigeons, null);
        return new MateHolder(mContext, view, mItemClickListener, mItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(final MateHolder holder, final int position) {
        final InnerRing bean = mRingLists.get(position);
        holder.ringCode.setText(bean.getRing_code());
        if (bean.getDove_code() != null){
            holder.pegionCode.setText(bean.getDove_code());
        }
    }

    @Override
    public int getItemCount() {
        return mRingLists.size();
    }

    public void setOnItemClickListener(MyItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(MyItemLongClickListener itemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class MateHolder extends BasicRvHolder {
        TextView ringCode;
        TextView pegionCode;
        MateHolder(Context mContext, View itemView, MyItemClickListener myItemClickListener, MyItemLongClickListener myItemLongClickListener) {
            super(mContext, itemView, myItemClickListener, myItemLongClickListener);
            ringCode = (TextView) itemView.findViewById(R.id.item_mate_pigeons_tv);
            pegionCode = (TextView) itemView.findViewById(R.id.item_mate_rings_tv);
        }
    }
}
