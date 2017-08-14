package com.haoxi.dove.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haoxi.dove.R;
import com.haoxi.dove.callback.OnEmptyClickListener;
import com.haoxi.dove.callback.OnLoadMoreListener;
import com.haoxi.dove.callback.RecyclerViewOnItemClickListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lifei on 2017/4/24.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    public static final int TYPE_HEADER = 1;
    public static final int TYPE_ITEM = 2;
    public static final int TYPE_MORE = 3;
    public static final int TYPE_EMPTY = 4;
    public static final int TYPE_MORE_FAIL = 5;

    protected List<T> mRvDatas;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected RecyclerViewOnItemClickListener mClickListener;

    protected boolean mShowLoadMoreView;
    protected boolean mShowEmptyView;

    private Boolean mEnableLoadMore;

    private RecyclerView.LayoutManager mLayoutManager;

    private int mLastPosition = -1;
    private int mMoreItemCount;
    private String mExtraMsg;


    private OnEmptyClickListener mEmptyClickListener;
    private OnLoadMoreListener mLoadMoreListener;

    public BaseRecyclerAdapter(List<T> mRvDatas, Context mContext) {
        this(mRvDatas,mContext,null);
    }

    public BaseRecyclerAdapter(List<T> mRvDatas, Context mContext, RecyclerView.LayoutManager layoutManager) {

        this.mLayoutManager = layoutManager;
        this.mRvDatas = mRvDatas == null ? new ArrayList<T>() : mRvDatas;
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_MORE) {
            return new BaseRecyclerViewHolder(mContext, mInflater.inflate(R.layout.item_load_more, parent, false));
        }
        else if (viewType == TYPE_MORE_FAIL) {
            final BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(mContext,
                    mInflater.inflate(R.layout.item_load_more_failed, parent, false));

            if (mLoadMoreListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEnableLoadMore = true;
                        mShowLoadMoreView = true;
                        notifyItemChanged(getItemCount() - 1);
                        holder.itemView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mLoadMoreListener.onLoadMore();
                            }
                        },300);
                    }
                });
            }

            return holder;
        }
        else if (viewType == TYPE_EMPTY){
            final BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(mContext,
                    mInflater.inflate(R.layout.item_empty_view,parent,false));

            if (mEmptyClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEmptyClickListener.onEmptyClick();
                    }
                });
            }
            return holder;
        }else {
            final BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(mContext,mInflater.inflate(getItemLayoutId(viewType),parent,false));

            if (mClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.getLayoutPosition() != RecyclerView.NO_POSITION) {
                            mClickListener.onItemClickListener(v,holder.getLayoutPosition(),false);
                        }
                    }
                });
            }
            return holder;
        }
    }


    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {

        Log.e("sddfaf",getItemCount()+"----3333");

        if (getItemViewType(position) == TYPE_MORE)
        {}else if (getItemViewType(position) == TYPE_MORE_FAIL) {
            //holder.setText(R.id.tv_faild,mExtraMsg+" 请点击重试");
            holder.setText(R.id.tv_faild,mExtraMsg);
        }else if (getItemViewType(position) == TYPE_EMPTY){
            holder.setText(R.id.tv_error,mExtraMsg);
        }else {
            bindData(holder,position,mRvDatas.get(position));
        }

        if (!mShowLoadMoreView && mLoadMoreListener != null && (mEnableLoadMore != null && mEnableLoadMore)
                && !mShowLoadMoreView && position == getItemCount() - 1 && getItemCount() >= mMoreItemCount) {
            mShowLoadMoreView = true;
            holder.itemView.postDelayed(new Runnable() {
                @Override
                public void run() {

                   mLoadMoreListener.onLoadMore();
                    notifyItemInserted(getItemCount());
                }
            },300);
        }

    }

    protected abstract void bindData(BaseRecyclerViewHolder holder, int position, T t);

    protected abstract int getItemLayoutId(int viewType);

    @Override
    public int getItemViewType(int position) {

        if (mShowEmptyView) {
            return TYPE_EMPTY;
        }

        if (mLoadMoreListener != null && (mEnableLoadMore != null && mEnableLoadMore)
                && mShowLoadMoreView && getItemCount() - 1 == position) {
            return TYPE_MORE;
        }

        if (mLoadMoreListener != null && !mShowLoadMoreView && (mEnableLoadMore != null && !mEnableLoadMore)
                && getItemCount() - 1 == position) {
            return TYPE_MORE_FAIL;
        }

        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {

        int i = mLoadMoreListener == null || mEnableLoadMore == null ? 0 :
                (mEnableLoadMore && mShowLoadMoreView) || (!mShowLoadMoreView && !mEnableLoadMore) ? 1 : 0;

        return mShowEmptyView ? 1 : mRvDatas != null ? mRvDatas.size() + i : 0;

    }

    public void add(int postition, T item) {
        mRvDatas.add(postition, item);
        notifyItemInserted(postition);
    }

    public void delete(int pos, T item) {
        mRvDatas.remove(pos);
        notifyItemRemoved(pos);
    }

    public void addMoreData(List<T> data) {
        int startPos = mRvDatas.size();
        mRvDatas.addAll(data);
        notifyItemRangeInserted(startPos, data.size());
    }

    public List<T> getData() {
        return mRvDatas;
    }

    public void setData(List<T> data) {
        mRvDatas = data;
        notifyDataSetChanged();
    }

    public void showEmptyView(boolean mShowEmptyView, String msg) {
        this.mShowEmptyView = mShowEmptyView;
        mExtraMsg = msg;
    }

    public void setmClickListener(RecyclerViewOnItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public void setmEmptyClickListener(OnEmptyClickListener mEmptyClickListener) {
        this.mEmptyClickListener = mEmptyClickListener;
    }

    public void setmLoadMoreListener(int moreItemCount, OnLoadMoreListener mLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener;
        mMoreItemCount = moreItemCount;
        mEnableLoadMore = true;
    }

    public void loadMoreSuccess() {
        mEnableLoadMore = true;
        mShowLoadMoreView = false;
        notifyItemRemoved(getItemCount());
    }

    public void loadMoreFailed(String errorMsg) {
        mEnableLoadMore = false;
        mShowLoadMoreView = false;
        mExtraMsg = errorMsg;
        notifyItemChanged(getItemCount() - 1);
    }


    /**
     * 设置是否开启底部加载
     *
     * @param enableLoadMore
     */
    public void enableLoadMore(Boolean enableLoadMore) {
        this.mEnableLoadMore = enableLoadMore;
    }
}
