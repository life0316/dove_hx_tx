package com.haoxi.dove.utils;

import android.content.Context;

import com.haoxi.dove.callback.SaveImgCallback;
import com.haoxi.dove.retrofit.RegistService.IOurNewService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017\6\20 0020.
 *
 * 通过url下载图片并保存到本地
 *
 * 1、通过retrofit
 * 2、通过HttpURLConnection
 *
 *
 */
public class SavePicByUrlUtils {

    private Context context;

    public static void downloadImg(final Context context, IOurNewService ourNewService, final String imgUrl, final SaveImgCallback saveImgCallback){

//        Call<ResponseBody> resultCall = ourNewService.downloadImage(imgUrl);

//        resultCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//                writeResponseBodyToDisk(context,imgUrl,response.body(),saveImgCallback);
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                saveImgCallback.saveImaFailure("下载异常");
//            }
//        });

        Observable<ResponseBody> resultCall = ourNewService.downloadImage(imgUrl);

        resultCall
                .subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        saveImgCallback.saveImaFailure("下载异常---"+e.toString());
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        writeResponseBodyToDisk(context,imgUrl,response,saveImgCallback);
                    }
                });
    }

    public static void writeResponseBodyToDisk(Context context,String imageUrl,ResponseBody body,SaveImgCallback saveImgCallback){
        if (body == null) {
            //ApiUtils.showToast(context,"图片源错误");
            saveImgCallback.saveImaFailure("图片源错误");
            return;
        }

        InputStream is = body.byteStream();

        String suffix = imageUrl.substring(imageUrl.lastIndexOf("."));

        String path = context.getFilesDir() + File.separator + "pigeon";

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        File file = new File(path,"pigeon_"+suffix);
        if (file.exists()) {
            file.delete();
            file = new File(path,"pigeon_"+System.currentTimeMillis()+suffix);
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1){
                fos.write(buffer,0,len);
            }

            saveImgCallback.saveImgSuccess("保存成功-------"+file.getAbsolutePath());

            fos.flush();
            fos.close();
            bis.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
            saveImgCallback.saveImaFailure("写入异常---"+e.toString());
        }
    }
}
