package org.zywx.wbpalmstar.plugin.citylistview;

import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class EUExCityListView extends EUExBase{

	private static String TAG = "EUExCityListView";
	public static final String CITYLISTVIEW_FUN_PARAMS_KEY = "citylistviewFunParamsKey";
	public static final String CITYLISTVIEW_ACTIVITY_ID = "citylistviewActivityID";
	public static final String CITYLISTVIEW_EXTRA_UEXBASE_OBJ = "org.zywx.wbpalmstar.plugin.citylistview.CITYLISTVIEW_EXTRA_UEXBASE_OBJ";
	public static final String CITYLISTVIEW_CB_LOAD_DATA = "uexCityListView.cbLoadData";
	public static final String CITYLISTVIEW_ON_ITEM_CLICK = "uexCityListView.onItemClick";

	public static final int CITYLISTVIEW_MSG_OPEN = 0;
	public static final int CITYLISTVIEW_MSG_CLOSE = 1;
	public static final int CITYLISTVIEW_MSG_SET_LOCAL_CITY = 2;
	public static final int CITYLISTVIEW_MSG_SET_HOT_CITY = 3;
	public static final int CITYLISTVIEW_MSG_SET_ALL_CITY = 4;
	public static final int CITYLISTVIEW_MSG_SET_VIEW_STYLE = 5;

    private CityListViewView cityListView;

	public EUExCityListView(Context context, EBrowserView inParent) {
		super(context, inParent);
	}

	public void open(String[] params) {
		sendMessageWithType(CITYLISTVIEW_MSG_OPEN, params);
	}

	public void close(String[] params) {
		sendMessageWithType(CITYLISTVIEW_MSG_CLOSE, params);
	}

	/**
	 * 设置定位城市
	 * 
	 * @param params
	 */
	public void setLocalCity(final String[] params) {
		sendMessageWithType(CITYLISTVIEW_MSG_SET_LOCAL_CITY, params);
	}

	/**
	 * 设置热门城市
	 * 
	 * @param params
	 */
	public void setHotCity(final String[] params) {
		sendMessageWithType(CITYLISTVIEW_MSG_SET_HOT_CITY, params);
	}

	/**
	 * 设置所有城市
	 * 
	 * @param params
	 */
	public void setAllCity(final String[] params) {
		sendMessageWithType(CITYLISTVIEW_MSG_SET_ALL_CITY, params);
	}

	/**
	 * 设置背景布局
	 * 
	 * @param params
	 */
	public void setViewStyle(final String[] params) {
		sendMessageWithType(CITYLISTVIEW_MSG_SET_VIEW_STYLE, params);
	}

	private void sendMessageWithType(int msgType, String[] params) {
		if (mHandler == null) {
			return;
		}
		Message msg = new Message();
		msg.what = msgType;
		msg.obj = this;
		Bundle b = new Bundle();
		b.putStringArray(CITYLISTVIEW_FUN_PARAMS_KEY, params);
		msg.setData(b);
		mHandler.sendMessage(msg);
	}

	@Override
	public void onHandleMessage(Message msg) {
		if (msg.what == CITYLISTVIEW_MSG_OPEN) {
			handleOpen(msg);
		} else {
			handleMessage(msg);
		}
	}

	private void handleOpen(Message msg) {
		Log.i(TAG, " handleOpen");
		String[] params = msg.getData().getStringArray(
				CITYLISTVIEW_FUN_PARAMS_KEY);
		try {
            if (cityListView != null) return;
            cityListView = new CityListViewView(mContext, this);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					Integer.parseInt(params[2]), Integer.parseInt(params[3]));
			lp.leftMargin = Integer.parseInt(params[0]);
			lp.topMargin = Integer.parseInt(params[1]);
			addView2CurrentWindow(cityListView, lp);
			String js = SCRIPT_HEADER + "if(" + CITYLISTVIEW_CB_LOAD_DATA + "){"
					+ CITYLISTVIEW_CB_LOAD_DATA + "();}";
			onCallback(js);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addView2CurrentWindow(final View child,
			RelativeLayout.LayoutParams parms) {
		int l = (int) (parms.leftMargin);
		int t = (int) (parms.topMargin);
		int w = parms.width;
		int h = parms.height;
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
		lp.leftMargin = l;
		lp.topMargin = t;
		adptLayoutParams(parms, lp);
		mBrwView.addViewToCurrentWindow(child, lp);
	}

	private void handleMessage(Message msg) {
		if (cityListView != null) {
			String[] params = msg.getData().getStringArray(
					CITYLISTVIEW_FUN_PARAMS_KEY);
			switch (msg.what) {
			case CITYLISTVIEW_MSG_CLOSE:
				handleClose(cityListView);
				break;
			case CITYLISTVIEW_MSG_SET_LOCAL_CITY:
				handleSetLocalCity(cityListView, params);
				break;
			case CITYLISTVIEW_MSG_SET_HOT_CITY:
				handleSetHotCity(cityListView, params);
				break;
			case CITYLISTVIEW_MSG_SET_ALL_CITY:
				handleSetAllCity(cityListView, params);
				break;
			case CITYLISTVIEW_MSG_SET_VIEW_STYLE:
				handleSetViewStyle(cityListView, params);
				break;
			}
		}
	}

	private void handleClose(CityListViewView activity) {
		Log.i(TAG, " handleClose");
		mBrwView.removeViewFromCurrentWindow(cityListView);
        cityListView = null;
	}

	private void handleSetLocalCity(final CityListViewView activity,
			String[] params) {
		if (params.length < 1) {
			return;
		}
		String localcity = CityDataInfo.getLocalCity(params[0]);
		Log.i(TAG, " handleSetLocalCity " + localcity);
		activity.setLocalcity(localcity);
	}

	private void handleSetHotCity(final CityListViewView activity,
			String[] params) {
		if (params.length < 1) {
			return;
		}
		String[] hotCityStr = CityDataInfo.getHotCityList(params[0]);
		Log.i(TAG, " handleSetHotCity " + hotCityStr.length);
		activity.setHotCityList(hotCityStr);
	}

	private void handleSetAllCity(final CityListViewView activity,
			String[] params) {
		if (params.length < 1) {
			return;
		}
		Log.i(TAG, " handleSetAllCity " + params[0]);
		activity.setAllCitys(params[0]);
	}

	private void handleSetViewStyle(final CityListViewView activity,
			String[] params) {
		if (params.length < 1) {
			return;
		}
		Object[] objectArray = CityDataInfo.getViewStyle(params[0]);
		Log.i(TAG, " handleSetViewStyle " + objectArray);
		activity.setViewStyle(objectArray);
	}

	public interface OnCloseListener {
		public void onClose(String cityCode);
	}

	@Override
	protected boolean clean() {
		close(null);
		return false;
	}
}
