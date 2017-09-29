package com.haoxi.dove.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyLmAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<T> datas = new ArrayList<>(); // 数据源
    private Context context;    // 上下文Context
    private int normalType = 0;     // 第一种ViewType，正常的item
    private boolean hasMore = true;   // 变量，是否有更多数据
    private boolean fadeTips = false; // 变量，是否隐藏了底部的提示
    private Handler mHandler = new Handler(Looper.getMainLooper()); //获取主线程的Handler
    private MyItemClickListener myItemClickListener;
    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public MyLmAdapter(Context context, boolean hasMore,int holdType) {
        // 初始化变量
        this.context = context;
        this.hasMore = hasMore;
        this.holdType = holdType;
    }

    // 获取条目数量，之所以要加1是因为增加了一条footView
    @Override
    public int getItemCount() {
        return datas.size() + 1;
    }

    // 自定义方法，获取列表中数据源的最后一个位置，比getItemCount少1，因为不计上footView
    public int getRealLastPosition() {
        return datas.size();
    }


    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return 1;
        } else {
            return normalType;
        }
    }

    public List<T> getDatas(){
        return datas;
    }

    public T getItem(int position){
        return datas.get(position);
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
                mItemClickListener.onItemClick(v,getPosition());
            }
        }
    }

    // // 底部footView的ViewHolder，用以缓存findView操作
    class FootHolder extends RecyclerView.ViewHolder {
        private TextView tips;
         FootHolder(View itemView) {
            super(itemView);
            tips = (TextView) itemView.findViewById(R.id.tips);
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
            if (holdType == 1){
                return new CommentHolder(context,view,myItemClickListener);
            }else {
                return new MyRefrashHolder(context,view,myItemClickListener);
            }
        } else {
            return new FootHolder(LayoutInflater.from(context).inflate(R.layout.footview, null));
        }
    }

    int layoutRes = 0;

    public void setLayout(int res){
       layoutRes = res;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // 如果是正常的imte，直接设置TextView的值
        if (holder instanceof BasicRvHolder) {
            onHolderListener.toInitHolder(holder,position,datas.get(position));
        } else {
            // 之所以要设置可见，是因为我在没有更多数据时会隐藏了这个footView
            ((FootHolder) holder).tips.setVisibility(View.VISIBLE);
            // 只有获取数据为空时，hasMore为false，所以当我们拉到底部时基本都会首先显示“正在加载更多...”
            if (hasMore) {
                // 不隐藏footView提示
                fadeTips = false;
                if (datas.size() > 0) {
                    // 如果查询数据发现增加之后，就显示正在加载更多
                    ((FootHolder) holder).tips.setText("正在加载更多...");
                }
            } else {
                if (datas.size() > 0) {
                    // 如果查询数据发现并没有增加时，就显示没有更多数据了
                    ((FootHolder) holder).tips.setText("没有更多数据了");

                    // 然后通过延时加载模拟网络请求的时间，在500ms后执行
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 隐藏提示条
                            ((FootHolder) holder).tips.setVisibility(View.GONE);
                            // 将fadeTips设置true
                            fadeTips = true;
                            // hasMore设为true是为了让再次拉到底时，会先显示正在加载更多
                            hasMore = true;
                        }
                    }, 500);
                }
            }
        }
    }

    // 暴露接口，改变fadeTips的方法
    public boolean isFadeTips() {
        return fadeTips;
    }

    // 暴露接口，下拉刷新时，通过暴露方法将数据源置为空
    public void resetDatas() {
        datas = new ArrayList<>();
    }

    // 暴露接口，更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void updateList(List<T> newDatas, boolean hasMore) {
        // 在原有的数据之上增加新数据
        if (newDatas != null) {
//            datas.addAll(newDatas);
            this.datas = newDatas;
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    // 暴露接口，更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void addData(List<T> newDatas, boolean hasMore) {
        // 在原有的数据之上增加新数据
        if (newDatas != null) {
            datas.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    public OnHolder2Listener<T, RecyclerView.ViewHolder> onHolderListener;

    public void setOnHolderListener(OnHolder2Listener onHolderListener) {
        this.onHolderListener = onHolderListener;
    }
}
