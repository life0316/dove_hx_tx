package com.haoxi.dove.utils;

import com.haoxi.dove.bean.RouteBean;

import java.util.Comparator;

/**
 * Created by lifei on 2017/3/11.
 */

public class RouteComparator implements Comparator<RouteBean> {
    @Override
    public int compare(RouteBean o1, RouteBean o2) {

        //2017-04-12 11:03:56


        if (Integer.parseInt(o1.getRoute_name()) >  Integer.parseInt(o2.getRoute_name())) {

            return 1;
        } else {
            return -1;
        }
    }
}
