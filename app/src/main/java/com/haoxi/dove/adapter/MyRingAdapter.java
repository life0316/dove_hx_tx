package com.haoxi.dove.adapter;


import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.base.MyBaseRvAdapter;
import com.haoxi.dove.newin.bean.InnerRing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyRingAdapter extends MyBaseRvAdapter<MyRingAdapter.MyCircleHolder>  implements View.OnClickListener, View.OnLongClickListener {

    private List<InnerRing> datas = new ArrayList<>();
    private MyCircleHolder holder;
    private Context mContext;
    private List<Integer> checkPosList = new ArrayList<>();
    private List<Integer> unCheckPosList = new ArrayList<>();
    private Map<Integer,Boolean> map = new HashMap<>();
    //是否显示单选框,默认false
    private boolean isshowBox = false;
    //接口实例
    private RecyclerViewOnItemClickListener onItemClickListener;
    private Boolean longClickTag = false;
    private MyItemCheckListener mItemCheckListener;
    private int count;
    private final List<String> mateList;
    public boolean isshowBox() {
        return isshowBox;
    }
    public MyItemCheckListener getItemCheckListener() {
        return mItemCheckListener;
    }
    public Boolean getLongClickTag() {
        return longClickTag;
    }
    public interface MyItemCheckListener {
        void itemChecked(View view,int count);
    }

    public void setItemCheckListener(MyItemCheckListener itemCheckListener) {
        mItemCheckListener = itemCheckListener;
    }

    public MyCircleHolder getHolder() {
        return holder;
    }

    public MyRingAdapter(Context mContext) {
        this.mContext = mContext;
        mateList = MyApplication.getMyBaseApplication().getMateList();
    }


    //初始化map集合,默认为不选中
    private void initMap() {
        for (int i = 0; i < datas.size(); i++) {
            map.put(i, false);
        }
        count = 0;
    }

    public void addData(List<InnerRing> datas) {
        this.datas = datas;
        count = datas.size();
        unCheckPosList.clear();
        map.clear();
        notifyDataSetChanged();
    }

    public void addDatas(List<InnerRing> datas) {
        this.datas = datas;
        unCheckPosList.clear();
        count = datas.size();
        specialUpdate();
    }
    public void notifyDatas(List<InnerRing> datas) {
        this.datas = datas;
        checkPosList.clear();
        unCheckPosList.clear();
        notifyDataSetChanged();
    }

    public void setOpen(boolean isopen) {
        if (datas != null) {
            for (int i = 0; i < datas.size(); i++) {
                ((InnerRing) datas.get(i)).setOpen(isopen);
            }
            notifyDataSetChanged();
        }
    }

    private void specialUpdate() {
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                for (int i = 0; i < getItemCount(); i++) {
                    notifyItemChanged(i);
                }
            }
        };
        handler.post(r);
    }


    @Override
    public MyCircleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itme_myring, null);
        MyCircleHolder viewHolder = new MyCircleHolder(view);
        this.holder = viewHolder;
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyCircleHolder holder, final int position) {
        final InnerRing ringBean = (InnerRing)datas.get(position);
        //长按显示/隐藏
        if (isshowBox) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.GONE);
        }

        //设置Tag
        holder.root.setTag(position);

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //用map集合保存
                map.put(position, isChecked);
                count = 0;
                for (int i = 0; i < map.size(); i++) {
                    if (map.get(i) != null && map.get(i)) {
                        count++;
                    }
                }
                if (mItemCheckListener != null) {

                    mItemCheckListener.itemChecked(buttonView,count);
                }
            }
        });
        if (!map.containsKey(position)){
            map.put(position,false);
        }
        holder.mCheckBox.setChecked(map.get(position));
        if (!"-1".equals(ringBean.getRing_code())) {
            holder.mCircleId.setText(ringBean.getRing_code());
        }

        if (!"".equals(ringBean.getDoveid()) && !"-1".equals(ringBean.getDoveid()) && ringBean.getDoveid() != null){
            holder.mMate.setText("匹配:"+ringBean.getDoveid());
            holder.mMate.setTextColor(Color.GREEN);
            if (!mateList.contains(ringBean.getDoveid())) {
                mateList.add(ringBean.getDoveid());
            }
        }else {
            holder.mMate.setText("未匹配");
            holder.mMate.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return datas == null&&datas.size() ==0?0:datas.size();
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            onItemClickListener.onItemClickListener(v, (Integer) v.getTag(),longClickTag);
        }
    }

    private int longIntTag = 0;

    public void setLongIntTag(int longIntTag) {
        this.longIntTag = longIntTag;
    }

    @Override
    public boolean onLongClick(View v) {
        if (longClickTag) {
            longIntTag = 1;
        }
        if (longIntTag == 1) {
            return false;
        }
        //不管显示隐藏，清空状态
        initMap();
        return longClickTag?false :onItemClickListener != null && onItemClickListener.onItemLongClickListener(v, (Integer) v.getTag(),longClickTag);
    }

    public void setLongClickTag(boolean flag){
        this.longClickTag = flag;
        if (flag) {
            longIntTag = 1;
        }else {
            longIntTag = 0;
        }
    }

    //设置是否显示CheckBox
    public void setShowBox() {
        //取反
        isshowBox = !isshowBox;
    }

    public void setIsShow(boolean show){
        isshowBox = show;
    }

    //点击item选中CheckBox
    public void setSelectItem(int position) {
        //对当前状态取反
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
        if (mItemCheckListener != null) {
            mItemCheckListener.itemChecked(null,count);
        }
        notifyItemChanged(position);
    }

    //返回集合给MainActivity
    public Map<Integer, Boolean> getMap() {
        return map;
    }
    //设置点击事件
    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //接口回调设置点击事件
    public interface RecyclerViewOnItemClickListener {
        //点击事件
        void onItemClickListener(View view, int position,boolean longClickTag);
        //长按事件
        boolean onItemLongClickListener(View view, int position,boolean longClickTag);
    }


    class MyCircleHolder extends RecyclerView.ViewHolder  {
        @BindView(R.id.item_rv_mycircle_id) public TextView mCircleId;
        @BindView(R.id.item_rv_mycircle_activate) public TextView mActivite;
        @BindView(R.id.item_rv_mycircle_mate) public TextView mMate;
        @BindView(R.id.ch)
        public CheckBox mCheckBox;
        public View root;
        public MyCircleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.root = itemView;
        }
    }
}
