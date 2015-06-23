package org.zywx.wbpalmstar.plugin.citylistview;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.plugin.citylistview.view.SortModel;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CityDataInfo {
	
	private final static String searchBar = "searchBar";
	private final static String placehoderText = "placehoderText"; 
	private final static String bgColor = "bgColor";
	private final static String textColor = "textColor";
	private final static String inputBgColor = "inputBgColor";
	private final static String headerView = "headerView";
	private final static String separatorLineColor = "separatorLineColor";
	private final static String sectionHeaderTitleColor = "sectionHeaderTitleColor";
	private final static String itemTextColor = "itemTextColor";
	private final static String sectionHeaderBgColor = "sectionHeaderBgColor";
	private final static String indexBarTextColor = "indexBarTextColor";
	private final static String listView = "listView";
	private final static String indexBar = "indexBar";
	private final static String localCity = "localCity";
	private final static String hotCity = "hotCity";
	public final static String allCity = "allCity";

	/**
	 * 获取城市定位
	 * @param msg
	 * @return
	 */
	public static String getLocalCity(String msg){
		if (msg == null || msg.length() == 0) {
			return null;
		}
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				String localStr = jsonObj.optString(localCity);
				return localStr;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 设置热门城市
	 * @param msg
	 * @return
	 */
	public static String[] getHotCityList(String msg){
		if (msg == null || msg.length() == 0) {
			return null;
		}
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				JSONArray hotCitysArray = jsonObj.optJSONArray(hotCity);
				if(hotCitysArray != null){
					String[] hotCitys = new String[hotCitysArray.length()];
					for(int i=0,size = hotCitysArray.length(); i < size; i++){
						String hotCity = hotCitysArray.optString(i);
						hotCitys[i] = hotCity;
					}
					return hotCitys;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	} 
	
	/**
	 * 设置列表风格
	 */
	public static Object[] getViewStyle(String msg){
		if (msg == null || msg.length() == 0) {
			return null;
		}
		try {
			Object[] object = new Object[4];
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				JSONObject searchBarObj = jsonObj.optJSONObject(searchBar);
				if(searchBarObj != null){
					SearchBar searchbar = new SearchBar();
					searchbar.setPlacehoderText(searchBarObj.optString(placehoderText));
					searchbar.setBgColor(searchBarObj.optString(bgColor));
					searchbar.setTextColor(searchBarObj.optString(textColor));
					searchbar.setInputBgColor(searchBarObj.optString(inputBgColor));
					object[0] = searchbar;
				}
				JSONObject headerViewObj = jsonObj.optJSONObject(headerView);
				if(headerViewObj != null){
					HeaderView headerView = new HeaderView();
					headerView.setBgColor(headerViewObj.optString(bgColor));
					headerView.setSeparatorLineColor(headerViewObj.optString(separatorLineColor));
					headerView.setSectionHeaderTitleColor(headerViewObj.optString(sectionHeaderTitleColor));
					headerView.setItemTextColor(headerViewObj.optString(itemTextColor));
					object[1] = headerView;
				}
				JSONObject listViewObj = jsonObj.optJSONObject(listView);
				if(listViewObj != null){
					ListViewBean listView = new ListViewBean();
					listView.setBgColor(listViewObj.optString(bgColor));
					listView.setSeparatorLineColor(listViewObj.optString(separatorLineColor));
					listView.setSectionHeaderTitleColor(listViewObj.optString(sectionHeaderTitleColor));
					listView.setSectionHeaderBgColor(listViewObj.optString(sectionHeaderBgColor));
					listView.setItemTextColor(listViewObj.optString(itemTextColor));
					object[2] = listView;
				}
				JSONObject indexBarObj = jsonObj.optJSONObject(indexBar);
				if(indexBarObj != null){
					IndexBar indexBar = new IndexBar();
					indexBar.setIndexBarTextColor(indexBarObj.optString(indexBarTextColor));
					object[3] = indexBar;
				}
			}
			return object;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	} 
	
	/**
	 * 读取文本数据
	 * 
	 * @param context 程序上下文
	 * @return String, 读取到的文本内容，失败返回null
	 */
	public static String readCityJSON(Context context, String resPath) {
		InputStream is = null;
		String content = null;
		try {
			String path = resPath.substring(BUtility.F_Widget_RES_SCHEMA.length());
			String widgetPath = BUtility.F_Widget_RES_path + path;
			is = context.getAssets().open(widgetPath);
			if (is != null) {
				byte[] buffer = new byte[1024];
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
				while (true) {
					int readLength = is.read(buffer);
					if (readLength == -1)
						break;
					arrayOutputStream.write(buffer, 0, readLength);
				}
				is.close();
				arrayOutputStream.close();
				content = new String(arrayOutputStream.toByteArray());
			}
		} catch (Exception e) {
			e.printStackTrace();
			content = null;
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return content;
	}

	public static List<SortModel> getCityList(String JSONString,
			String key) {
		List<SortModel> list = new ArrayList<SortModel>();
		JsonObject result = new JsonParser().parse(JSONString)
				.getAsJsonObject().getAsJsonObject(key);
		Iterator iterator = result.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, JsonElement> entry = (Entry<String, JsonElement>) iterator
					.next();
			SortModel sortModel = new SortModel();
			sortModel.setName(entry.getKey());
			String pinyin = entry.getValue().getAsString();
			sortModel.setPinyin(pinyin);
			String sortString = pinyin.substring(0, 1).toUpperCase(Locale.US);
			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase(Locale.US));
			} else {
				sortModel.setSortLetters("#");
			}
			list.add(sortModel);
		}
		return list;
	}
}
