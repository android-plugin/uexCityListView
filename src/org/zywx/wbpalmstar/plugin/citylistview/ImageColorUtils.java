package org.zywx.wbpalmstar.plugin.citylistview;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.zywx.wbpalmstar.base.BUtility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;

public class ImageColorUtils {

	public static int parseColor(String colorStr) {
		if (TextUtils.isEmpty(colorStr))
			return 0;
		colorStr.trim();
		if ("rgb".equals(colorStr.toLowerCase().substring(0, 3))) {
			if ("rgba".equals(colorStr.toLowerCase().subSequence(0, 4))) {
				String colorTemp = colorStr.substring(5, colorStr.length() - 1);
				String colorArray[] = colorTemp.split(",");
				return Color.argb(Integer.parseInt(colorArray[3]),
						Integer.parseInt(colorArray[0]),
						Integer.parseInt(colorArray[1]),
						Integer.parseInt(colorArray[2]));
			} else {
				String colorTemp = colorStr.substring(4, colorStr.length() - 1);
				String colorArray[] = colorTemp.split(",");
				return Color.rgb(Integer.parseInt(colorArray[0]),
						Integer.parseInt(colorArray[1]),
						Integer.parseInt(colorArray[2]));
			}
		} else if (colorStr.charAt(0) == '#' && colorStr.length() == 4) {
			String colorTemp = colorStr.substring(1);
			StringBuffer colorSb = new StringBuffer();
			colorSb.append("#");
			colorSb.append(colorTemp.charAt(0));
			colorSb.append(colorTemp.charAt(0));
			colorSb.append(colorTemp.charAt(1));
			colorSb.append(colorTemp.charAt(1));
			colorSb.append(colorTemp.charAt(2));
			colorSb.append(colorTemp.charAt(2));
			return Color.parseColor(colorSb.toString());
		} else if (colorStr.charAt(0) == '#' && colorStr.length() == 9) {
			return Color.argb(Integer.parseInt(colorStr.substring(1, 3), 16),
					Integer.parseInt(colorStr.substring(3, 5), 16),
					Integer.parseInt(colorStr.substring(5, 7), 16),
					Integer.parseInt(colorStr.substring(7, 9), 16));
		} else {
			return Color.parseColor(colorStr);
		}
	}

	public static Bitmap getImage(Context ctx, String imgUrl) {
		if (imgUrl == null || imgUrl.length() == 0) {
			return null;
		}

		Bitmap bitmap = null;
		InputStream is = null;
		try {
			if (imgUrl.startsWith(BUtility.F_Widget_RES_SCHEMA)) {
				is = BUtility.getInputStreamByResPath(ctx, imgUrl);
				bitmap = BitmapFactory.decodeStream(is);
			} else if (imgUrl.startsWith(BUtility.F_FILE_SCHEMA)) {
				imgUrl = imgUrl.replace(BUtility.F_FILE_SCHEMA, "");
				bitmap = BitmapFactory.decodeFile(imgUrl);
			} else if (imgUrl.startsWith(BUtility.F_Widget_RES_path)) {
				try {
					is = ctx.getAssets().open(imgUrl);
					if (is != null) {
						bitmap = BitmapFactory.decodeStream(is);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (imgUrl.startsWith("/")) {
				bitmap = BitmapFactory.decodeFile(imgUrl);
			} else if (imgUrl.startsWith("http://")) {
				bitmap = downloadNetworkBitmap(imgUrl);
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	private static Bitmap downloadNetworkBitmap(String url) {
		byte[] data = downloadImageFromNetwork(url);
		if (data == null || data.length == 0) {
			return null;
		}
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	private static byte[] downloadImageFromNetwork(String url) {
		InputStream is = null;
		byte[] data = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			BasicHttpParams httpParams = new BasicHttpParams();
			// HttpConnectionParams.setConnectionTimeout(httpParams, 60);
			// HttpConnectionParams.setSoTimeout(httpParams, 60);
			HttpResponse httpResponse = new DefaultHttpClient(httpParams)
					.execute(httpGet);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				is = httpResponse.getEntity().getContent();
				data = transStreamToBytes(is, 4096);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private static byte[] transStreamToBytes(InputStream is, int buffSize) {
		if (is == null) {
			return null;
		}
		if (buffSize <= 0) {
			throw new IllegalArgumentException(
					"buffSize can not less than zero.....");
		}
		byte[] data = null;
		byte[] buffer = new byte[buffSize];
		int actualSize = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while ((actualSize = is.read(buffer)) != -1) {
				baos.write(buffer, 0, actualSize);
			}
			data = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	
	public static Bitmap setAlpha(Bitmap sourceImg, int number) {
		 int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		 sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg
		 .getWidth(), sourceImg.getHeight());// ���ͼƬ��ARGBֵ
		 number = number * 255 / 100;
		 for (int i = 0; i < argb.length; i++) {
			 argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
		 }
		 sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg
		 .getHeight(), Config.ARGB_8888);
		
		 return sourceImg;
	}
	
	
	public static Drawable setShape(int fillColor){ //内部填充颜色
		int strokeWidth = 1; // 3dp 边框宽度
	    int roundRadius = 15; // 8dp 圆角半径
	    int strokeColor = Color.parseColor("#FFCBCBCB");//边框颜色
	    GradientDrawable gd = new GradientDrawable();//创建drawable
	    gd.setColor(fillColor);
	    gd.setCornerRadius(roundRadius);
	    gd.setStroke(strokeWidth, strokeColor);
		return gd;
	}
}
