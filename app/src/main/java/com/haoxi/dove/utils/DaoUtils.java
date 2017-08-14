package com.haoxi.dove.utils;

/**
 * Created by Administrator on 2017\6\30 0030.
 */

public class DaoUtils {

//    public static InnerCircleBean getCircle(String playerId,String userId,String circleId){
//        MyApplication.getDaoSession().getInnerCircleBeanDao()
//                .queryBuilder().where(InnerCircleBeanDao.Properties.Playerid.eq(playerId)
//                ,InnerCircleBeanDao.Properties.Userid.eq(userId)
//                , InnerCircleBeanDao.Properties.Circleid.eq(circleId))
//                .rx().list()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<List<InnerCircleBean>>() {
//                    @Override
//                    public void call(List<InnerCircleBean> innerCircleBeans) {
//                        CircleBean circleBean = new CircleBean();
//                        circleBean.setMsg("从数据库中获取");
//                        Collections.sort(innerCircleBeans, new CircleComparator());
//                        circleBean.setData(innerCircleBeans);
//                        requestSuccess(circleBean);
//                    }
//                });
//    }
}
