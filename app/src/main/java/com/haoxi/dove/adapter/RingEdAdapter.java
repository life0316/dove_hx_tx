package com.haoxi.dove.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.bean.IsMateBean;
import com.haoxi.dove.newin.bean.InnerRing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lifei on 2017/1/14.
 */
public class RingEdAdapter extends BaseExpandableListAdapter {

    class ExpandableChildHolder{

        ImageView pigeonHead;
        TextView circleNumber;

    }

    class ExpandableGroupHolder{
        TextView friendName;
        TextView pigeonNumber;
        ImageView jianTou;
    }

    private List<IsMateBean> groupData = new ArrayList<>();
    private List<List<InnerRing>> childData = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mGroupInflater;
    private LayoutInflater mChildInflater;


    public RingEdAdapter(Context mContext){
        this.mContext = mContext;


        mGroupInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mChildInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addDatas( List<IsMateBean> groupData, List<List<InnerRing>> childData){
        this.groupData = groupData;
        this.childData = childData;
        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return groupData.size()==0?0:groupData.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
        if (getChildrenCount(groupPosition) != 0) {
            groupData.get(groupPosition).setIconUp(false);
            notifyDataSetChanged();
        }

    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        if (getChildrenCount(groupPosition) != 0) {
            groupData.get(groupPosition).setIconUp(true);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childData.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public ExpandableGroupHolder mHolder;

    public ExpandableGroupHolder getmHolder() {
        return mHolder;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ExpandableGroupHolder holder = null;
        if (convertView == null){
            convertView = mGroupInflater.inflate(R.layout.item_rv_friend_pigeon_1,null);
            holder = new ExpandableGroupHolder();
            holder.friendName = (TextView)convertView.findViewById(R.id.item_rv_friend_pigeon_name);
            holder.pigeonNumber = (TextView)convertView.findViewById(R.id.item_rv_friend_pigeon_number);
            holder.jianTou = (ImageView)convertView.findViewById(R.id.item_rv_friend_pigeon_iv);
            convertView.setTag(holder);
        }else{
            holder = (ExpandableGroupHolder)convertView.getTag();
        }

        mHolder = holder;

        if (groupData.get(groupPosition).isIconUp()) {
            holder.jianTou.setImageResource(R.mipmap.icon_up);
        }else {
            holder.jianTou.setImageResource(R.mipmap.icon_down);
        }

        holder.friendName.setText(groupData.get(groupPosition).getTitle());
        holder.pigeonNumber.setText(String.valueOf(getChildrenCount(groupPosition)));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ExpandableChildHolder holder = null;
        if (convertView == null){
            convertView = mChildInflater.inflate(R.layout.item_rv_mycircle2,null);
            holder = new ExpandableChildHolder();
            holder.pigeonHead = (ImageView)convertView.findViewById(R.id.item_rv_mycircle2_head);
            holder.circleNumber = (TextView)convertView.findViewById(R.id.item_rv_mycircle2_ringcode);
            convertView.setTag(holder);
        }else {
            holder = (ExpandableChildHolder)convertView.getTag();
        }


        InnerRing circleBean = this.childData.get(groupPosition).get(childPosition);

        holder.circleNumber.setText("鸽环编号："+circleBean.getRing_code());

        holder.pigeonHead.setImageResource(R.mipmap.icon_img1);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
