package com.haoxi.dove.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.base.MyBaseRvAdapter;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.utils.ApiUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
public class MyPigeonAdapter extends MyBaseRvAdapter<MyPigeonAdapter.MyPigeonHolder> implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "MyPigeonAdapter";
    private List<InnerDoveData> datas = new ArrayList<>();
    private Context mContext;
    private MyPigeonHolder holder;
    private Map<Integer, Boolean> map = new HashMap<>();
    //是否显示单选框,默认false
    private boolean isshowBox = false;
    //接口实例
    private RecyclerViewOnItemClickListener onItemClickListener;
    private Boolean longClickTag = false;
    private int count;
    private final List<String> mPigeonCodes;
    private List<Integer> checkPosList = new ArrayList<>();
    private List<Integer> unCheckPosList = new ArrayList<>();


    public MyPigeonHolder getHolder() {
        return holder;
    }
    private MyItemCheckListener mItemCheckListener;
    public MyItemCheckListener getItemCheckListener() {
        return mItemCheckListener;
    }
    public Boolean getLongClickTag() {
        return longClickTag;
    }
    public interface MyItemCheckListener {
        void itemChecked(View view, int count);
    }

    public void setItemCheckListener(MyItemCheckListener itemCheckListener) {
        mItemCheckListener = itemCheckListener;
    }

    public MyPigeonAdapter(Context mContext) {
        this.mContext = mContext;
        MyApplication application = MyApplication.getMyBaseApplication();
        mPigeonCodes = application.getmPigeonCodes();
        initMap();

    }

    public void addData(List<InnerDoveData> datas) {
        this.datas = datas;
        initMap();
        notifyDataSetChanged();
    }

    //初始化map集合,默认为不选中
    private void initMap() {
        map.clear();
        for (int i = 0; i < datas.size(); i++) {
            map.put(i, false);
        }
    }


    public void addDatas(List<InnerDoveData> datas) {
        this.datas.clear();
        this.datas.addAll(datas);
        mPigeonCodes.clear();
        checkPosList.clear();
        unCheckPosList.clear();
        specialUpdate();
    }

    public void notifyDatas(List<InnerDoveData> datas) {
        this.datas = datas;
        checkPosList.clear();
        unCheckPosList.clear();
        notifyDataSetChanged();
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

    public void setOpen(boolean isopen) {
        if (datas != null) {
            for (int i = 0; i < datas.size(); i++) {
                datas.get(i).setOpen(isopen);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return datas == null && datas.size() == 0 ? 0 : datas.size();
    }

    @Override
    public MyPigeonHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itme_mypigeon, null);
        MyPigeonHolder viewHolder = new MyPigeonHolder(view);
        this.holder = viewHolder;
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyPigeonHolder holder, final int position) {
        final InnerDoveData innerDoveData =datas.get(position);
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
                    if (map.get(i)) {
                        count++;
                    }
                }
                mItemCheckListener.itemChecked(buttonView, count);
            }
        });

        if (!map.containsKey(position)) {
            map.put(position, false);
        }

        holder.mCheckBox.setChecked(map.get(position));

        if (innerDoveData.getGender() != null) {
            if ("".equals(innerDoveData.getGender()))
                innerDoveData.setGender("1");

            switch (innerDoveData.getGender()) {
                case "1":
                case "公":
                    holder.mPegionSex.setImageResource(R.mipmap.icon_male);
                    holder.mPegionHead.setImageResource(R.mipmap.icon_img_2);
                    break;
                case "2":
                case "母":
                    holder.mPegionSex.setImageResource(R.mipmap.icon_female);
                    holder.mPegionHead.setImageResource(R.mipmap.icon_img_3);
                    break;
            }
        }

        if (innerDoveData.getRing_code() != null && !"".equals(innerDoveData.getRing_code())&& !"-1".equals(innerDoveData.getRing_code())) {

            holder.mIsMate.setVisibility(View.GONE);
            holder.mPegionColor.setVisibility(View.GONE);
            holder.mPegionOld.setText("匹配：" + innerDoveData.getRing_code());
        } else {
            holder.mPegionColor.setVisibility(View.VISIBLE);
            holder.mPegionColor.setText(innerDoveData.getColor());

            if (!"".equals(innerDoveData.getCreate_time()) && innerDoveData.getCreate_time() != null) {
                String pigeonBirthday = innerDoveData.getCreate_time();

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateNowStr = sdf.format(date);
                int getMonth = ApiUtils.getMonth(dateNowStr, pigeonBirthday.split(" ")[0]);
                int year = getMonth / 12;
                int month = getMonth % 12;
                holder.mPegionOld.setText(year == 0 ? (month == 0 ? "1个月" : month + "个月") : (month == 0 ? year + "年" : year + "年" + month + "个月"));
            } else {
                holder.mPegionOld.setText("1个月");
            }
        }
        holder.mCircleId.setText(innerDoveData.getFoot_ring());
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            onItemClickListener.onItemClickListener(v, (Integer) v.getTag(), longClickTag);
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
        return longClickTag ? false : onItemClickListener != null && onItemClickListener.onItemLongClickListener(v, (Integer) v.getTag(), longClickTag);
    }


    //设置是否显示CheckBox
    public void setShowBox() {
        //取反
        isshowBox = !isshowBox;
    }

    public void setIsShow(boolean show) {
        isshowBox = show;
    }

    public void setLongClickTag(boolean flag) {
        this.longClickTag = flag;
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
        mItemCheckListener.itemChecked(null, count);
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

    class MyPigeonHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_rv_mypigeon_ismate)
        public TextView mIsMate;
        @BindView(R.id.item_rv_mypigeon_pigeonold)
        public TextView mPegionOld;
        @BindView(R.id.item_rv_mypigeon_pigeoncolor)
        public TextView mPegionColor;
        @BindView(R.id.item_rv_mypigeon_circleid)
        public TextView mCircleId;
        @BindView(R.id.item_rv_mypigeon_pigeonsex)
        public ImageView mPegionSex;
        @BindView(R.id.item_rv_mypigeon_iv)
        public ImageView mPegionHead;
        @BindView(R.id.ll)
        public LinearLayout ll;
        @BindView(R.id.item_rv_mypigeon_cb)
        public CheckBox mCheckBox;
        public View root;

        public MyPigeonHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.root = itemView;

        }
    }

    //接口回调设置点击事件
    public interface RecyclerViewOnItemClickListener {
        //点击事件
        void onItemClickListener(View view, int position, boolean longClickTag);

        //长按事件
        boolean onItemLongClickListener(View view, int position, boolean longClickTag);
    }
}
