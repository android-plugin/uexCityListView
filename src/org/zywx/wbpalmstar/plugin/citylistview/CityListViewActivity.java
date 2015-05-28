package org.zywx.wbpalmstar.plugin.citylistview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.citylistview.view.PinnedHeaderListView;
import org.zywx.wbpalmstar.plugin.citylistview.view.PinyinComparator;
import org.zywx.wbpalmstar.plugin.citylistview.view.SideBar;
import org.zywx.wbpalmstar.plugin.citylistview.view.SideBar.OnTouchingLetterChangedListener;
import org.zywx.wbpalmstar.plugin.citylistview.view.SortAdapter;
import org.zywx.wbpalmstar.plugin.citylistview.view.SortAdapter.SortOnClickListener;
import org.zywx.wbpalmstar.plugin.citylistview.view.SortModel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CityListViewActivity extends Activity implements TextWatcher {
	private EUExCityListView mUexBaseObj;
	private final static String lastCitysKey = "lastCitys";
	private final static String firstCityKey = "firstCity";
	private final static String secondCityKey = "secondCity";
	private final static String threeCityKey = "threeCity";
	// --------------列表界面成员变量--------------
	private PinnedHeaderListView sortListView;
	private SortAdapter sortAdapter;
	private EditText mSearchEditText;
	private LinearLayout top_search_linearLayout;
	private LinearLayout searchLinearLayout;
	private TextView promptMsg;
	private List<SortModel> sourceDateList;
	// 根据拼音来排列ListView里面的数据类
	private PinyinComparator pinyinComparator;
	private SideBar sideBar;
	private TextView dialog;
	private View cityheaderView;
	// 定位城市
	private TextView locationCityTextView;
	// 最近搜索
	private LinearLayout lastvisitcitysLinearLayout;
	// 热门城市
	private LinearLayout hotcitysLinearLayout;
	private String textColor;
	private String indexBarColor;
	private ListViewBean listViewBean;
	private SharedPreferences sharePrefrence;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent.hasExtra(EUExCityListView.CITYLISTVIEW_EXTRA_UEXBASE_OBJ)) {
			mUexBaseObj = (EUExCityListView) intent
					.getParcelableExtra(EUExCityListView.CITYLISTVIEW_EXTRA_UEXBASE_OBJ);
		}
		sharePrefrence = getSharedPreferences(lastCitysKey,
				Activity.MODE_PRIVATE);
		findView();
		addLastCityView();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		realeaseDatas();
		super.onDestroy();
	}

	private void realeaseDatas() {
		if (sourceDateList != null) {
			sourceDateList.clear();
			sourceDateList = null;
		}
	}

	private void findView() {
		setContentView(EUExUtil.getResLayoutID("plugin_uexcitylistview_main"));
		searchLinearLayout = (LinearLayout) findViewById(EUExUtil
				.getResIdID("searchLinearLayout"));
		mSearchEditText = (EditText) findViewById(EUExUtil
				.getResIdID("top_search_editText"));
		top_search_linearLayout = (LinearLayout) findViewById(EUExUtil
				.getResIdID("top_search_linearLayout"));
		sortListView = (PinnedHeaderListView) findViewById(EUExUtil
				.getResIdID("country_lvcountry"));
		// 定位城市等列表头
		cityheaderView = getLayoutInflater().inflate(
				EUExUtil.getResLayoutID("plugin_uexcitylistview_header"),
				sortListView, false);
		locationCityTextView = (TextView) cityheaderView.findViewById(EUExUtil
				.getResIdID("locationCityTextView"));
		// 最近搜索的城市
		lastvisitcitysLinearLayout = (LinearLayout) cityheaderView
				.findViewById(EUExUtil.getResIdID("lastvisitcitys"));
		// 热门城市
		hotcitysLinearLayout = (LinearLayout) cityheaderView
				.findViewById(EUExUtil.getResIdID("hotcitysLinearLayout"));
		sortListView.addHeaderView(cityheaderView);
		promptMsg = (TextView) findViewById(EUExUtil.getResIdID("tv_visa_people_select_info"));
	}

	private void initCityListView() {
		sideBar = (SideBar) findViewById(EUExUtil.getResIdID("sidrbar"));
		sideBar.setIndexBarColor(indexBarColor);
		dialog = (TextView) findViewById(EUExUtil.getResIdID("dialog"));
		sideBar.setTextView(dialog);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				if (s.equals("#")) {
					sortListView.setSelection(0);
				} else {
					// 该字母首次出现的位置
					int position = sortAdapter.getPositionForSection(s
							.charAt(0));
					if (position != -1) {
						sortListView.setSelection(position + 1);
					}
				}
			}
		});
		pinyinComparator = new PinyinComparator();
		// 根据a-z进行排序源数据
		Collections.sort(sourceDateList, pinyinComparator);
		if (sourceDateList != null) {
			SortModel sortModellast = null;
			for (int i = 0, size = sourceDateList.size(); i < size; i++) {
				SortModel sortModel = sourceDateList.get(i);
				if (sortModel != null && sortModellast != null) {
					char lastSortChar = sortModellast.getSortLetters()
							.toUpperCase(Locale.US).charAt(0);
					char sortChar = sortModel.getSortLetters()
							.toUpperCase(Locale.US).charAt(0);
					if (lastSortChar != sortChar) {
						sortModellast.setHiddenLine(true);
					} else {
						sortModellast.setHiddenLine(false);
					}
				}
				sortModellast = sortModel;
			}
			if (sortModellast != null) {
				sortModellast.setHiddenLine(true);
			}
		}
		sortAdapter = new SortAdapter(this, sourceDateList, sortListView);
		sortAdapter.setHiddenImgTitle(false);
		sortAdapter.setListViewBean(listViewBean);
		sortAdapter.setSoftOnClickListener(new SortOnClickListener() {
			@Override
			public void onClick(View view, int position) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				String city = ((SortModel) sortAdapter.getItem(position))
						.getName();
				saveCityAndCallback(city);
			}
		});
		sortListView.setAdapter(sortAdapter);
		sortListView.setOnScrollListener(sortAdapter);
		// 拼音显示头
		View headerView = getLayoutInflater()
				.inflate(
						EUExUtil.getResLayoutID("plugin_uexcitylistview_listitem_header"),
						sortListView, false);
		sortListView.setPinnedHeader(headerView);
		sortListView.setPinnedHeaderHidden(true);
		mSearchEditText.addTextChangedListener(this);
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		boolean isSearchMode = false;
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = sourceDateList;
			sortAdapter.setHiddenImgTitle(false);
			sideBar.setVisibility(View.VISIBLE);
			sortListView.setPinnedHeaderHidden(true);
			sortListView.addHeaderView(cityheaderView);
		} else {
			sortAdapter.setHiddenImgTitle(true);
			sideBar.setVisibility(View.GONE);
			sortListView.setPinnedHeaderHidden(true);
			sortListView.removeHeaderView(cityheaderView);
			filterDateList.clear();
			for (SortModel sortModel : sourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| sortModel.getPinyin().startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
					isSearchMode = true;
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		if (filterDateList.size() == 0) {
			promptMsg.setVisibility(View.VISIBLE);
		} else {
			promptMsg.setVisibility(View.GONE);
		}
		sortAdapter.updateListView(filterDateList, isSearchMode);
	}

	@Override
	public void afterTextChanged(Editable editable) {
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
		filterData(s.toString().toLowerCase(Locale.US));
	}

	/**
	 * 设置定位城市
	 * 
	 * @param localCity
	 */

	public void setLocalcity(String localCity) {
		setLocationCityView(localCity);
	}

	/**
	 * 设置热门城市
	 * 
	 * @param hotCitys
	 */

	public void setHotCityList(String[] hotCitys) {
		addHotCityView(hotCitys);
	}

	/**
	 * 设置背景
	 * 
	 * @param objectArray
	 */
	public void setViewStyle(Object[] objectArray) {
		if (objectArray != null && objectArray.length == 4) {
			SearchBar searchBar = (SearchBar) objectArray[0];
			if (searchBar != null) {
				if (!TextUtils.isEmpty(searchBar.getPlacehoderText())
						&& mSearchEditText != null) {
					mSearchEditText.setHint(searchBar.getPlacehoderText());
				}
				if (!TextUtils.isEmpty(searchBar.getTextColor())
						&& mSearchEditText != null) {
					mSearchEditText.setTextColor(ImageColorUtils
							.parseColor(searchBar.getTextColor()));
				}
				if (!TextUtils.isEmpty(searchBar.getBgColor())
						&& top_search_linearLayout != null) {
					top_search_linearLayout.setBackgroundColor(ImageColorUtils
							.parseColor(searchBar.getBgColor()));
				}
				if (!TextUtils.isEmpty(searchBar.getInputBgColor())
						&& searchLinearLayout != null) {
					searchLinearLayout.setBackgroundDrawable(ImageColorUtils
							.setShape(ImageColorUtils.parseColor(searchBar
									.getInputBgColor())));
				}
			}
			HeaderView headerView = (HeaderView) objectArray[1];
			if (headerView != null) {
				if (!TextUtils.isEmpty(headerView.getBgColor())
						&& cityheaderView != null) {
					cityheaderView.setBackgroundColor(ImageColorUtils
							.parseColor(headerView.getBgColor()));
				}
				if (!TextUtils.isEmpty(headerView.getSectionHeaderTitleColor())
						&& cityheaderView != null) {
					String titleColor = headerView.getSectionHeaderTitleColor();
					TextView localTextView = (TextView) cityheaderView
							.findViewById(EUExUtil.getResIdID("localTextView"));
					localTextView.setTextColor(ImageColorUtils
							.parseColor(titleColor));

					TextView lastTextView = (TextView) cityheaderView
							.findViewById(EUExUtil.getResIdID("lastTextView"));
					lastTextView.setTextColor(ImageColorUtils
							.parseColor(titleColor));

					TextView hotTextView = (TextView) cityheaderView
							.findViewById(EUExUtil.getResIdID("hotTextView"));
					hotTextView.setTextColor(ImageColorUtils
							.parseColor(titleColor));
				}
				if (!TextUtils.isEmpty(headerView.getSeparatorLineColor())
						&& cityheaderView != null) {
					String lineColor = headerView.getSeparatorLineColor();
					cityheaderView.findViewById(
							EUExUtil.getResIdID("localLineView"))
							.setBackgroundColor(
									ImageColorUtils.parseColor(lineColor));
					cityheaderView.findViewById(
							EUExUtil.getResIdID("hotLineView"))
							.setBackgroundColor(
									ImageColorUtils.parseColor(lineColor));
					cityheaderView.findViewById(
							EUExUtil.getResIdID("lastLineView"))
							.setBackgroundColor(
									ImageColorUtils.parseColor(lineColor));
				}
				if (!TextUtils.isEmpty(headerView.getItemTextColor())) {
					textColor = headerView.getItemTextColor();
				}
			}
			ListViewBean listViewBean = (ListViewBean) objectArray[2];
			if (listViewBean != null) {
				if (!TextUtils.isEmpty(listViewBean.getBgColor())
						&& sortListView != null) {
					sortListView.setBackgroundColor(ImageColorUtils
							.parseColor(listViewBean.getBgColor()));
				}
				this.listViewBean = listViewBean;
				if (sortAdapter != null) {
					sortAdapter.setListViewBean(listViewBean);
				}
				sortAdapter.notifyDataSetChanged();
			}
			IndexBar indexBar = (IndexBar) objectArray[3];
			if (indexBar != null) {
				indexBarColor = indexBar.getIndexBarTextColor();
				if (sideBar != null) {
					sideBar.setIndexBarColor(indexBarColor);
				}
			}
		
		}
	}

	/**
	 * 设置全部城市
	 * 
	 * @param resPath
	 */
	public void setAllCitys(String resPath) {
		String cityString = CityDataInfo.readCityJSON(this, resPath);
		sourceDateList = CityDataInfo.getCityList(cityString, CityDataInfo.allCity);
		initCityListView();
	}
	
	/**
	 * 设置定位城市
	 * 
	 * @param city
	 */
	private void setLocationCityView(final String city) {
		if (TextUtils.isEmpty(city))
			return;
		locationCityTextView.setText(city);
		if (!TextUtils.isEmpty(textColor)) {
			locationCityTextView.setTextColor(ImageColorUtils
					.parseColor(textColor));
		}
		locationCityTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				saveCityAndCallback(city);
			}
		});
	}

	/**
	 * 设置最近城市
	 */
	private void addLastCityView() {
		final String firstCity = sharePrefrence.getString(firstCityKey, "");
		final String secondCity = sharePrefrence.getString(secondCityKey, "");
		final String threeCity = sharePrefrence.getString(threeCityKey, "");
		if (TextUtils.isEmpty(firstCity) && TextUtils.isEmpty(secondCity)
				&& TextUtils.isEmpty(threeCity)) {
			return;
		}
		lastvisitcitysLinearLayout.removeAllViews();
		View lastvisitCityView = getLayoutInflater()
				.inflate(
						EUExUtil.getResLayoutID("plugin_uexcitylistview_listitem_additem"),
						lastvisitcitysLinearLayout, false);
		TextView firstCityTextView = (TextView) lastvisitCityView
				.findViewById(EUExUtil.getResIdID("firstCityTextView"));
		if (!TextUtils.isEmpty(firstCity)) {
			firstCityTextView.setVisibility(View.VISIBLE);
			firstCityTextView.setText(firstCity);
			firstCityTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					saveCityAndCallback(firstCity);
				}
			});
		} else {
			firstCityTextView.setVisibility(View.INVISIBLE);
			firstCityTextView.setOnClickListener(null);
		}
		TextView secondCityTextView = (TextView) lastvisitCityView
				.findViewById(EUExUtil.getResIdID("secondCityTextView"));
		if (!TextUtils.isEmpty(secondCity)) {
			secondCityTextView.setVisibility(View.VISIBLE);
			secondCityTextView.setText(secondCity);
			secondCityTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					saveCityAndCallback(secondCity);
				}
			});
		} else {
			secondCityTextView.setVisibility(View.INVISIBLE);
			secondCityTextView.setOnClickListener(null);
		}
		TextView threeCityTextView = (TextView) lastvisitCityView
				.findViewById(EUExUtil.getResIdID("threeCityTextView"));
		if (!TextUtils.isEmpty(threeCity)) {
			threeCityTextView.setVisibility(View.VISIBLE);
			threeCityTextView.setText(threeCity);
			threeCityTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					saveCityAndCallback(threeCity);
				}
			});
		} else {
			threeCityTextView.setVisibility(View.INVISIBLE);
			threeCityTextView.setOnClickListener(null);
		}
		lastvisitcitysLinearLayout.addView(lastvisitCityView);
	}

	private void saveCityAndCallback(String city) {
		saveLastCity(city);
		if (mUexBaseObj != null) {
			String js = EUExBase.SCRIPT_HEADER + "if(" + EUExCityListView.CITYLISTVIEW_ON_ITEM_CLICK + "){"
					+ EUExCityListView.CITYLISTVIEW_ON_ITEM_CLICK + "('" + city + "');}";
			mUexBaseObj.onCallback(js);
		}
	}
	
	/**
	 * 保存最近浏览
	 * 
	 * @param cityName
	 */
	private void saveLastCity(String cityName) {
		if (TextUtils.isEmpty(cityName)) {
			return;
		}
		if (sharePrefrence == null) {
			sharePrefrence = getSharedPreferences(lastCitysKey,
					Activity.MODE_PRIVATE);
		}
		final String firstCity = sharePrefrence.getString(firstCityKey, "");
		final String secondCity = sharePrefrence.getString(secondCityKey, "");
		final String threeCity = sharePrefrence.getString(threeCityKey, "");
		SharedPreferences.Editor editor = sharePrefrence.edit();
		editor.putString(firstCityKey, cityName);
		if (!cityName.equals(firstCity)) {
			editor.putString(secondCityKey, firstCity);
		}
		if (!cityName.equals(firstCity) && !cityName.equals(secondCity)) {
			editor.putString(threeCityKey, secondCity);
		}
		editor.commit();
	}

	/**
	 * 设置热门城市
	 * 
	 * @param hotCitys
	 */
	private void addHotCityView(final String[] hotCitys) {
		if (hotCitys == null)
			return;
		hotcitysLinearLayout.removeAllViews();
		ArrayList<String[]> hotCitysList = new ArrayList<String[]>();
		for (int i = 0, length = hotCitys.length; i < length; i++) {
			if (i % 3 == 0) {
				String[] array = new String[3];
				array[0] = hotCitys[i];
				hotCitysList.add(array);
			} else {
				String[] hotCityArray = hotCitysList.get(i / 3);
				if (hotCityArray != null && hotCityArray.length == 3) {
					hotCityArray[i % 3] = hotCitys[i];
				}
			}
		}
		for (int i = 0, size = hotCitysList.size(); i < size; i++) {
			final String[] hotCityArray = hotCitysList.get(i);
			if (hotCityArray != null && hotCityArray.length == 3) {
				View hotCityView = getLayoutInflater()
						.inflate(
								EUExUtil.getResLayoutID("plugin_uexcitylistview_listitem_additem"),
								hotcitysLinearLayout, false);
				TextView firstCityTextView = (TextView) hotCityView
						.findViewById(EUExUtil.getResIdID("firstCityTextView"));
				if (!TextUtils.isEmpty(hotCityArray[0])) {
					firstCityTextView.setText(hotCityArray[0]);
					firstCityTextView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							saveCityAndCallback(hotCityArray[0]);
						}
					});
				} else {
					firstCityTextView.setVisibility(View.INVISIBLE);
				}

				TextView secondCityTextView = (TextView) hotCityView
						.findViewById(EUExUtil.getResIdID("secondCityTextView"));
				if (!TextUtils.isEmpty(hotCityArray[1])) {
					secondCityTextView.setText(hotCityArray[1]);
					secondCityTextView
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View view) {
									saveCityAndCallback(hotCityArray[1]);
								}
							});
				} else {
					secondCityTextView.setVisibility(View.INVISIBLE);
				}

				TextView threeCityTextView = (TextView) hotCityView
						.findViewById(EUExUtil.getResIdID("threeCityTextView"));
				if (!TextUtils.isEmpty(hotCityArray[2])) {
					threeCityTextView.setText(hotCityArray[2]);
					threeCityTextView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							saveCityAndCallback(hotCityArray[2]);
						}
					});
				} else {
					threeCityTextView.setVisibility(View.INVISIBLE);
				}
				if (!TextUtils.isEmpty(textColor)) {
					firstCityTextView.setTextColor(ImageColorUtils
							.parseColor(textColor));
					secondCityTextView.setTextColor(ImageColorUtils
							.parseColor(textColor));
					threeCityTextView.setTextColor(ImageColorUtils
							.parseColor(textColor));
				}
				hotcitysLinearLayout.addView(hotCityView);
			}
		}
	}
}