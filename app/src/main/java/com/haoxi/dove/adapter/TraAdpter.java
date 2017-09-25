package com.haoxi.dove.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.callback.MyRvItemClickListener;
import com.haoxi.dove.newin.bean.InnerDoveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraAdpter extends RecyclerView.Adapter<TraAdpter.MyHolder> implements View.OnClickListener {
    private List<InnerDoveData> pigeonDatas = new ArrayList<>();
    private Context mContext;

    private Map<Integer, Boolean> map = new HashMap<>();

    //是否显示单选框，默认 false
    private boolean isShowBox = true;

    private MyRvItemClickListener onItemClickListener;

    //
    private MyItemCheckListener itemCheckListener;

    private int count;

    private boolean longClickTag = false;

    private interface MyItemCheckListener {
        void itemChecked(View view, int count);
    }

    public void setOnItemClickListener(MyRvItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setItemCheckListener(MyItemCheckListener itemCheckListener) {
        this.itemCheckListener = itemCheckListener;
    }

    public TraAdpter(Context context) {

        this.mContext = context;

    }

    public void addDatas(List<InnerDoveData> myPigeonBeen){
        this.pigeonDatas = myPigeonBeen;
        initMap();
        notifyDataSetChanged();
    }

    public void notifyDatas(List<InnerDoveData> myPigeonBeen) {
        this.pigeonDatas.clear();
        this.pigeonDatas = myPigeonBeen;

        initMap();
        notifyDataSetChanged();
    }

    //初始化map集合，默认为不选中
    public void initMap() {
        map.clear();
        for (int i = 0; i < pigeonDatas.size(); i++) {
            map.put(i, false);
        }

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

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                map.put(position, isChecked);
                count = 0;

                for (int i = 0; i < map.size(); i++) {
                    if (map.get(i)) {
                        count++;
                    }
                }
            }
        });


        //设置checkbox的状态
        if (map.get(position) == null) {
            map.put(position, false);
        }

        holder.mCheckBox.setChecked(map.get(position));
        if (map.get(position)) {
            holder.mPigeonName.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        }else {
            holder.mPigeonName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        }

    }

    public void setShowBox() {
        isShowBox = !isShowBox;
    }

    public void setShowBox(boolean isShowBox){
        this.isShowBox = isShowBox;
        notifyDataSetChanged();
    }

    public boolean isShowBox() {
        return isShowBox;
    }

    //点击item选中checkbox
    public void setSelectItem(int position) {

        if (map.get(position)) {
            map.put(position, false);
        } else {
            map.put(position, true);
        }

        count = 0;

        for (int i = 0; i < map.size(); i++) {
            if (map.get(i)) {
                count++;
            }
        }

        notifyItemChanged(position);

    }



    //点击item选中checkbox
    public void setFlyItem(int position) {

        map.put(position, true);

        notifyItemChanged(position);

    }


    public Map<Integer, Boolean> getMap() {
        return map;
    }

    @Override
    public int getItemCount() {

        return pigeonDatas == null || pigeonDatas.size() == 0 ? 0 : pigeonDatas.size();
    }

    @Override
    public void onClick(View v) {

        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (Integer) v.getTag(),isShowBox,pigeonDatas.get((int)v.getTag()));
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
