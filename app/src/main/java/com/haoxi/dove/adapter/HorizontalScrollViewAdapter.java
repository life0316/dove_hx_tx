package com.haoxi.dove.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;

import java.util.List;

public class HorizontalScrollViewAdapter
{

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Integer> mDatas;
    private String[] mDatasStr;

    public HorizontalScrollViewAdapter(Context context, List<Integer> mDatas, String[] mDatasStr)
    {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = mDatas;
        this.mDatasStr = mDatasStr;
    }

    public int getCount()
    {
        return mDatas.size();
    }

    public Object getItem(int position)
    {
        return mDatas.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.gallery_item, parent, false);
            viewHolder.mImg = (ImageView) convertView
                    .findViewById(R.id.id_index_gallery_item_image);
            viewHolder.mText = (TextView) convertView
                    .findViewById(R.id.id_index_gallery_item_text);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mImg.setImageResource(mDatas.get(position));
        viewHolder.mText.setText(mDatasStr[position]);

        return convertView;
    }

    private class ViewHolder
    {
        ImageView mImg;
        TextView mText;
    }

}

