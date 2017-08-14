package com.haoxi.dove.utils;

import com.haoxi.dove.newin.bean.PointBean;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by Administrator on 2017\6\28 0028.
 */

public class PointConverter implements PropertyConverter<PointBean, String> {

    @Override
    public PointBean convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        else {
                String[] _str = databaseValue.split("-");

                String time = _str[0];
                String speed = _str[1];
                String dir = _str[2];
                String height = _str[3];
                String lat = _str[4];
                String lng = _str[5];
                PointBean pointBean = new PointBean();
                pointBean.setTime(time);
                pointBean.setSpeed(Float.valueOf(speed));
                pointBean.setDir(Float.valueOf(dir));
                pointBean.setHeight(Float.valueOf(height));
                pointBean.setLat(Double.valueOf(lat));
                pointBean.setLng(Double.valueOf(lng));

            return pointBean;
        }
    }

    @Override
    public String convertToDatabaseValue(PointBean entityProperty) {
        if(entityProperty==null){
            return null;
        }
        else{
            return entityProperty.toString();
        }
    }
}
