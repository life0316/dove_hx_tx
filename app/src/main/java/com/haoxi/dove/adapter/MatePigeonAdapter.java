package com.haoxi.dove.adapter;

import android.annotation.SuppressLint;
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
import com.haoxi.dove.newin.bean.InnerDoveData;

import java.util.ArrayList;
import java.util.List;
public class MatePigeonAdapter extends RecyclerView.Adapter<MatePigeonAdapter.MateHolder>{

    //条目点击监听
    private MyItemClickListener mItemClickListener;

    //长按条目监听
    private MyItemLongClickListener mItemLongClickListener;

    private List<InnerDoveData> mPigeonLists = new ArrayList<>();

    private Context mContext;

    private int tag = 0;

    private AddItemClickListener addItemClickListener;


    public interface AddItemClickListener{
        void addItemClick(String msg, int position, InnerDoveData bean);
    }

    public void setAddItemClickListener(AddItemClickListener addItemClickListener) {
        this.addItemClickListener = addItemClickListener;
    }

    public MatePigeonAdapter(Context mContext) {

        this.mContext = mContext;
    }

    public void addPigeonDatas(List<InnerDoveData> mPigeon_lists){
        this.mPigeonLists = mPigeon_lists;
        notifyDataSetChanged();
    }
    public void addDatas(List<InnerDoveData> mPigeon_lists){
        this.mPigeonLists = mPigeon_lists;
        tag = 1;
        notifyDataSetChanged();
    }

    @Override
    public MateHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_mate_pigeon, null);

        return new MateHolder(mContext, view, mItemClickListener, mItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(final MateHolder holder, @SuppressLint("RecyclerView") final int position) {

        final InnerDoveData bean = mPigeonLists.get(position);
        holder.pegionName.setText("环号: "+mPigeonLists.get(position).getFoot_ring());
        if (tag == 1) {
            holder.pegionMate.setVisibility(View.VISIBLE);
                holder.pegionMate.setText(bean.isSetMate()?"取消":"添加");

            holder.pegionMate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addItemClickListener.addItemClick(holder.pegionMate.getText().toString().trim(),position,bean);
                }
            });
        }else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v,holder.getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mPigeonLists.size();
    }

    public void setOnItemClickListener(MyItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(MyItemLongClickListener itemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener;
    }

    class MateHolder extends BasicRvHolder {
        TextView pegionName;
        TextView pegionMate;
        MateHolder(Context mContext, View itemView, MyItemClickListener myItemClickListener, MyItemLongClickListener myItemLongClickListener) {
            super(mContext, itemView, myItemClickListener, myItemLongClickListener);
            pegionName = (TextView) itemView.findViewById(R.id.item_rv_mate_pigeon_tv);
            pegionMate = (TextView) itemView.findViewById(R.id.item_rv_mate_matetv);
        }
    }
}
