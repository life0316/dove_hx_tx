package com.haoxi.dove.newin;


import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.newin.bean.OurCode;
import java.util.Map;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
public class OurCodeModel extends BaseModel implements IOuerCodeMode<OurCode> {
    private static final String TAG = "OurCodeModel";

    @Override
    public void getValidCode(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {

        ourNewService.getValidVerCode(map).
            subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }

    @Override
    public void getRequestCode(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.getRequestVerCode(map).
        subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }

    @Override
    public void resetPwd(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.getResetPwd(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }

    @Override
    public void exitApp(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.exitApp(map).
                subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }

    @Override
    public void updateUserInfo(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
            ourNewService.getUpdateInfo(map)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            requestCallback.beforeRequest();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(new Func1<OurCode, Boolean>() {
                        @Override
                        public Boolean call(OurCode codesBean) {
                            if (codesBean.getCode() != 200){
                                requestCallback.requestError(codesBean.getMsg());
                            }
                            return 200 == codesBean.getCode();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<>(requestCallback));
    }

    @Override
    public void addDove(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {

                ourNewService.addPigeon(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void deleteDove(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.deleteDove(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }

    @Override
    public void updateDoveInfo(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.updateDove(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }

    @Override
    public void addRing(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.addRing(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }

    @Override
    public void deleteRing(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {

        ourNewService.deleteRing(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void updateRingInfo(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.updateRing(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void ringWithDove(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.matchRing(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void ringUnbindDove(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.dematchRing(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }

    @Override
    public void endFly(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.stopFly(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void deleteFly(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.deleteFly(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void addAttention(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.attentionFriend(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void removeAttention(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.removeAttention(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {

                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void addCircle(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.addCircle(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void shareCircle(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.shareCircle(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }

                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }

    @Override
    public void deleteSingleCircle(Map<String, String> map, final RequestCallback<OurCode> requestCallback) {
        ourNewService.deleteSingle(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void deleteAllCircle(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.deleteAll(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void addComment(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.addComment(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void addReply(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.addReply(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }

    @Override
    public void removeComment(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.removeComment(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void addFab(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.addFab(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void removeFab(Map<String, String> map,final RequestCallback<OurCode> requestCallback) {
        ourNewService.removeFab(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurCode, Boolean>() {
                    @Override
                    public Boolean call(OurCode codesBean) {
                        if (codesBean.getCode() != 200){
                            requestCallback.requestError(codesBean.getMsg());
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }
}
