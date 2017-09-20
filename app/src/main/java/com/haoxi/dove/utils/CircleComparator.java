package com.haoxi.dove.utils;

import com.haoxi.dove.newin.bean.InnerCircleBean;

import java.util.Comparator;
public class CircleComparator implements Comparator<InnerCircleBean> {
    @Override
    public int compare(InnerCircleBean o1, InnerCircleBean o2) {

        if (ApiUtils.careTime(o1.getCreate_time(),o2.getCreate_time())){
            return 1;
        }else {
            return -1;
        }
    }
}
