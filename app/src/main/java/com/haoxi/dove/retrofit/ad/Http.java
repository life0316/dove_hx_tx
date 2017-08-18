package com.haoxi.dove.retrofit.ad;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

public class Http {

	private static final boolean deBug = true;
	private static final String TAG = Http.class.getSimpleName();

	private static InputStream doGet(String urlAddres, String ua) {
		if (TextUtils.isEmpty(urlAddres))
			return null;
		if (deBug)
			Log.d(TAG, urlAddres);
		try {
			URL url = new URL(urlAddres);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			if(ua!=null){
				conn.setRequestProperty("User-Agent", ua);
			}
			
			if (conn.getResponseCode() == 200)
				return conn.getInputStream();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}

	public static String get(String url, String ua) {
		InputStream is = doGet(url, ua);
		return parserInputStreamToString(is);
	}

	public static Bitmap downloadImage(String url, String ua) {
		InputStream is = doGet(url, ua);
		return parserInputStreamToBitmap(is);
	}

	private static String parserInputStreamToString(InputStream is) {
		String result = null;
		if (is != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buff = new byte[4 * 1024];
			int len = 0;
			try {
				while ((len = is.read(buff)) != -1) {
					baos.write(buff, 0, len);
				}
			} catch (IOException e) {
//				e.printStackTrace();
			} finally {
				try {
					baos.flush();
					result = baos.toString();
					baos.close();
					if (is != null)
						is.close();
				} catch (IOException e) {
//					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private static Bitmap parserInputStreamToBitmap(InputStream is) {
		Bitmap bt = null;
		if (is != null) {
			bt = BitmapFactory.decodeStream(is);
			try {
				is.close();
			} catch (IOException e) {
//				e.printStackTrace();
			}
		}
		return bt;
	}
}
