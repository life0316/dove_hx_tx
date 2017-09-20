package com.haoxi.dove.utils;

import com.haoxi.dove.newin.bean.PointBean;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InnerRbsConverter implements PropertyConverter<List<PointBean>, String> {
    @Override
    public ArrayList<PointBean> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        else {
            List<String> list = Arrays.asList(databaseValue.split(","));

            ArrayList<PointBean> pointBeans = new ArrayList<>();

            for (String str:list) {
                String[] _str = str.split("-");

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
                pointBeans.add(pointBean);
            }

            return pointBeans;
        }
    }

    @Override
    public String convertToDatabaseValue(List<PointBean> entityProperty) {
        if(entityProperty==null){
            return null;
        }
        else{
            StringBuilder sb= new StringBuilder();
            for(PointBean link:entityProperty){
                sb.append(link.toString());
                sb.append(",");
            }
            return sb.toString();
        }
    }
}
