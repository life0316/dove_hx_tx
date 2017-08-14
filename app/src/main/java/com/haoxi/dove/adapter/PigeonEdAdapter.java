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
import com.haoxi.dove.callback.ExpandItemClickListener;
import com.haoxi.dove.newin.bean.InnerDoveData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lifei on 2017/1/14.
 */
public class PigeonEdAdapter extends BaseExpandableListAdapter {

    class ExpandableChildHolder{

        ImageView pigeonHead;
        ImageView pigeonSix;
        TextView pigeonOld;
        TextView pigeonColor;
        TextView circleNumber;

    }

    class ExpandableGroupHolder{
        TextView friendName;
        TextView pigeonNumber;
        ImageView jianTou;
    }

    private List<IsMateBean> groupData = new ArrayList<>();
    private List<List<InnerDoveData>> childData = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mGroupInflater;
    private LayoutInflater mChildInflater;

    public PigeonEdAdapter(Context mContext){
        this.mContext = mContext;

        mGroupInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mChildInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addDatas(List<IsMateBean> groupData, List<List<InnerDoveData>> childData){
        this.groupData = groupData;
        this.childData = childData;
        notifyDataSetChanged();
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
    public int getGroupCount() {
        return groupData.size()==0?0:groupData.size();
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
        return groupPosition;
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {

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
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {

        ExpandableChildHolder holder = null;
        if (convertView == null){
            convertView = mChildInflater.inflate(R.layout.item_rv_friend_pigeon_2,null);
            holder = new ExpandableChildHolder();
            holder.pigeonHead = (ImageView)convertView.findViewById(R.id.item_rv_friend_pigeon_2_iv);
            holder.pigeonSix = (ImageView)convertView.findViewById(R.id.item_rv_friend_pigeon_2_pigeonsix);
            holder.pigeonOld = (TextView)convertView.findViewById(R.id.item_rv_friend_pigeon_2_pigeonold);
            holder.pigeonColor = (TextView)convertView.findViewById(R.id.item_rv_friend_pigeon_2_pigeoncolor);
            holder.circleNumber = (TextView)convertView.findViewById(R.id.item_rv_friend_pigeon_2_circlenumber);
            convertView.setTag(holder);
        }else {
            holder = (ExpandableChildHolder)convertView.getTag();
        }

        final InnerDoveData pigeonBean = this.childData.get(groupPosition).get(childPosition);


        holder.circleNumber.setText(pigeonBean.getDoveid());

//
//        if (!"".equals(pigeonBean.getPIGEON_BIRTHDAY()) && pigeonBean.getPIGEON_BIRTHDAY() != null) {
//
//            String pigeonBirthday = pigeonBean.getPIGEON_BIRTHDAY();
//
//            Calendar calendar = Calendar.getInstance();
//
//            Date date = new Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String dateNowStr = sdf.format(date);
//
//            int getMonth = ApiUtils.getMonth(dateNowStr, pigeonBirthday.split(" ")[0]);
//
//            int year = getMonth / 12;
//            int month = getMonth % 12;
//
//            holder.pigeonOld.setText(year == 0 ? (month == 0? "1个月":month + "月") : (month == 0?year + "年":year + "年" + month + "月"));
//
//        }else {
//            holder.pigeonOld.setText("1个月");
//        }



        holder.pigeonColor.setText(pigeonBean.getColor());



        String sex = pigeonBean.getGender();
        if (!"".equals(sex)&&sex != null){
            switch (sex){
                case "1":
                    holder.pigeonHead.setImageResource(R.mipmap.icon_img_2);
                    holder.pigeonSix.setImageResource(R.mipmap.icon_male);
                    break;
                case "2":
                    holder.pigeonHead.setImageResource(R.mipmap.icon_img3);
                    holder.pigeonSix.setImageResource(R.mipmap.icon_female);
                    break;
            }
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupPosition == 0){

                    expandItemClickListener.itemClick(pigeonBean,childPosition,pigeonBean.getFoot_ring(),pigeonBean.getDoveid(),pigeonBean.getRingid(),pigeonBean.getRing_code());
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private ExpandItemClickListener expandItemClickListener;

    public void setExpandItemClickListener(ExpandItemClickListener expandItemClickListener) {
        this.expandItemClickListener = expandItemClickListener;
    }
}
