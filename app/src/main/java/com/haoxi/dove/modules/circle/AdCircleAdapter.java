package com.haoxi.dove.modules.circle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BasicRvHolder;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.OnHolder2Listener;
import com.haoxi.dove.holder.CommentHolder;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.ytb.logic.external.NativeResource;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdCircleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<InnerCircleBean> datas = new ArrayList<>(); // 数据源
    private List<NativeResource> adDatas = new ArrayList<>(); // 原生广告数据源


    private Context context;    // 上下文Context

    private int normalType = 0;     // 第一种ViewType，正常的item
    private int adType = 1;     // 第二种ViewType，广告的item


    private MyItemClickListener myItemClickListener;

    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public AdCircleAdapter(Context context, int holdType) {
        // 初始化变量
        this.context = context;
        this.holdType = holdType;
    }

    // 获取条目数量，之所以要加1是因为增加了一条footView
    @Override
    public int getItemCount() {
        return datas.size() <= 3 ? datas.size() : datas.size() + adDatas.size();
    }

    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {


        if (position % 10 == 1 && adDatas.size() > 0) {

            return adType;
        } else {
            return normalType;
        }
    }

    public List<InnerCircleBean> getDatas(){
        return datas;
    }

    public List<NativeResource> getAdDatas() {
        return adDatas;
    }

    public Object getItem(int position){

        if (getItemViewType(position) == adType){
            return adDatas.get(position);
        }else {
            return datas.get(position);
        }
    }

    // 正常item的ViewHolder，用以缓存findView操作
    public class MyRefrashHolder extends BasicRvHolder implements View.OnClickListener{

        public MyItemClickListener mItemClickListener;
        public Context mContext;

        public Button mCommentBtn;
        public Button mTranspondBtn;
        public Button mPraiseBtn;
        public Button mAddFriendBtn;
        public TextView mContentTv;
        public CircleImageView mUserIcon;
        public TextView mUserName;
        public TextView mCreateTimeTv;
        public ImageView mDownIv;
        public ImageView mContentImage;
        public RecyclerView mRecyclerView;
        public FrameLayout transpondFl;

        public TextView mTranContentTv;
        public TextView mTranName;
        public ImageView mTranContentImage;
        public RecyclerView mTranRv;


        public MyRefrashHolder(Context mContext, View itemView, MyItemClickListener myItemClickListener) {
            super(mContext,itemView,myItemClickListener,null);

            this.mItemClickListener = myItemClickListener;

            itemView.setOnClickListener(this);

            mTranspondBtn = (Button) itemView.findViewById(R.id.friend_share);
            mPraiseBtn = (Button) itemView.findViewById(R.id.friend_dislike);
            mCommentBtn = (Button) itemView.findViewById(R.id.friend_comments);
            mAddFriendBtn = (Button) itemView.findViewById(R.id.item_dynamic_add_friend);
            mContentTv = (TextView)itemView.findViewById(R.id.friend_text);
            mCreateTimeTv = (TextView)itemView.findViewById(R.id.friend_date);
            mDownIv = (ImageView)itemView.findViewById(R.id.itme_dynamic_down);
            mUserIcon = (CircleImageView) itemView.findViewById(R.id.de_icon);
            mUserName = (TextView)itemView.findViewById(R.id.friend_name);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.item_isfriend_rv);

            mContentImage = (ImageView)itemView.findViewById(R.id.item_isfriend_imageview);

            transpondFl = (FrameLayout) itemView.findViewById(R.id.item_tran_fl);
            mTranContentTv = (TextView)itemView.findViewById(R.id.tran_circle_text);
            mTranName = (TextView)itemView.findViewById(R.id.tran_circle_name);
            mTranContentImage = (ImageView)itemView.findViewById(R.id.tran_circle_imageview);
            mTranRv = (RecyclerView) itemView.findViewById(R.id.tran_circle_rv);

        }

        @Override
        public void onClick(View v) {

            if (mItemClickListener != null){
                mItemClickListener.onItemClick(v,getAdapterPosition());
            }
        }
    }

    // // 底部footView的ViewHolder，用以缓存findView操作
    class AdHolder extends RecyclerView.ViewHolder {
        public TextView content;
        public ImageView adImage;
        AdHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.content);
            adImage = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public int holdType = 0;

    public void setHoldType(int holdType) {
        this.holdType = holdType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 根据返回的ViewType，绑定不同的布局文件，这里只有两种
        if (viewType == normalType) {

            View view = LayoutInflater.from(context).inflate(layoutRes == 0?R.layout.item_friend_circle:layoutRes, null);
            return new MyRefrashHolder(context,view,myItemClickListener);
        } else {
            return new AdHolder(LayoutInflater.from(context).inflate(R.layout.item_ad_native, null));
        }
    }

    int layoutRes = 0;

    public void setLayout(int res){
       layoutRes = res;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
//        // 如果是正常的imte，直接设置TextView的值
//        if (holder instanceof BasicRvHolder) {
//            int curPost = position >= 1? position - adDatas.size():position;
//            onHolderListener.toInitHolder(holder,curPost,datas.get(curPost));
//        } else {
//            onHolderListener.toInitHolder(holder,position,adDatas.get(0));
//        }

        // 如果是正常的imte，直接设置TextView的值
        if (holder instanceof BasicRvHolder) {
//            Log.e("mafegbadsgae",position+"----------p");
            int curPost = position >= 1? position - (adDatas.size() / 10 + adDatas.size()):position;
//            Log.e("mafegbadsgae",curPost+"----------curPost");
            onHolderListener.toInitHolder(holder,curPost,datas.get(curPost));
        } else {

            Log.e("mafegbadsgae",position+"----------p");
            Log.e("mafegbadsgae",(position / 10)+"----------p------");
            if (position / 10 < adDatas.size()){
                onHolderListener.toInitHolder(holder,position,adDatas.get(position / 10));
            }
        }
    }

    public void updateList(List<InnerCircleBean> newDatas) {
        if (newDatas != null) {
            this.datas = newDatas;
        }
        notifyDataSetChanged();
    }

    public void addData(List<InnerCircleBean> newDatas) {
        if (newDatas != null) {
            datas.addAll(newDatas);
        }
        notifyDataSetChanged();
    }

    public void updateAdList(List<NativeResource> newDatas) {
        if (newDatas != null) {
            this.adDatas = newDatas;
        }
        notifyDataSetChanged();
    }
    public void addAdData(List<NativeResource> newDatas) {
        if (newDatas != null) {
            adDatas.addAll(newDatas);
        }
        notifyDataSetChanged();
    }

    public OnHolder2Listener<Object, RecyclerView.ViewHolder> onHolderListener;

    public void setOnHolderListener(OnHolder2Listener onHolderListener) {
        this.onHolderListener = onHolderListener;
    }
}
