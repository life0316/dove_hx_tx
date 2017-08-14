package com.haoxi.dove.newin.ourcircle.model;


import android.util.Log;

import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.newin.bean.InnerComment;
import com.haoxi.dove.newin.bean.InnerCommentDao;
import com.haoxi.dove.newin.bean.OurCommentBean;

import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by lifei on 2017/3/29.
 */

public class OurCommentModel extends BaseModel implements IGetModel<OurCommentBean> {

    private static final String TAG = "OurCodeModel";


    public OurCommentModel() {
    }

    @Override
    public void getDatasFromNets(Map<String, String> map, RequestCallback<OurCommentBean> requestCallback) {

        ourNewService.getComment(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCommentBean, Boolean>() {
                    @Override
                    public Boolean call(OurCommentBean myDynamicBean) {

                        Log.e(TAG,myDynamicBean.getMsg()+"======="+myDynamicBean.getCode());

                        return myDynamicBean.getCode() == 200;
                    }
                })
                .doOnNext(new Action1<OurCommentBean>() {
                    @Override
                    public void call(OurCommentBean ourCommentBean) {

                        for (final InnerComment innerComment: ourCommentBean.getData()) {
                            MyApplication.getDaoSession().getInnerCommentDao()
                                    .queryBuilder().where(
//                                            InnerCircleBeanDao.Properties.Playerid.eq(innerComment.getPlayerid()),
                                    InnerCommentDao.Properties.Userid.eq(innerComment.getUserid()),
                                    InnerCommentDao.Properties.Circleid.eq(innerComment.getCircleid()),
                                    InnerCommentDao.Properties.Commentid.eq(innerComment.getCommentid())
                            )
                                    .rx().list()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<List<InnerComment>>() {
                                        @Override
                                        public void call(List<InnerComment> innerCommentList) {

                                            Log.e("fasdfefasdf",innerCommentList.size()+"-------");

                                            if (innerCommentList.size() > 0){
                                                Log.e("",innerCommentList.get(0).getId()+"-------id");
                                                innerComment.setId(innerCommentList.get(0).getId());
                                                MyApplication.getDaoSession().getInnerCommentDao().updateInTx(innerComment);
                                            }else {
                                                MyApplication.getDaoSession().getInnerCommentDao()
                                                        .insertOrReplaceInTx(innerComment);
                                            }
                                        }
                                    });
                        }
                    }
                })
                .subscribe(new BaseSubscriber<OurCommentBean>(requestCallback));
    }
}
